package vibe.makersround.makersround_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import vibe.makersround.makersround_backend.util.ApiLogger;
import com.github.f4b6a3.ulid.UlidCreator;

import java.io.IOException;

/**
 * HTTP 요청/응답 로깅 필터
 * 
 * 모든 HTTP 요청과 응답을 로깅합니다.
 * 요청/응답 본문을 읽기 위해 ContentCachingRequestWrapper와 ContentCachingResponseWrapper를 사용합니다.
 */
@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private final ApiLogger apiLogger;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 정적 리소스나 특정 경로는 로깅 제외
        if (shouldSkipLogging(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 프론트엔드에서 전달된 Request ID를 우선 사용하고, 없으면 서버에서 새로 생성
        // ULID는 시간 정보를 포함하므로 정렬에 유리함
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = UlidCreator.getUlid().toString();
        }

        // Logback MDC에 requestId 저장 (모든 로그 패턴에서 %X{requestId}로 접근 가능)
        MDC.put("requestId", requestId);

        // 응답 헤더에도 동일한 Request ID를 포함하여 클라이언트와의 연계를 명확히 함
        response.setHeader("X-Request-ID", requestId);

        // 요청 본문을 읽기 위해 Wrapper 사용 (최대 본문 크기: 10KB)
        HttpServletRequest requestToUse = request;
        ContentCachingRequestWrapper wrappedRequest = null;
        if (!(request instanceof ContentCachingRequestWrapper)) {
            wrappedRequest = new ContentCachingRequestWrapper(request, 10240);
            requestToUse = wrappedRequest;
        } else {
            wrappedRequest = (ContentCachingRequestWrapper) request;
        }
        
        // 응답 본문을 읽기 위해 Wrapper 사용
        HttpServletResponse responseToUse = response;
        ContentCachingResponseWrapper wrappedResponse = null;
        if (!(response instanceof ContentCachingResponseWrapper)) {
            wrappedResponse = new ContentCachingResponseWrapper(response);
            responseToUse = wrappedResponse;
        } else {
            wrappedResponse = (ContentCachingResponseWrapper) response;
        }

        // 요청 시작 시간 기록
        long startTime = System.currentTimeMillis();
        
        try {
            // 요청 로깅
            apiLogger.logBackendRequest(requestToUse);

            // 필터 체인 실행
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            // 요청 처리 시간 계산
            long duration = System.currentTimeMillis() - startTime;
            
            // 응답 본문을 클라이언트에 복사 (Wrapper 사용 시 필요)
            if (wrappedResponse != null) {
                wrappedResponse.copyBodyToResponse();
            }
            
            // 응답 로깅
            apiLogger.logBackendResponse(requestToUse, responseToUse, duration);

            // 요청 처리 완료 후 MDC 정리 (스레드 로컬 컨텍스트 누수 방지)
            MDC.clear();
        }
    }

    /**
     * 로깅을 건너뛸 경로 확인
     */
    private boolean shouldSkipLogging(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 정적 리소스 제외
        if (path.startsWith("/static/") || 
            path.startsWith("/css/") || 
            path.startsWith("/js/") || 
            path.startsWith("/images/") ||
            path.startsWith("/favicon.ico") ||
            path.startsWith("/error")) {
            return true;
        }
        
        // Swagger UI 제외 (선택사항)
        if (path.startsWith("/swagger-ui/") || 
            path.startsWith("/v3/api-docs/") ||
            path.startsWith("/swagger-resources/")) {
            return true;
        }
        
        return false;
    }
}
