package vibe.bizplan.bizplan_be_inclass.dto.auth;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 회원가입 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.1 - 회원가입
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {

    private UserInfo user;
    private SubscriptionInfo subscription;
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
        private LocalDateTime planStartDate;
        private LocalDateTime planEndDate;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscriptionInfo {
        private String planKey;
        private Integer originalPrice;
        private Integer discountedPrice;
        private Integer discountRate;
        private String promotionPhase;
        private String promotionCode;
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

