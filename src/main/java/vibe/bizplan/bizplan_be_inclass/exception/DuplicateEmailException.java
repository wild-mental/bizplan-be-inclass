package vibe.bizplan.bizplan_be_inclass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 이메일 중복 예외
 * 
 * 이미 등록된 이메일로 사전 등록 시도 시 발생
 * HTTP 409 Conflict 응답
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException {
    
    public DuplicateEmailException(String message) {
        super(message);
    }
}

