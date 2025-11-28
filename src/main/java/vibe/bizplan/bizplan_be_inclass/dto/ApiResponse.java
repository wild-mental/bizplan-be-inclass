package vibe.bizplan.bizplan_be_inclass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 표준 API 응답 래퍼
 * 모든 API 응답은 이 형식을 따릅니다.
 * 
 * Rule 304: Standard Envelope Pattern
 * {
 *   "success": true/false,
 *   "data": { ... },
 *   "error": { "code": "...", "message": "..." }
 * }
 * 
 * @param <T> 응답 데이터 타입
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private final boolean success;
    private final T data;
    private final ErrorInfo error;

    /**
     * 성공 응답 생성
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 에러 응답 생성
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param <T> 데이터 타입
     * @return 에러 응답 객체
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message));
    }

    /**
     * 에러 정보 클래스
     */
    @Getter
    @AllArgsConstructor
    public static class ErrorInfo {
        private final String code;
        private final String message;
    }
}

