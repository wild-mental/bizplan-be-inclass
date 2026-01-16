package vibe.makersround.makersround_backend.dto.ab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsEventLog {

    @JsonProperty("_log_meta")
    private LogMeta logMeta;

    private EventInfo event;

    private ExperimentInfo experiment;

    private UserInfo user;

    private PersonaInfo persona;

    private ContextInfo context;

    private ConversionInfo conversion;

    private AttributionInfo attribution;

    private DeviceInfo device;

    private Map<String, Object> properties;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LogMeta {

        @JsonProperty("log_type")
        private String logType;

        @JsonProperty("log_version")
        private String logVersion;

        @JsonProperty("logged_at")
        private String loggedAt;

        @JsonProperty("request_id")
        private String requestId;

        public static LogMeta now(String logType, String logVersion) {
            return LogMeta.builder()
                    .logType(logType)
                    .logVersion(logVersion)
                    .loggedAt(Instant.now().toString())
                    .requestId(UUID.randomUUID().toString().substring(0, 13))
                    .build();
        }

        public static LogMeta analyticsEvent() {
            return now("ANALYTICS_EVENT", "1.0");
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventInfo {

        private String id;

        private String type;

        private String name;

        private String timestamp;

        public static EventInfo exposure() {
            return EventInfo.builder()
                    .id("evt_" + UUID.randomUUID().toString().substring(0, 13))
                    .type("exposure")
                    .name("ab_test_exposure")
                    .timestamp(Instant.now().toString())
                    .build();
        }

        public static EventInfo conversion(String eventName) {
            return EventInfo.builder()
                    .id("evt_" + UUID.randomUUID().toString().substring(0, 13))
                    .type("conversion")
                    .name(eventName != null ? eventName : "ab_test_conversion")
                    .timestamp(Instant.now().toString())
                    .build();
        }

        public static EventInfo error(String eventName) {
            return EventInfo.builder()
                    .id("evt_" + UUID.randomUUID().toString().substring(0, 13))
                    .type("error")
                    .name(eventName != null ? eventName : "step_error")
                    .timestamp(Instant.now().toString())
                    .build();
        }

        public static EventInfo stepProgress(String eventName) {
            return EventInfo.builder()
                    .id("evt_" + UUID.randomUUID().toString().substring(0, 13))
                    .type("step_progress")
                    .name(eventName != null ? eventName : "step_progress")
                    .timestamp(Instant.now().toString())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExperimentInfo {

        private String id;

        private String name;

        @JsonProperty("variant_id")
        private String variantId;

        @JsonProperty("variant_name")
        private String variantName;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {

        @JsonProperty("visitor_id")
        private String visitorId;

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("client_id")
        private String clientId;

        @JsonProperty("session_id")
        private String sessionId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PersonaInfo {

        private String jtbd;

        private String source;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContextInfo {

        @JsonProperty("page_location")
        private String pageLocation;

        @JsonProperty("page_section")
        private String pageSection;

        @JsonProperty("button_text")
        private String buttonText;

        @JsonProperty("step_id")
        private String stepId;

        @JsonProperty("error_type")
        private String errorType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ConversionInfo {

        private String type;

        private String value;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttributionInfo {

        @JsonProperty("utm_source")
        private String utmSource;

        @JsonProperty("utm_medium")
        private String utmMedium;

        @JsonProperty("utm_campaign")
        private String utmCampaign;

        @JsonProperty("utm_content")
        private String utmContent;

        private String referer;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeviceInfo {

        @JsonProperty("user_agent")
        private String userAgent;

        @JsonProperty("ip_address")
        private String ipAddress;
    }

    public static AnalyticsEventLog exposure(ExperimentInfo experiment, UserInfo user) {
        return AnalyticsEventLog.builder()
                .logMeta(LogMeta.analyticsEvent())
                .event(EventInfo.exposure())
                .experiment(experiment)
                .user(user)
                .build();
    }

    public static AnalyticsEventLog conversion(
            ExperimentInfo experiment,
            UserInfo user,
            ConversionInfo conversionInfo,
            String eventName) {
        return AnalyticsEventLog.builder()
                .logMeta(LogMeta.analyticsEvent())
                .event(EventInfo.conversion(eventName))
                .experiment(experiment)
                .user(user)
                .conversion(conversionInfo)
                .build();
    }
}
