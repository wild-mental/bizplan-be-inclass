package vibe.bizplan.bizplan_be_inclass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import vibe.bizplan.bizplan_be_inclass.entity.Project;

import java.time.LocalDateTime;

/**
 * 프로젝트 응답 DTO
 * 
 * Rule 301: Never expose Entity in Controller, always map to DTO
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Getter
@Builder
@Schema(description = "프로젝트 응답 정보")
public class ProjectResponse {
    
    /**
     * 프로젝트 고유 식별자 (UUID 문자열)
     */
    @Schema(description = "프로젝트 고유 식별자 (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private final String projectId;
    
    /**
     * 사용된 템플릿 코드
     */
    @Schema(description = "사용된 템플릿 코드", example = "KSTARTUP_2025")
    private final String templateCode;
    
    /**
     * 프로젝트 상태
     */
    @Schema(description = "프로젝트 상태", example = "DRAFT", allowableValues = {"DRAFT", "IN_PROGRESS", "SUBMITTED", "COMPLETED"})
    private final String status;
    
    /**
     * 생성 시각
     */
    @Schema(description = "프로젝트 생성 시각", example = "2025-12-17T15:30:00")
    private final LocalDateTime createdAt;

    /**
     * Entity -> DTO 변환
     * 
     * @param project 프로젝트 엔티티
     * @return 프로젝트 응답 DTO
     */
    public static ProjectResponse fromEntity(Project project) {
        return ProjectResponse.builder()
                .projectId(project.getId().toString())
                .templateCode(project.getTemplateCode())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .build();
    }
}

