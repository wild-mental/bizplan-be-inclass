package vibe.makersround.makersround_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.makersround.makersround_backend.dto.ApiResponse;
import vibe.makersround.makersround_backend.dto.ab.request.AnalyticsEventRequest;
import vibe.makersround.makersround_backend.dto.ab.request.ConversionRequest;
import vibe.makersround.makersround_backend.dto.ab.response.ActiveExperimentsResponse;
import vibe.makersround.makersround_backend.dto.ab.response.ConversionResponse;
import vibe.makersround.makersround_backend.service.ABTestService;
import vibe.makersround.makersround_backend.util.AnalyticsEventLogger;

@RestController
@RequestMapping("/api/v1/ab-tests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "A/B Test", description = "A/B 테스트 API - 실험 조회 및 전환 추적")
public class ABTestController {

    private final ABTestService abTestService;
    private final AnalyticsEventLogger analyticsEventLogger;

    @Operation(
            summary = "활성 실험 조회",
            description = "지정된 페이지에 대한 활성 A/B 테스트 실험 목록과 방문자에게 할당된 변형을 조회합니다."
    )
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ActiveExperimentsResponse>> getActiveExperiments(
            @Parameter(description = "페이지 식별자 (landing, pricing 등)", required = true)
            @RequestParam String page,
            @Parameter(description = "방문자 고유 ID", required = true)
            @RequestParam("visitor_id") String visitorId
    ) {
        log.debug("A/B 테스트 활성 실험 조회 - page: {}, visitorId: {}", page, visitorId);
        ActiveExperimentsResponse response = abTestService.getActiveExperiments(page, visitorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "전환 이벤트 기록",
            description = "A/B 테스트 실험에 대한 전환 이벤트를 기록합니다."
    )
    @PostMapping("/conversions")
    public ResponseEntity<ApiResponse<ConversionResponse>> recordConversion(
            @Valid @RequestBody ConversionRequest request
    ) {
        log.debug("A/B 테스트 전환 기록 - experimentId: {}, eventType: {}",
                request.getExperimentId(), request.getEventType());
        ConversionResponse response = abTestService.recordConversion(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "분석 이벤트 기록",
            description = "A/B 테스트 관련 분석 이벤트를 파일 로그로 기록합니다. (exposure, conversion, error, step_progress 등)"
    )
    @PostMapping("/events")
    public ResponseEntity<ApiResponse<Void>> recordEvent(
            @Valid @RequestBody AnalyticsEventRequest request
    ) {
        log.debug("A/B 테스트 분석 이벤트 기록 - eventType: {}, experimentId: {}",
                request.getEventType(), request.getExperimentId());

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog eventLog = buildEventLog(request);
        analyticsEventLogger.logEvent(eventLog);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog buildEventLog(AnalyticsEventRequest request) {
        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.ExperimentInfo experiment =
                vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.ExperimentInfo.builder()
                        .id(request.getExperimentId())
                        .name(request.getExperimentName())
                        .variantId(request.getVariantId())
                        .variantName(request.getVariantName())
                        .build();

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.UserInfo user =
                vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.UserInfo.builder()
                        .visitorId(request.getVisitorId())
                        .userId(request.getUserId())
                        .clientId(request.getClientId())
                        .sessionId(request.getSessionId())
                        .build();

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.EventInfo eventInfo = buildEventInfo(request);

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.PersonaInfo personaInfo = null;
        if (request.getPersona() != null) {
            personaInfo = vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.PersonaInfo.builder()
                    .jtbd(request.getPersona().getJtbd())
                    .source(request.getPersona().getSource())
                    .build();
        }

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.ContextInfo contextInfo = null;
        if (request.getContext() != null) {
            contextInfo = vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.ContextInfo.builder()
                    .pageLocation(request.getContext().getPageLocation())
                    .pageSection(request.getContext().getPageSection())
                    .buttonText(request.getContext().getButtonText())
                    .stepId(request.getContext().getStepId())
                    .errorType(request.getContext().getErrorType())
                    .build();
        }

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.ConversionInfo conversionInfo = null;
        if (request.getConversion() != null) {
            conversionInfo = vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.ConversionInfo.builder()
                    .type(request.getConversion().getType())
                    .value(request.getConversion().getValue())
                    .build();
        }

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.AttributionInfo attributionInfo = null;
        if (request.getAttribution() != null) {
            attributionInfo = vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.AttributionInfo.builder()
                    .utmSource(request.getAttribution().getUtmSource())
                    .utmMedium(request.getAttribution().getUtmMedium())
                    .utmCampaign(request.getAttribution().getUtmCampaign())
                    .utmContent(request.getAttribution().getUtmContent())
                    .referer(request.getAttribution().getReferer())
                    .build();
        }

        vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.DeviceInfo deviceInfo = null;
        if (request.getDevice() != null) {
            deviceInfo = vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.DeviceInfo.builder()
                    .userAgent(request.getDevice().getUserAgent())
                    .ipAddress(request.getDevice().getIpAddress())
                    .build();
        }

        return vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.builder()
                .logMeta(vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.LogMeta.analyticsEvent())
                .event(eventInfo)
                .experiment(experiment)
                .user(user)
                .persona(personaInfo)
                .context(contextInfo)
                .conversion(conversionInfo)
                .attribution(attributionInfo)
                .device(deviceInfo)
                .properties(request.getProperties())
                .build();
    }

    private vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.EventInfo buildEventInfo(AnalyticsEventRequest request) {
        String eventType = request.getEventType();
        String eventName = request.getEventName();

        return switch (eventType) {
            case "exposure" -> vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.EventInfo.exposure();
            case "conversion" -> vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.EventInfo.conversion(eventName);
            case "error" -> vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.EventInfo.error(eventName);
            case "step_progress" -> vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.EventInfo.stepProgress(eventName);
            default -> vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.EventInfo.builder()
                    .id("evt_" + java.util.UUID.randomUUID().toString().substring(0, 13))
                    .type(eventType)
                    .name(eventName != null ? eventName : eventType)
                    .timestamp(java.time.Instant.now().toString())
                    .build();
        };
    }
}
