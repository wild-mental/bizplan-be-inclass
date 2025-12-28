package vibe.bizplan.bizplan_be_inclass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vibe.bizplan.bizplan_be_inclass.dto.auth.*;
import vibe.bizplan.bizplan_be_inclass.exception.AuthenticationException;
import vibe.bizplan.bizplan_be_inclass.exception.DuplicateEmailException;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.security.CustomUserDetailsService;
import vibe.bizplan.bizplan_be_inclass.service.AuthService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup - 성공")
    void signup_success() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트 사용자")
                .plan("프로")
                .termsAgreed(true)
                .privacyAgreed(true)
                .build();

        SignupResponse response = SignupResponse.builder()
                .user(SignupResponse.UserInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .email("test@example.com")
                        .name("테스트 사용자")
                        .plan("프로")
                        .planStartDate(LocalDateTime.now())
                        .planEndDate(LocalDateTime.now().plusMonths(6))
                        .createdAt(LocalDateTime.now())
                        .build())
                .subscription(SignupResponse.SubscriptionInfo.builder()
                        .planKey("pro")
                        .originalPrice(799000)
                        .discountedPrice(559300)
                        .discountRate(30)
                        .build())
                .tokens(SignupResponse.TokenInfo.builder()
                        .accessToken("access-token")
                        .refreshToken("refresh-token")
                        .expiresIn(3600L)
                        .build())
                .build();

        given(authService.signup(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup - 이메일 중복")
    void signup_duplicateEmail() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트 사용자")
                .plan("프로")
                .termsAgreed(true)
                .privacyAgreed(true)
                .build();

        given(authService.signup(any())).willThrow(new DuplicateEmailException("test@example.com"));

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - 성공")
    void login_success() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        LoginResponse response = LoginResponse.builder()
                .user(LoginResponse.UserInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .email("test@example.com")
                        .name("테스트 사용자")
                        .plan("프로")
                        .build())
                .tokens(LoginResponse.TokenInfo.builder()
                        .accessToken("access-token")
                        .refreshToken("refresh-token")
                        .expiresIn(3600L)
                        .build())
                .build();

        given(authService.login(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tokens.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - 인증 실패")
    void login_authenticationFailed() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("wrong-password")
                .build();

        given(authService.login(any())).willThrow(
                new AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다"));

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

