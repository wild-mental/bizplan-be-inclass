package vibe.bizplan.bizplan_be_inclass.exception;

/**
 * 인증 관련 예외
 * 로그인 실패, 잘못된 토큰 등의 경우 발생
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

