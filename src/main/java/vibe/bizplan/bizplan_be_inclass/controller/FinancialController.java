package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.financial.*;
import vibe.bizplan.bizplan_be_inclass.service.FinancialService;

import java.util.UUID;

/**
 * 재무 시뮬레이션 컨트롤러
 * 3년 재무 예측 및 손익분기점 분석 API를 제공합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 8 - 재무 시뮬레이션 API
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/financial")
@RequiredArgsConstructor
@Tag(name = "Financial", description = "재무 시뮬레이션 API")
public class FinancialController {

    private final FinancialService financialService;

    /**
     * 재무 시뮬레이션 계산
     * 
     * POST /api/v1/projects/{projectId}/financial/simulate
     */
    @Operation(
            summary = "재무 시뮬레이션 계산",
            description = "입력된 재무 데이터를 기반으로 3년 예측 재무제표와 손익분기점을 계산합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "시뮬레이션 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FinancialSimulateResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @PostMapping("/simulate")
    public ResponseEntity<ApiResponse<FinancialSimulateResponse>> simulate(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "시뮬레이션 요청", required = true)
            @Valid @RequestBody FinancialSimulateRequest request) {

        FinancialSimulateResponse response = financialService.simulate(projectId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

