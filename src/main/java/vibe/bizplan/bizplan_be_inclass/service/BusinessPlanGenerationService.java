package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateRequest;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse.BusinessPlanSection;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse.ExportOptions;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse.GenerationMetadata;
import vibe.bizplan.bizplan_be_inclass.entity.BusinessPlan;
import vibe.bizplan.bizplan_be_inclass.exception.GeminiGenerationException;
import vibe.bizplan.bizplan_be_inclass.repository.BusinessPlanGenerationRepository;
import vibe.bizplan.bizplan_be_inclass.repository.BusinessPlanRepository;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * 사업계획서 생성을 위해 Gemini (Spring AI)를 호출하는 서비스 레이어.
 *
 * - 입력: BusinessPlanGenerateRequest (FE 스펙 그대로)
 * - 출력: BusinessPlanGenerateResponse (기존 DTO 구조 유지)
 * - 역할:
 *   - 시스템 프롬프트를 사용해 "전문적인 문서 편집자" 역할을 부여
 *   - 사용자의 사업계획서 초안을 기반으로 보강된 문서를 생성
 *   - UsageMetadata 를 통해 토큰 사용량(prompt/completion/total)을 추출
 *   - 리포지토리를 통해 비용 추적용 로그를 남김
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)  // Rule 306: 기본은 읽기 전용
public class BusinessPlanGenerationService {

    // API 경로 상수
    private static final String API_BASE_PATH = "/api/v1/business-plan/";
    
    // 섹션 기본값 상수
    private static final String DEFAULT_SECTION_ID = "section-1";
    private static final String DEFAULT_SECTION_TITLE = "AI 보강 사업계획서";
    
    // 시스템 프롬프트 상수
    private static final String SYSTEM_PROMPT = """
            당신은 한국어 기반의 **전문적인 사업계획서 편집자이자 컨설턴트**입니다.
            
            역할:
            - 정부 지원사업, 은행 대출, 투자 유치를 위한 사업계획서를 전문적으로 윤문하고 보강합니다.
            - 사용자가 작성한 초안을 존중하되, 논리적 구조와 설득력을 높이기 위해 내용을 재구성합니다.
            - 재무 수치는 임의로 바꾸지 않고, 표현과 설명만 더 명확하게 다듬습니다.
            
            출력 스타일 가이드:
            - 한국어로 작성합니다.
            - 공식적이고 전문적인 톤(tone: professional)을 유지합니다.
            - 마크다운(Markdown) 형식을 사용하여 섹션/소제목을 명확히 구분합니다.
            - 실제 제출 가능한 사업계획서 수준의 완성도를 목표로 합니다.
            """;

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final BusinessPlanGenerationRepository generationRepository;
    private final BusinessPlanRepository businessPlanRepository;

    @Value("${spring.ai.google.genai.chat.options.model:gemini-2.5-flash-lite}")
    private String geminiModelName;

