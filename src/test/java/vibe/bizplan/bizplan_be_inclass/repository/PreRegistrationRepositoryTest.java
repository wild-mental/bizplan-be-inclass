package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration.PlanType;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration.RegistrationStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PreRegistrationRepository 테스트
 * 
 * Rule 306: Repository Layer 테스트는 실제 DB 사용
 * Rule 303: 실제 DB (H2) 사용하여 Query Methods 검증
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("PreRegistrationRepository 테스트")
class PreRegistrationRepositoryTest {

    @Autowired
    private PreRegistrationRepository repository;

    private PreRegistration testEntity;

    @BeforeEach
    void setUp() {
        testEntity = createTestEntity("test@example.com", "MR2026-TEST01");
    }

    @Test
    @DisplayName("사전 등록 저장 및 조회")
    void save_and_findById() {
        // when
        PreRegistration saved = repository.save(testEntity);
        Optional<PreRegistration> found = repository.findById(saved.getId());
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getSelectedPlan()).isEqualTo(PlanType.pro);
    }

    @Test
    @DisplayName("이메일로 조회")
    void findByEmail() {
        // given
        repository.save(testEntity);
        
        // when
        Optional<PreRegistration> found = repository.findByEmail("test@example.com");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void existsByEmail() {
        // given
        repository.save(testEntity);
        
        // when & then
        assertThat(repository.existsByEmail("test@example.com")).isTrue();
        assertThat(repository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("할인 코드로 조회")
    void findByDiscountCode() {
        // given
        repository.save(testEntity);
        
        // when
        Optional<PreRegistration> found = repository.findByDiscountCode("MR2026-TEST01");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("상태별 조회")
    void findByStatus() {
        // given
        repository.save(testEntity);
        PreRegistration anotherEntity = createTestEntity("another@example.com", "MR2026-TEST02");
        repository.save(anotherEntity);
        
        // when
        List<PreRegistration> confirmed = repository.findByStatus(RegistrationStatus.CONFIRMED);
        
        // then
        assertThat(confirmed).hasSize(2);
    }

    @Test
    @DisplayName("요금제별 조회")
    void findBySelectedPlan() {
        // given
        repository.save(testEntity);
        
        PreRegistration plusEntity = PreRegistration.builder()
                .name("플러스 사용자")
                .email("plus@example.com")
                .phone("010-2222-2222")
                .selectedPlan(PlanType.plus)
                .agreeTerms(true)
                .agreeMarketing(false)
                .discountCode("MR2026-TEST02")
                .discountRate(30)
                .originalPrice(399000)
                .discountedPrice(279300)
                .build();
        repository.save(plusEntity);
        
        // when
        List<PreRegistration> proUsers = repository.findBySelectedPlan(PlanType.pro);
        List<PreRegistration> plusUsers = repository.findBySelectedPlan(PlanType.plus);
        
        // then
        assertThat(proUsers).hasSize(1);
        assertThat(plusUsers).hasSize(1);
    }

    @Test
    @DisplayName("마케팅 동의 수 조회")
    void countMarketingAgreed() {
        // given
        repository.save(testEntity);  // agreeMarketing = false
        
        PreRegistration marketingAgreedEntity = PreRegistration.builder()
                .name("마케팅 동의자")
                .email("marketing@example.com")
                .phone("010-3333-3333")
                .selectedPlan(PlanType.pro)
                .agreeTerms(true)
                .agreeMarketing(true)  // 마케팅 동의
                .discountCode("MR2026-TEST03")
                .discountRate(30)
                .originalPrice(799000)
                .discountedPrice(559300)
                .build();
        repository.save(marketingAgreedEntity);
        
        // when
        Long count = repository.countMarketingAgreed();
        
        // then
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("키워드로 검색 (이름 또는 이메일)")
    void searchByKeyword() {
        // given
        repository.save(testEntity);
        
        // when
        List<PreRegistration> byName = repository.searchByKeyword("테스트");
        List<PreRegistration> byEmail = repository.searchByKeyword("test");
        List<PreRegistration> noMatch = repository.searchByKeyword("없는검색어");
        
        // then
        assertThat(byName).hasSize(1);
        assertThat(byEmail).hasSize(1);
        assertThat(noMatch).isEmpty();
    }

    // ========== Helper Methods ==========

    private PreRegistration createTestEntity(String email, String discountCode) {
        return PreRegistration.builder()
                .name("테스트")
                .email(email)
                .phone("010-1234-5678")
                .selectedPlan(PlanType.pro)
                .agreeTerms(true)
                .agreeMarketing(false)
                .discountCode(discountCode)
                .discountRate(30)
                .originalPrice(799000)
                .discountedPrice(559300)
                .build();
    }
}
