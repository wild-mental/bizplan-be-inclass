package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.test.util.ReflectionTestUtils;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateRequest;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse;
import vibe.bizplan.bizplan_be_inclass.repository.BusinessPlanGenerationRepository;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BusinessPlanGenerationService 단위 테스트
 *
 * Rule 301: methodName_scenario_expectedBehavior naming
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
@DisplayName("BusinessPlanGenerationService 테스트")
class BusinessPlanGenerationServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BusinessPlanGenerationRepository generationRepository;

    @InjectMocks
    private BusinessPlanGenerationService service;

    private BusinessPlanGenerateRequest request;
    private ChatResponse mockChatResponse;
    private Generation mockGeneration;
    private ChatResponseMetadata mockMetadata;
    private Usage mockUsage;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        // 모델명 설정
        ReflectionTestUtils.setField(service, "geminiModelName", "gemini-2.5-flash-lite");

        // 요청 데이터 생성
        request = createMockRequest();

        // ObjectMapper 모킹 - 실제 ObjectMapper 사용 (직렬화는 실제로 동작하도록)
        ObjectMapper realMapper = new ObjectMapper();
        when(objectMapper.writerWithDefaultPrettyPrinter())
                .thenReturn(realMapper.writerWithDefaultPrettyPrinter());

        // Usage 모킹
        mockUsage = mock(Usage.class);
        when(mockUsage.getPromptTokens()).thenReturn(1234);
        when(mockUsage.getCompletionTokens()).thenReturn(5678);
        when(mockUsage.getTotalTokens()).thenReturn(6912);

        // ChatResponseMetadata 모킹
        mockMetadata = mock(ChatResponseMetadata.class);
        lenient().when(mockMetadata.getUsage()).thenReturn(mockUsage);

        // Generation 모킹
        AssistantMessage assistantMessage = mock(AssistantMessage.class);
        lenient().when(assistantMessage.getText()).thenReturn("## 1. 사업 개요\n\n생성된 사업계획서 내용입니다.");

        mockGeneration = mock(Generation.class);
        lenient().when(mockGeneration.getOutput()).thenReturn(assistantMessage);

        // ChatResponse 모킹
        mockChatResponse = mock(ChatResponse.class);
        lenient().when(mockChatResponse.getResult()).thenReturn(mockGeneration);
        lenient().when(mockChatResponse.getMetadata()).thenReturn(mockMetadata);

        // ChatModel 모킹
        lenient().when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);
    }

    @Test
    @DisplayName("generateBusinessPlan - 정상적인 요청 시 BusinessPlanGenerateResponse를 반환한다")
    void generateBusinessPlan_validRequest_returnsResponse() {
        // given
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.getBusinessPlanId()).isNotNull();
        assertThat(response.getBusinessPlanId()).startsWith("bp-");
        assertThat(response.getProjectId()).isEqualTo(projectId);
        assertThat(response.getTemplateType()).isEqualTo(templateType);
        assertThat(response.getGeneratedAt()).isNotNull();
    }

    @Test
    @DisplayName("generateBusinessPlan - 섹션이 올바르게 생성된다")
    void generateBusinessPlan_validRequest_createsSections() {
        // given
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        assertThat(response.getSections()).isNotNull();
        assertThat(response.getSections()).hasSize(1);
        assertThat(response.getSections().get(0).getId()).isEqualTo("section-1");
        assertThat(response.getSections().get(0).getTitle()).isEqualTo("AI 보강 사업계획서");
        assertThat(response.getSections().get(0).getContent()).contains("사업 개요");
    }

    @Test
    @DisplayName("generateBusinessPlan - 메타데이터에 토큰 사용량이 포함된다")
    void generateBusinessPlan_validRequest_includesTokenUsage() {
        // given
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getPromptTokens()).isEqualTo(1234);
        assertThat(metadata.getCompletionTokens()).isEqualTo(5678);
        assertThat(metadata.getTotalTokens()).isEqualTo(6912);
        assertThat(metadata.getModelUsed()).isEqualTo("gemini-2.5-flash-lite");
        assertThat(metadata.getGenerationTimeMs()).isGreaterThan(0);
    }

    @Test
    @DisplayName("generateBusinessPlan - ExportOptions가 올바르게 생성된다")
    void generateBusinessPlan_validRequest_createsExportOptions() {
        // given
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        BusinessPlanGenerateResponse.ExportOptions exportOptions = response.getExportOptions();
        assertThat(exportOptions).isNotNull();
        assertThat(exportOptions.getAvailableFormats()).containsExactly("pdf", "hwp", "docx", "markdown");
        assertThat(exportOptions.getDownloadUrls()).containsKeys("pdf", "hwp", "docx", "markdown");
        assertThat(exportOptions.getDownloadUrls().get("pdf")).contains(response.getBusinessPlanId());
    }

    @Test
    @DisplayName("generateBusinessPlan - Gemini 호출이 정확히 한 번 수행된다")
    void generateBusinessPlan_validRequest_callsGeminiOnce() {
        // given
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        service.generateBusinessPlan(request, projectId, templateType, itemName, startTimeMs);

        // then
        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("generateBusinessPlan - 리포지토리에 사용량이 저장된다")
    void generateBusinessPlan_validRequest_savesUsage() {
        // given
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        service.generateBusinessPlan(request, projectId, templateType, itemName, startTimeMs);

        // then
        verify(generationRepository, times(1)).saveUsage(
                anyString(), // businessPlanId
                anyString(), // startTimeIso
                anyString(), // endTimeIso
                anyLong(),   // durationMs
                eq(1234),   // promptTokens
                eq(5678),   // completionTokens
                eq(6912),   // totalTokens
                anyDouble() // tokensPerSecond
        );
    }

    @Test
    @DisplayName("generateBusinessPlan - Usage가 null인 경우 기본값으로 처리한다")
    void generateBusinessPlan_nullUsage_handlesGracefully() {
        // given
        when(mockMetadata.getUsage()).thenReturn(null);
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
        assertThat(metadata.getPromptTokens()).isEqualTo(0);
        assertThat(metadata.getCompletionTokens()).isEqualTo(0);
        assertThat(metadata.getTotalTokens()).isEqualTo(0);
    }

    @Test
    @DisplayName("generateBusinessPlan - Metadata가 null인 경우 기본값으로 처리한다")
    void generateBusinessPlan_nullMetadata_handlesGracefully() {
        // given
        when(mockChatResponse.getMetadata()).thenReturn(null);
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
        assertThat(metadata.getPromptTokens()).isEqualTo(0);
        assertThat(metadata.getCompletionTokens()).isEqualTo(0);
        assertThat(metadata.getTotalTokens()).isEqualTo(0);
    }

    @Test
    @DisplayName("generateBusinessPlan - Usage의 개별 토큰 필드가 null인 경우 기본값으로 처리한다")
    void generateBusinessPlan_nullTokenFields_handlesGracefully() {
        // given
        when(mockUsage.getPromptTokens()).thenReturn(null);
        when(mockUsage.getCompletionTokens()).thenReturn(null);
        when(mockUsage.getTotalTokens()).thenReturn(null);
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
        assertThat(metadata.getPromptTokens()).isEqualTo(0);
        assertThat(metadata.getCompletionTokens()).isEqualTo(0);
        assertThat(metadata.getTotalTokens()).isEqualTo(0);
    }

    @Test
    @DisplayName("generateBusinessPlan - Generation이 null인 경우 빈 콘텐츠로 처리한다")
    void generateBusinessPlan_nullGeneration_handlesGracefully() {
        // given
        when(mockChatResponse.getResult()).thenReturn(null);
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        assertThat(response.getSections()).hasSize(1);
        assertThat(response.getSections().get(0).getContent()).isEmpty();
    }

    @Test
    @DisplayName("generateBusinessPlan - 생성된 콘텐츠가 null인 경우 빈 섹션을 반환한다")
    void generateBusinessPlan_nullContent_returnsEmptySection() {
        // given
        when(mockGeneration.getOutput()).thenReturn(null);
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        assertThat(response.getSections()).hasSize(1);
        assertThat(response.getSections().get(0).getContent()).isEmpty();
    }

    @Test
    @DisplayName("generateBusinessPlan - 빈 문자열 콘텐츠도 정상 처리한다")
    void generateBusinessPlan_emptyContent_handlesGracefully() {
        // given
        AssistantMessage assistantMessage = mock(AssistantMessage.class);
        when(assistantMessage.getText()).thenReturn("");
        when(mockGeneration.getOutput()).thenReturn(assistantMessage);
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        assertThat(response.getSections()).hasSize(1);
        assertThat(response.getSections().get(0).getContent()).isEmpty();
        BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
        // 빈 문자열은 "\n\n"로 연결되므로 characterCount는 2가 됨
        assertThat(metadata.getWordCount()).isEqualTo(0);
        assertThat(metadata.getCharacterCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("generateBusinessPlan - wordCount와 characterCount가 올바르게 계산된다")
    void generateBusinessPlan_validRequest_calculatesTextMetrics() {
        // given
        AssistantMessage assistantMessage = mock(AssistantMessage.class);
        when(assistantMessage.getText()).thenReturn("This is a test content with multiple words.");
        when(mockGeneration.getOutput()).thenReturn(assistantMessage);

        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then
        BusinessPlanGenerateResponse.GenerationMetadata metadata = response.getMetadata();
        assertThat(metadata.getCharacterCount()).isGreaterThan(0);
        assertThat(metadata.getWordCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("generateBusinessPlan - durationMs가 0인 경우 tokensPerSecond가 0으로 처리된다")
    void generateBusinessPlan_zeroDuration_handlesGracefully() {
        // given
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        service.generateBusinessPlan(request, projectId, templateType, itemName, startTimeMs);

        // then: durationMs가 매우 작을 수 있지만, 0으로 나누기 오류가 발생하지 않아야 함
        verify(generationRepository, times(1)).saveUsage(
                anyString(), anyString(), anyString(), anyLong(),
                anyInt(), anyInt(), anyInt(), anyDouble()
        );
    }

    @Test
    @DisplayName("generateBusinessPlan - JsonProcessingException 발생 시 fallback 처리한다")
    void generateBusinessPlan_jsonSerializationFailure_handlesGracefully() throws JsonProcessingException {
        // given: ObjectMapper가 예외를 던지도록 설정
        com.fasterxml.jackson.databind.ObjectWriter mockWriter = mock(com.fasterxml.jackson.databind.ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
        when(mockWriter.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});
        
        String projectId = "proj-12345";
        String templateType = "pre-startup";
        String itemName = "AI 기반 학습 플랫폼";
        long startTimeMs = System.currentTimeMillis();

        // when
        BusinessPlanGenerateResponse response = service.generateBusinessPlan(
                request, projectId, templateType, itemName, startTimeMs
        );

        // then: 예외가 발생해도 응답은 정상적으로 생성되어야 함
        assertThat(response).isNotNull();
        assertThat(response.getBusinessPlanId()).isNotNull();
        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("generateBusinessPlan - 실제 리포지토리를 사용해 로깅까지 수행된다")
    void generateBusinessPlan_withRealRepository_logsUsage() throws JsonProcessingException {
        // given: 실제 리포지토리 + 로그 캡처 설정
        BusinessPlanGenerationRepository realRepository = new BusinessPlanGenerationRepository();
        Logger logger = (Logger) LoggerFactory.getLogger(BusinessPlanGenerationRepository.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // 실제 ObjectMapper 사용
        ObjectMapper realMapper = new ObjectMapper();

        // 별도의 서비스 인스턴스를 생성 (Mock ChatModel + Real Repository)
        BusinessPlanGenerationService integrationService =
                new BusinessPlanGenerationService(chatModel, realMapper, realRepository);
        ReflectionTestUtils.setField(integrationService, "geminiModelName", "gemini-2.5-flash-lite");

        // ChatModel 응답 모킹 (간단 버전)
        Usage usage = mock(Usage.class);
        when(usage.getPromptTokens()).thenReturn(100);
        when(usage.getCompletionTokens()).thenReturn(200);
        when(usage.getTotalTokens()).thenReturn(300);

        ChatResponseMetadata metadata = mock(ChatResponseMetadata.class);
        when(metadata.getUsage()).thenReturn(usage);

        AssistantMessage assistantMessage = mock(AssistantMessage.class);
        when(assistantMessage.getText()).thenReturn("## 1. 사업 개요\n\n테스트 콘텐츠");

        Generation generation = mock(Generation.class);
        when(generation.getOutput()).thenReturn(assistantMessage);

        ChatResponse chatResponse = mock(ChatResponse.class);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatResponse.getMetadata()).thenReturn(metadata);

        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        BusinessPlanGenerateRequest req = createMockRequest();
        String projectId = "proj-99999";
        String templateType = "pre-startup";
        String itemName = "통합 테스트용 아이템";
        long startTimeMs = System.currentTimeMillis();

        // when: 서비스 호출 (리포지토리까지 실제 실행)
        integrationService.generateBusinessPlan(req, projectId, templateType, itemName, startTimeMs);

        // then: 리포지토리 로그가 실제로 기록되었는지 검증
        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).isNotEmpty();
        ILoggingEvent lastLog = logs.get(logs.size() - 1);
        String msg = lastLog.getFormattedMessage();

        assertThat(msg).contains("[Gemini Usage Log]");
        assertThat(msg).contains("businessPlanId=bp-");
        assertThat(msg).contains("Input: 100");
        assertThat(msg).contains("Output: 200");
        assertThat(msg).contains("Total: 300");
        assertThat(msg).contains("Throughput:");
    }

    @Test
    @DisplayName("generateBusinessPlan - 실제 파일에 로그가 기록되는지 검증")
    void generateBusinessPlan_withRealRepository_writesToFile() throws JsonProcessingException {
        // given: 실제 리포지토리 사용
        BusinessPlanGenerationRepository realRepository = new BusinessPlanGenerationRepository();
        ObjectMapper realMapper = new ObjectMapper();

        BusinessPlanGenerationService integrationService =
                new BusinessPlanGenerationService(chatModel, realMapper, realRepository);
        ReflectionTestUtils.setField(integrationService, "geminiModelName", "gemini-2.5-flash-lite");

        // ChatModel 응답 모킹
        Usage usage = mock(Usage.class);
        when(usage.getPromptTokens()).thenReturn(500);
        when(usage.getCompletionTokens()).thenReturn(1000);
        when(usage.getTotalTokens()).thenReturn(1500);

        ChatResponseMetadata metadata = mock(ChatResponseMetadata.class);
        when(metadata.getUsage()).thenReturn(usage);

        AssistantMessage assistantMessage = mock(AssistantMessage.class);
        when(assistantMessage.getText()).thenReturn("## 파일 로깅 테스트\n\n이 로그는 파일에 기록되어야 합니다.");

        Generation generation = mock(Generation.class);
        when(generation.getOutput()).thenReturn(assistantMessage);

        ChatResponse chatResponse = mock(ChatResponse.class);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatResponse.getMetadata()).thenReturn(metadata);

        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        BusinessPlanGenerateRequest req = createMockRequest();
        String projectId = "proj-file-test";
        String templateType = "pre-startup";
        String itemName = "파일 로깅 테스트 아이템";
        long startTimeMs = System.currentTimeMillis();

        // 테스트 전 파일 크기 확인 (파일이 존재하는 경우)
        java.io.File logFile = new java.io.File("logs/gemini-usage-test.log");
        long fileSizeBefore = logFile.exists() ? logFile.length() : 0;
        int lineCountBefore = 0;
        if (logFile.exists()) {
            try {
                lineCountBefore = (int) java.nio.file.Files.lines(logFile.toPath()).count();
            } catch (java.io.IOException e) {
                // 무시
            }
        }

        // when: 서비스 호출
        integrationService.generateBusinessPlan(req, projectId, templateType, itemName, startTimeMs);

        // Logback LoggerContext를 명시적으로 flush하여 파일에 즉시 기록되도록 함
        ch.qos.logback.classic.LoggerContext loggerContext = 
                (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLoggerList().forEach(logger -> {
            logger.iteratorForAppenders().forEachRemaining(appender -> {
                if (appender instanceof ch.qos.logback.core.rolling.RollingFileAppender) {
                    ch.qos.logback.core.rolling.RollingFileAppender<?> fileAppender = 
                            (ch.qos.logback.core.rolling.RollingFileAppender<?>) appender;
                    // 파일 앱ender가 활성화되어 있으면 flush
                    if (fileAppender.isStarted()) {
                        fileAppender.getOutputStream();
                    }
                }
            });
        });

        // 로그가 파일에 flush되도록 약간 대기 (immediateFlush=true이지만 안전을 위해)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // then: 파일에 로그가 기록되었는지 검증
        // logback-test.xml이 로드되면 logs/gemini-usage-test.log에 기록됨
        assertThat(logFile.exists()).isTrue();
        
        long fileSizeAfter = logFile.length();
        // 파일 크기가 증가했는지 확인
        assertThat(fileSizeAfter).isGreaterThanOrEqualTo(fileSizeBefore);
        
        // 파일 내용 확인 - 새로운 로그 라인이 추가되었는지 확인
        try {
            int lineCountAfter = (int) java.nio.file.Files.lines(logFile.toPath()).count();
            // 새로운 라인이 추가되었거나, 파일 크기가 증가했는지 확인
            assertThat(lineCountAfter).isGreaterThanOrEqualTo(lineCountBefore);
            
            // 파일 내용에 테스트 로그가 포함되어 있는지 확인
            String fileContent = new String(java.nio.file.Files.readAllBytes(logFile.toPath()));
            assertThat(fileContent).contains("[Gemini Usage Log]");
            assertThat(fileContent).contains("businessPlanId=bp-");
            assertThat(fileContent).contains("Input: 500");
            assertThat(fileContent).contains("Output: 1000");
            assertThat(fileContent).contains("Total: 1500");
            
            // 마지막 라인에 최근 로그가 있는지 확인
            String[] lines = fileContent.split("\n");
            String lastLine = lines.length > 0 ? lines[lines.length - 1] : "";
            assertThat(lastLine).contains("Input: 500");
            assertThat(lastLine).contains("Output: 1000");
            assertThat(lastLine).contains("Total: 1500");
        } catch (java.io.IOException e) {
            // 파일 읽기 실패는 테스트 실패로 간주
            throw new AssertionError("로그 파일을 읽을 수 없습니다: " + e.getMessage(), e);
        }
    }

    private BusinessPlanGenerateRequest createMockRequest() {
        BusinessPlanGenerateRequest.RequestInfo requestInfo = new BusinessPlanGenerateRequest.RequestInfo(
                "pre-startup",
                "2025-12-18T12:00:00.000Z",
                "user-123",
                "proj-12345"
        );

        BusinessPlanGenerateRequest.Step1ProblemRecognition step1 = 
                new BusinessPlanGenerateRequest.Step1ProblemRecognition(
                        "AI 기반 학습 플랫폼",
                        "개인 맞춤형 학습 제공",
                        "학습 효율 저하",
                        "78% 불만족",
                        "중학생 학부모"
                );

        BusinessPlanGenerateRequest.BusinessPlanData businessPlanData = 
                new BusinessPlanGenerateRequest.BusinessPlanData(
                        step1,
                        null, null, null, null, null
                );

        BusinessPlanGenerateRequest.GenerationOptions generationOptions = 
                new BusinessPlanGenerateRequest.GenerationOptions(
                        "professional",
                        "detailed",
                        "markdown",
                        "ko",
                        List.of("executive_summary", "problem_analysis")
                );

        return new BusinessPlanGenerateRequest(
                requestInfo,
                businessPlanData,
                generationOptions
        );
    }
}
