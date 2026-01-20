package vibe.makersround.makersround_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_backend.dto.ab.request.ConversionRequest;
import vibe.makersround.makersround_backend.dto.ab.request.ExperimentCreateRequest;
import vibe.makersround.makersround_backend.dto.ab.request.ExperimentUpdateRequest;
import vibe.makersround.makersround_backend.dto.ab.response.*;
import vibe.makersround.makersround_backend.entity.ab.ABAssignment;
import vibe.makersround.makersround_backend.entity.ab.ABConversion;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;
import vibe.makersround.makersround_backend.entity.ab.ABVariant;
import vibe.makersround.makersround_backend.repository.ab.ABAssignmentRepository;
import vibe.makersround.makersround_backend.repository.ab.ABConversionRepository;
import vibe.makersround.makersround_backend.repository.ab.ABExperimentRepository;
import vibe.makersround.makersround_backend.repository.ab.ABVariantRepository;
import vibe.makersround.makersround_backend.util.AnalyticsEventLogger;
import vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.PersonaInfo;
import vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.AttributionInfo;
import vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog.DeviceInfo;
import vibe.makersround.makersround_backend.dto.ab.request.UserPropertiesDto;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ABTestService {

    private final ABExperimentRepository experimentRepository;
    private final ABVariantRepository variantRepository;
    private final ABAssignmentRepository assignmentRepository;
    private final ABConversionRepository conversionRepository;
    private final ObjectMapper objectMapper;
    private final AnalyticsEventLogger analyticsEventLogger;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ActiveExperimentsResponse getActiveExperiments(String page, String visitorId) {
        // 페이지 파라미터 정규화: 'landing' -> '/'
        String normalizedPage = normalizePage(page);
        
        List<ABExperiment> activeExperiments = experimentRepository
                .findActiveExperimentsWithVariants("running", normalizedPage);

        List<AssignedExperimentDto> assignedExperiments = new ArrayList<>();

        for (ABExperiment experiment : activeExperiments) {
            ABVariant assignedVariant = getOrAssignVariant(experiment, visitorId);
            if (assignedVariant != null) {
                assignedExperiments.add(AssignedExperimentDto.builder()
                        .experimentId(experiment.getId())
                        .experimentName(experiment.getName())
                        .variantId(assignedVariant.getId())
                        .variantName(assignedVariant.getName())
                        .content(parseContentJson(assignedVariant.getContentJson()))
                        .build());
            }
        }

        return ActiveExperimentsResponse.builder()
                .experiments(assignedExperiments)
                .visitorId(visitorId)
                .build();
    }

    @Transactional
    public ABVariant getOrAssignVariant(ABExperiment experiment, String visitorId) {
        Optional<ABAssignment> existingAssignment = assignmentRepository
                .findByVisitorIdAndExperimentId(visitorId, experiment.getId());

        if (existingAssignment.isPresent()) {
            return existingAssignment.get().getVariant();
        }

        ABVariant selectedVariant = selectVariantByWeight(experiment.getVariants());
        if (selectedVariant == null) {
            return null;
        }

        ABAssignment newAssignment = ABAssignment.builder()
                .experiment(experiment)
                .variant(selectedVariant)
                .visitorId(visitorId)
                .build();
        assignmentRepository.save(newAssignment);

        return selectedVariant;
    }

    private ABVariant selectVariantByWeight(List<ABVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            return null;
        }

        int totalWeight = variants.stream()
                .mapToInt(v -> v.getWeight() != null ? v.getWeight() : 0)
                .sum();

        if (totalWeight <= 0) {
            return variants.get(0);
        }

        int randomValue = new Random().nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (ABVariant variant : variants) {
            cumulativeWeight += variant.getWeight() != null ? variant.getWeight() : 0;
            if (randomValue < cumulativeWeight) {
                return variant;
            }
        }

        return variants.get(0);
    }

    @Transactional
    public ConversionResponse recordConversion(ConversionRequest request) {
        Optional<ABAssignment> assignment = assignmentRepository
                .findByVisitorIdAndExperimentId(request.getVisitorId(), request.getExperimentId());

        if (assignment.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found for visitor: " + request.getVisitorId());
        }

        String eventDataJson = null;
        if (request.getEventData() != null) {
            try {
                eventDataJson = objectMapper.writeValueAsString(request.getEventData());
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize event data", e);
            }
        }

        ABConversion conversion = ABConversion.builder()
                .assignment(assignment.get())
                .eventType(request.getEventType())
                .eventDataJson(eventDataJson)
                .build();

        ABConversion saved = conversionRepository.save(conversion);

        logConversionToAnalytics(request, assignment.get());

        return ConversionResponse.builder()
                .conversionId(saved.getId())
                .assignmentId(assignment.get().getId())
                .eventType(saved.getEventType())
                .convertedAt(saved.getConvertedAt().format(ISO_FORMATTER))
                .build();
    }

    private void logConversionToAnalytics(ConversionRequest request, ABAssignment assignment) {
        try {
            UserPropertiesDto userProps = request.getUserProperties();
            
            PersonaInfo persona = null;
            AttributionInfo attribution = null;
            DeviceInfo device = null;
            
            if (userProps != null) {
                persona = PersonaInfo.builder()
                        .jtbd(userProps.getJtbd())
                        .source(userProps.getSource())
                        .build();
                
                attribution = AttributionInfo.builder()
                        .utmSource(userProps.getUtmSource())
                        .utmMedium(userProps.getUtmMedium())
                        .utmCampaign(userProps.getUtmCampaign())
                        .utmContent(userProps.getUtmContent())
                        .referer(userProps.getReferer())
                        .build();
                
                device = DeviceInfo.builder()
                        .userAgent(userProps.getBrowser() + " / " + userProps.getOs())
                        .build();
            }
            
            analyticsEventLogger.logConversionWithAttribution(
                    request.getExperimentId(),
                    assignment.getVariant().getId(),
                    request.getVisitorId(),
                    request.getEventType(),
                    null,
                    persona,
                    attribution,
                    device
            );
        } catch (Exception e) {
            log.warn("Failed to log conversion to analytics: {}", e.getMessage());
        }
    }

    public List<ExperimentResponse> getAllExperiments() {
        return experimentRepository.findAll().stream()
                .map(this::toExperimentResponse)
                .collect(Collectors.toList());
    }

    public ExperimentResponse getExperimentById(String id) {
        ABExperiment experiment = experimentRepository.findByIdWithVariants(id)
                .orElseThrow(() -> new IllegalArgumentException("Experiment not found: " + id));
        return toExperimentResponse(experiment);
    }

    @Transactional
    public ExperimentResponse createExperiment(ExperimentCreateRequest request) {
        if (experimentRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Experiment with name already exists: " + request.getName());
        }

        ABExperiment experiment = ABExperiment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .targetPage(request.getTargetPage())
                .targetElement(request.getTargetElement())
                .trafficPercentage(request.getTrafficPercentage())
                .status("draft")
                .build();

        ABExperiment savedExperiment = experimentRepository.save(experiment);

        if (request.getVariants() != null) {
            for (ExperimentCreateRequest.VariantCreateRequest variantRequest : request.getVariants()) {
                ABVariant variant = ABVariant.builder()
                        .experiment(savedExperiment)
                        .name(variantRequest.getName())
                        .weight(variantRequest.getWeight())
                        .isControl(variantRequest.getIsControl())
                        .contentJson(serializeContent(variantRequest.getContent()))
                        .build();
                variantRepository.save(variant);
            }
        }

        return getExperimentById(savedExperiment.getId());
    }

    @Transactional
    public ExperimentResponse updateExperiment(String id, ExperimentUpdateRequest request) {
        ABExperiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Experiment not found: " + id));

        if (request.getName() != null) {
            experiment.setName(request.getName());
        }
        if (request.getDescription() != null) {
            experiment.setDescription(request.getDescription());
        }
        if (request.getTargetPage() != null) {
            experiment.setTargetPage(request.getTargetPage());
        }
        if (request.getTargetElement() != null) {
            experiment.setTargetElement(request.getTargetElement());
        }
        if (request.getTrafficPercentage() != null) {
            experiment.setTrafficPercentage(request.getTrafficPercentage());
        }

        experimentRepository.save(experiment);

        if (request.getVariants() != null) {
            for (ExperimentUpdateRequest.VariantUpdateRequest variantRequest : request.getVariants()) {
                if (variantRequest.getId() != null) {
                    ABVariant variant = variantRepository.findById(variantRequest.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + variantRequest.getId()));

                    if (variantRequest.getName() != null) {
                        variant.setName(variantRequest.getName());
                    }
                    if (variantRequest.getWeight() != null) {
                        variant.setWeight(variantRequest.getWeight());
                    }
                    if (variantRequest.getIsControl() != null) {
                        variant.setIsControl(variantRequest.getIsControl());
                    }
                    if (variantRequest.getContent() != null) {
                        variant.setContentJson(serializeContent(variantRequest.getContent()));
                    }
                    variantRepository.save(variant);
                } else {
                    ABVariant newVariant = ABVariant.builder()
                            .experiment(experiment)
                            .name(variantRequest.getName())
                            .weight(variantRequest.getWeight())
                            .isControl(variantRequest.getIsControl())
                            .contentJson(serializeContent(variantRequest.getContent()))
                            .build();
                    variantRepository.save(newVariant);
                }
            }
        }

        return getExperimentById(id);
    }

    @Transactional
    public void deleteExperiment(String id) {
        ABExperiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Experiment not found: " + id));
        experimentRepository.delete(experiment);
    }

    @Transactional
    public ExperimentResponse startExperiment(String id) {
        ABExperiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Experiment not found: " + id));

        experiment.start();
        experimentRepository.save(experiment);

        return getExperimentById(id);
    }

    @Transactional
    public ExperimentResponse stopExperiment(String id) {
        ABExperiment experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Experiment not found: " + id));

        experiment.stop();
        experimentRepository.save(experiment);

        return getExperimentById(id);
    }

    public ExperimentStatsResponse getExperimentStats(String id) {
        ABExperiment experiment = experimentRepository.findByIdWithVariants(id)
                .orElseThrow(() -> new IllegalArgumentException("Experiment not found: " + id));

        long totalAssignments = assignmentRepository.countByExperimentId(id);

        List<ExperimentStatsResponse.VariantStatsDto> variantStats = new ArrayList<>();
        Double controlConversionRate = null;
        String winner = null;
        double highestConversionRate = 0;

        for (ABVariant variant : experiment.getVariants()) {
            long assignments = assignmentRepository.countByVariantId(variant.getId());
            long conversions = conversionRepository.countByVariantId(variant.getId());
            double conversionRate = assignments > 0 ? (double) conversions / assignments * 100 : 0;

            if (Boolean.TRUE.equals(variant.getIsControl())) {
                controlConversionRate = conversionRate;
            }

            if (conversionRate > highestConversionRate) {
                highestConversionRate = conversionRate;
                winner = variant.getName();
            }

            String uplift = null;
            if (controlConversionRate != null && controlConversionRate > 0 && !Boolean.TRUE.equals(variant.getIsControl())) {
                double upliftValue = ((conversionRate - controlConversionRate) / controlConversionRate) * 100;
                uplift = String.format("%+.1f%%", upliftValue);
            }

            variantStats.add(ExperimentStatsResponse.VariantStatsDto.builder()
                    .variantId(variant.getId())
                    .name(variant.getName())
                    .isControl(variant.getIsControl())
                    .assignments(assignments)
                    .conversions(conversions)
                    .conversionRate(conversionRate)
                    .uplift(uplift)
                    .build());
        }

        return ExperimentStatsResponse.builder()
                .experimentId(id)
                .experimentName(experiment.getName())
                .status(experiment.getStatus())
                .totalAssignments(totalAssignments)
                .variants(variantStats)
                .statisticalSignificance(calculateStatisticalSignificance(variantStats))
                .winner(winner)
                .build();
    }

    private Double calculateStatisticalSignificance(List<ExperimentStatsResponse.VariantStatsDto> variants) {
        if (variants.size() < 2) {
            return null;
        }

        long totalSamples = variants.stream()
                .mapToLong(ExperimentStatsResponse.VariantStatsDto::getAssignments)
                .sum();

        if (totalSamples < 100) {
            return null;
        }

        return Math.min(0.95, totalSamples / 10000.0);
    }

    private ExperimentResponse toExperimentResponse(ABExperiment experiment) {
        List<ABVariant> variants = variantRepository.findByExperimentId(experiment.getId());

        return ExperimentResponse.builder()
                .id(experiment.getId())
                .name(experiment.getName())
                .description(experiment.getDescription())
                .targetPage(experiment.getTargetPage())
                .targetElement(experiment.getTargetElement())
                .status(experiment.getStatus())
                .trafficPercentage(experiment.getTrafficPercentage())
                .startDate(experiment.getStartDate() != null ? experiment.getStartDate().format(ISO_FORMATTER) : null)
                .endDate(experiment.getEndDate() != null ? experiment.getEndDate().format(ISO_FORMATTER) : null)
                .createdAt(experiment.getCreatedAt() != null ? experiment.getCreatedAt().format(ISO_FORMATTER) : null)
                .updatedAt(experiment.getUpdatedAt() != null ? experiment.getUpdatedAt().format(ISO_FORMATTER) : null)
                .variants(variants.stream()
                        .map(this::toVariantDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private ExperimentResponse.VariantDto toVariantDto(ABVariant variant) {
        return ExperimentResponse.VariantDto.builder()
                .id(variant.getId())
                .name(variant.getName())
                .weight(variant.getWeight())
                .isControl(variant.getIsControl())
                .content(parseContentJson(variant.getContentJson()))
                .createdAt(variant.getCreatedAt() != null ? variant.getCreatedAt().format(ISO_FORMATTER) : null)
                .build();
    }

    private Map<String, Object> parseContentJson(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse content JSON", e);
            return new HashMap<>();
        }
    }

    private String serializeContent(Map<String, Object> content) {
        if (content == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize content", e);
            return "{}";
        }
    }

    /**
     * 페이지 파라미터를 정규화합니다.
     * 프론트엔드에서 'landing'으로 요청하는 경우를 '/'로 변환합니다.
     * 
     * @param page 원본 페이지 파라미터
     * @return 정규화된 페이지 경로
     */
    private String normalizePage(String page) {
        if (page == null || page.isEmpty()) {
            return "/";
        }
        
        // 'landing'을 '/'로 변환
        if ("landing".equalsIgnoreCase(page)) {
            return "/";
        }
        
        // 이미 '/'로 시작하면 그대로 반환
        if (page.startsWith("/")) {
            return page;
        }
        
        // 그 외의 경우 '/'를 앞에 추가
        return "/" + page;
    }
}
