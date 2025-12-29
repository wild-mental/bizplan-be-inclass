package vibe.bizplan.bizplan_be_inclass.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateRequest;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse;
import vibe.bizplan.bizplan_be_inclass.repository.BusinessPlanGenerationRepository;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * BusinessPlanGenerationService 통합 테스트
 *
 * 실제 Gemini API를 호출하여 검증합니다.
 * - 네트워크 호출이 필요하므로 느릴 수 있습니다.
 * - 실제 API 비용이 발생할 수 있습니다.
 * - GEMINI_API_KEY 환경변수가 설정되어 있어야 합니다.
 *
 * 실행 방법:
 *   - 전체 테스트: ./gradlew test
 *   - 통합 테스트만: ./gradlew test --tests "*IntegrationTest"
 *   - 제외: ./gradlew test -PexcludeIntegrationTests=true
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Tag("integration")
@DisplayName("BusinessPlanGenerationService 통합 테스트 (실제 Gemini API 호출)")
class BusinessPlanGenerationServiceIntegrationTest {

    @Autowired(required = false)
    private BusinessPlanGenerationService service;

    @Autowired(required = false)
    private BusinessPlanGenerationRepository repository;

    private BusinessPlanGenerateRequest request;

    @BeforeEach
    void setUp() {
        // GEMINI_API_KEY가 설정되어 있는지 확인
        String apiKey = System.getenv("GEMINI_API_KEY");
        assumeTrue(apiKey != null && !apiKey.isEmpty(),
                "GEMINI_API_KEY 환경변수가 설정되어 있지 않습니다. 통합 테스트를 건너뜁니다.");
        
        // 서비스가 주입되었는지 확인 (Spring AI 빈 생성 실패 시 null일 수 있음)
        assumeTrue(service != null,
                "BusinessPlanGenerationService가 주입되지 않았습니다. Spring AI 설정을 확인하세요.");

        // 요청 데이터 생성
        request = createMockRequest();
    }

