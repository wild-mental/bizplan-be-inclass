package vibe.bizplan.bizplan_be_inclass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import vibe.bizplan.bizplan_be_inclass.entity.Project;

import java.time.LocalDateTime;

/**
 * 프로젝트 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 4 - 프로젝트 관리 API
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 응답 정보")
public class ProjectResponse {
    
    @Schema(description = "프로젝트 고유 식별자 (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "프로젝트 이름", example = "LearnAI")
    private String name;
    
    @Schema(description = "템플릿 ID", example = "pre-startup")
    private String templateId;

    @Schema(description = "템플릿 이름", example = "예비창업패키지")
    private String templateName;

    @Schema(description = "지원 프로그램", example = "2026-1")
    private String supportProgram;

    @Schema(description = "프로젝트 설명")
    private String description;
    
    @Schema(description = "프로젝트 상태", example = "draft", 
            allowableValues = {"draft", "in_progress", "completed", "archived"})
    private String status;

    @Schema(description = "진행 상황")
    private ProgressInfo progress;
    
    @Schema(description = "프로젝트 생성 시각")
    private LocalDateTime createdAt;

    @Schema(description = "프로젝트 수정 시각")
    private LocalDateTime updatedAt;

    // 기존 호환성을 위한 getter
    public String getProjectId() {
        return id;
    }

    public String getTemplateCode() {
        return templateId;
    }

    /**
     * 진행 상황 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgressInfo {
        private Integer currentStep;
        private Integer totalSteps;
        private java.util.List<Integer> completedSteps;
        private Double percentComplete;
    }

    /**
     * Entity -> DTO 변환 (간단 버전)
     */
    public static ProjectResponse fromEntity(Project project) {
        return fromEntity(project, null);
    }

    /**
     * Entity -> DTO 변환 (템플릿 이름 포함)
     */
    public static ProjectResponse fromEntity(Project project, String templateName) {
        int totalSteps = 8; // 기본 8단계
        int currentStep = project.getCurrentStep() != null ? project.getCurrentStep() : 1;
        
        return ProjectResponse.builder()
                .id(project.getId().toString())
                .name(project.getName())
                .templateId(project.getTemplateCode())
                .templateName(templateName)
                .supportProgram(project.getSupportProgram())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .progress(ProgressInfo.builder()
                        .currentStep(currentStep)
                        .totalSteps(totalSteps)
                        .completedSteps(java.util.Collections.emptyList())
                        .percentComplete((double) (currentStep - 1) / totalSteps * 100)
                        .build())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}

