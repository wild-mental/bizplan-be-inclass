package vibe.bizplan.bizplan_be_inclass.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vibe.bizplan.bizplan_be_inclass.dto.preregistration.*;
import vibe.bizplan.bizplan_be_inclass.dto.preregistration.PreRegistrationRequest.PlanType;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration;
import vibe.bizplan.bizplan_be_inclass.entity.Promotion;
import vibe.bizplan.bizplan_be_inclass.exception.DuplicateEmailException;
import vibe.bizplan.bizplan_be_inclass.exception.PromotionEndedException;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.PreRegistrationRepository;
import vibe.bizplan.bizplan_be_inclass.repository.PromotionRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PreRegistrationService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PreRegistrationService 테스트")
class PreRegistrationServiceTest {

    @Mock
    private PreRegistrationRepository preRegistrationRepository;
    
    @Mock
    private PromotionRepository promotionRepository;
    
    @InjectMocks
    private PreRegistrationService service;

    private PreRegistrationRequest testRequest;
    private Promotion activePromotion;

    @BeforeEach
    void setUp() {
        testRequest = createTestRequest();
        activePromotion = createActivePromotion();
    }

    @Test
    @DisplayName("사전 등록 성공")
    void register_success() {
        // given
        when(preRegistrationRepository.existsByEmail(anyString())).thenReturn(false);
        when(promotionRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePromotion));
        when(preRegistrationRepository.findByDiscountCode(anyString())).thenReturn(Optional.empty());
        when(preRegistrationRepository.save(any())).thenAnswer(invocation -> {
            PreRegistration entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
            ReflectionTestUtils.setField(entity, "registeredAt", LocalDateTime.now());
            return entity;
        });
        
