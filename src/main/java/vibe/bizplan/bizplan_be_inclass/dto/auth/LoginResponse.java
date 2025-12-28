package vibe.bizplan.bizplan_be_inclass.dto.auth;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 로그인 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.3 - 로그인
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private UserInfo user;
    private TokenInfo tokens;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private String id;
        private String email;
        private String name;
        private String plan;
        private LocalDateTime planEndDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
    }
}

