package vibe.bizplan.bizplan_be_inclass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 생성 요청 DTO
 * 
 * Rule 301: Use jakarta.validation annotations
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 생성 요청")
public class CreateProjectRequest {
    
    /**
     * 사용할 템플릿 코드
     * 예: KSTARTUP_2025, BANK_LOAN_2025
     */
    @NotBlank(message = "templateCode는 필수 항목입니다.")
    @Schema(
            description = "사용할 템플릿 코드 (GET /api/v1/projects/templates 에서 조회 가능)",
            example = "KSTARTUP_2025",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String templateCode;
}

