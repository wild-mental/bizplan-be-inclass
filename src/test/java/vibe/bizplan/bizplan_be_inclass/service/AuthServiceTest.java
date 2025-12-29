package vibe.bizplan.bizplan_be_inclass.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vibe.bizplan.bizplan_be_inclass.repository.UserRepository;
import org.springframework.test.util.ReflectionTestUtils;
import vibe.bizplan.bizplan_be_inclass.dto.auth.*;
import vibe.bizplan.bizplan_be_inclass.entity.*;
import vibe.bizplan.bizplan_be_inclass.exception.AuthenticationException;
import vibe.bizplan.bizplan_be_inclass.exception.DuplicateEmailException;
import vibe.bizplan.bizplan_be_inclass.exception.InvalidTokenException;
import vibe.bizplan.bizplan_be_inclass.repository.*;
import vibe.bizplan.bizplan_be_inclass.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PreRegistrationRepository preRegistrationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트 사용자")
                .plan("프로")
                .phone("010-1234-5678")
                .termsAgreed(true)
                .privacyAgreed(true)
                .marketingConsent(false)
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("$2a$10$hashed")
                .name("테스트 사용자")
                .provider(User.AuthProvider.local)
                .emailVerified(false)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
            ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
            return user;
        });
        when(jwtTokenProvider.createAccessToken(any(), anyString())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenValidityInSeconds()).thenReturn(3600L);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getTokens().getAccessToken()).isEqualTo("access-token");
        verify(userRepository).save(any(User.class));
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_duplicateEmail() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(DuplicateEmailException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(subscriptionRepository.findByUserAndStatus(any(), any()))
                .thenReturn(Optional.empty());
        when(jwtTokenProvider.createAccessToken(any(), anyString())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenValidityInSeconds()).thenReturn(3600L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getTokens().getAccessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_userNotFound() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_invalidPassword() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_success() {
        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .user(testUser)
                .token("old-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(jwtTokenProvider.createAccessToken(any(), anyString())).thenReturn("new-access-token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getAccessTokenValidityInSeconds()).thenReturn(3600L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("old-refresh-token")
                .build();

        // when
        TokenRefreshResponse response = authService.refreshToken(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        // refreshToken 메서드는 기존 토큰 폐기(1번) + 새 토큰 생성(1번) = 총 2번 save 호출
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 토큰 없음")
    void refreshToken_tokenNotFound() {
        // given
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("invalid-token")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("유효하지 않은 리프레시 토큰입니다");
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 만료된 토큰")
    void refreshToken_expiredToken() {
        // given
        RefreshToken expiredToken = RefreshToken.builder()
                .user(testUser)
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(expiredToken));

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("expired-token")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("만료되었거나 폐기된 토큰입니다");
    }
}

