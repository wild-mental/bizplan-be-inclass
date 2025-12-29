package vibe.bizplan.bizplan_be_inclass.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 소셜 로그인 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.2 - 소셜 로그인
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginRequest {

    @NotBlank(message = "액세스 토큰은 필수입니다")
    private String accessToken;

    @NotNull(message = "요금제 선택은 필수입니다")
    private String plan;

    @AssertTrue(message = "이용약관에 동의해야 합니다")
    private Boolean termsAgreed;

    @AssertTrue(message = "개인정보처리방침에 동의해야 합니다")
    private Boolean privacyAgreed;

    @Builder.Default
    private Boolean marketingConsent = false;
}

