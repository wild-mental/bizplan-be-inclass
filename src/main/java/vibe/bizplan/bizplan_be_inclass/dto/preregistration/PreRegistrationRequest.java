package vibe.bizplan.bizplan_be_inclass.dto.preregistration;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 사전 등록 요청 DTO
 * 
 * PRE-SUB-FUNC-002 명세서 준수
 * Rule 304: Request DTO validation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegistrationRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글 또는 영문만 입력 가능합니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX입니다")
    private String phone;

    @NotNull(message = "요금제 선택은 필수입니다")
    private PlanType plan;

    @Size(max = 50, message = "사업 분야는 50자 이내여야 합니다")
    private String businessCategory;

    @Builder.Default
    private Boolean marketingConsent = false;

    /**
     * 요금제 유형
     */
    public enum PlanType {
        plus, pro, premium
    }
}
