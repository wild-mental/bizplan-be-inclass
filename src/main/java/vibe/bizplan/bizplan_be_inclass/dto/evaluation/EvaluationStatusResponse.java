package vibe.bizplan.bizplan_be_inclass.dto.evaluation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * AI 평가 상태 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 6.2 - 평가 진행 상태 조회
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 평가 상태 응답")
public class EvaluationStatusResponse {

    @Schema(description = "평가 ID")
    private String evaluationId;

    @Schema(description = "평가 상태", example = "processing")
    private String status;

    @Schema(description = "진행률 (0-100)")
    private Integer progress;

    @Schema(description = "현재 진행 중인 단계")
    private String currentStage;

    @Schema(description = "단계별 상태")
    private List<StageInfo> stages;

    @Schema(description = "예상 남은 시간 (초)")
    private Integer estimatedRemaining;

    @Schema(description = "대기열 위치 (초기 요청 시)")
    private Integer queuePosition;

    @Schema(description = "예상 소요 시간 (초, 초기 요청 시)")
    private Integer estimatedTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StageInfo {
        private String id;
        private String name;
        private String status;  // pending, processing, completed
        private Integer score;
    }
}

