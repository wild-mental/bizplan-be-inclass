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
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateRequest;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse.BusinessPlanSection;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse.ExportOptions;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse.GenerationMetadata;
import vibe.bizplan.bizplan_be_inclass.repository.BusinessPlanGenerationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class BusinessPlanGenerationService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final BusinessPlanGenerationRepository generationRepository;

    @Value("${spring.ai.google.genai.chat.options.model:gemini-2.5-flash-lite}")
    private String geminiModelName;

    /**
     * Gemini를 호출해 사업계획서를 생성합니다.
     *
     * @param request     프론트엔드에서 전달한 전체 요청
     * @param projectId   프로젝트 ID (컨트롤러에서 추출/보정된 값)
     * @param templateType 템플릿 유형
     * @param itemName    사업 아이템명 (프롬프트에 포함)
     * @param startTimeMs 요청 시작 시간 (밀리초) - end-to-end 생성 시간 계산용
     * @return BusinessPlanGenerateResponse (기존 스펙 그대로)
     */
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

        // 2. Gemini 호출
        long geminiStartTime = System.currentTimeMillis();
        String geminiStartTimeIso = Instant.now().toString();
        
        ChatResponse chatResponse = chatModel.call(prompt);
        
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

        // businessPlanId를 먼저 생성
        String businessPlanId = buildBusinessPlanId();

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
                        "pdf", "/api/v1/business-plan/" + businessPlanId + "/export/pdf",
                        "hwp", "/api/v1/business-plan/" + businessPlanId + "/export/hwp",
                        "docx", "/api/v1/business-plan/" + businessPlanId + "/export/docx",
                        "markdown", "/api/v1/business-plan/" + businessPlanId + "/export/markdown"
                ))
                .build();

        // 7. 최종 응답 DTO 생성 (기존 필드 구조 100% 유지)
        return BusinessPlanGenerateResponse.builder()
                .businessPlanId(businessPlanId)
                .projectId(projectId)
                .generatedAt(Instant.now().toString())
                .templateType(templateType)
                .sections(sections)
                .metadata(generationMetadata)
                .exportOptions(exportOptions)
                .build();
    }

    /**
     * Gemini에 전달할 시스템 프롬프트를 구성합니다.
     */
    private String buildSystemPrompt() {
        return """
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
     */
    private String safeSerialize(BusinessPlanGenerateRequest request) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.warn("BusinessPlanGenerateRequest 직렬화에 실패했습니다. 간단한 문자열로 대체합니다.", e);
            return "Failed to serialize request. Use high-level fields only.";
        }
    }

    /**
     * Gemini가 생성한 마크다운 본문을 BusinessPlanSection 리스트로 매핑합니다.
     *
     * MVP 단계에서는 전체 본문을 하나의 섹션으로 감싸서 반환합니다.
     * - FE/스펙에서 요구하는 섹션 배열 구조를 유지하기 위함입니다.
     */
    private List<BusinessPlanSection> mapToSections(String generatedContent) {
        String safeContent = generatedContent != null ? generatedContent : "";

        BusinessPlanSection section = BusinessPlanSection.builder()
                .id("section-1")
                .title("AI 보강 사업계획서")
                .content(safeContent)
                .order(1)
                .build();

        return List.of(section);
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
     * 스펙에 맞는 businessPlanId를 생성합니다.
     * 예: bp-2025-12-17-550e8400
     */
    private String buildBusinessPlanId() {
        return "bp-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

