package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.admin.UserListResponse;
import vibe.bizplan.bizplan_be_inclass.dto.admin.UserStatisticsResponse;
import vibe.bizplan.bizplan_be_inclass.service.AdminService;

/**
 * 어드민 컨트롤러
 * 사용자 목록 조회 및 통계 제공
 * 
 * 어드민 권한: 환경 변수 ADMIN_EMAILS에 등록된 이메일만 접근 가능
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "어드민 API - 사용자 관리 및 통계")
public class AdminController {

    private final AdminService adminService;

    @Value("${app.admin.emails:admin@makersround.world}")
    private String adminEmails;

    /**
     * 어드민 권한 확인
     */
    private boolean isAdmin(UserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        String email = userDetails.getUsername();
        String[] allowedEmails = adminEmails.split(",");
        for (String allowedEmail : allowedEmails) {
            if (email.trim().equalsIgnoreCase(allowedEmail.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 사용자 목록 조회
     * 
     * GET /api/v1/admin/users
     */
    @Operation(
            summary = "사용자 목록 조회",
            description = "필터링, 정렬, 페이징을 지원하는 사용자 목록을 조회합니다. 어드민 권한 필요."
    )
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<UserListResponse>> getUserList(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 필드 (createdAt, email, name)", example = "createdAt")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "정렬 방향 (ASC, DESC)", example = "DESC")
            @RequestParam(required = false) String sortDirection,
            @Parameter(description = "요금제 필터 (기본, 플러스, 프로, 프리미엄)")
            @RequestParam(required = false) String planFilter,
            @Parameter(description = "소셜 로그인 제공자 필터 (local, google, kakao, naver)")
            @RequestParam(required = false) String providerFilter,
            @Parameter(description = "이메일 인증 필터")
            @RequestParam(required = false) Boolean emailVerifiedFilter,
            @Parameter(description = "검색 키워드 (이메일, 이름)")
            @RequestParam(required = false) String searchKeyword
    ) {
        // 어드민 권한 확인
        if (!isAdmin(userDetails)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("FORBIDDEN", "어드민 권한이 필요합니다"));
        }

        UserListResponse response = adminService.getUserList(
                page, size, sortBy, sortDirection,
                planFilter, providerFilter, emailVerifiedFilter, searchKeyword
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자 통계 조회
     * 
     * GET /api/v1/admin/statistics
     */
    @Operation(
            summary = "사용자 통계 조회",
            description = "가입 시간별, 요금제별, 소셜 로그인별 통계를 조회합니다. 어드민 권한 필요."
    )
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<UserStatisticsResponse>> getUserStatistics(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 어드민 권한 확인
        if (!isAdmin(userDetails)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("FORBIDDEN", "어드민 권한이 필요합니다"));
        }

        UserStatisticsResponse response = adminService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
