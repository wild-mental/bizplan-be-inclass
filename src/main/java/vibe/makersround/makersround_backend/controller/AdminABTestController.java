package vibe.makersround.makersround_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.makersround.makersround_backend.dto.ApiResponse;
import vibe.makersround.makersround_backend.dto.ab.request.ExperimentCreateRequest;
import vibe.makersround.makersround_backend.dto.ab.request.ExperimentUpdateRequest;
import vibe.makersround.makersround_backend.dto.ab.response.ExperimentResponse;
import vibe.makersround.makersround_backend.dto.ab.response.ExperimentStatsResponse;
import vibe.makersround.makersround_backend.dto.ab.response.FunnelAnalysisResponse;
import vibe.makersround.makersround_backend.service.ABTestService;
import vibe.makersround.makersround_backend.service.AnalyticsLogParserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/ab-tests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin A/B Test", description = "A/B 테스트 관리 API - 실험 CRUD 및 통계")
public class AdminABTestController {

    private final ABTestService abTestService;
    private final AnalyticsLogParserService analyticsLogParserService;

    @Operation(
            summary = "전체 실험 목록 조회",
            description = "등록된 모든 A/B 테스트 실험 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperimentResponse>>> getAllExperiments() {
        log.debug("A/B 테스트 전체 목록 조회");
        List<ExperimentResponse> experiments = abTestService.getAllExperiments();
        return ResponseEntity.ok(ApiResponse.success(experiments));
    }

    @Operation(
            summary = "실험 상세 조회",
            description = "특정 A/B 테스트 실험의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperimentResponse>> getExperiment(
            @Parameter(description = "실험 ID", required = true)
            @PathVariable String id
    ) {
        log.debug("A/B 테스트 상세 조회 - id: {}", id);
        ExperimentResponse experiment = abTestService.getExperimentById(id);
        return ResponseEntity.ok(ApiResponse.success(experiment));
    }

    @Operation(
            summary = "새 실험 생성",
            description = "새로운 A/B 테스트 실험을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ExperimentResponse>> createExperiment(
            @Valid @RequestBody ExperimentCreateRequest request
    ) {
        log.debug("A/B 테스트 생성 - name: {}", request.getName());
        ExperimentResponse experiment = abTestService.createExperiment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(experiment));
    }

    @Operation(
            summary = "실험 수정",
            description = "기존 A/B 테스트 실험의 정보를 수정합니다."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperimentResponse>> updateExperiment(
            @Parameter(description = "실험 ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody ExperimentUpdateRequest request
    ) {
        log.debug("A/B 테스트 수정 - id: {}", id);
        ExperimentResponse experiment = abTestService.updateExperiment(id, request);
        return ResponseEntity.ok(ApiResponse.success(experiment));
    }

    @Operation(
            summary = "실험 삭제",
            description = "A/B 테스트 실험을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExperiment(
            @Parameter(description = "실험 ID", required = true)
            @PathVariable String id
    ) {
        log.debug("A/B 테스트 삭제 - id: {}", id);
        abTestService.deleteExperiment(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "실험 시작",
            description = "A/B 테스트 실험을 시작합니다. 상태가 'running'으로 변경됩니다."
    )
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<ExperimentResponse>> startExperiment(
            @Parameter(description = "실험 ID", required = true)
            @PathVariable String id
    ) {
        log.debug("A/B 테스트 시작 - id: {}", id);
        ExperimentResponse experiment = abTestService.startExperiment(id);
        return ResponseEntity.ok(ApiResponse.success(experiment));
    }

    @Operation(
            summary = "실험 중지",
            description = "A/B 테스트 실험을 중지합니다. 상태가 'paused'로 변경됩니다."
    )
    @PostMapping("/{id}/stop")
    public ResponseEntity<ApiResponse<ExperimentResponse>> stopExperiment(
            @Parameter(description = "실험 ID", required = true)
            @PathVariable String id
    ) {
        log.debug("A/B 테스트 중지 - id: {}", id);
        ExperimentResponse experiment = abTestService.stopExperiment(id);
        return ResponseEntity.ok(ApiResponse.success(experiment));
    }

    @Operation(
            summary = "실험 통계 조회",
            description = "A/B 테스트 실험의 통계 정보를 조회합니다. 변형별 할당 수, 전환 수, 전환율 등을 포함합니다."
    )
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<ExperimentStatsResponse>> getExperimentStats(
            @Parameter(description = "실험 ID", required = true)
            @PathVariable String id
    ) {
        log.debug("A/B 테스트 통계 조회 - id: {}", id);
        ExperimentStatsResponse stats = abTestService.getExperimentStats(id);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(
            summary = "퍼널 분석 조회",
            description = "A/B 테스트 실험의 퍼널 분석을 조회합니다. 단계별 전환율과 변형별 비교를 제공합니다."
    )
    @GetMapping("/analytics/funnel")
    public ResponseEntity<ApiResponse<FunnelAnalysisResponse>> getFunnelAnalysis(
            @Parameter(description = "실험 ID", required = true)
            @RequestParam String experimentId,
            @Parameter(description = "시작 날짜 (ISO 형식: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료 날짜 (ISO 형식: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.debug("퍼널 분석 조회 - experimentId: {}, startDate: {}, endDate: {}", experimentId, startDate, endDate);
        FunnelAnalysisResponse response = analyticsLogParserService.analyzeFunnel(experimentId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
