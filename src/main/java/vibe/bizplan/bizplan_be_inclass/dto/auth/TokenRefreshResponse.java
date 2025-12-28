package vibe.bizplan.bizplan_be_inclass.dto.auth;

import lombok.*;

/**
 * 토큰 갱신 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.4 - 토큰 갱신
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefreshResponse {
    
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}

