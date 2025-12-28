package vibe.bizplan.bizplan_be_inclass.dto.evaluation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI 평가 결과 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 6.3 - 평가 결과 조회
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 평가 결과 응답")
public class EvaluationResultResponse {

    @Schema(description = "평가 ID")
    private String evaluationId;

    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "평가 완료 시각")
    private LocalDateTime completedAt;

    @Schema(description = "요약 정보")
    private Summary summary;

    @Schema(description = "영역별 점수")
    private Map<String, ScoreInfo> scores;

    @Schema(description = "강점 목록")
    private List<FeedbackItem> strengths;

    @Schema(description = "약점 목록")
    private List<FeedbackItem> weaknesses;

    @Schema(description = "개선 권장사항")
    private List<Recommendation> recommendations;

    @Schema(description = "접근 레벨", example = "demo")
    private String accessLevel;

    @Schema(description = "업그레이드 안내")
    private UpgradePrompt upgradePrompt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Integer totalScore;
        private String grade;  // A, B, C, D, F
        private Integer passRate;
        private String passRateMessage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreInfo {
        private Integer score;
        private String label;
        private String letter;
        private String color;
        private Integer maxScore;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeedbackItem {
        private String area;
        private String title;
        private String description;
        private Boolean isBlurred;  // 유료 전환 유도용
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Recommendation {
        private Integer priority;
        private String area;
        private String title;
        private String description;
        private Boolean isBlurred;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpgradePrompt {
        private String message;
        private List<String> availablePlans;
    }
}

