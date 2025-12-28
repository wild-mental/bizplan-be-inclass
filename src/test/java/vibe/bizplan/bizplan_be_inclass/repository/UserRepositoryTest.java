package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser("test@example.com");
    }

    @Test
    @DisplayName("사용자 저장 및 조회")
    void save_and_findById() {
        User saved = repository.save(testUser);
        Optional<User> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getName()).isEqualTo("테스트 사용자");
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail() {
        repository.save(testUser);

        Optional<User> found = repository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트 사용자");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void existsByEmail() {
        repository.save(testUser);

        assertThat(repository.existsByEmail("test@example.com")).isTrue();
        assertThat(repository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("소셜 로그인 제공자와 ID로 조회")
    void findByProviderAndProviderId() {
        User socialUser = User.builder()
                .email("social@example.com")
                .name("소셜 사용자")
                .provider(User.AuthProvider.google)
                .providerId("google-12345")
                .emailVerified(true)
                .build();
        repository.save(socialUser);

        Optional<User> found = repository.findByProviderAndProviderId(
                User.AuthProvider.google, "google-12345");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("social@example.com");
    }

    private User createTestUser(String email) {
        return User.builder()
                .email(email)
                .passwordHash("$2a$10$hashedpassword")
                .name("테스트 사용자")
                .phone("010-1234-5678")
                .provider(User.AuthProvider.local)
                .emailVerified(false)
                .marketingConsent(false)
                .build();
    }
}

