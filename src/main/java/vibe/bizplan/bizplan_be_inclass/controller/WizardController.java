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
import vibe.bizplan.bizplan_be_inclass.dto.wizard.*;
import vibe.bizplan.bizplan_be_inclass.service.WizardService;

import java.util.UUID;

/**
 * Wizard 컨트롤러
 * 사업계획서 작성 Wizard API를 제공합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 5 - 사업계획서 작성 Wizard API
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/wizard")
@RequiredArgsConstructor
@Tag(name = "Wizard", description = "사업계획서 작성 Wizard API")
public class WizardController {

    private final WizardService wizardService;

    /**
     * Wizard 데이터 저장 (자동 저장)
     * 
     * PUT /api/v1/projects/{projectId}/wizard
     */
    @Operation(
            summary = "Wizard 데이터 저장",
            description = "현재 단계의 Wizard 데이터를 저장합니다. 자동 저장에 사용됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WizardSaveResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @PutMapping
    public ResponseEntity<ApiResponse<WizardSaveResponse>> saveWizardData(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "Wizard 저장 요청", required = true)
            @Valid @RequestBody WizardSaveRequest request) {

        WizardSaveResponse response = wizardService.saveWizardData(projectId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Wizard 전체 데이터 조회
     * 
     * GET /api/v1/projects/{projectId}/wizard
     */
    @Operation(
            summary = "Wizard 전체 데이터 조회",
            description = "프로젝트의 모든 Wizard 단계 데이터를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WizardDataResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<WizardDataResponse>> getWizardData(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId) {

        WizardDataResponse response = wizardService.getWizardData(projectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 자금 집행계획 검증
     * 
     * POST /api/v1/projects/{projectId}/budget/validate
     */
    @Operation(
            summary = "자금 집행계획 검증",
            description = "예비창업패키지 또는 초기창업패키지의 자금 집행계획을 검증합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "검증 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BudgetValidateResponse.class))
            )
    })
    @PostMapping("/budget/validate")
    public ResponseEntity<ApiResponse<BudgetValidateResponse>> validateBudget(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "자금 검증 요청", required = true)
            @Valid @RequestBody BudgetValidateRequest request) {

        BudgetValidateResponse response = wizardService.validateBudget(projectId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

