package vibe.bizplan.bizplan_be_inclass.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 토큰 갱신 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.4 - 토큰 갱신
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefreshRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다")
    private String refreshToken;
}

