package vibe.bizplan.bizplan_be_inclass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 표준 API 응답 래퍼
 * 모든 API 응답은 이 형식을 따릅니다.
 * 
 * Rule 304: Standard Envelope Pattern
 * POC-FUNC-001 스펙 준수:
 * {
 *   "success": true/false,
 *   "data": { ... } or null,
 *   "error": { "code": "...", "message": "..." } or null
 * }
 * 
 * 주의: error 필드는 성공 시에도 null로 명시적으로 포함됨 (POC-FUNC-001 스펙)
 * 
 * @param <T> 응답 데이터 타입
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 * @see <a href="tasks/POC-FUNC-001.md">POC-FUNC-001 태스크 문서</a>
 */
@Getter
@AllArgsConstructor
@Schema(description = "표준 API 응답 래퍼")
public class ApiResponse<T> {
    
    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;
    
    @Schema(description = "응답 데이터 (성공 시 데이터, 실패 시 null)")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private final T data;
    
    @Schema(description = "에러 정보 (성공 시 null, 실패 시 에러 상세)")
    @JsonInclude(JsonInclude.Include.ALWAYS)
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
    @Schema(description = "에러 상세 정보")
    public static class ErrorInfo {
        @Schema(description = "에러 코드", example = "INVALID_TEMPLATE")
        private final String code;
        
        @Schema(description = "에러 메시지", example = "유효하지 않은 템플릿 코드입니다.")
        private final String message;
    }
}

