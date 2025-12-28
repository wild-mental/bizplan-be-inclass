package vibe.bizplan.bizplan_be_inclass.dto.evaluation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * AI 평가 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 6.1 - 평가 요청
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 평가 요청")
public class EvaluationRequest {

    @NotBlank(message = "프로젝트 ID는 필수입니다")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "평가 유형", example = "full")
    @Builder.Default
    private String evaluationType = "demo";

    @NotNull(message = "입력 데이터는 필수입니다")
    @Schema(description = "평가 입력 데이터")
    private InputData inputData;

    @Schema(description = "평가 옵션")
    private EvaluationOptions options;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InputData {
        private String businessName;
        private String businessField;
        private String targetMarket;
        private String problemStatement;
        private String solutionSummary;
        private List<String> differentiators;
        private String teamExperience;
        private Long fundingGoal;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EvaluationOptions {
        @Builder.Default
        private Boolean includeDetailedFeedback = true;
        @Builder.Default
        private String language = "ko";
    }
}

