package vibe.bizplan.bizplan_be_inclass.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.auth.*;
import vibe.bizplan.bizplan_be_inclass.entity.*;
import vibe.bizplan.bizplan_be_inclass.exception.*;
import vibe.bizplan.bizplan_be_inclass.repository.*;
import vibe.bizplan.bizplan_be_inclass.security.JwtTokenProvider;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/**
 * 인증 서비스
 * 회원가입, 로그인, 토큰 갱신 등 인증 관련 비즈니스 로직을 담당합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 2 - 인증 API
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PreRegistrationRepository preRegistrationRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    // 요금제별 가격 정보
    private static final Map<String, Integer> PLAN_PRICES = Map.of(
            "basic", 0,
            "plus", 399000,
            "pro", 799000,
            "premium", 1499000
    );

    private static final Map<String, String> PLAN_KEYS = Map.of(
            "기본", "basic",
            "플러스", "plus",
            "프로", "pro",
            "프리미엄", "premium"
    );

    /**
     * 회원가입
     * 
     * @param request 회원가입 요청
     * @return 회원가입 응답 (사용자 정보, 구독 정보, 토큰)
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.info("회원가입 시도: {}", request.getEmail());

        // 1. 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // 2. 프로모션 코드 유효성 검사 및 할인 정보 조회
        PreRegistration preRegistration = null;
        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            preRegistration = preRegistrationRepository.findByDiscountCode(request.getPromotionCode())
                    .orElse(null);
        }

        // 3. 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .businessCategory(request.getBusinessCategory())
                .marketingConsent(request.getMarketingConsent())
                .provider(User.AuthProvider.local)
                .emailVerified(false)
                .build();
        
        user = userRepository.save(user);

        // 4. 구독 생성
        String planKey = PLAN_KEYS.getOrDefault(request.getPlan(), "basic");
        int originalPrice = PLAN_PRICES.getOrDefault(planKey, 0);
        int discountRate = preRegistration != null ? preRegistration.getDiscountRate() : 0;
        int discountedPrice = originalPrice - (originalPrice * discountRate / 100);
        String promotionPhase = preRegistration != null ? preRegistration.getPromotionPhase() : "NONE";

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(6); // 6개월 구독

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(request.getPlan())
                .planKey(Subscription.PlanKey.valueOf(planKey))
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .discountRate(discountRate)
                .promotionPhase(promotionPhase)
                .promotionCode(request.getPromotionCode())
                .startDate(startDate)
                .endDate(endDate)
                .status(Subscription.SubscriptionStatus.active)
                .build();

        subscriptionRepository.save(subscription);

        // 5. 이메일 인증 토큰 생성 및 발송
        createAndSendVerificationToken(user);

        // 6. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = createAndSaveRefreshToken(user);

        // 6. 응답 생성
        return SignupResponse.builder()
                .user(SignupResponse.UserInfo.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .name(user.getName())
                        .plan(request.getPlan())
                        .planStartDate(startDate)
                        .planEndDate(endDate)
                        .createdAt(user.getCreatedAt())
                        .build())
                .subscription(SignupResponse.SubscriptionInfo.builder()
                        .planKey(planKey)
                        .originalPrice(originalPrice)
                        .discountedPrice(discountedPrice)
                        .discountRate(discountRate)
                        .promotionPhase(promotionPhase)
                        .promotionCode(request.getPromotionCode())
                        .build())
                .tokens(SignupResponse.TokenInfo.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                        .build())
                .build();
    }

    /**
     * 로그인
     * 
     * @param request 로그인 요청
     * @return 로그인 응답 (사용자 정보, 토큰)
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 시도: {}", request.getEmail());

        // 1. 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다"));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // 3. 활성 구독 조회
        Optional<Subscription> activeSubscription = subscriptionRepository
                .findByUserAndStatus(user, Subscription.SubscriptionStatus.active);

        // 4. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = createAndSaveRefreshToken(user);

        // 5. 응답 생성
        return LoginResponse.builder()
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .name(user.getName())
                        .plan(activeSubscription.map(Subscription::getPlan).orElse("기본"))
                        .planEndDate(activeSubscription.map(Subscription::getEndDate).orElse(null))
                        .build())
                .tokens(LoginResponse.TokenInfo.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                        .build())
                .build();
    }

    /**
     * 토큰 갱신
     * 
     * @param request 토큰 갱신 요청
     * @return 새로운 토큰 정보
     */
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        log.info("토큰 갱신 시도");

        // 1. 리프레시 토큰 조회 및 검증
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 리프레시 토큰입니다"));

        if (!storedToken.isValid()) {
            throw new InvalidTokenException("만료되었거나 폐기된 토큰입니다");
        }

        // 2. 기존 토큰 폐기
        storedToken.revoke();
        refreshTokenRepository.save(storedToken);

        // 3. 새 토큰 생성
        User user = storedToken.getUser();
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = createAndSaveRefreshToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .build();
    }

    /**
     * 로그아웃 (모든 리프레시 토큰 폐기)
     * 
     * @param userId 사용자 ID
     */
    @Transactional
    public void logout(String userId) {
        log.info("로그아웃: {}", userId);
        
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        refreshTokenRepository.revokeAllByUser(user);
    }

    /**
     * 소셜 로그인
     * 
     * @param provider 소셜 로그인 제공자 (google, kakao, naver)
     * @param request 소셜 로그인 요청
     * @return 소셜 로그인 응답
     */
    @Transactional
    public SocialLoginResponse socialLogin(String provider, SocialLoginRequest request) {
        log.info("소셜 로그인 시도: provider={}", provider);

        // TODO: 실제 소셜 로그인 구현 (OAuth2 토큰 검증)
        // 현재는 모의 구현으로 처리

        User.AuthProvider authProvider = User.AuthProvider.valueOf(provider);
        
        // 가상의 소셜 사용자 정보 (실제로는 각 제공자 API 호출 필요)
        String mockEmail = "social_" + System.currentTimeMillis() + "@" + provider + ".com";
        String mockProviderId = "social_id_" + System.currentTimeMillis();
        String mockName = provider.substring(0, 1).toUpperCase() + provider.substring(1) + " User";

        // 기존 사용자 조회 또는 신규 생성
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(authProvider, mockProviderId);
        boolean isNewUser = existingUser.isEmpty();

        User user;
        if (isNewUser) {
            user = User.builder()
                    .email(mockEmail)
                    .name(mockName)
                    .provider(authProvider)
                    .providerId(mockProviderId)
                    .marketingConsent(request.getMarketingConsent())
                    .emailVerified(true) // 소셜 로그인은 이메일 인증된 것으로 처리
                    .build();
            user = userRepository.save(user);

            // 구독 생성
            String planKey = PLAN_KEYS.getOrDefault(request.getPlan(), "basic");
            int originalPrice = PLAN_PRICES.getOrDefault(planKey, 0);

            Subscription subscription = Subscription.builder()
                    .user(user)
                    .plan(request.getPlan())
                    .planKey(Subscription.PlanKey.valueOf(planKey))
                    .originalPrice(originalPrice)
                    .discountedPrice(originalPrice)
                    .discountRate(0)
                    .promotionPhase("NONE")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusMonths(6))
                    .status(Subscription.SubscriptionStatus.active)
                    .build();
            subscriptionRepository.save(subscription);
        } else {
            user = existingUser.get();
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = createAndSaveRefreshToken(user);

        return SocialLoginResponse.builder()
                .user(SocialLoginResponse.UserInfo.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .name(user.getName())
                        .provider(provider)
                        .plan(request.getPlan())
                        .build())
                .isNewUser(isNewUser)
                .tokens(SignupResponse.TokenInfo.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                        .build())
                .build();
    }

    /**
     * 리프레시 토큰 생성 및 저장
     */
    private String createAndSaveRefreshToken(User user) {
        String token = jwtTokenProvider.createRefreshToken(user.getId());
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValidityInMs() / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    /**
     * 이메일 인증 토큰 생성 및 발송
     * 
     * @param user 사용자
     */
    @Transactional
    public void createAndSendVerificationToken(User user) {
        // 기존 미사용 토큰 무효화
        emailVerificationTokenRepository.invalidateAllByUser(user);

        // 새 토큰 생성
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24시간 유효

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        // 이메일 발송
        emailService.sendVerificationEmail(user.getEmail(), token, user.getName());
    }

    /**
     * 이메일 인증 확인
     * 
     * @param request 이메일 인증 요청
     */
    @Transactional
    public void verifyEmail(EmailVerificationRequest request) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 인증 토큰입니다"));

        if (!token.isValid()) {
            throw new InvalidTokenException("만료되었거나 이미 사용된 토큰입니다");
        }

        // 토큰 사용 처리
        token.markAsUsed();
        emailVerificationTokenRepository.save(token);

        // 사용자 이메일 인증 상태 업데이트
        User user = token.getUser();
        user.verifyEmail();
        userRepository.save(user);

        log.info("이메일 인증 완료: {}", user.getEmail());
    }

    /**
     * 이메일 인증 재발송
     * 
     * @param email 사용자 이메일
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        if (user.getEmailVerified()) {
            throw new BadRequestException("이미 인증된 이메일입니다");
        }

        createAndSendVerificationToken(user);
    }

    /**
     * 비밀번호 재설정 요청
     * 
     * @param request 비밀번호 재설정 요청
     */
    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("해당 이메일로 등록된 사용자를 찾을 수 없습니다"));

        // 기존 미사용 토큰 무효화
        passwordResetTokenRepository.invalidateAllByUser(user);

        // 새 토큰 생성
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // 1시간 유효

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // 이메일 발송
        emailService.sendPasswordResetEmail(user.getEmail(), token, user.getName());

        log.info("비밀번호 재설정 요청: {}", user.getEmail());
    }

    /**
     * 비밀번호 재설정 확인
     * 
     * @param request 비밀번호 재설정 확인 요청
     */
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 재설정 토큰입니다"));

        if (!token.isValid()) {
            throw new InvalidTokenException("만료되었거나 이미 사용된 토큰입니다");
        }

        // 토큰 사용 처리
        token.markAsUsed();
        passwordResetTokenRepository.save(token);

        // 비밀번호 업데이트
        User user = token.getUser();
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("비밀번호 재설정 완료: {}", user.getEmail());
    }

    /**
     * 보안 토큰 생성
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

