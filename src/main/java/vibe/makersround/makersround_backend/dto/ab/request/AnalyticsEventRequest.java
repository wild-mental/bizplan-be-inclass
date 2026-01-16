package vibe.makersround.makersround_backend.dto.ab.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsEventRequest {

    @NotBlank(message = "Event type is required")
    private String eventType;

    private String eventName;

    @NotBlank(message = "Experiment ID is required")
    private String experimentId;

    private String experimentName;

    @NotBlank(message = "Variant ID is required")
    private String variantId;

    private String variantName;

    @NotBlank(message = "Visitor ID is required")
    private String visitorId;

    private String userId;

    private String clientId;

    private String sessionId;

    private PersonaData persona;

    private ContextData context;

    private ConversionData conversion;

    private AttributionData attribution;

    private DeviceData device;

    private Map<String, Object> properties;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonaData {
        private String jtbd;
        private String source;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContextData {
        private String pageLocation;
        private String pageSection;
        private String buttonText;
        private String stepId;
        private String errorType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConversionData {
        private String type;
        private String value;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttributionData {
        private String utmSource;
        private String utmMedium;
        private String utmCampaign;
        private String utmContent;
        private String referer;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceData {
        private String userAgent;
        private String ipAddress;
    }
}
