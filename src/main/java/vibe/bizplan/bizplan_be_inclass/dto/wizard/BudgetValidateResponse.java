package vibe.bizplan.bizplan_be_inclass.dto.wizard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 자금 집행계획 검증 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 5.3 - 자금 집행계획 검증
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "자금 집행계획 검증 응답")
public class BudgetValidateResponse {

    @Schema(description = "유효성 여부")
    private Boolean isValid;

    @Schema(description = "예산 요약")
    private BudgetSummary summary;

    @Schema(description = "검증 결과 목록")
    private List<ValidationResult> validations;

    @Schema(description = "경고 목록")
    private List<Warning> warnings;

    @Schema(description = "개선 권장사항")
    private List<Recommendation> recommendations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BudgetSummary {
        private Long totalBudget;
        private Long phase1Total;
        private Long phase2Total;
        private Long phase1Remaining;
        private Long phase2Remaining;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidationResult {
        private String rule;
        private Boolean passed;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Warning {
        private String type;
        private String field;
        private String message;
        private String suggestion;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Recommendation {
        private String type;
        private String message;
    }
}

