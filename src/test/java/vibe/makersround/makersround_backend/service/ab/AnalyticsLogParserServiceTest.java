package vibe.makersround.makersround_backend.service.ab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog;
import vibe.makersround.makersround_backend.dto.ab.response.FunnelAnalysisResponse;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;
import vibe.makersround.makersround_backend.repository.ab.ABExperimentRepository;
import vibe.makersround.makersround_backend.service.AnalyticsLogParserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsLogParserService 테스트")
class AnalyticsLogParserServiceTest {

    @Mock
    private ABExperimentRepository experimentRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AnalyticsLogParserService analyticsLogParserService;

    @TempDir
    Path tempDir;

    private String testExperimentId;
    private String testVariantId;
    private String testVisitorId;

    @BeforeEach
    void setUp() {
        testExperimentId = "exp-" + UUID.randomUUID().toString().substring(0, 8);
        testVariantId = "var-" + UUID.randomUUID().toString().substring(0, 8);
        testVisitorId = "visitor-" + UUID.randomUUID().toString().substring(0, 8);

        ReflectionTestUtils.setField(analyticsLogParserService, "logDirectory", tempDir.toString());
    }

    @Test
    @DisplayName("analyzeFunnel - 로그 파일이 없을 때 빈 결과 반환")
    void analyzeFunnel_noLogFiles_returnsEmptyResult() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        ABExperiment mockExperiment = ABExperiment.builder()
                .id(testExperimentId)
                .name("Test Experiment")
                .build();
        when(experimentRepository.findById(testExperimentId)).thenReturn(Optional.of(mockExperiment));

        // When
        FunnelAnalysisResponse result = analyticsLogParserService.analyzeFunnel(testExperimentId, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getExperimentId()).isEqualTo(testExperimentId);
        assertThat(result.getExperimentName()).isEqualTo("Test Experiment");
        assertThat(result.getTotalVisitors()).isZero();
        assertThat(result.getSteps()).hasSize(5);
    }

    @Test
    @DisplayName("analyzeFunnel - 로그 파일 파싱 및 퍼널 분석")
    void analyzeFunnel_withLogFiles_parsesFunnelCorrectly() throws IOException {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        createTestLogFile();

        ABExperiment mockExperiment = ABExperiment.builder()
                .id(testExperimentId)
                .name("Test Experiment")
                .build();
        when(experimentRepository.findById(testExperimentId)).thenReturn(Optional.of(mockExperiment));

        // When
        FunnelAnalysisResponse result = analyticsLogParserService.analyzeFunnel(testExperimentId, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getExperimentId()).isEqualTo(testExperimentId);
    }

    @Test
    @DisplayName("countEventsByType - 이벤트 타입별 카운트")
    void countEventsByType_countsCorrectly() throws IOException {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        createTestLogFile();

        // When
        Map<String, Long> result = analyticsLogParserService.countEventsByType(testExperimentId, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("analyzeFunnel - 실험이 존재하지 않을 때 Unknown 이름 반환")
    void analyzeFunnel_experimentNotFound_returnsUnknownName() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(experimentRepository.findById(testExperimentId)).thenReturn(Optional.empty());

        // When
        FunnelAnalysisResponse result = analyticsLogParserService.analyzeFunnel(testExperimentId, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getExperimentName()).isEqualTo("Unknown");
    }

    @Test
    @DisplayName("analyzeFunnel - 퍼널 단계가 올바른 순서로 반환")
    void analyzeFunnel_funnelStepsInCorrectOrder() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        ABExperiment mockExperiment = ABExperiment.builder()
                .id(testExperimentId)
                .name("Test Experiment")
                .build();
        when(experimentRepository.findById(testExperimentId)).thenReturn(Optional.of(mockExperiment));

        // When
        FunnelAnalysisResponse result = analyticsLogParserService.analyzeFunnel(testExperimentId, startDate, endDate);

        // Then
        assertThat(result.getSteps()).hasSize(5);
        assertThat(result.getSteps().get(0).getName()).isEqualTo("exposure");
        assertThat(result.getSteps().get(1).getName()).isEqualTo("cta_click");
        assertThat(result.getSteps().get(2).getName()).isEqualTo("demo_start");
        assertThat(result.getSteps().get(3).getName()).isEqualTo("demo_finish");
        assertThat(result.getSteps().get(4).getName()).isEqualTo("signup");
    }

    private void createTestLogFile() throws IOException {
        Path logFile = tempDir.resolve("analytics-events.log");
        
        String logEntry = createLogEntry("exposure", testExperimentId, testVariantId, testVisitorId);
        Files.writeString(logFile, logEntry + "\n");
    }

    private String createLogEntry(String eventType, String experimentId, String variantId, String visitorId) {
        AnalyticsEventLog eventLog = AnalyticsEventLog.builder()
                .logMeta(AnalyticsEventLog.LogMeta.builder()
                        .logType("ANALYTICS_EVENT")
                        .logVersion("1.0")
                        .loggedAt(Instant.now().toString())
                        .requestId(UUID.randomUUID().toString().substring(0, 13))
                        .build())
                .event(AnalyticsEventLog.EventInfo.builder()
                        .id("evt_" + UUID.randomUUID().toString().substring(0, 13))
                        .type(eventType)
                        .name("ab_test_" + eventType)
                        .timestamp(Instant.now().toString())
                        .build())
                .experiment(AnalyticsEventLog.ExperimentInfo.builder()
                        .id(experimentId)
                        .variantId(variantId)
                        .build())
                .user(AnalyticsEventLog.UserInfo.builder()
                        .visitorId(visitorId)
                        .build())
                .build();

        try {
            return objectMapper.writeValueAsString(eventLog);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create log entry", e);
        }
    }
}
