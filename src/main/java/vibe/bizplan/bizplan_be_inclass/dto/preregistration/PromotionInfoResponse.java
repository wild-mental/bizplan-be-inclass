package vibe.bizplan.bizplan_be_inclass.dto.preregistration;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 현재 프로모션 정보 응답 DTO
 * 
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
     * 현재 할인율 (%)
     */
    private Integer discountRate;
    
    /**
     * Phase A 종료일
     */
    private LocalDateTime phaseAEnd;
    
    /**
     * Phase B 종료일
     */
    private LocalDateTime phaseBEnd;
    
    /**
     * 요금제별 가격 정보
     */
    private Map<String, PriceInfo> prices;

    /**
     * 가격 정보 내부 DTO
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
        private Integer saved;
    }
}

