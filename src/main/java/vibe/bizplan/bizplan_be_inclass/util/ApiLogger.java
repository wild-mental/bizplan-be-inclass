package vibe.bizplan.bizplan_be_inclass.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * API 요청/응답 로깅 유틸리티
 * 
 * 프론트엔드에서 받은 요청과 백엔드에서 보낸 응답을 로깅합니다.
 * 설정으로 온오프 제어가 가능하며, 파일과 콘솔 모두에 로깅합니다.
 */
@Component
@Slf4j
public class ApiLogger {

    private final ObjectMapper objectMapper;
    
    @Value("${app.logging.api.enabled:true}")
    private boolean loggingEnabled;
    
    @Value("${app.logging.api.log-to-file:true}")
    private boolean logToFile;
    
    @Value("${app.logging.api.log-to-console:true}")
    private boolean logToConsole;
    
    @Value("${app.logging.api.max-body-size:10000}")
    private int maxBodySize;

    public ApiLogger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 백엔드 요청 로깅
     * 
     * @param request - HttpServletRequest
     */
    public void logBackendRequest(HttpServletRequest request) {
        if (!loggingEnabled) {
            return;
        }

        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullUrl = queryString != null ? uri + "?" + queryString : uri;
            
            // 헤더 정보 수집
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
            
            // 요청 본문 읽기
            String requestBody = "";
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
                byte[] contentAsByteArray = wrappedRequest.getContentAsByteArray();
                if (contentAsByteArray.length > 0) {
                    String body = new String(contentAsByteArray, StandardCharsets.UTF_8);
                    requestBody = truncateBody(body);
                }
            }
            
            // 로그 메시지 생성
            String logMessage = buildRequestLogMessage(method, fullUrl, headers, requestBody);
            
            // 로깅
            if (logToFile) {
                log.info("[API_REQUEST] {}", logMessage);
            }
            if (logToConsole) {
                System.out.println("[API_REQUEST] " + logMessage);
            }
            
        } catch (Exception e) {
            log.error("Error logging backend request", e);
        }
    }

    /**
     * 백엔드 응답 로깅
     * 
     * @param request - HttpServletRequest
     * @param response - HttpServletResponse
     * @param duration - 요청 처리 시간 (ms)
     */
    public void logBackendResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        if (!loggingEnabled) {
            return;
        }

        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();
            
            // 응답 헤더 정보 수집
            Map<String, String> headers = new HashMap<>();
            Collection<String> headerNames = response.getHeaderNames();
            for (String headerName : headerNames) {
                headers.put(headerName, response.getHeader(headerName));
            }
            
            // 응답 본문 읽기
            String responseBody = "";
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrappedResponse = (ContentCachingResponseWrapper) response;
                byte[] contentAsByteArray = wrappedResponse.getContentAsByteArray();
                if (contentAsByteArray.length > 0) {
                    String body = new String(contentAsByteArray, StandardCharsets.UTF_8);
                    responseBody = truncateBody(body);
                }
            }
            
            // 로그 메시지 생성
            String logMessage = buildResponseLogMessage(method, uri, status, duration, headers, responseBody);
            
            // 로깅
            if (logToFile) {
                log.info("[API_RESPONSE] {}", logMessage);
            }
            if (logToConsole) {
                System.out.println("[API_RESPONSE] " + logMessage);
            }
            
        } catch (Exception e) {
            log.error("Error logging backend response", e);
        }
    }

    /**
     * 요청 로그 메시지 생성
     */
    private String buildRequestLogMessage(String method, String url, Map<String, String> headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(url);
        
        if (!headers.isEmpty()) {
            try {
                String headersJson = objectMapper.writeValueAsString(headers);
                sb.append(" | Headers: ").append(headersJson);
            } catch (Exception e) {
                sb.append(" | Headers: ").append(headers.toString());
            }
        }
        
        if (!body.isEmpty()) {
            sb.append(" | Body: ").append(body);
        }
        
        return sb.toString();
    }

    /**
     * 응답 로그 메시지 생성
     */
    private String buildResponseLogMessage(String method, String uri, int status, long duration, 
                                           Map<String, String> headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(uri);
        sb.append(" | Status: ").append(status);
        sb.append(" | Duration: ").append(duration).append("ms");
        
        if (!headers.isEmpty()) {
            try {
                String headersJson = objectMapper.writeValueAsString(headers);
                sb.append(" | Headers: ").append(headersJson);
            } catch (Exception e) {
                sb.append(" | Headers: ").append(headers.toString());
            }
        }
        
        if (!body.isEmpty()) {
            sb.append(" | Body: ").append(body);
        }
        
        return sb.toString();
    }

    /**
     * 본문 크기 제한
     */
    private String truncateBody(String body) {
        if (body == null) {
            return "";
        }
        if (body.length() > maxBodySize) {
            return body.substring(0, maxBodySize) + "... (truncated)";
        }
        return body;
    }
}
