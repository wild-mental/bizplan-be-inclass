package vibe.bizplan.bizplan_be_inclass.dto.preregistration;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 사전 등록 응답 DTO
 * 
 * Rule 304: Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegistrationResponse {

    /**
     * 등록 ID (UUID)
     */
    private String id;
    
    /**
     * 발급된 할인 코드
     */
    private String discountCode;
    
    /**
     * 적용된 할인율 (%)
     */
    private Integer discountRate;
    
    /**
     * 선택한 요금제
     */
    private String selectedPlan;
    
    /**
     * 정가
     */
    private Integer originalPrice;
    
    /**
     * 할인가
     */
    private Integer discountedPrice;
    
    /**
     * 절약 금액
     */
    private Integer savedAmount;
    
    /**
     * 등록 일시
     */
    private LocalDateTime registeredAt;
    
    /**
     * 등록 상태
     */
    private String status;
}

