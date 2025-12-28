package vibe.bizplan.bizplan_be_inclass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 프로모션 종료 예외
 * 
 * 프로모션이 종료된 상태에서 사전 등록 시도 시 발생
 * HTTP 410 Gone 응답
 */
@ResponseStatus(HttpStatus.GONE)
public class PromotionEndedException extends RuntimeException {
    
    public PromotionEndedException(String message) {
        super(message);
    }
}

