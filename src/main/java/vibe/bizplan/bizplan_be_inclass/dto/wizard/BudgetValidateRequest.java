package vibe.bizplan.bizplan_be_inclass.dto.wizard;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * 자금 집행계획 검증 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 5.3 - 자금 집행계획 검증
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "자금 집행계획 검증 요청")
public class BudgetValidateRequest {

    @NotBlank(message = "템플릿 타입은 필수입니다")
    @Schema(description = "템플릿 타입", example = "pre-startup")
    private String templateType;

    @Schema(description = "예비창업패키지 단계별 예산")
    private List<BudgetPhase> budgetPhases;

    @Schema(description = "초기창업패키지 매칭펀드")
    private MatchingFund matchingFund;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BudgetPhase {
        private Integer phase;
        private Long maxAmount;
        private List<BudgetItem> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BudgetItem {
        private String id;
        private String name;
        private Long amount;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatchingFund {
        private Long totalBudget;
        private Long governmentFund;
        private Long selfCash;
        private Long selfInKind;
        private List<BudgetItem> items;
    }
}

