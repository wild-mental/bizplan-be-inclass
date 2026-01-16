package vibe.makersround.makersround_backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog;
import vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnalyticsEventLogger {

    private static final Logger analyticsLog = LoggerFactory.getLogger("analytics.events");
    private final ObjectMapper objectMapper;

    public void logEvent(AnalyticsEventLog event) {
        try {
            String jsonLine = objectMapper.writeValueAsString(event);
            analyticsLog.info(jsonLine);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize analytics event: {}", e.getMessage());
        }
    }

    public void logExposure(String experimentId, String experimentName,
                            String variantId, String variantName,
                            String visitorId, String sessionId) {
        ExperimentInfo experiment = ExperimentInfo.builder()
                .id(experimentId)
                .name(experimentName)
                .variantId(variantId)
                .variantName(variantName)
                .build();

        UserInfo user = UserInfo.builder()
                .visitorId(visitorId)
                .sessionId(sessionId)
                .build();

        AnalyticsEventLog eventLog = AnalyticsEventLog.builder()
                .logMeta(LogMeta.analyticsEvent())
                .event(EventInfo.exposure())
                .experiment(experiment)
                .user(user)
                .build();

        logEvent(eventLog);
    }

    public void logConversion(String experimentId, String variantId,
                              String visitorId, String conversionType,
                              String conversionValue) {
        ExperimentInfo experiment = ExperimentInfo.builder()
                .id(experimentId)
                .variantId(variantId)
                .build();

        UserInfo user = UserInfo.builder()
                .visitorId(visitorId)
                .build();

        ConversionInfo conversion = ConversionInfo.builder()
                .type(conversionType)
                .value(conversionValue)
                .build();

        AnalyticsEventLog eventLog = AnalyticsEventLog.builder()
                .logMeta(LogMeta.analyticsEvent())
                .event(EventInfo.conversion("ab_test_conversion"))
                .experiment(experiment)
                .user(user)
                .conversion(conversion)
                .build();

        logEvent(eventLog);
    }

    public void logError(String experimentId, String variantId,
                         String visitorId, String stepId,
                         String errorType, String pageLocation) {
        ExperimentInfo experiment = ExperimentInfo.builder()
                .id(experimentId)
                .variantId(variantId)
                .build();

        UserInfo user = UserInfo.builder()
                .visitorId(visitorId)
                .build();

        ContextInfo context = ContextInfo.builder()
                .stepId(stepId)
                .errorType(errorType)
                .pageLocation(pageLocation)
                .build();

        AnalyticsEventLog eventLog = AnalyticsEventLog.builder()
                .logMeta(LogMeta.analyticsEvent())
                .event(EventInfo.error("step_error"))
                .experiment(experiment)
                .user(user)
                .context(context)
                .build();

        logEvent(eventLog);
    }
}
