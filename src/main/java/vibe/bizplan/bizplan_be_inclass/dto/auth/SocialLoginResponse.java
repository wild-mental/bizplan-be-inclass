package vibe.bizplan.bizplan_be_inclass.dto.auth;

import lombok.*;

/**
 * 소셜 로그인 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.2 - 소셜 로그인
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginResponse {

    private UserInfo user;
    private Boolean isNewUser;
    private SignupResponse.TokenInfo tokens;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private String id;
        private String email;
        private String name;
        private String provider;
        private String plan;
    }
}