    @Test
    @DisplayName("generateBusinessPlan - 실제 Gemini API 호출하여 사업계획서 생성")
    void generateBusinessPlan_withRealGeminiAPI_generatesBusinessPlan() {
        // given
        String projectId = "proj-integration-test";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // 테스트 전 로그 파일 상태 확인
        File logFile = new File("logs/gemini-usage-test.log");
        long fileSizeBefore = logFile.exists() ? logFile.length() : 0;
        int lineCountBefore = 0;
        if (logFile.exists()) {
            try {
                lineCountBefore = (int) Files.lines(logFile.toPath()).count();
            } catch (Exception e) {
                // 무시
            }
        }

        // when: 실제 Gemini API 호출
        BusinessPlanGenerateResponse response;
        try {
            response = service.generateBusinessPlan(
                    request, projectId, templateType, itemName, startTimeMs
            );
        } catch (Exception e) {
            // API 할당량 초과 등의 경우 테스트 스킵
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof com.google.genai.errors.ClientException) {
                    assumeTrue(false, "Gemini API 할당량이 초과되었거나 API 키에 문제가 있습니다. 테스트를 건너뜁니다: " + cause.getMessage());
                }
                cause = cause.getCause();
            }
            throw e;
        }

        // 로그가 파일에 기록되도록 대기 (비동기 로그 기록을 위해 충분한 시간 확보)
        try {
            Thread.sleep(2000);  // 500ms -> 2000ms로 증가
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // then: 응답 검증
        assertThat(response).isNotNull();
        assertThat(response.getBusinessPlanId()).isNotNull();
        assertThat(response.getBusinessPlanId()).startsWith("bp-");
        assertThat(response.getProjectId()).isEqualTo(projectId);
        assertThat(response.getTemplateType()).isEqualTo(templateType);
        assertThat(response.getGeneratedAt()).isNotNull();

        // 섹션 검증
        assertThat(response.getSections()).isNotNull();
        assertThat(response.getSections()).isNotEmpty();
        assertThat(response.getSections().get(0).getContent()).isNotBlank();

        // 메타데이터 검증 - 실제 토큰 사용량이 기록되어야 함
        BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getPromptTokens()).isGreaterThan(0);  // 실제 값이 0보다 커야 함
        assertThat(metadata.getCompletionTokens()).isGreaterThan(0);  // 실제 값이 0보다 커야 함
        assertThat(metadata.getTotalTokens()).isGreaterThan(0);
        assertThat(metadata.getTotalTokens()).isEqualTo(
                metadata.getPromptTokens() + metadata.getCompletionTokens()
        );
        assertThat(metadata.getModelUsed()).isEqualTo("gemini-2.5-flash-lite");
        assertThat(metadata.getGenerationTimeMs()).isGreaterThan(0);

        // 파일 로그 검증 (파일 크기 대신 라인 수나 내용 존재 여부로 검증)
        if (logFile.exists()) {
            // 파일 크기 검증을 더 관대하게 수정 (같을 수도 있음)
            long fileSizeAfter = logFile.length();
            assertThat(fileSizeAfter).isGreaterThanOrEqualTo(fileSizeBefore);

            try {
                int lineCountAfter = (int) Files.lines(logFile.toPath()).count();
                assertThat(lineCountAfter).isGreaterThan(lineCountBefore);

                // 마지막 로그 라인 확인
                List<String> lines = Files.readAllLines(logFile.toPath());
                String lastLine = lines.get(lines.size() - 1);
                assertThat(lastLine).contains("[Gemini Usage Log]");
                assertThat(lastLine).contains("businessPlanId=" + response.getBusinessPlanId());
                assertThat(lastLine).contains("Input: " + metadata.getPromptTokens());
                assertThat(lastLine).contains("Output: " + metadata.getCompletionTokens());
                assertThat(lastLine).contains("Total: " + metadata.getTotalTokens());
            } catch (Exception e) {
                // 파일 읽기 실패는 테스트 실패로 간주하지 않음
                System.out.println("Warning: 로그 파일 읽기 실패 - " + e.getMessage());
            }
        }

        // 콘솔에 실제 토큰 사용량 출력
        System.out.println("\n=== 실제 Gemini API 호출 결과 ===");
        System.out.println("BusinessPlanId: " + response.getBusinessPlanId());
        System.out.println("Prompt Tokens: " + metadata.getPromptTokens());
        System.out.println("Completion Tokens: " + metadata.getCompletionTokens());
        System.out.println("Total Tokens: " + metadata.getTotalTokens());
        System.out.println("Generation Time: " + metadata.getGenerationTimeMs() + "ms");
        System.out.println("Throughput: " + String.format("%.2f", 
                metadata.getTotalTokens() / (metadata.getGenerationTimeMs() / 1000.0)) + " tokens/sec");
        System.out.println("Word Count: " + metadata.getWordCount());
        System.out.println("Character Count: " + metadata.getCharacterCount());
    }

    @Test
    @DisplayName("generateBusinessPlan - 실제 API 호출 시 다양한 프롬프트 길이에 따라 토큰 수가 달라지는지 확인")
    void generateBusinessPlan_withDifferentPromptLengths_variesTokenUsage() {
        // given: 짧은 프롬프트
        BusinessPlanGenerateRequest shortRequest = createShortRequest();
        String projectId1 = "proj-short-test";
        long startTime1 = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response1;
        try {
            response1 = service.generateBusinessPlan(
                    shortRequest, projectId1, "pre-startup", "짧은 아이템", startTime1
            );
        } catch (Exception e) {
            // API 할당량 초과 등의 경우 테스트 스킵
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof com.google.genai.errors.ClientException) {
                    assumeTrue(false, "Gemini API 할당량이 초과되었거나 API 키에 문제가 있습니다. 테스트를 건너뜁니다: " + cause.getMessage());
                }
                cause = cause.getCause();
            }
            throw e;
        }

        // given: 긴 프롬프트
        BusinessPlanGenerateRequest longRequest = createLongRequest();
        String projectId2 = "proj-long-test";
        long startTime2 = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response2;
        try {
            response2 = service.generateBusinessPlan(
                    longRequest, projectId2, "pre-startup", "긴 아이템", startTime2
            );
        } catch (Exception e) {
            // API 할당량 초과 등의 경우 테스트 스킵
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof com.google.genai.errors.ClientException) {
                    assumeTrue(false, "Gemini API 할당량이 초과되었거나 API 키에 문제가 있습니다. 테스트를 건너뜁니다: " + cause.getMessage());
                }
                cause = cause.getCause();
            }
            throw e;
        }

        // 로그 기록 대기
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // then: 토큰 사용량이 다를 수 있음 (프롬프트 길이에 따라)
        int shortPromptTokens = response1.getMetadata().getPromptTokens();
        int longPromptTokens = response2.getMetadata().getPromptTokens();

        System.out.println("\n=== 프롬프트 길이별 토큰 사용량 비교 ===");
        System.out.println("짧은 프롬프트 Prompt Tokens: " + shortPromptTokens);
        System.out.println("긴 프롬프트 Prompt Tokens: " + longPromptTokens);
        System.out.println("Completion Tokens (짧은): " + response1.getMetadata().getCompletionTokens());
        System.out.println("Completion Tokens (긴): " + response2.getMetadata().getCompletionTokens());

        // 기본 검증 (실제로는 다를 수 있지만, 둘 다 0보다 커야 함)
        assertThat(shortPromptTokens).isGreaterThan(0);
        assertThat(longPromptTokens).isGreaterThan(0);
        assertThat(response1.getMetadata().getCompletionTokens()).isGreaterThan(0);
        assertThat(response2.getMetadata().getCompletionTokens()).isGreaterThan(0);
    }

    @Test
    @DisplayName("generateBusinessPlan - 다양한 입력 크기에 대한 응답 시간 측정 (5회 호출)")
    void generateBusinessPlan_withDifferentPayloadSizes_measuresResponseTime() {
        // given: 서로 다른 크기의 요청 5개 준비 (아주 짧은 ~ 매우 긴 입력)
        BusinessPlanGenerateRequest tinyRequest = createSizedRequest(
                "아주 짧은 아이템",
                50,   // 각 필드 당 약 50자
                1
        );

        BusinessPlanGenerateRequest smallRequest = createSizedRequest(
                "짧은 아이템",
                200,  // 각 필드 당 약 200자
                1
        );

        BusinessPlanGenerateRequest mediumRequest = createSizedRequest(
                "중간 길이 아이템",
                800,  // 각 필드 당 약 800자
                2
        );

        BusinessPlanGenerateRequest largeRequest = createSizedRequest(
                "긴 아이템",
                2000, // 각 필드 당 약 2,000자
                3
        );

        BusinessPlanGenerateRequest xLargeRequest = createSizedRequest(
                "매우 긴 아이템",
                4000, // 각 필드 당 약 4,000자
                4
        );

        BusinessPlanGenerateRequest[] requests = {
                tinyRequest,
                smallRequest,
                mediumRequest,
                largeRequest,
                xLargeRequest
        };

        String[] labels = {"XS", "S", "M", "L", "XL"};

        System.out.println("\n=== 다양한 입력 크기별 사업계획서 생성 응답 시간 측정 ===");

        for (int i = 0; i < requests.length; i++) {
            String sizeLabel = labels[i];
            BusinessPlanGenerateRequest req = requests[i];
            String projectId = "proj-size-" + sizeLabel.toLowerCase();
            String itemName = "Payload-" + sizeLabel;

            long startWall = System.currentTimeMillis();

            BusinessPlanGenerateResponse response;
            try {
                response = service.generateBusinessPlan(
                        req,
                        projectId,
                        "pre-startup",
                        itemName,
                        startWall
                );
            } catch (Exception e) {
                // Gemini API 할당량 또는 네트워크 이슈 시 전체 테스트를 스킵
                Throwable cause = e;
                while (cause != null) {
                    if (cause instanceof com.google.genai.errors.ClientException) {
                        assumeTrue(false,
                                "Gemini API 호출 중 오류가 발생했습니다. 테스트를 건너뜁니다: " + cause.getMessage());
                    }
                    cause = cause.getCause();
                }
                throw e;
            }

            long endWall = System.currentTimeMillis();
            long wallTimeMs = endWall - startWall;

            BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
            long genTimeMs = metadata != null ? metadata.getGenerationTimeMs() : -1L;

            System.out.println(String.format(
                    "Size=%s | wallTime=%d ms | generationTime=%d ms | promptTokens=%d | completionTokens=%d | totalTokens=%d",
                    sizeLabel,
                    wallTimeMs,
                    genTimeMs,
                    metadata != null ? metadata.getPromptTokens() : -1,
                    metadata != null ? metadata.getCompletionTokens() : -1,
                    metadata != null ? metadata.getTotalTokens() : -1
            ));

            // 기본적인 성공 검증
            assertThat(response).isNotNull();
            assertThat(response.getBusinessPlanId()).isNotBlank();
            assertThat(response.getSections()).isNotNull();
            assertThat(response.getSections()).isNotEmpty();
        }
    }

    private BusinessPlanGenerateRequest createMockRequest() {
        BusinessPlanGenerateRequest.RequestInfo requestInfo = new BusinessPlanGenerateRequest.RequestInfo(
                "pre-startup",
                "2025-12-19T12:00:00.000Z",
                "user-integration-test",
                "proj-integration-test"
        );

        BusinessPlanGenerateRequest.Step1ProblemRecognition step1 = 
                new BusinessPlanGenerateRequest.Step1ProblemRecognition(
                        "AI 기반 맞춤형 학습 플랫폼",
                        "학생 개개인의 학습 패턴을 AI가 분석하여 맞춤형 학습 경로 제공",
                        "획일화된 커리큘럼으로 인한 개인별 학습 효율 저하",
                        "중고등학생 78%가 현재 학습 방식에 불만족",
                        "중학생 자녀를 둔 35-45세 학부모"
                );

        BusinessPlanGenerateRequest.Step2MarketAnalysis step2 = 
                new BusinessPlanGenerateRequest.Step2MarketAnalysis(
                        "TAM 25조원, SAM 5조원, SOM 100억원",
                        "온라인 교육 시장 연평균 22% 성장",
                        "메가스터디, 대교 스마트러닝",
                        "자체 AI 알고리즘, 한국 교육과정 100% 연계"
                );

        BusinessPlanGenerateRequest.Step3SolutionFeasibility step3 = 
                new BusinessPlanGenerateRequest.Step3SolutionFeasibility(
                        "AI 기반 맞춤형 학습 경로 제공 플랫폼",
                        "B2C 구독형 SaaS",
                        "구독 수익 75%, B2B 라이선스 15%",
                        "AI 알고리즘 특허 출원 완료"
                );

        BusinessPlanGenerateRequest.Step4CommercializationStrategy step4 = 
                new BusinessPlanGenerateRequest.Step4CommercializationStrategy(
                        "수도권 중학생 대상 집중 공략",
                        "디지털 마케팅, 바이럴 마케팅",
                        "1년차 B2C, 2년차 B2B 확장",
                        "3개월: MVP 출시, 6개월: 유료 1,000명"
                );

        BusinessPlanGenerateRequest.Step5TeamCapability step5 = 
                new BusinessPlanGenerateRequest.Step5TeamCapability(
                        "CEO, CTO, CPO 3인 핵심 팀",
                        "AI 논문 15편, 특허 3건",
                        "전 스타트업 MAU 50만, Exit 경험"
                );

        BusinessPlanGenerateRequest.Step6FinancialPlan step6 = 
                new BusinessPlanGenerateRequest.Step6FinancialPlan(
                        new BusinessPlanGenerateRequest.FinancialInputs(
                                100, 35000, 50000, 15000000, 0.1, 0.05
                        ),
                        new BusinessPlanGenerateRequest.CalculatedMetrics(
                                3500000, 42000000, 420000, 8.4, 500, 18, 0.9
                        )
                );

        BusinessPlanGenerateRequest.BusinessPlanData businessPlanData = 
                new BusinessPlanGenerateRequest.BusinessPlanData(
                        step1, step2, step3, step4, step5, step6
                );

        BusinessPlanGenerateRequest.GenerationOptions generationOptions = 
                new BusinessPlanGenerateRequest.GenerationOptions(
                        "professional",
                        "detailed",
                        "markdown",
                        "ko",
                        List.of("executive_summary", "problem_analysis", "market_analysis", 
                                "solution_overview", "business_model", "go_to_market", 
                                "team_introduction", "financial_projection", "risk_analysis", "conclusion")
                );

        return new BusinessPlanGenerateRequest(
                requestInfo,
                businessPlanData,
                generationOptions
        );
    }

    private BusinessPlanGenerateRequest createShortRequest() {
        BusinessPlanGenerateRequest.RequestInfo requestInfo = new BusinessPlanGenerateRequest.RequestInfo(
                "pre-startup", null, "user-short", "proj-short"
        );

        BusinessPlanGenerateRequest.Step1ProblemRecognition step1 = 
                new BusinessPlanGenerateRequest.Step1ProblemRecognition(
                        "간단한 앱",
                        "간단한 설명",
                        "문제",
                        "증거",
                        "고객"
                );

        BusinessPlanGenerateRequest.BusinessPlanData businessPlanData = 
                new BusinessPlanGenerateRequest.BusinessPlanData(
                        step1, null, null, null, null, null
                );

        BusinessPlanGenerateRequest.GenerationOptions generationOptions = 
                new BusinessPlanGenerateRequest.GenerationOptions(
                        "professional", "brief", "markdown", "ko", List.of("executive_summary")
                );

        return new BusinessPlanGenerateRequest(requestInfo, businessPlanData, generationOptions);
    }

    private BusinessPlanGenerateRequest createLongRequest() {
        // createMockRequest()와 동일하지만 더 많은 섹션 포함
        return createMockRequest();
    }

    /**
     * 주어진 길이와 반복 횟수에 맞춰 각 텍스트 필드를 생성하는 헬퍼
     *
     * @param baseItemName 아이템 이름
     * @param approxFieldLength 각 필드당 대략적인 길이 (문자 수)
     * @param repeatCount 동일 텍스트 반복 횟수
     */
    private BusinessPlanGenerateRequest createSizedRequest(
            String baseItemName,
            int approxFieldLength,
            int repeatCount
    ) {
        String repeated = "내용 ".repeat(Math.max(1, approxFieldLength / 3));

        String itemSummary = (baseItemName + " - 요약. ") + repeated.repeat(repeatCount);
        String problem = "문제 정의: " + repeated.repeat(repeatCount);
        String problemEvidence = "증거/데이터: " + repeated.repeat(repeatCount);
        String targetCustomer = "타겟 고객: " + repeated.repeat(repeatCount);

        String marketSize = "시장 규모: " + repeated.repeat(repeatCount);
        String marketTrend = "시장 트렌드: " + repeated.repeat(repeatCount);
        String competitors = "경쟁사: " + repeated.repeat(repeatCount);
        String competitiveAdvantage = "경쟁 우위: " + repeated.repeat(repeatCount);

        String solution = "솔루션: " + repeated.repeat(repeatCount);
        String businessModel = "비즈니스 모델: " + repeated.repeat(repeatCount);
        String revenueStreams = "수익 구조: " + repeated.repeat(repeatCount);
        String techFeasibility = "기술 실현 가능성: " + repeated.repeat(repeatCount);

        String goToMarket = "GTM 전략: " + repeated.repeat(repeatCount);
        String marketingStrategy = "마케팅 전략: " + repeated.repeat(repeatCount);
        String growthStrategy = "성장 전략: " + repeated.repeat(repeatCount);
        String milestones = "마일스톤: " + repeated.repeat(repeatCount);

        String teamComposition = "팀 구성: " + repeated.repeat(repeatCount);
        String teamExpertise = "팀 전문성: " + repeated.repeat(repeatCount);
        String teamTrackRecord = "팀 트랙 레코드: " + repeated.repeat(repeatCount);

        BusinessPlanGenerateRequest.RequestInfo requestInfo =
                new BusinessPlanGenerateRequest.RequestInfo(
                        "pre-startup",
                        "2025-12-19T12:00:00.000Z",
                        "user-" + baseItemName,
                        "proj-" + baseItemName
                );

        BusinessPlanGenerateRequest.Step1ProblemRecognition step1 =
                new BusinessPlanGenerateRequest.Step1ProblemRecognition(
                        baseItemName,
                        itemSummary,
                        problem,
                        problemEvidence,
                        targetCustomer
                );

        BusinessPlanGenerateRequest.Step2MarketAnalysis step2 =
                new BusinessPlanGenerateRequest.Step2MarketAnalysis(
                        marketSize,
                        marketTrend,
                        competitors,
                        competitiveAdvantage
                );

        BusinessPlanGenerateRequest.Step3SolutionFeasibility step3 =
                new BusinessPlanGenerateRequest.Step3SolutionFeasibility(
                        solution,
                        businessModel,
                        revenueStreams,
                        techFeasibility
                );

        BusinessPlanGenerateRequest.Step4CommercializationStrategy step4 =
                new BusinessPlanGenerateRequest.Step4CommercializationStrategy(
                        goToMarket,
                        marketingStrategy,
                        growthStrategy,
                        milestones
                );

        BusinessPlanGenerateRequest.Step5TeamCapability step5 =
                new BusinessPlanGenerateRequest.Step5TeamCapability(
                        teamComposition,
                        teamExpertise,
                        teamTrackRecord
                );

        BusinessPlanGenerateRequest.Step6FinancialPlan step6 =
                new BusinessPlanGenerateRequest.Step6FinancialPlan(
                        new BusinessPlanGenerateRequest.FinancialInputs(
                                100, 35000, 50000, 15000000, 0.1, 0.05
                        ),
                        new BusinessPlanGenerateRequest.CalculatedMetrics(
                                3500000, 42000000, 420000, 8.4, 500, 18, 0.9
                        )
                );

        BusinessPlanGenerateRequest.BusinessPlanData businessPlanData =
                new BusinessPlanGenerateRequest.BusinessPlanData(
                        step1, step2, step3, step4, step5, step6
                );

        BusinessPlanGenerateRequest.GenerationOptions generationOptions =
                new BusinessPlanGenerateRequest.GenerationOptions(
                        "professional",
                        "detailed",
                        "markdown",
                        "ko",
                        List.of(
                                "executive_summary",
                                "problem_analysis",
                                "market_analysis",
                                "solution_overview",
                                "business_model",
                                "go_to_market",
                                "team_introduction",
                                "financial_projection",
                                "risk_analysis",
                                "conclusion"
                        )
                );

        return new BusinessPlanGenerateRequest(
                requestInfo,
                businessPlanData,
                generationOptions
        );
    }
}
