package vibe.bizplan.bizplan_be_inclass.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 회원가입 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.1 - 회원가입
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일을 입력해주세요")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$", 
             message = "비밀번호는 영문과 숫자를 포함해야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    private String name;

    @NotNull(message = "요금제 선택은 필수입니다")
    private String plan;

    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
    private String phone;

    private String businessCategory;

    @AssertTrue(message = "이용약관에 동의해야 합니다")
    private Boolean termsAgreed;

    @AssertTrue(message = "개인정보처리방침에 동의해야 합니다")
    private Boolean privacyAgreed;

    @Builder.Default
    private Boolean marketingConsent = false;

    private String promotionCode;
}

