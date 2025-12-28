package vibe.bizplan.bizplan_be_inclass.dto.preregistration;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 사전 등록 응답 DTO
 * 
 * PRE-SUB-FUNC-002 명세서 준수
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
    private String registrationId;
    
    /**
     * 선택한 요금제
     */
    private String plan;
    
    /**
     * 적용된 프로모션 Phase ("A" or "B")
     */
    private String promotionPhase;
    
    /**
     * 적용된 할인율 (%)
     */
    private Integer discountRate;
    
    /**
     * 발급된 할인 코드
     */
    private String discountCode;
    
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
    private Integer savings;
    
    /**
     * 할인 코드 만료일
     */
    private LocalDateTime expiresAt;
    
    /**
     * 등록 일시
     */
    private LocalDateTime createdAt;
}
