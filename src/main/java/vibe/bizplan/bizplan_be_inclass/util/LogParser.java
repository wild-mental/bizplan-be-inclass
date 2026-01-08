package vibe.bizplan.bizplan_be_inclass.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * API 로그 파싱 유틸리티
 * api-requests.log 파일의 로그 라인을 파싱하여 구조화된 데이터로 변환
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LogParser {

    private final ObjectMapper objectMapper;

    /**
     * 로그 라인 패턴
     * 예: 01KE9NW1YSBXMQFV7A94FFEMX1 2026-01-06 19:52:52.021 INFO --- [...] ... : [API_REQUEST] POST /api/v1/projects | Headers: {...}
     */
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+)\\s+" +                                          // Request ID
        "(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+" +  // Timestamp
        "\\S+\\s+---\\s+\\[.*?\\]\\s+\\S+\\s+:\\s+" +             // Level, thread, logger
        "\\[API_REQUEST\\]\\s+" +                                 // Marker
        "(\\S+)\\s+" +                                            // HTTP Method
        "(\\S+)" +                                                 // API Path
        "(?:\\s+\\|\\s+Headers:\\s+(\\{.*\\}))?"                  // Headers (optional)
    );

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 로그 라인을 파싱하여 ParsedLogEntry 객체로 변환
     * 
     * @param logLine 로그 라인 문자열
     * @return 파싱된 로그 엔트리, 파싱 실패 시 null
     */
    public ParsedLogEntry parse(String logLine) {
        try {
            Matcher matcher = LOG_PATTERN.matcher(logLine);
            if (!matcher.find()) {
                return null;
            }

            String requestId = matcher.group(1);
            String timestampStr = matcher.group(2);
            String method = matcher.group(3);
            String apiPath = matcher.group(4);
            String headersJson = matcher.group(5);

            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, TIMESTAMP_FORMATTER);

            String referer = null;
            if (headersJson != null) {
                try {
                    Map<String, String> headers = objectMapper.readValue(
                        headersJson, 
                        new TypeReference<Map<String, String>>() {}
                    );
                    referer = headers.get("referer");
                } catch (Exception e) {
                    // Headers 파싱 실패는 무시
                    log.debug("Headers 파싱 실패: {}", e.getMessage());
                }
            }

            return ParsedLogEntry.builder()
                .requestId(requestId)
                .timestamp(timestamp)
                .method(method)
                .apiPath(apiPath)
                .referer(referer)
                .build();

        } catch (Exception e) {
            log.debug("로그 파싱 실패: {}", logLine);
            return null;
        }
    }

    /**
     * 파싱된 로그 엔트리 데이터 클래스
     */
    @Data
    @Builder
    public static class ParsedLogEntry {
        /** 요청 고유 ID (ULID) */
        private String requestId;
        
        /** 타임스탬프 */
        private LocalDateTime timestamp;
        
        /** HTTP 메서드 (GET, POST 등) */
        private String method;
        
        /** API 경로 */
        private String apiPath;
        
        /** Referer 헤더 값 */
        private String referer;
    }
}
