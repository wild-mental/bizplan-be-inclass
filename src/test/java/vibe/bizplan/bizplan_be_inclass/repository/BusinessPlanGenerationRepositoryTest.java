package vibe.bizplan.bizplan_be_inclass.repository;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BusinessPlanGenerationRepository 단위 테스트
 *
 * Rule 301: methodName_scenario_expectedBehavior naming
 */
@DisplayName("BusinessPlanGenerationRepository 테스트")
class BusinessPlanGenerationRepositoryTest {

    private BusinessPlanGenerationRepository repository;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        repository = new BusinessPlanGenerationRepository();

        // 로그 캡처 설정
        Logger logger = (Logger) LoggerFactory.getLogger(BusinessPlanGenerationRepository.class);
        logger.setAdditive(true);
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        listAppender.list.clear();
    }

    @Test
    @DisplayName("saveUsage - 사용량 정보가 로그로 기록된다")
    void saveUsage_validData_logsUsage() {
        // given
        String businessPlanId = "bp-2025-12-18-550e8400";
        String startTimeIso = "2025-12-18T14:30:15.123Z";
        String endTimeIso = "2025-12-18T14:30:19.500Z";
        long durationMs = 4377L;
        int promptTokens = 1234;
        int completionTokens = 5678;
        int totalTokens = 6912;
        double tokensPerSecond = 1578.25;

        // when
        repository.saveUsage(
                businessPlanId, startTimeIso, endTimeIso, durationMs,
                promptTokens, completionTokens, totalTokens, tokensPerSecond
        );

        // then
        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).isNotEmpty();
        
        ILoggingEvent logEvent = logs.get(logs.size() - 1);
        String msg = logEvent.getFormattedMessage();
        assertThat(msg).contains("[Gemini Usage Log]");
        assertThat(msg).contains("businessPlanId=" + businessPlanId);
        assertThat(msg).contains("StartTime: " + startTimeIso);
        assertThat(msg).contains("EndTime: " + endTimeIso);
        assertThat(msg).contains("Duration: " + durationMs + "ms");
        assertThat(msg).contains("Input: " + promptTokens);
        assertThat(msg).contains("Output: " + completionTokens);
        assertThat(msg).contains("Total: " + totalTokens);
        assertThat(msg).contains("Throughput:");
    }

    @Test
    @DisplayName("saveUsage - 토큰 수가 0인 경우도 로그로 기록된다")
    void saveUsage_zeroTokens_logsUsage() {
        // given
        String businessPlanId = "bp-2025-12-18-550e8400";
        String startTimeIso = "2025-12-18T14:30:15.123Z";
        String endTimeIso = "2025-12-18T14:30:15.200Z";
        long durationMs = 77L;
        int promptTokens = 0;
        int completionTokens = 0;
        int totalTokens = 0;
        double tokensPerSecond = 0.0;

        // when
        repository.saveUsage(
                businessPlanId, startTimeIso, endTimeIso, durationMs,
                promptTokens, completionTokens, totalTokens, tokensPerSecond
        );

        // then
        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).isNotEmpty();
        String msg = logs.get(logs.size() - 1).getFormattedMessage();
        assertThat(msg).contains("Input: 0");
        assertThat(msg).contains("Output: 0");
        assertThat(msg).contains("Total: 0");
    }

    @Test
    @DisplayName("saveUsage - 큰 토큰 수도 올바르게 로그로 기록된다")
    void saveUsage_largeTokens_logsUsage() {
        // given
        String businessPlanId = "bp-2025-12-18-550e8400";
        String startTimeIso = "2025-12-18T14:30:15.123Z";
        String endTimeIso = "2025-12-18T14:30:25.500Z";
        long durationMs = 10377L;
        int promptTokens = 50000;
        int completionTokens = 100000;
        int totalTokens = 150000;
        double tokensPerSecond = 14450.25;

        // when
        repository.saveUsage(
                businessPlanId, startTimeIso, endTimeIso, durationMs,
                promptTokens, completionTokens, totalTokens, tokensPerSecond
        );

        // then
        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).isNotEmpty();
        String msg = logs.get(logs.size() - 1).getFormattedMessage();
        assertThat(msg).contains("Input: 50000");
        assertThat(msg).contains("Output: 100000");
        assertThat(msg).contains("Total: 150000");
    }

    @Test
    @DisplayName("saveUsage - 처리량이 소수점으로 기록된다")
    void saveUsage_decimalThroughput_logsCorrectly() {
        // given
        String businessPlanId = "bp-2025-12-18-550e8400";
        String startTimeIso = "2025-12-18T14:30:15.123Z";
        String endTimeIso = "2025-12-18T14:30:19.500Z";
        long durationMs = 4377L;
        int promptTokens = 1234;
        int completionTokens = 5678;
        int totalTokens = 6912;
        double tokensPerSecond = 1578.256789;

        // when
        repository.saveUsage(
                businessPlanId, startTimeIso, endTimeIso, durationMs,
                promptTokens, completionTokens, totalTokens, tokensPerSecond
        );

        // then
        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).isNotEmpty();
        // 처리량은 String.format("%.2f")로 포맷되므로 소수점 둘째 자리까지 표시
        assertThat(logs.get(logs.size() - 1).getFormattedMessage()).contains("1578.26");
    }
}
