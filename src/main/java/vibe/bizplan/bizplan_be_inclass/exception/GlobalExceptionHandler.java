package vibe.bizplan.bizplan_be_inclass.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;

/**
 * 전역 예외 처리기
 * 
 * Rule 301: Use global @RestControllerAdvice with standard error response
 * Rule 304: Consistent error response structure
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Slf4j
@RestControllerAdvice(basePackages = "vibe.bizplan.bizplan_be_inclass.controller")
public class GlobalExceptionHandler {

    /**
     * Validation 에러 처리 (400 Bad Request)
     * 
     * @param ex MethodArgumentNotValidException
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        // 첫 번째 필드 에러 메시지 추출
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("유효성 검사 실패");
        
        log.warn("Validation failed: {}", message);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_INPUT", message));
    }

    /**
     * 유효하지 않은 템플릿 예외 처리 (400 Bad Request)
     * 
     * @param ex InvalidTemplateException
     * @return 에러 응답
     */
    @ExceptionHandler(InvalidTemplateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTemplateException(
            InvalidTemplateException ex) {
        
        log.warn("Invalid template requested: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_TEMPLATE", ex.getMessage()));
    }

    /**
     * IllegalArgumentException 처리 (400 Bad Request)
     * 
     * @param ex IllegalArgumentException
     * @return 에러 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_ARGUMENT", ex.getMessage()));
    }

    /**
     * Gemini 생성 예외 처리
     * 
     * @param ex GeminiGenerationException
     * @return 에러 응답
     */
    @ExceptionHandler(GeminiGenerationException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeminiGenerationException(
            GeminiGenerationException ex) {
        
        log.error("Gemini generation failed: businessPlanId={}, errorCode={}, retryCount={}, message={}",
                ex.getBusinessPlanId(), ex.getErrorCode(), ex.getRetryCount(), ex.getMessage(), ex.getCause());
        
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * 이메일 중복 예외 처리 (409 Conflict)
     * 
     * @param ex DuplicateEmailException
     * @return 에러 응답
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmailException(
            DuplicateEmailException ex) {
        
        log.warn("Duplicate email: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("DUPLICATE_EMAIL", ex.getMessage()));
    }

    /**
     * 프로모션 종료 예외 처리 (410 Gone)
     * 
     * @param ex PromotionEndedException
     * @return 에러 응답
     */
    @ExceptionHandler(PromotionEndedException.class)
    public ResponseEntity<ApiResponse<Void>> handlePromotionEndedException(
            PromotionEndedException ex) {
        
        log.warn("Promotion ended: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(ApiResponse.error("PROMOTION_ENDED", ex.getMessage()));
    }

    /**
     * 리소스 미발견 예외 처리 (404 Not Found)
     * 
     * @param ex ResourceNotFoundException
     * @return 에러 응답
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("NOT_FOUND", ex.getMessage()));
    }

    /**
     * 기타 예외 처리 (500 Internal Server Error)
     * 
     * @param ex Exception
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        
        log.error("Unexpected error occurred", ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}

