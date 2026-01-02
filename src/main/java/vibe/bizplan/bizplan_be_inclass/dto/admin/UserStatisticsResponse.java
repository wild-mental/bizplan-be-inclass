package vibe.bizplan.bizplan_be_inclass.dto.admin;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 사용자 통계 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatisticsResponse {
    
    // 전체 통계
    private OverallStatistics overall;
    
    // 가입 시간별 통계 (일별, 주별, 월별)
    private List<TimeSeriesData> signupByDate;
    private List<TimeSeriesData> signupByWeek;
    private List<TimeSeriesData> signupByMonth;
    
    // 요금제별 통계
    private List<PlanStatistics> byPlan;
    
    // 소셜 로그인별 통계
    private List<ProviderStatistics> byProvider;
    
    // 사업 분야별 통계
    private List<CategoryStatistics> byCategory;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OverallStatistics {
        private Long totalUsers;
        private Long verifiedUsers;
        private Long unverifiedUsers;
        private Long marketingConsentUsers;
        private Long paidPlanUsers;
        private Long freePlanUsers;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeSeriesData {
        private String date; // YYYY-MM-DD, YYYY-WW, YYYY-MM 형식
        private Long count;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanStatistics {
        private String plan; // 기본, 플러스, 프로, 프리미엄
        private Long count;
        private Double percentage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProviderStatistics {
        private String provider; // local, google, kakao, naver
        private Long count;
        private Double percentage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryStatistics {
        private String category;
        private Long count;
        private Double percentage;
    }
}
