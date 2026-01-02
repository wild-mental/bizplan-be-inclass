package vibe.bizplan.bizplan_be_inclass.dto.admin;

import lombok.*;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 목록 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponse {
    
    private List<UserInfo> users;
    private PaginationInfo pagination;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private String id;
        private String email;
        private String name;
        private String phone;
        private String businessCategory;
        private String provider; // local, google, kakao, naver
        private Boolean emailVerified;
        private Boolean marketingConsent;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // 구독 정보
        private SubscriptionInfo subscription;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscriptionInfo {
        private String plan;
        private String planKey;
        private Integer originalPrice;
        private Integer discountedPrice;
        private Integer discountRate;
        private String promotionPhase;
        private String promotionCode;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {
        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }
}
