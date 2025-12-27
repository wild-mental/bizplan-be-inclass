package vibe.bizplan.bizplan_be_inclass.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Gemini API 호출 중 발생하는 예외
 * 
 * API 키 누락/만료, 토큰 한도 초과, 모델 응답 실패 등의 상황을 처리합니다.
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues">GitHub Issues</a>
 */
@Getter
public class GeminiGenerationException extends RuntimeException {
    
    /**
     * HTTP 상태 코드
     */
    private final HttpStatus httpStatus;
    
    /**
     * 에러 코드
     */
    private final String errorCode;
    
    /**
     * 사업계획서 ID (생성 시도한 ID)
     */
    private final String businessPlanId;
    
    /**
     * 재시도 횟수
     */
    private final int retryCount;
    
    /**
     * 원본 예외 (있는 경우)
     */
    private final Throwable cause;
    
    /**
     * API 키 누락/만료 예외 생성
     * 
     * @param businessPlanId 사업계획서 ID
     * @param message 에러 메시지
     */
    public GeminiGenerationException(String businessPlanId, String message) {
        this(businessPlanId, message, HttpStatus.UNAUTHORIZED, "GEMINI_UNAUTHORIZED", 0, null);
    }
    
    /**
     * 토큰 한도 초과 예외 생성
     * 
     * @param businessPlanId 사업계획서 ID
     * @param message 에러 메시지
     * @param retryCount 재시도 횟수
     */
    public GeminiGenerationException(String businessPlanId, String message, int retryCount) {
        this(businessPlanId, message, HttpStatus.TOO_MANY_REQUESTS, "GEMINI_RATE_LIMIT", retryCount, null);
    }
    
    /**
     * 모델 응답 실패 예외 생성
     * 
     * @param businessPlanId 사업계획서 ID
     * @param message 에러 메시지
     * @param cause 원본 예외
     */
    public GeminiGenerationException(String businessPlanId, String message, Throwable cause) {
        this(businessPlanId, message, HttpStatus.INTERNAL_SERVER_ERROR, "GEMINI_GENERATION_FAILED", 0, cause);
    }
    
    /**
     * 전체 생성자
     * 
     * @param businessPlanId 사업계획서 ID
     * @param message 에러 메시지
     * @param httpStatus HTTP 상태 코드
     * @param errorCode 에러 코드
     * @param retryCount 재시도 횟수
     * @param cause 원본 예외
     */
    public GeminiGenerationException(
            String businessPlanId,
            String message,
            HttpStatus httpStatus,
            String errorCode,
            int retryCount,
            Throwable cause
    ) {
        super(message, cause);
        this.businessPlanId = businessPlanId;
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.retryCount = retryCount;
        this.cause = cause;
    }
}
