package vibe.bizplan.bizplan_be_inclass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 프로젝트 생성 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 4.1 - 프로젝트 생성
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 생성 요청")
public class CreateProjectRequest {
    
    /**
     * 프로젝트 이름
     */
    @Size(max = 100, message = "프로젝트 이름은 100자 이내여야 합니다")
    @Schema(description = "프로젝트 이름", example = "LearnAI")
    private String name;

    /**
     * 사용할 템플릿 ID
     */
    @NotBlank(message = "templateId는 필수 항목입니다.")
    @Schema(
            description = "사용할 템플릿 ID (pre-startup, early-startup, policy-fund)",
            example = "pre-startup",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String templateId;

    /**
     * 지원 프로그램
     */
    @Schema(description = "지원 프로그램", example = "2026-1")
    private String supportProgram;

    /**
     * 프로젝트 설명
     */
    @Size(max = 500, message = "프로젝트 설명은 500자 이내여야 합니다")
    @Schema(description = "프로젝트 설명", example = "AI 기반 맞춤형 학습 플랫폼")
    private String description;

    // 기존 호환성을 위해 templateCode getter 추가
    public String getTemplateCode() {
        return templateId;
    }
}