        // when
        PreRegistrationResponse response = service.register(testRequest);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountCode()).startsWith("MR2026-");
        assertThat(response.getDiscountRate()).isEqualTo(30);
        assertThat(response.getSelectedPlan()).isEqualTo("pro");
        assertThat(response.getOriginalPrice()).isEqualTo(799000);
        
        verify(preRegistrationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void register_duplicateEmail_throwsException() {
        // given
        when(preRegistrationRepository.existsByEmail(anyString())).thenReturn(true);
        
        // when & then
        assertThatThrownBy(() -> service.register(testRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("이미 등록된 이메일입니다");
        
        verify(preRegistrationRepository, never()).save(any());
    }

    @Test
    @DisplayName("프로모션 종료 시 예외 발생")
    void register_promotionEnded_throwsException() {
        // given
        Promotion endedPromotion = createEndedPromotion();
        when(preRegistrationRepository.existsByEmail(anyString())).thenReturn(false);
        when(promotionRepository.findByIsActiveTrue()).thenReturn(Optional.of(endedPromotion));
        
        // when & then
        assertThatThrownBy(() -> service.register(testRequest))
                .isInstanceOf(PromotionEndedException.class)
                .hasMessageContaining("프로모션이 종료");
        
        verify(preRegistrationRepository, never()).save(any());
    }

    @Test
    @DisplayName("활성화된 프로모션이 없을 때 예외 발생")
    void register_noActivePromotion_throwsException() {
        // given
        when(preRegistrationRepository.existsByEmail(anyString())).thenReturn(false);
        when(promotionRepository.findByIsActiveTrue()).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> service.register(testRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("활성화된 프로모션이 없습니다");
    }

    @Test
    @DisplayName("이메일 중복 체크 - 존재하는 경우")
    void checkEmail_exists() {
        // given
        PreRegistration existing = createTestEntity();
        when(preRegistrationRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existing));
        
        // when
        EmailCheckResponse response = service.checkEmail("test@example.com");
        
        // then
        assertThat(response.getExists()).isTrue();
        assertThat(response.getDiscountCode()).isEqualTo("MR2026-TEST01");
    }

    @Test
    @DisplayName("이메일 중복 체크 - 존재하지 않는 경우")
    void checkEmail_notExists() {
        // given
        when(preRegistrationRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        // when
        EmailCheckResponse response = service.checkEmail("new@example.com");
        
        // then
        assertThat(response.getExists()).isFalse();
        assertThat(response.getDiscountCode()).isNull();
    }

    @Test
    @DisplayName("ID로 등록 정보 조회 - 성공")
    void getById_success() {
        // given
        PreRegistration entity = createTestEntity();
        UUID testId = UUID.randomUUID();
        ReflectionTestUtils.setField(entity, "id", testId);
        when(preRegistrationRepository.findById(testId)).thenReturn(Optional.of(entity));
        
        // when
        PreRegistrationResponse response = service.getById(testId.toString());
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountCode()).isEqualTo("MR2026-TEST01");
    }

    @Test
    @DisplayName("ID로 등록 정보 조회 - 실패")
    void getById_notFound() {
        // given
        UUID testId = UUID.randomUUID();
        when(preRegistrationRepository.findById(testId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> service.getById(testId.toString()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("등록 정보를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("할인 코드로 조회 - 성공")
    void getByDiscountCode_success() {
        // given
        PreRegistration entity = createTestEntity();
        when(preRegistrationRepository.findByDiscountCode("MR2026-TEST01"))
                .thenReturn(Optional.of(entity));
        
        // when
        PreRegistrationResponse response = service.getByDiscountCode("MR2026-TEST01");
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getSelectedPlan()).isEqualTo("pro");
    }

    @Test
    @DisplayName("프로모션 정보 조회")
    void getPromotionInfo_success() {
        // given
        when(promotionRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePromotion));
        
        // when
        PromotionInfoResponse response = service.getPromotionInfo();
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCurrentPhase()).isIn("A", "B");
        assertThat(response.getPrices()).containsKeys("plus", "pro", "premium");
        assertThat(response.getPrices().get("pro").getOriginal()).isEqualTo(799000);
    }

    // ========== Helper Methods ==========

    private PreRegistrationRequest createTestRequest() {
        return PreRegistrationRequest.builder()
                .name("테스트")
                .email("test@example.com")
                .phone("010-1234-5678")
                .selectedPlan(PlanType.pro)
                .agreeTerms(true)
                .agreeMarketing(false)
                .build();
    }

    private Promotion createActivePromotion() {
        LocalDateTime now = LocalDateTime.now();
        return Promotion.builder()
                .id(UUID.randomUUID())
                .code("pre-registration-2026-h1")
                .name("2026 상반기 사전 등록")
                .phaseAStart(now.minusDays(1))
                .phaseAEnd(now.plusDays(5))
                .phaseADiscountRate(30)
                .phaseBStart(now.plusDays(5))
                .phaseBEnd(now.plusMonths(2))
                .phaseBDiscountRate(10)
                .isActive(true)
                .build();
    }

    private Promotion createEndedPromotion() {
        LocalDateTime now = LocalDateTime.now();
        return Promotion.builder()
                .id(UUID.randomUUID())
                .code("pre-registration-2026-h1")
                .name("2026 상반기 사전 등록")
                .phaseAStart(now.minusMonths(2))
                .phaseAEnd(now.minusMonths(1))
                .phaseADiscountRate(30)
                .phaseBStart(now.minusMonths(1))
                .phaseBEnd(now.minusDays(1))  // 이미 종료됨
                .phaseBDiscountRate(10)
                .isActive(true)
                .build();
    }

    private PreRegistration createTestEntity() {
        PreRegistration entity = PreRegistration.builder()
                .name("테스트")
                .email("test@example.com")
                .phone("010-1234-5678")
                .selectedPlan(PreRegistration.PlanType.pro)
                .agreeTerms(true)
                .agreeMarketing(false)
                .discountCode("MR2026-TEST01")
                .discountRate(30)
                .originalPrice(799000)
                .discountedPrice(559300)
                .build();
        ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(entity, "registeredAt", LocalDateTime.now());
        ReflectionTestUtils.setField(entity, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(entity, "updatedAt", LocalDateTime.now());
        return entity;
    }
}

