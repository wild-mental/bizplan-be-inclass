package vibe.makersround.makersround_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vibe.makersround.makersround_backend.dto.analytics.*;
import vibe.makersround.makersround_backend.util.LogParser;
import vibe.makersround.makersround_backend.util.LogParser.ParsedLogEntry;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.*;

/**
 * 로그 분석 서비스
 * API 로그 파일을 파싱하여 서비스 이용 통계를 제공합니다.
 * 
 * 더미 데이터 모드: useDummyData = true 일 때 실제 로그 대신 더미 데이터 반환
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogAnalyticsService {

    private final LogParser logParser;

    @Value("${app.logging.log-dir:./logs}")
    private String logDir;

    /**
     * 더미 데이터 사용 여부
     * true: 더미 데이터 반환 (테스트용)
     * false: 실제 로그 파일 파싱
     */
    @Value("${app.analytics.use-dummy-data:true}")
    private boolean useDummyData;

    /**
     * 랜딩 페이지 시간대별 접속 추이
     */
    @Cacheable(value = "landingHourlyUsage", key = "#date.toString()")
    public HourlyUsageResponse getLandingHourlyUsage(LocalDate date) {
        if (useDummyData) {
            return generateDummyHourlyData(date, "랜딩 페이지", 50, 150);
        }
        
        List<ParsedLogEntry> entries = getLogEntriesForDate(date);
        List<ParsedLogEntry> landingEntries = entries.stream()
            .filter(this::isLandingPageRequest)
            .collect(Collectors.toList());

        return buildHourlyResponse(date, landingEntries, "랜딩 페이지");
    }

    /**
     * 사업계획서 작성 데모 시간대별 이용 추이
     */
    @Cacheable(value = "writingDemoHourlyUsage", key = "#date.toString()")
    public HourlyUsageResponse getWritingDemoHourlyUsage(LocalDate date) {
        if (useDummyData) {
            return generateDummyHourlyData(date, "사업계획서 작성 데모", 20, 80);
        }
        
        List<ParsedLogEntry> entries = getLogEntriesForDate(date);
        List<ParsedLogEntry> writingEntries = entries.stream()
            .filter(this::isWritingDemoRequest)
            .collect(Collectors.toList());

        return buildHourlyResponse(date, writingEntries, "사업계획서 작성 데모");
    }

    /**
     * AI 평가 데모 시간대별 이용 추이
     */
    @Cacheable(value = "evaluationDemoHourlyUsage", key = "#date.toString()")
    public HourlyUsageResponse getEvaluationDemoHourlyUsage(LocalDate date) {
        if (useDummyData) {
            return generateDummyHourlyData(date, "AI 평가 데모", 10, 50);
        }
        
        List<ParsedLogEntry> entries = getLogEntriesForDate(date);
        List<ParsedLogEntry> evaluationEntries = entries.stream()
            .filter(this::isEvaluationDemoRequest)
            .collect(Collectors.toList());

        return buildHourlyResponse(date, evaluationEntries, "AI 평가 데모");
    }

    /**
     * 서비스 이용 요약
     */
    @Cacheable(value = "usageSummary", key = "#startDate.toString() + '-' + #endDate.toString()")
    public UsageSummaryResponse getUsageSummary(LocalDate startDate, LocalDate endDate) {
        if (useDummyData) {
            return generateDummySummary(startDate, endDate);
        }
        
        List<ParsedLogEntry> allEntries = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            allEntries.addAll(getLogEntriesForDate(date));
        }

        long landingCount = allEntries.stream().filter(this::isLandingPageRequest).count();
        long writingCount = allEntries.stream().filter(this::isWritingDemoRequest).count();
        long evaluationCount = allEntries.stream().filter(this::isEvaluationDemoRequest).count();
        long totalRequests = allEntries.size();

        return UsageSummaryResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalRequests(totalRequests)
            .landingPageViews(landingCount)
            .writingDemoUsage(writingCount)
            .evaluationDemoUsage(evaluationCount)
            .uniqueRequestIds(allEntries.stream()
                .map(ParsedLogEntry::getRequestId)
                .distinct()
                .count())
            .build();
    }

    /**
     * 일별 이용 추이
     */
    @Cacheable(value = "dailyUsage", key = "#startDate.toString() + '-' + #endDate.toString()")
    public DailyUsageResponse getDailyUsage(LocalDate startDate, LocalDate endDate) {
        if (useDummyData) {
            return generateDummyDailyData(startDate, endDate);
        }
        
        List<DailyUsageResponse.DailyData> dailyDataList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<ParsedLogEntry> entries = getLogEntriesForDate(date);
            
            long landingCount = entries.stream().filter(this::isLandingPageRequest).count();
            long writingCount = entries.stream().filter(this::isWritingDemoRequest).count();
            long evaluationCount = entries.stream().filter(this::isEvaluationDemoRequest).count();

            dailyDataList.add(DailyUsageResponse.DailyData.builder()
                .date(date)
                .landingPageViews(landingCount)
                .writingDemoUsage(writingCount)
                .evaluationDemoUsage(evaluationCount)
                .totalRequests(entries.size())
                .build());
        }

        return DailyUsageResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .data(dailyDataList)
            .build();
    }

    // ============================================================
    // 더미 데이터 생성 메서드
    // ============================================================

    /**
     * 더미 시간대별 데이터 생성
     * 오전/오후에 따른 자연스러운 트래픽 패턴 시뮬레이션
     */
    private HourlyUsageResponse generateDummyHourlyData(LocalDate date, String category, int minCount, int maxCount) {
        Random random = ThreadLocalRandom.current();
        List<HourlyUsageResponse.HourlyData> hourlyDataList = new ArrayList<>();
        
        // 시간대별 가중치 (업무 시간에 높은 트래픽)
        double[] hourlyWeights = {
            0.1, 0.05, 0.03, 0.02, 0.02, 0.05,  // 00-05시: 낮은 트래픽
            0.1, 0.3, 0.6, 0.9, 1.0, 0.95,      // 06-11시: 증가
            0.8, 0.85, 1.0, 0.95, 0.9, 0.7,     // 12-17시: 높은 트래픽
            0.5, 0.4, 0.3, 0.25, 0.2, 0.15      // 18-23시: 감소
        };

        long totalCount = 0;
        int peakHour = 0;
        long peakCount = 0;

        for (int hour = 0; hour < 24; hour++) {
            // 가중치 기반 랜덤 값 생성
            double weight = hourlyWeights[hour];
            int baseRange = maxCount - minCount;
            long count = (long) (minCount + (baseRange * weight) + random.nextInt((int)(baseRange * 0.3)));
            
            // 약간의 랜덤성 추가
            count = Math.max(0, count + random.nextInt(11) - 5);
            
            totalCount += count;
            
            if (count > peakCount) {
                peakCount = count;
                peakHour = hour;
            }

            hourlyDataList.add(HourlyUsageResponse.HourlyData.builder()
                .hour(hour)
                .hourLabel(String.format("%02d:00", hour))
                .count(count)
                .build());
        }

        return HourlyUsageResponse.builder()
            .date(date)
            .category(category)
            .totalCount(totalCount)
            .uniqueRequests((long) (totalCount * 0.7)) // 약 70%가 고유 요청
            .peakHour(peakHour)
            .hourlyData(hourlyDataList)
            .build();
    }

    /**
     * 더미 일별 데이터 생성
     * 주말에 낮은 트래픽 패턴 시뮬레이션
     */
    private DailyUsageResponse generateDummyDailyData(LocalDate startDate, LocalDate endDate) {
        Random random = ThreadLocalRandom.current();
        List<DailyUsageResponse.DailyData> dailyDataList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 주말은 트래픽 감소
            double weekendFactor = (date.getDayOfWeek().getValue() >= 6) ? 0.4 : 1.0;
            
            long landingViews = (long) ((100 + random.nextInt(100)) * weekendFactor);
            long writingUsage = (long) ((40 + random.nextInt(50)) * weekendFactor);
            long evaluationUsage = (long) ((20 + random.nextInt(30)) * weekendFactor);

            dailyDataList.add(DailyUsageResponse.DailyData.builder()
                .date(date)
                .landingPageViews(landingViews)
                .writingDemoUsage(writingUsage)
                .evaluationDemoUsage(evaluationUsage)
                .totalRequests(landingViews + writingUsage + evaluationUsage + random.nextInt(50))
                .build());
        }

        return DailyUsageResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .data(dailyDataList)
            .build();
    }

    /**
     * 더미 요약 데이터 생성
     */
    private UsageSummaryResponse generateDummySummary(LocalDate startDate, LocalDate endDate) {
        Random random = ThreadLocalRandom.current();
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        long landingViews = days * (120 + random.nextInt(50));
        long writingUsage = days * (50 + random.nextInt(30));
        long evaluationUsage = days * (25 + random.nextInt(20));
        long totalRequests = landingViews + writingUsage + evaluationUsage + days * random.nextInt(30);

        return UsageSummaryResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalRequests(totalRequests)
            .landingPageViews(landingViews)
            .writingDemoUsage(writingUsage)
            .evaluationDemoUsage(evaluationUsage)
            .uniqueRequestIds((long) (totalRequests * 0.6))
            .build();
    }

    // ============================================================
    // 실제 로그 파싱 메서드
    // ============================================================

    /**
     * 특정 날짜의 로그 파일 로드
     */
    private List<ParsedLogEntry> getLogEntriesForDate(LocalDate date) {
        List<ParsedLogEntry> entries = new ArrayList<>();
        
        try {
            // 오늘 날짜면 현재 로그 파일 읽기
            if (date.equals(LocalDate.now())) {
                Path currentLog = Paths.get(logDir, "api-requests.log");
                if (Files.exists(currentLog)) {
                    entries.addAll(parseLogFile(currentLog, date));
                }
            }
            
            // 아카이브된 로그 파일 검색 (api-requests.YYYY-MM-DD.N.log)
            String datePattern = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Path logDirPath = Paths.get(logDir);
            
            if (Files.exists(logDirPath)) {
                try (Stream<Path> paths = Files.list(logDirPath)) {
                    List<Path> archiveLogs = paths
                        .filter(p -> p.getFileName().toString().contains("api-requests." + datePattern))
                        .collect(Collectors.toList());
                    
                    for (Path logFile : archiveLogs) {
                        entries.addAll(parseLogFile(logFile, date));
                    }
                }
            }
        } catch (IOException e) {
            log.error("로그 파일 읽기 실패: {}", e.getMessage());
        }

        return entries;
    }

    /**
     * 로그 파일 파싱
     */
    private List<ParsedLogEntry> parseLogFile(Path logFile, LocalDate filterDate) throws IOException {
        List<ParsedLogEntry> entries = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(logFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // [API_REQUEST] 라인만 처리 (응답은 제외)
                if (line.contains("[API_REQUEST]")) {
                    ParsedLogEntry entry = logParser.parse(line);
                    if (entry != null && entry.getTimestamp().toLocalDate().equals(filterDate)) {
                        entries.add(entry);
                    }
                }
            }
        }

        return entries;
    }

    /**
     * 랜딩 페이지 요청 판별
     */
    private boolean isLandingPageRequest(ParsedLogEntry entry) {
        String referer = entry.getReferer();
        if (referer == null || referer.isEmpty()) {
            return false;
        }
        
        try {
            String path = extractPathFromReferer(referer);
            return path.equals("/") || path.startsWith("/#");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 작성 데모 요청 판별
     */
    private boolean isWritingDemoRequest(ParsedLogEntry entry) {
        String referer = entry.getReferer();
        String apiPath = entry.getApiPath();
        
        // Referer 기반 판별
        if (referer != null) {
            String path = extractPathFromReferer(referer);
            if (path.contains("/writing-demo") || path.contains("/wizard/")) {
                return true;
            }
        }
        
        // API 경로 기반 판별
        if (apiPath != null) {
            if (apiPath.equals("/api/v1/projects") && "POST".equals(entry.getMethod())) {
                return true;
            }
            if (apiPath.startsWith("/api/v1/wizard/")) {
                return true;
            }
            if (apiPath.startsWith("/api/v1/business-plan")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 평가 데모 요청 판별
     */
    private boolean isEvaluationDemoRequest(ParsedLogEntry entry) {
        String referer = entry.getReferer();
        String apiPath = entry.getApiPath();
        
        // Referer 기반 판별
        if (referer != null) {
            String path = extractPathFromReferer(referer);
            if (path.contains("/evaluation-demo")) {
                return true;
            }
        }
        
        // API 경로 기반 판별
        if (apiPath != null && apiPath.startsWith("/api/v1/evaluations")) {
            return true;
        }

        return false;
    }

    /**
     * Referer URL에서 경로 추출
     */
    private String extractPathFromReferer(String referer) {
        try {
            if (referer.contains("://")) {
                int pathStart = referer.indexOf("/", referer.indexOf("://") + 3);
                if (pathStart > 0) {
                    return referer.substring(pathStart);
                }
            }
            return referer;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 시간대별 응답 빌드
     */
    private HourlyUsageResponse buildHourlyResponse(LocalDate date, List<ParsedLogEntry> entries, String category) {
        // 시간대별 집계
        Map<Integer, Long> hourlyCount = entries.stream()
            .collect(Collectors.groupingBy(
                e -> e.getTimestamp().getHour(),
                Collectors.counting()
            ));

        // 0-23시 데이터 생성 (빈 시간대는 0으로 채움)
        List<HourlyUsageResponse.HourlyData> hourlyDataList = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            hourlyDataList.add(HourlyUsageResponse.HourlyData.builder()
                .hour(hour)
                .hourLabel(String.format("%02d:00", hour))
                .count(hourlyCount.getOrDefault(hour, 0L))
                .build());
        }

        // 통계 계산
        long totalCount = entries.size();
        long uniqueUsers = entries.stream()
            .map(ParsedLogEntry::getRequestId)
            .distinct()
            .count();
        int peakHour = hourlyCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(0);

        return HourlyUsageResponse.builder()
            .date(date)
            .category(category)
            .totalCount(totalCount)
            .uniqueRequests(uniqueUsers)
            .peakHour(peakHour)
            .hourlyData(hourlyDataList)
            .build();
    }
}
