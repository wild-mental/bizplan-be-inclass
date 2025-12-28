package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.auth.*;
import vibe.bizplan.bizplan_be_inclass.security.CustomUserDetailsService;
import vibe.bizplan.bizplan_be_inclass.service.AuthService;

/**
 * 인증 컨트롤러
 * 회원가입, 로그인, 토큰 갱신 등 인증 관련 API를 제공합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 2 - 인증 API
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 API - 회원가입, 로그인, 토큰 관리")
public class AuthController {

    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 회원가입
     * 
     * POST /api/v1/auth/signup
     */
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자 계정을 생성합니다. 프로모션 코드가 있는 경우 할인이 적용됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SignupResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효성 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 이메일"
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Parameter(description = "회원가입 요청 정보", required = true)
            @Valid @RequestBody SignupRequest request) {
        
        SignupResponse response = authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * 로그인
     * 
     * POST /api/v1/auth/login
     */
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. 성공 시 JWT 토큰을 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Parameter(description = "로그인 요청 정보", required = true)
            @Valid @RequestBody LoginRequest request) {
        
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 소셜 로그인
     * 
     * POST /api/v1/auth/social/{provider}
     */
    @Operation(
            summary = "소셜 로그인",
            description = "Google, Kakao, Naver 등 소셜 계정으로 로그인합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "소셜 로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SocialLoginResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "지원하지 않는 소셜 로그인 제공자"
            )
    })
    @PostMapping("/social/{provider}")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialLogin(
            @Parameter(description = "소셜 로그인 제공자 (google, kakao, naver)", required = true)
            @PathVariable String provider,
            @Parameter(description = "소셜 로그인 요청 정보", required = true)
            @Valid @RequestBody SocialLoginRequest request) {
        
        SocialLoginResponse response = authService.socialLogin(provider, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 갱신
     * 
     * POST /api/v1/auth/refresh
     */
    @Operation(
            summary = "토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenRefreshResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 리프레시 토큰"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Parameter(description = "토큰 갱신 요청 정보", required = true)
            @Valid @RequestBody TokenRefreshRequest request) {
        
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 로그아웃
     * 
     * POST /api/v1/auth/logout
     */
    @Operation(
            summary = "로그아웃",
            description = "현재 사용자의 모든 리프레시 토큰을 폐기합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 필요"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var user = userDetailsService.getUserByEmail(userDetails.getUsername());
        authService.logout(user.getId().toString());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

