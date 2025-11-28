package vibe.bizplan.bizplan_be_inclass.exception;

/**
 * 유효하지 않은 템플릿 코드 예외
 * 지원하지 않는 템플릿 코드가 요청되었을 때 발생합니다.
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
public class InvalidTemplateException extends RuntimeException {
    
    /**
     * 예외 생성자
     * 
     * @param templateCode 유효하지 않은 템플릿 코드
     */
    public InvalidTemplateException(String templateCode) {
        super("지원하지 않는 템플릿 코드입니다: " + templateCode);
    }
}

