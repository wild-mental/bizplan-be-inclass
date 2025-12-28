package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.Subscription;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SubscriptionRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SubscriptionRepository 테스트")
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUser = userRepository.save(testUser);
        testSubscription = createTestSubscription(testUser);
    }

    @Test
    @DisplayName("구독 저장 및 조회")
    void save_and_findById() {
        Subscription saved = subscriptionRepository.save(testSubscription);
        Optional<Subscription> found = subscriptionRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPlan()).isEqualTo("프로");
        assertThat(found.get().getPlanKey()).isEqualTo(Subscription.PlanKey.pro);
    }

    @Test
    @DisplayName("사용자의 활성 구독 조회")
    void findByUserAndStatus() {
        subscriptionRepository.save(testSubscription);

        Optional<Subscription> found = subscriptionRepository.findByUserAndStatus(
                testUser, Subscription.SubscriptionStatus.active);

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(Subscription.SubscriptionStatus.active);
    }

    @Test
    @DisplayName("사용자의 모든 구독 조회 (최신순)")
    void findByUserOrderByCreatedAtDesc() {
        subscriptionRepository.save(testSubscription);

        Subscription expired = Subscription.builder()
                .user(testUser)
                .plan("플러스")
                .planKey(Subscription.PlanKey.plus)
                .originalPrice(399000)
                .discountedPrice(399000)
                .startDate(LocalDateTime.now().minusMonths(7))
                .endDate(LocalDateTime.now().minusMonths(1))
                .status(Subscription.SubscriptionStatus.expired)
                .build();
        subscriptionRepository.save(expired);

        List<Subscription> subscriptions = subscriptionRepository
                .findByUserOrderByCreatedAtDesc(testUser);

        assertThat(subscriptions).hasSize(2);
        assertThat(subscriptions.get(0).getCreatedAt())
                .isAfterOrEqualTo(subscriptions.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("사용자의 활성 구독 존재 여부 확인")
    void existsByUserAndStatus() {
        subscriptionRepository.save(testSubscription);

        assertThat(subscriptionRepository.existsByUserAndStatus(
                testUser, Subscription.SubscriptionStatus.active)).isTrue();
        assertThat(subscriptionRepository.existsByUserAndStatus(
                testUser, Subscription.SubscriptionStatus.expired)).isFalse();
    }

    private User createTestUser() {
        return User.builder()
                .email("test@example.com")
                .passwordHash("$2a$10$hashed")
                .name("테스트 사용자")
                .provider(User.AuthProvider.local)
                .build();
    }

    private Subscription createTestSubscription(User user) {
        return Subscription.builder()
                .user(user)
                .plan("프로")
                .planKey(Subscription.PlanKey.pro)
                .originalPrice(799000)
                .discountedPrice(559300)
                .discountRate(30)
                .promotionPhase("A")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(6))
                .status(Subscription.SubscriptionStatus.active)
                .build();
    }
}

