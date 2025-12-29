package vibe.bizplan.bizplan_be_inclass.dto.wizard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Wizard 전체 데이터 조회 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 5.2 - Wizard 전체 데이터 조회
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Wizard 전체 데이터 응답")
public class WizardDataResponse {

    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "템플릿 ID")
    private String templateId;

    @Schema(description = "현재 단계")
    private Integer currentStep;

    @Schema(description = "단계별 데이터 목록")
    private List<StepInfo> steps;

    @Schema(description = "마지막 저장 시각")
    private LocalDateTime lastSavedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StepInfo {
        private Integer stepId;
        private String title;
        private String status;  // pending, in_progress, completed
        private Map<String, Object> data;
    }
}

