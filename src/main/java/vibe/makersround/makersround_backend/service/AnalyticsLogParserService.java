package vibe.makersround.makersround_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vibe.makersround.makersround_backend.dto.ab.AnalyticsEventLog;
import vibe.makersround.makersround_backend.dto.ab.response.FunnelAnalysisResponse;
import vibe.makersround.makersround_backend.dto.ab.response.FunnelAnalysisResponse.FunnelStep;
import vibe.makersround.makersround_backend.dto.ab.response.FunnelAnalysisResponse.VariantFunnel;
import vibe.makersround.makersround_backend.repository.ab.ABExperimentRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsLogParserService {

    private final ObjectMapper objectMapper;
    private final ABExperimentRepository experimentRepository;

    @Value("${logging.file.path:logs}")
    private String logDirectory;

    private static final List<String> FUNNEL_STEPS = List.of(
            "exposure", "cta_click", "demo_start", "demo_finish", "signup"
    );

    private static final String ANALYTICS_LOG_PREFIX = "analytics-events";

    public FunnelAnalysisResponse analyzeFunnel(String experimentId, LocalDate startDate, LocalDate endDate) {
        log.debug("퍼널 분석 시작 - experimentId: {}, startDate: {}, endDate: {}", experimentId, startDate, endDate);

        List<AnalyticsEventLog> events = parseLogFiles(startDate, endDate)
                .filter(e -> e.getExperiment() != null && experimentId.equals(e.getExperiment().getId()))
                .toList();

        String experimentName = experimentRepository.findById(experimentId)
                .map(exp -> exp.getName())
                .orElse("Unknown");

        Set<String> allVisitors = events.stream()
                .filter(e -> e.getUser() != null && e.getUser().getVisitorId() != null)
                .map(e -> e.getUser().getVisitorId())
                .collect(Collectors.toSet());

        List<FunnelStep> steps = buildFunnelSteps(events, allVisitors.size());

        Map<String, List<AnalyticsEventLog>> eventsByVariant = events.stream()
                .filter(e -> e.getExperiment() != null && e.getExperiment().getVariantId() != null)
                .collect(Collectors.groupingBy(e -> e.getExperiment().getVariantId()));

        Map<String, VariantFunnel> byVariant = buildVariantFunnels(eventsByVariant);

        log.debug("퍼널 분석 완료 - 총 {} 이벤트, {} 방문자", events.size(), allVisitors.size());

        return FunnelAnalysisResponse.builder()
                .experimentId(experimentId)
                .experimentName(experimentName)
                .startDate(startDate)
                .endDate(endDate)
                .totalVisitors(allVisitors.size())
                .steps(steps)
                .byVariant(byVariant)
                .build();
    }

    private List<FunnelStep> buildFunnelSteps(List<AnalyticsEventLog> events, int totalVisitors) {
        List<FunnelStep> steps = new ArrayList<>();
        long previousCount = totalVisitors;

        for (String stepName : FUNNEL_STEPS) {
            long count = countUniqueVisitorsForStep(events, stepName);

            double rate = totalVisitors > 0 ? (count * 100.0) / totalVisitors : 0;
            double conversionRate = previousCount > 0 ? (count * 100.0) / previousCount : 0;

            steps.add(FunnelStep.builder()
                    .name(stepName)
                    .count(count)
                    .rate(Math.round(rate * 100.0) / 100.0)
                    .conversionRate(Math.round(conversionRate * 100.0) / 100.0)
                    .build());

            previousCount = count;
        }

        return steps;
    }

    private long countUniqueVisitorsForStep(List<AnalyticsEventLog> events, String stepName) {
        return events.stream()
                .filter(e -> e.getEvent() != null && stepName.equals(e.getEvent().getType()))
                .filter(e -> e.getUser() != null && e.getUser().getVisitorId() != null)
                .map(e -> e.getUser().getVisitorId())
                .distinct()
                .count();
    }

    private Map<String, VariantFunnel> buildVariantFunnels(Map<String, List<AnalyticsEventLog>> eventsByVariant) {
        Map<String, VariantFunnel> result = new LinkedHashMap<>();

        for (Map.Entry<String, List<AnalyticsEventLog>> entry : eventsByVariant.entrySet()) {
            String variantId = entry.getKey();
            List<AnalyticsEventLog> variantEvents = entry.getValue();

            String variantName = variantEvents.stream()
                    .filter(e -> e.getExperiment() != null && e.getExperiment().getVariantName() != null)
                    .map(e -> e.getExperiment().getVariantName())
                    .findFirst()
                    .orElse("Unknown");

            Set<String> variantVisitors = variantEvents.stream()
                    .filter(e -> e.getUser() != null && e.getUser().getVisitorId() != null)
                    .map(e -> e.getUser().getVisitorId())
                    .collect(Collectors.toSet());

            List<FunnelStep> steps = buildFunnelSteps(variantEvents, variantVisitors.size());

            result.put(variantId, VariantFunnel.builder()
                    .variantId(variantId)
                    .variantName(variantName)
                    .totalVisitors(variantVisitors.size())
                    .steps(steps)
                    .build());
        }

        return result;
    }

    private Stream<AnalyticsEventLog> parseLogFiles(LocalDate startDate, LocalDate endDate) {
        List<Path> logFiles = findLogFiles(startDate, endDate);
        log.debug("파싱할 로그 파일: {}", logFiles);

        return logFiles.stream()
                .flatMap(this::parseLogFile)
                .filter(event -> isWithinDateRange(event, startDate, endDate));
    }

    private List<Path> findLogFiles(LocalDate startDate, LocalDate endDate) {
        List<Path> files = new ArrayList<>();
        Path logDir = Paths.get(logDirectory);

        if (!Files.exists(logDir)) {
            log.warn("로그 디렉토리가 존재하지 않음: {}", logDirectory);
            return files;
        }

        try (Stream<Path> paths = Files.list(logDir)) {
            files = paths
                    .filter(path -> path.getFileName().toString().startsWith(ANALYTICS_LOG_PREFIX))
                    .filter(path -> isLogFileInDateRange(path, startDate, endDate))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            log.error("로그 파일 목록 조회 실패", e);
        }

        return files;
    }

    private boolean isLogFileInDateRange(Path path, LocalDate startDate, LocalDate endDate) {
        String fileName = path.getFileName().toString();

        if (fileName.equals(ANALYTICS_LOG_PREFIX + ".log")) {
            return true;
        }

        try {
            String dateStr = extractDateFromFileName(fileName);
            if (dateStr != null) {
                LocalDate fileDate = LocalDate.parse(dateStr);
                return !fileDate.isBefore(startDate) && !fileDate.isAfter(endDate);
            }
        } catch (Exception e) {
            log.debug("파일명에서 날짜 추출 실패: {}", fileName);
        }

        return true;
    }

    private String extractDateFromFileName(String fileName) {
        int prefixLen = ANALYTICS_LOG_PREFIX.length() + 1;
        if (fileName.length() > prefixLen + 10) {
            return fileName.substring(prefixLen, prefixLen + 10);
        }
        return null;
    }

    private Stream<AnalyticsEventLog> parseLogFile(Path path) {
        try {
            BufferedReader reader = Files.newBufferedReader(path);
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .onClose(() -> {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            log.warn("로그 파일 리더 닫기 실패", e);
                        }
                    });
        } catch (IOException e) {
            log.error("로그 파일 읽기 실패: {}", path, e);
            return Stream.empty();
        }
    }

    private AnalyticsEventLog parseLine(String line) {
        try {
            return objectMapper.readValue(line, AnalyticsEventLog.class);
        } catch (Exception e) {
            log.trace("로그 라인 파싱 실패: {}", line.substring(0, Math.min(100, line.length())));
            return null;
        }
    }

    private boolean isWithinDateRange(AnalyticsEventLog event, LocalDate startDate, LocalDate endDate) {
        if (event.getLogMeta() == null || event.getLogMeta().getLoggedAt() == null) {
            return true;
        }

        try {
            String loggedAt = event.getLogMeta().getLoggedAt();
            LocalDateTime eventTime = LocalDateTime.parse(loggedAt.replace("Z", ""), 
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDate eventDate = eventTime.toLocalDate();

            return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
        } catch (Exception e) {
            log.trace("이벤트 날짜 파싱 실패: {}", event.getLogMeta().getLoggedAt());
            return true;
        }
    }

    public Map<String, Long> countEventsByType(String experimentId, LocalDate startDate, LocalDate endDate) {
        return parseLogFiles(startDate, endDate)
                .filter(e -> e.getExperiment() != null && experimentId.equals(e.getExperiment().getId()))
                .filter(e -> e.getEvent() != null && e.getEvent().getType() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getEvent().getType(),
                        Collectors.counting()
                ));
    }
}
