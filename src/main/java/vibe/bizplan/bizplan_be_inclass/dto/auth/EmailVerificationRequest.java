package vibe.bizplan.bizplan_be_inclass.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 이메일 인증 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationRequest {

    @NotBlank(message = "인증 토큰은 필수입니다")
    private String token;
}

