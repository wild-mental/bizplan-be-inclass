package vibe.bizplan.bizplan_be_inclass.dto;

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
public class CreateProjectRequest {
    
    /**
     * 사용할 템플릿 코드
     * 예: KSTARTUP_2025, BANK_LOAN_2025
     */
    @NotBlank(message = "templateCode는 필수 항목입니다.")
    private String templateCode;
}

