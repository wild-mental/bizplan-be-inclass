package vibe.bizplan.bizplan_be_inclass.dto.preregistration;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 현재 프로모션 정보 응답 DTO
 * 
 * PRE-SUB-FUNC-002 명세서 준수
 * Rule 304: Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionInfoResponse {

    /**
     * 프로모션 활성 상태
     */
    private Boolean isActive;
    
    /**
     * 현재 Phase ("A", "B", "ENDED", "NOT_STARTED")
     */
    private String currentPhase;
    
    /**
     * Phase 상세 정보 목록
     */
    private List<PhaseInfo> phases;
    
    /**
     * 카운트다운 정보
     */
    private CountdownInfo countdown;
    
    /**
     * 요금제별 가격 정보
     */
    private Map<String, PriceInfo> pricing;

    /**
     * Phase 상세 정보 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhaseInfo {
        /**
         * Phase 코드 ("A" or "B")
         */
        private String phase;
        
        /**
         * Phase 이름
         */
        private String name;
        
        /**
         * 할인율 (%)
         */
        private Integer discountRate;
        
        /**
         * 시작일
         */
        private LocalDateTime startDate;
        
        /**
         * 종료일
         */
        private LocalDateTime endDate;
        
        /**
         * 현재 Phase 여부
         */
        private Boolean isCurrentPhase;
    }

    /**
     * 카운트다운 정보 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CountdownInfo {
        /**
         * 카운트다운 대상 일시
         */
        private LocalDateTime targetDate;
        
        /**
         * 남은 일수
         */
        private Long remainingDays;
        
        /**
         * 남은 시간
         */
        private Long remainingHours;
        
        /**
         * 남은 분
         */
        private Long remainingMinutes;
        
        /**
         * 남은 초
         */
        private Long remainingSeconds;
    }

    /**
     * 가격 정보 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PriceInfo {
        /**
         * 정가
         */
        private Integer original;
        
        /**
         * 할인가
         */
        private Integer discounted;
        
        /**
         * 절약 금액
         */
        private Integer savings;
    }
}
