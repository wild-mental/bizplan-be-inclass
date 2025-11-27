package vibe.bizplan.bizplan_be_inclass.dto;

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
public class ProjectResponse {
    
    /**
     * 프로젝트 고유 식별자 (UUID 문자열)
     */
    private final String projectId;
    
    /**
     * 사용된 템플릿 코드
     */
    private final String templateCode;
    
    /**
     * 프로젝트 상태
     */
    private final String status;
    
    /**
     * 생성 시각
     */
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

