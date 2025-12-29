package vibe.bizplan.bizplan_be_inclass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 리소스 미발견 예외
 * 
 * 요청한 리소스를 찾을 수 없을 때 발생
 * HTTP 404 Not Found 응답
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