    /**
     * Gemini를 호출해 사업계획서를 생성합니다.
     * 
     * Rule 306: 쓰기 작업이므로 @Transactional 명시
     *
     * @param request     프론트엔드에서 전달한 전체 요청
     * @param projectId   프로젝트 ID (컨트롤러에서 추출/보정된 값)
     * @param templateType 템플릿 유형
     * @param itemName    사업 아이템명 (프롬프트에 포함)
     * @param startTimeMs 요청 시작 시간 (밀리초) - end-to-end 생성 시간 계산용
     * @return BusinessPlanGenerateResponse (기존 스펙 그대로)
     */
    @Transactional  // Rule 306: 쓰기 작업이므로 트랜잭션 명시
    public BusinessPlanGenerateResponse generateBusinessPlan(
            BusinessPlanGenerateRequest request,
            String projectId,
            String templateType,
            String itemName,
            long startTimeMs
    ) {

        // 1. 프롬프트 구성
        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(request, itemName);

        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
        ));

        // businessPlanId를 먼저 생성 (예외 처리 시 필요)
        String businessPlanId = buildBusinessPlanId();

        // 2. Gemini 호출 (예외 처리 포함)
        long geminiStartTime = System.currentTimeMillis();
        String geminiStartTimeIso = Instant.now().toString();
        
        ChatResponse chatResponse;
        try {
            chatResponse = chatModel.call(prompt);
        } catch (HttpStatusCodeException e) {
            // HTTP 상태 코드 기반 예외 처리
            handleHttpException(businessPlanId, e);
            throw e; // 컴파일러 경고 방지 (실제로는 handleHttpException에서 예외를 던짐)
        } catch (RuntimeException e) {
            // 기타 런타임 예외 처리
            log.error("Gemini API 호출 중 예상치 못한 오류 발생: businessPlanId={}", businessPlanId, e);
            throw new GeminiGenerationException(
                    businessPlanId,
                    "Gemini API 호출 중 오류가 발생했습니다: " + e.getMessage(),
                    e
            );
        }
        
        long geminiEndTime = System.currentTimeMillis();
        String geminiEndTimeIso = Instant.now().toString();
        long geminiDurationMs = geminiEndTime - geminiStartTime;

        Generation generation = chatResponse.getResult();
        String generatedContent = generation != null && generation.getOutput() != null
                ? generation.getOutput().getText()
                : "";

        // 3. Usage 메타데이터 추출 (토큰 사용량)
        ChatResponseMetadata metadata = chatResponse.getMetadata();
        Usage usage = metadata != null ? metadata.getUsage() : null;

        int promptTokens = usage != null && usage.getPromptTokens() != null ? usage.getPromptTokens() : 0;
        int completionTokens = usage != null && usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0;
        int totalTokens = usage != null && usage.getTotalTokens() != null ? usage.getTotalTokens() : promptTokens + completionTokens;

        // 토큰 처리량 계산 (tokens/sec)
        double tokensPerSecond = geminiDurationMs > 0 
                ? totalTokens / (geminiDurationMs / 1000.0) 
                : 0.0;

        // 리포지토리 레이어에 위임 (로깅 및 향후 DB 저장 확장 대비)
        // 로그는 Repository 레이어에서만 기록하여 중복 방지
        generationRepository.saveUsage(businessPlanId, geminiStartTimeIso, geminiEndTimeIso, 
                geminiDurationMs, promptTokens, completionTokens, totalTokens, tokensPerSecond);

        // 4. 생성 결과를 BusinessPlanSection 리스트로 매핑
        List<BusinessPlanSection> sections = mapToSections(generatedContent);

        // 5. GenerationMetadata 구성 (토큰/시간/모델 정보 포함)
        long generationTimeMs = System.currentTimeMillis() - startTimeMs;
        GenerationMetadata generationMetadata = buildGenerationMetadata(
                sections,
                generationTimeMs,
                promptTokens,
                completionTokens,
                totalTokens
        );

        // 6. Export 옵션 구성 (기존 Mock 스펙 유지)
        ExportOptions exportOptions = ExportOptions.builder()
                .availableFormats(List.of("pdf", "hwp", "docx", "markdown"))
                .downloadUrls(Map.of(
                        "pdf", API_BASE_PATH + businessPlanId + "/export/pdf",
                        "hwp", API_BASE_PATH + businessPlanId + "/export/hwp",
                        "docx", API_BASE_PATH + businessPlanId + "/export/docx",
                        "markdown", API_BASE_PATH + businessPlanId + "/export/markdown"
                ))
                .build();

        // 7. 최종 응답 DTO 생성 (기존 필드 구조 100% 유지)
        BusinessPlanGenerateResponse response = BusinessPlanGenerateResponse.builder()
                .businessPlanId(businessPlanId)
                .projectId(projectId)
                .generatedAt(Instant.now().toString())
                .templateType(templateType)
                .sections(sections)
                .metadata(generationMetadata)
                .exportOptions(exportOptions)
                .build();

        // 8. 데이터베이스에 저장 (Rule 306: Service Layer에서 Entity 저장)
        saveBusinessPlanToDatabase(request, response, projectId, templateType, 
                geminiStartTimeIso, geminiEndTimeIso, geminiDurationMs,
                promptTokens, completionTokens, totalTokens, tokensPerSecond);

        return response;
    }

    /**
     * Gemini에 전달할 시스템 프롬프트를 구성합니다.
     * 
     * @return 시스템 프롬프트 문자열 (클래스 상수에서 반환)
     */
    private String buildSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    /**
     * 사용자가 보낸 BusinessPlanGenerateRequest를 기반으로 User 메시지 내용을 구성합니다.
     */
    private String buildUserPrompt(BusinessPlanGenerateRequest request, String itemName) {
        String serializedRequest = safeSerialize(request);

        return """
                아래는 사용자가 작성한 사업계획서 초안 데이터입니다.
                이 데이터를 기반으로, 제출 가능한 수준의 완성도 높은 사업계획서 본문을 작성해 주세요.
                
                요구사항:
                - 입력된 6단계 구조(문제 인식 → 시장 분석 → 실현 가능성/비즈니스 모델 → 사업화 전략 → 팀 역량 → 재무 계획)를 크게 벗어나지 않는 선에서 재구성합니다.
                - 각 단계는 마크다운 섹션 제목(예: `## 1. 사업 개요`)과 하위 소제목으로 구성합니다.
                - 문장은 자연스럽고 설득력 있게 다듬고, 불명확한 부분은 합리적인 수준에서 보완 설명을 추가합니다.
                - 숫자, 지표, 비율 등은 가능하면 유지하되, 해석과 의미를 더 명확히 설명합니다.
                - FE는 이 마크다운을 그대로 렌더링하여 사용자에게 보여줍니다.
                
                참고 정보:
                - 주요 사업 아이템명: %s
                
                === 사용자 입력 JSON 시작 ===
                %s
                === 사용자 입력 JSON 끝 ===
                """.formatted(itemName, serializedRequest);
    }

    /**
     * 요청 객체를 JSON 문자열로 안전하게 직렬화합니다.
     * 직렬화에 실패하더라도 Gemini 호출이 가능하도록 fallback 을 제공합니다.
     * 
     * 주의: 직렬화 실패는 예상치 못한 상황이므로 WARN 레벨로 로깅합니다.
     * Usage 로그 파일에는 기록되지 않고, 에러 로그 파일에만 기록됩니다.
     */
    private String safeSerialize(BusinessPlanGenerateRequest request) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            // WARN 레벨로 로깅하여 에러 로그 파일에 기록됨
            // Usage 로그 파일에는 기록되지 않음 (필터에 의해 제외됨)
            log.warn("BusinessPlanGenerateRequest 직렬화에 실패했습니다. 간단한 문자열로 대체합니다. 원인: {}", e.getMessage());
            return "Failed to serialize request. Use high-level fields only.";
        }
    }

    /**
     * Gemini가 생성한 마크다운 본문을 BusinessPlanSection 리스트로 매핑합니다.
     *
     * 마크다운 H2(##) 기준으로 섹션을 자동 분할합니다.
     * - 각 섹션에 고유 ID 부여 (section-1, section-2, ...)
     * - 섹션 제목 추출 (## 1. 사업 개요 → title: "1. 사업 개요")
     * - H2가 없는 경우 기본 섹션 하나로 반환
     *
     * @param generatedContent Gemini가 생성한 마크다운 본문
     * @return BusinessPlanSection 리스트
     */
    private List<BusinessPlanSection> mapToSections(String generatedContent) {
        if (generatedContent == null || generatedContent.isBlank()) {
            return List.of(createDefaultSection());
        }

        // 마크다운 H2(##) 기준으로 분할
        // 정규표현식을 사용하여 H2 헤더 위치를 찾고 분할
        // Pattern.MULTILINE을 사용하여 각 줄의 시작을 인식
        // (?=^## )는 positive lookahead로, ## 뒤에 공백이 오는 경우를 찾아 분할
        // 하지만 split은 첫 번째 부분을 빈 문자열로 만들 수 있으므로, 직접 파싱하는 방식 사용
        Pattern h2Pattern = Pattern.compile("^##\\s+(.+)$", Pattern.MULTILINE);
        java.util.regex.Matcher matcher = h2Pattern.matcher(generatedContent);

        List<BusinessPlanSection> sections = new java.util.ArrayList<>();
        int sectionIndex = 0;
        int lastEnd = 0;
        String currentTitle = null;

        while (matcher.find()) {
            int matchStart = matcher.start();
            String title = matcher.group(1).trim();

            // 이전 섹션 저장 (H2 헤더가 발견되었으므로)
            if (currentTitle != null || lastEnd < matchStart) {
                String content = generatedContent.substring(lastEnd, matchStart).trim();
                // 첫 번째 H2 이전의 내용 처리
                if (currentTitle == null && !content.isEmpty()) {
                    // H2가 없는 첫 부분
                    sectionIndex++;
                    sections.add(BusinessPlanSection.builder()
                            .id("section-" + sectionIndex)
                            .title(DEFAULT_SECTION_TITLE)
                            .content(content)
                            .order(sectionIndex)
                            .build());
                } else if (currentTitle != null) {
                    // 이전 H2 섹션의 내용
                    sectionIndex++;
                    sections.add(BusinessPlanSection.builder()
                            .id("section-" + sectionIndex)
                            .title(currentTitle)
                            .content(content)
                            .order(sectionIndex)
                            .build());
                }
            }

            currentTitle = title;
            lastEnd = matcher.end();
        }

        // 마지막 섹션 처리
        if (currentTitle != null || lastEnd < generatedContent.length()) {
            String content = generatedContent.substring(lastEnd).trim();
            if (!content.isEmpty()) {
                sectionIndex++;
                sections.add(BusinessPlanSection.builder()
                        .id("section-" + sectionIndex)
                        .title(currentTitle != null ? currentTitle : DEFAULT_SECTION_TITLE)
                        .content(content)
                        .order(sectionIndex)
                        .build());
            }
        }

        // H2가 없어서 분할되지 않은 경우 기본 섹션 반환
        if (sections.isEmpty()) {
            return List.of(createDefaultSection(generatedContent));
        }

        return sections;
    }

    /**
     * 마크다운 섹션에서 제목을 추출합니다.
     * 
     * @param part 마크다운 섹션 텍스트 (## 제목 형식 포함)
     * @return 추출된 제목 (## 제거), 없으면 null
     */
    private String extractTitle(String part) {
        if (part == null || part.isBlank()) {
            return null;
        }

        // 첫 번째 줄에서 ## 제목 형식 추출
        String[] lines = part.split("\\n", 2);
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            // ## 로 시작하는 경우 ## 제거하고 제목 반환
            if (firstLine.startsWith("## ")) {
                return firstLine.substring(3).trim();
            } else if (firstLine.startsWith("##")) {
                return firstLine.substring(2).trim();
            }
        }

        return null;
    }

    /**
     * 기본 섹션을 생성합니다.
     * 
     * @param content 섹션 내용 (null이면 빈 문자열)
     * @return 기본 섹션
     */
    private BusinessPlanSection createDefaultSection(String content) {
        String safeContent = content != null ? content : "";
        return BusinessPlanSection.builder()
                .id(DEFAULT_SECTION_ID)
                .title(DEFAULT_SECTION_TITLE)
                .content(safeContent)
                .order(1)
                .build();
    }

    /**
     * 빈 기본 섹션을 생성합니다.
     * 
     * @return 빈 기본 섹션
     */
    private BusinessPlanSection createDefaultSection() {
        return createDefaultSection("");
    }

    /**
     * 토큰 사용량과 생성 시간, 텍스트 길이를 기반으로 GenerationMetadata 를 구성합니다.
     */
    private GenerationMetadata buildGenerationMetadata(
            List<BusinessPlanSection> sections,
            long generationTimeMs,
            int promptTokens,
            int completionTokens,
            int totalTokens
    ) {
        String fullText = sections.stream()
                .map(BusinessPlanSection::getContent)
                .reduce("", (a, b) -> a + "\n\n" + (b != null ? b : ""));

        int characterCount = fullText != null ? fullText.length() : 0;
        int wordCount = fullText != null && !fullText.isBlank()
                ? fullText.trim().split("\\s+").length
                : 0;

        return GenerationMetadata.builder()
                .totalSections(sections != null ? sections.size() : 0)
                .wordCount(wordCount)
                .characterCount(characterCount)
                .generationTimeMs(generationTimeMs)
                .modelUsed(geminiModelName)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(totalTokens)
                .build();
    }

    /**
     * HTTP 상태 코드 기반 예외를 GeminiGenerationException으로 변환합니다.
     * 
     * @param businessPlanId 사업계획서 ID
     * @param e HTTP 상태 코드 예외
     * @throws GeminiGenerationException 변환된 예외
     */
    private void handleHttpException(String businessPlanId, HttpStatusCodeException e) {
        int statusCode = e.getStatusCode().value();
        
        if (statusCode == 401 || statusCode == 403) {
            // API 키 누락/만료
            log.error("Gemini API 인증 실패: businessPlanId={}, statusCode={}", businessPlanId, statusCode, e);
            throw new GeminiGenerationException(
                    businessPlanId,
                    "Gemini API 인증에 실패했습니다. API 키를 확인해주세요."
            );
        } else if (statusCode == 429) {
            // 토큰 한도 초과
            log.warn("Gemini API 요청 한도 초과: businessPlanId={}, statusCode={}", businessPlanId, statusCode);
            throw new GeminiGenerationException(
                    businessPlanId,
                    "Gemini API 요청 한도가 초과되었습니다. 잠시 후 다시 시도해주세요.",
                    0
            );
        } else if (statusCode >= 500) {
            // 서버 오류
            log.error("Gemini API 서버 오류: businessPlanId={}, statusCode={}", businessPlanId, statusCode, e);
            throw new GeminiGenerationException(
                    businessPlanId,
                    "Gemini API 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                    e
            );
        } else {
            // 기타 클라이언트 오류 (400, 404 등)
            log.warn("Gemini API 클라이언트 오류: businessPlanId={}, statusCode={}, message={}",
                    businessPlanId, statusCode, e.getMessage());
            throw new GeminiGenerationException(
                    businessPlanId,
                    "Gemini API 요청이 실패했습니다: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 스펙에 맞는 businessPlanId를 생성합니다.
     * 예: bp-2025-12-17-550e8400
     */
    private String buildBusinessPlanId() {
        return "bp-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 사업계획서 데이터를 데이터베이스에 저장합니다.
     * 
     * Rule 306: Service Layer에서 DTO → Entity 변환 및 저장
     * 
     * @param request 요청 DTO
     * @param response 응답 DTO
     * @param projectId 프로젝트 ID
     * @param templateType 템플릿 유형
     * @param geminiStartTimeIso Gemini 시작 시간 (ISO 8601)
     * @param geminiEndTimeIso Gemini 종료 시간 (ISO 8601)
     * @param geminiDurationMs Gemini 소요 시간 (밀리초)
     * @param promptTokens 입력 토큰 수
     * @param completionTokens 출력 토큰 수
     * @param totalTokens 총 토큰 수
     * @param tokensPerSecond 토큰 처리량
     */
    private void saveBusinessPlanToDatabase(
            BusinessPlanGenerateRequest request,
            BusinessPlanGenerateResponse response,
            String projectId,
            String templateType,
            String geminiStartTimeIso,
            String geminiEndTimeIso,
            long geminiDurationMs,
            int promptTokens,
            int completionTokens,
            int totalTokens,
            double tokensPerSecond) {
        
        try {
            // 1. 요청 데이터 JSON 직렬화
            String requestDataJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(request);

            // 2. 응답 섹션 데이터 JSON 직렬화
            String responseSectionsJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(response.getSections());

            // 3. Gemini 메타데이터 JSON 직렬화
            GeminiMetadataJson geminiMetadata = GeminiMetadataJson.builder()
                    .startTimeIso(geminiStartTimeIso)
                    .endTimeIso(geminiEndTimeIso)
                    .durationMs(geminiDurationMs)
                    .promptTokens(promptTokens)
                    .completionTokens(completionTokens)
                    .totalTokens(totalTokens)
                    .tokensPerSecond(tokensPerSecond)
                    .modelUsed(geminiModelName)
                    .generationTimeMs(response.getMetadata() != null 
                            ? response.getMetadata().getGenerationTimeMs() : 0)
                    .wordCount(response.getMetadata() != null 
                            ? response.getMetadata().getWordCount() : 0)
                    .characterCount(response.getMetadata() != null 
                            ? response.getMetadata().getCharacterCount() : 0)
                    .totalSections(response.getMetadata() != null 
                            ? response.getMetadata().getTotalSections() : 0)
                    .build();
            
            String geminiMetadataJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(geminiMetadata);

            // 4. Entity 생성 (Rule 306: DTO → Entity 변환)
            // projectId를 UUID로 변환 (유효하지 않은 경우 null)
            UUID projectUuid = null;
            if (projectId != null && !projectId.isEmpty()) {
                try {
                    projectUuid = UUID.fromString(projectId);
                } catch (IllegalArgumentException e) {
                    // projectId가 UUID 형식이 아닌 경우 null로 설정
                    log.debug("projectId가 UUID 형식이 아닙니다: projectId={}, null로 설정", projectId);
                }
            }
            
            BusinessPlan businessPlan = BusinessPlan.builder()
                    .businessPlanId(response.getBusinessPlanId())
                    .projectId(projectUuid)
                    .userId(request.getRequestInfo() != null 
                            ? request.getRequestInfo().getUserId() : null)
                    .templateType(templateType)
                    .requestDataJson(requestDataJson)
                    .responseSectionsJson(responseSectionsJson)
                    .geminiMetadataJson(geminiMetadataJson)
                    .build();

            // 5. Repository를 통한 저장 (Rule 306: Repository는 Entity만 다룸)
            businessPlanRepository.save(businessPlan);
            
            log.info("사업계획서 데이터베이스 저장 완료: businessPlanId={}", response.getBusinessPlanId());
            
        } catch (JsonProcessingException e) {
            // JSON 직렬화 실패 시 로그만 기록하고 계속 진행 (응답은 정상 반환)
            log.warn("사업계획서 데이터베이스 저장 중 JSON 직렬화 실패: businessPlanId={}, 원인: {}", 
                    response.getBusinessPlanId(), e.getMessage());
        } catch (IllegalArgumentException e) {
            // UUID 파싱 실패 시 (projectId가 유효하지 않은 경우)
            log.warn("사업계획서 데이터베이스 저장 중 projectId 파싱 실패: businessPlanId={}, projectId={}, 원인: {}", 
                    response.getBusinessPlanId(), projectId, e.getMessage());
        } catch (Exception e) {
            // 기타 예외 발생 시에도 로그만 기록하고 계속 진행
            log.error("사업계획서 데이터베이스 저장 실패: businessPlanId={}", 
                    response.getBusinessPlanId(), e);
        }
    }

    /**
     * Gemini 메타데이터를 JSON으로 직렬화하기 위한 내부 클래스
     */
    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class GeminiMetadataJson {
        private String startTimeIso;
        private String endTimeIso;
        private long durationMs;
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
        private double tokensPerSecond;
        private String modelUsed;
        private long generationTimeMs;
        private int wordCount;
        private int characterCount;
        private int totalSections;
    }
}

