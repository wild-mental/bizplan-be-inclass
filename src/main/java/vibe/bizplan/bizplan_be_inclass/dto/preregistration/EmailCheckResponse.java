package vibe.bizplan.bizplan_be_inclass.dto.preregistration;

import lombok.*;

/**
 * 이메일 중복 체크 응답 DTO
 * 
 * Rule 304: Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCheckResponse {
    
    /**
     * 이메일 존재 여부
     */
    private Boolean exists;
    
    /**
     * 이미 등록된 경우 할인 코드 반환
     */
    private String discountCode;
}

