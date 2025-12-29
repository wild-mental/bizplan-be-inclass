package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.evaluation.*;
import vibe.bizplan.bizplan_be_inclass.service.EvaluationService;

import java.util.UUID;

/**
 * AI 평가 컨트롤러
 * 사업계획서 AI 평가 API를 제공합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 6 - AI 평가 API
 */
@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluations", description = "AI 평가 API")
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * 평가 요청
     * 
     * POST /api/v1/evaluations
     */
    @Operation(
            summary = "AI 평가 요청",
            description = "사업계획서에 대한 AI 평가를 요청합니다. 비동기로 처리되며 상태 조회 API로 진행 상황을 확인할 수 있습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "202",
                    description = "평가 요청 접수됨",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EvaluationStatusResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationStatusResponse>> createEvaluation(
            @Parameter(description = "평가 요청 정보", required = true)
            @Valid @RequestBody EvaluationRequest request) {

        EvaluationStatusResponse response = evaluationService.createEvaluation(request);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(response));
    }

    /**
     * 평가 상태 조회
     * 
     * GET /api/v1/evaluations/{evaluationId}/status
     */
    @Operation(
            summary = "평가 진행 상태 조회",
            description = "진행 중인 평가의 상태와 진행률을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상태 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EvaluationStatusResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "평가를 찾을 수 없음"
            )
    })
    @GetMapping("/{evaluationId}/status")
    public ResponseEntity<ApiResponse<EvaluationStatusResponse>> getEvaluationStatus(
            @Parameter(description = "평가 ID", required = true)
            @PathVariable UUID evaluationId) {

        EvaluationStatusResponse response = evaluationService.getEvaluationStatus(evaluationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 평가 결과 조회
     * 
     * GET /api/v1/evaluations/{evaluationId}/result
     */
    @Operation(
            summary = "평가 결과 조회",
            description = "완료된 평가의 상세 결과를 조회합니다. 6대 영역 점수, 강점/약점, 개선 권장사항이 포함됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "결과 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EvaluationResultResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "평가를 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "평가가 아직 완료되지 않음"
            )
    })
    @GetMapping("/{evaluationId}/result")
    public ResponseEntity<ApiResponse<EvaluationResultResponse>> getEvaluationResult(
            @Parameter(description = "평가 ID", required = true)
            @PathVariable UUID evaluationId) {

        EvaluationResultResponse response = evaluationService.getEvaluationResult(evaluationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

