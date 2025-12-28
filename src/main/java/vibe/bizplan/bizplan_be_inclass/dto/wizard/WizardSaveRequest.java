package vibe.bizplan.bizplan_be_inclass.dto.wizard;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

/**
 * Wizard 데이터 저장 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 5.1 - Wizard 데이터 저장
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Wizard 데이터 저장 요청")
public class WizardSaveRequest {

    @NotNull(message = "현재 단계는 필수입니다")
    @Min(value = 1, message = "단계는 1 이상이어야 합니다")
    @Max(value = 8, message = "단계는 8 이하이어야 합니다")
    @Schema(description = "현재 단계 번호", example = "3")
    private Integer currentStep;

    @NotNull(message = "단계 데이터는 필수입니다")
    @Schema(description = "단계별 입력 데이터")
    private Map<String, Object> stepData;

    @Schema(description = "단계 완료 여부", example = "false")
    @Builder.Default
    private Boolean isStepComplete = false;
}

