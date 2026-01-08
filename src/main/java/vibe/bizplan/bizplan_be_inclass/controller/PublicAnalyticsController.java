package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.analytics.*;
import vibe.bizplan.bizplan_be_inclass.service.LogAnalyticsService;

import java.time.LocalDate;

/**
 * 공개 분석 API 컨트롤러
 * 인증 없이 접근 가능한 서비스 이용 현황 분석 API
 * 
 * 모든 엔드포인트는 /api/v1/public/analytics 아래에 위치하며
 * SecurityConfig에서 permitAll()로 설정되어 인증 없이 접근 가능
 */
@RestController
@RequestMapping("/api/v1/public/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Analytics", description = "공개 분석 API - 인증 불필요")
public class PublicAnalyticsController {

    private final LogAnalyticsService logAnalyticsService;

    /**
     * 랜딩 페이지 시간대별 접속 추이
     * 
     * @param date 조회 날짜 (기본값: 오늘)
     * @return 시간대별 랜딩 페이지 접속 횟수
     */
    @Operation(
        summary = "랜딩 페이지 접속 추이",
        description = "시간대별 랜딩 페이지 접속 횟수를 조회합니다. 24시간 기준으로 데이터를 제공합니다."
    )
    @GetMapping("/landing/hourly")
    public ResponseEntity<ApiResponse<HourlyUsageResponse>> getLandingHourlyUsage(
        @Parameter(description = "조회 날짜 (기본값: 오늘)", example = "2026-01-07")
        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        log.info("랜딩 페이지 시간대별 접속 조회: {}", targetDate);
        
        HourlyUsageResponse response = logAnalyticsService.getLandingHourlyUsage(targetDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사업계획서 작성 데모 시간대별 이용 추이
     * 
     * @param date 조회 날짜 (기본값: 오늘)
     * @return 시간대별 사업계획서 작성 데모 이용 횟수
     */
    @Operation(
        summary = "사업계획서 작성 데모 이용 추이",
        description = "시간대별 사업계획서 작성 데모 이용 횟수를 조회합니다. /writing-demo, /wizard 페이지 접속 및 관련 API 호출을 집계합니다."
    )
    @GetMapping("/writing-demo/hourly")
    public ResponseEntity<ApiResponse<HourlyUsageResponse>> getWritingDemoHourlyUsage(
        @Parameter(description = "조회 날짜 (기본값: 오늘)", example = "2026-01-07")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        log.info("사업계획서 작성 데모 시간대별 이용 조회: {}", targetDate);
        
        HourlyUsageResponse response = logAnalyticsService.getWritingDemoHourlyUsage(targetDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * AI 평가 데모 시간대별 이용 추이
     * 
     * @param date 조회 날짜 (기본값: 오늘)
     * @return 시간대별 AI 평가 데모 이용 횟수
     */
    @Operation(
        summary = "AI 평가 데모 이용 추이",
        description = "시간대별 AI 평가 데모 이용 횟수를 조회합니다. /evaluation-demo 페이지 접속 및 평가 API 호출을 집계합니다."
    )
    @GetMapping("/evaluation-demo/hourly")
    public ResponseEntity<ApiResponse<HourlyUsageResponse>> getEvaluationDemoHourlyUsage(
        @Parameter(description = "조회 날짜 (기본값: 오늘)", example = "2026-01-07")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        log.info("AI 평가 데모 시간대별 이용 조회: {}", targetDate);
        
        HourlyUsageResponse response = logAnalyticsService.getEvaluationDemoHourlyUsage(targetDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 전체 서비스 이용 요약
     * 
     * @param startDate 시작 날짜 (기본값: 7일 전)
     * @param endDate 종료 날짜 (기본값: 오늘)
     * @return 서비스 이용 요약 통계
     */
    @Operation(
        summary = "서비스 이용 요약",
        description = "전체 서비스 이용 현황 요약 통계를 조회합니다. 랜딩 페이지 조회, 작성 데모, 평가 데모 이용 수를 합산하여 제공합니다."
    )
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<UsageSummaryResponse>> getUsageSummary(
        @Parameter(description = "시작 날짜 (기본값: 7일 전)", example = "2026-01-01")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "종료 날짜 (기본값: 오늘)", example = "2026-01-07")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        log.info("서비스 이용 요약 조회: {} ~ {}", start, end);
        
        UsageSummaryResponse response = logAnalyticsService.getUsageSummary(start, end);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 일별 이용 추이
     * 
     * @param startDate 시작 날짜 (기본값: 7일 전)
     * @param endDate 종료 날짜 (기본값: 오늘)
     * @return 일별 서비스 이용 횟수
     */
    @Operation(
        summary = "일별 이용 추이",
        description = "일별 서비스 이용 횟수를 조회합니다. 기간 내 각 날짜별로 랜딩 페이지, 작성 데모, 평가 데모 이용 수를 제공합니다."
    )
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyUsageResponse>> getDailyUsage(
        @Parameter(description = "시작 날짜 (기본값: 7일 전)", example = "2026-01-01")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "종료 날짜 (기본값: 오늘)", example = "2026-01-07")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        log.info("일별 이용 추이 조회: {} ~ {}", start, end);
        
        DailyUsageResponse response = logAnalyticsService.getDailyUsage(start, end);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
