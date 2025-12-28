package vibe.bizplan.bizplan_be_inclass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 프로젝트 수정 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 4 - 프로젝트 관리 API
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 수정 요청")
public class UpdateProjectRequest {

    @Size(max = 100, message = "프로젝트 이름은 100자 이내여야 합니다")
    @Schema(description = "프로젝트 이름", example = "LearnAI v2")
    private String name;

    @Schema(description = "프로젝트 상태 (draft, in_progress, completed, archived)", example = "in_progress")
    private String status;

    @Size(max = 500, message = "프로젝트 설명은 500자 이내여야 합니다")
    @Schema(description = "프로젝트 설명")
    private String description;
}

