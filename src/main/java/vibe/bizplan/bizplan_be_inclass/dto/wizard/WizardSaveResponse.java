package vibe.bizplan.bizplan_be_inclass.dto.wizard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Wizard 데이터 저장 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 5.1 - Wizard 데이터 저장
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Wizard 데이터 저장 응답")
public class WizardSaveResponse {

    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "현재 단계")
    private Integer currentStep;

    @Schema(description = "마지막 저장 시각")
    private LocalDateTime lastSavedAt;

    @Schema(description = "진행 상황")
    private ProgressInfo progress;

    @Schema(description = "검증 경고 목록")
    private List<ValidationWarning> validationWarnings;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressInfo {
        private Integer currentStep;
        private List<Integer> completedSteps;
        private Double percentComplete;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidationWarning {
        private String field;
        private String type;
        private String message;
    }
}

