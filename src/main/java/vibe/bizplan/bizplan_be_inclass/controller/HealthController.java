package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;

import java.time.Instant;

/**
 * 헬스체크 API Controller
 * 
 * 배포 완료 후 서비스 상태를 확인하기 위한 엔드포인트를 제공합니다.
 * 
 * @see REQ-NF-012-OPS-001.md (운영 요구사항)
 */
@RestController
@RequestMapping("/api/v1/health")
@Slf4j
@Tag(name = "Health", description = "헬스체크 API - 서비스 상태 확인")
public class HealthController {

    @Value("${spring.application.name:bizplan-be-inclass}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 헬스체크 엔드포인트
     * 
     * 배포 완료 후 서비스가 정상적으로 동작하는지 확인합니다.
     * 
     * @return 서비스 상태 정보
     */
    @Operation(
            summary = "헬스체크",
            description = """
                    서비스의 현재 상태를 확인합니다.
                    
                    **응답 정보:**
                    - `status`: 서비스 상태 (UP/DOWN)
                    - `timestamp`: 현재 시간 (ISO 8601)
                    - `application`: 애플리케이션 이름
                    - `version`: 애플리케이션 버전
                    - `environment`: 실행 환경 (local/dev/prod)
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "서비스 정상 동작 중"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<HealthResponse>> health() {
        log.debug("헬스체크 요청 수신");
        
        HealthResponse response = HealthResponse.builder()
                .status("UP")
                .timestamp(Instant.now().toString())
                .application(applicationName)
                .version("0.0.1-SNAPSHOT")
                .environment(getEnvironment())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 간단한 헬스체크 엔드포인트 (루트 경로)
     * 
     * 로드밸런서나 모니터링 도구에서 사용하기 위한 간단한 엔드포인트
     * 
     * @return 간단한 상태 정보
     */
    @Operation(
            summary = "간단한 헬스체크",
            description = "로드밸런서나 모니터링 도구를 위한 간단한 헬스체크 엔드포인트"
    )
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<SimpleHealthResponse>> ping() {
        return ResponseEntity.ok(ApiResponse.success(
                new SimpleHealthResponse("OK", Instant.now().toString())
        ));
    }

    /**
     * 실행 환경 확인
     */
    private String getEnvironment() {
        String activeProfile = System.getProperty("spring.profiles.active");
        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        return activeProfile != null && !activeProfile.isEmpty() ? activeProfile : "local";
    }

    /**
     * 헬스체크 응답 DTO
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class HealthResponse {
        private final String status;
        private final String timestamp;
        private final String application;
        private final String version;
        private final String environment;
    }

    /**
     * 간단한 헬스체크 응답 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class SimpleHealthResponse {
        private final String status;
        private final String timestamp;
    }
}

