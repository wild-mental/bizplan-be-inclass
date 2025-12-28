package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.RefreshToken;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RefreshTokenRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("RefreshTokenRepository 테스트")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUser = userRepository.save(testUser);
        testToken = createTestToken(testUser);
    }

    @Test
    @DisplayName("리프레시 토큰 저장 및 조회")
    void save_and_findById() {
        RefreshToken saved = refreshTokenRepository.save(testToken);
        Optional<RefreshToken> found = refreshTokenRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("test-refresh-token");
    }

    @Test
    @DisplayName("토큰 값으로 조회")
    void findByToken() {
        refreshTokenRepository.save(testToken);

        Optional<RefreshToken> found = refreshTokenRepository.findByToken("test-refresh-token");

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("유효한 토큰 존재 여부 확인")
    void existsValidToken() {
        RefreshToken validToken = RefreshToken.builder()
                .user(testUser)
                .token("valid-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(validToken);

        boolean exists = refreshTokenRepository.existsValidToken("valid-token");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("폐기된 토큰은 유효하지 않음")
    void existsValidToken_revoked() {
        RefreshToken revokedToken = RefreshToken.builder()
                .user(testUser)
                .token("revoked-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(true)
                .build();
        refreshTokenRepository.save(revokedToken);

        boolean exists = refreshTokenRepository.existsValidToken("revoked-token");

        assertThat(exists).isFalse();
    }

    private User createTestUser() {
        return User.builder()
                .email("test@example.com")
                .passwordHash("$2a$10$hashed")
                .name("테스트 사용자")
                .provider(User.AuthProvider.local)
                .build();
    }

    private RefreshToken createTestToken(User user) {
        return RefreshToken.builder()
                .user(user)
                .token("test-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }
}

