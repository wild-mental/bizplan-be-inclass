package vibe.bizplan.bizplan_be_inclass.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.preregistration.*;
import vibe.bizplan.bizplan_be_inclass.dto.preregistration.PreRegistrationRequest.PlanType;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration;
import vibe.bizplan.bizplan_be_inclass.entity.Promotion;
import vibe.bizplan.bizplan_be_inclass.exception.DuplicateEmailException;
import vibe.bizplan.bizplan_be_inclass.exception.PromotionEndedException;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.PreRegistrationRepository;
import vibe.bizplan.bizplan_be_inclass.repository.PromotionRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 사전 등록 서비스
 * 
 * PRE-SUB-FUNC-002 명세서 준수
 * Rule 306: Service Layer - 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PreRegistrationService {

    private final PreRegistrationRepository preRegistrationRepository;
    private final PromotionRepository promotionRepository;

    /**
     * 요금제별 정가 (원)
     */
    private static final Map<PlanType, Integer> ORIGINAL_PRICES = Map.of(
        PlanType.plus, 399000,
        PlanType.pro, 799000,
        PlanType.premium, 1499000
    );

    /**
     * 사전 등록 신청
     * PRE-SUB-FUNC-002: POST /api/v1/pre-registrations
     */
    @Transactional
    public PreRegistrationResponse register(PreRegistrationRequest request) {
        log.info("사전 등록 요청: email={}, plan={}", request.getEmail(), request.getPlan());

        // 1. 이메일 중복 체크
        if (preRegistrationRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 등록된 이메일입니다: " + request.getEmail());
        }

        // 2. 프로모션 활성 여부 확인
        Promotion promotion = getActivePromotion();
        String currentPhase = promotion.getCurrentPhase();
        if ("ENDED".equals(currentPhase) || "NOT_STARTED".equals(currentPhase)) {
            throw new PromotionEndedException("프로모션이 종료되었거나 아직 시작되지 않았습니다.");
        }

        // 3. 할인율 및 가격 계산
        Integer discountRate = promotion.getCurrentDiscountRate();
        PlanType planType = request.getPlan();
        Integer originalPrice = ORIGINAL_PRICES.get(planType);
        Integer discountedPrice = calculateDiscountedPrice(originalPrice, discountRate);

        // 4. 할인 코드 생성
        String discountCode = generateDiscountCode(planType, currentPhase);

        // 5. 만료일 계산 (현재 Phase 종료일)
        LocalDateTime expiresAt = "A".equals(currentPhase) 
            ? promotion.getPhaseAEnd() 
            : promotion.getPhaseBEnd();

        // 6. Entity 생성 및 저장
        PreRegistration entity = PreRegistration.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .selectedPlan(convertToPlanType(planType))
                .businessCategory(request.getBusinessCategory())
                .marketingConsent(request.getMarketingConsent() != null ? request.getMarketingConsent() : false)
                .promotionPhase(currentPhase)
                .discountCode(discountCode)
                .discountRate(discountRate)
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .expiresAt(expiresAt)
                .build();

        PreRegistration saved = preRegistrationRepository.save(entity);
        log.info("사전 등록 완료: id={}, discountCode={}", saved.getId(), discountCode);

        // 7. 응답 생성
        return mapToResponse(saved);
    }

    /**
     * 이메일 중복 체크
     * PRE-SUB-FUNC-002: GET /api/v1/pre-registrations/check-email
     */
    public EmailCheckResponse checkEmail(String email) {
        Optional<PreRegistration> existing = preRegistrationRepository.findByEmail(email);
        
        if (existing.isPresent()) {
            return EmailCheckResponse.builder()
                    .exists(true)
                    .discountCode(existing.get().getDiscountCode())
                    .build();
        }
        
        return EmailCheckResponse.builder()
                .exists(false)
                .build();
    }

    /**
     * 등록 정보 조회 (ID)
     * PRE-SUB-FUNC-002: GET /api/v1/pre-registrations/{id}
     */
    public PreRegistrationResponse getById(String id) {
        PreRegistration entity = preRegistrationRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("등록 정보를 찾을 수 없습니다: " + id));
        return mapToResponse(entity);
    }

    /**
     * 할인 코드로 조회
     * PRE-SUB-FUNC-002: GET /api/v1/pre-registrations/code/{discountCode}
     */
    public PreRegistrationResponse getByDiscountCode(String discountCode) {
        PreRegistration entity = preRegistrationRepository.findByDiscountCode(discountCode)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 할인 코드입니다: " + discountCode));
        return mapToResponse(entity);
    }

    /**
     * 현재 프로모션 정보 조회
     * PRE-SUB-FUNC-002: GET /api/v1/promotions/current
     */
    public PromotionInfoResponse getPromotionInfo() {
        Promotion promotion = getActivePromotion();
        String currentPhase = promotion.getCurrentPhase();
        Integer discountRate = promotion.getCurrentDiscountRate();

        // Phase 목록 생성
        List<PromotionInfoResponse.PhaseInfo> phases = buildPhaseList(promotion, currentPhase);

        // 카운트다운 계산
        PromotionInfoResponse.CountdownInfo countdown = buildCountdown(promotion, currentPhase);

        // 요금제별 가격 정보 계산
        Map<String, PromotionInfoResponse.PriceInfo> pricing = new HashMap<>();
        for (Map.Entry<PlanType, Integer> entry : ORIGINAL_PRICES.entrySet()) {
            Integer original = entry.getValue();
            Integer discounted = calculateDiscountedPrice(original, discountRate);
            pricing.put(entry.getKey().name(), PromotionInfoResponse.PriceInfo.builder()
                    .original(original)
                    .discounted(discounted)
                    .savings(original - discounted)
                    .build());
        }

        return PromotionInfoResponse.builder()
                .isActive(!"ENDED".equals(currentPhase) && !"NOT_STARTED".equals(currentPhase))
                .currentPhase(currentPhase)
                .phases(phases)
                .countdown(countdown)
                .pricing(pricing)
                .build();
    }

    // ========== Private Methods ==========

    /**
     * 활성화된 프로모션 조회
     */
    private Promotion getActivePromotion() {
        return promotionRepository.findByIsActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("활성화된 프로모션이 없습니다."));
    }

    /**
     * Phase 목록 생성
     */
    private List<PromotionInfoResponse.PhaseInfo> buildPhaseList(Promotion promotion, String currentPhase) {
        List<PromotionInfoResponse.PhaseInfo> phases = new ArrayList<>();
        
        phases.add(PromotionInfoResponse.PhaseInfo.builder()
                .phase("A")
                .name("연말연시 특별 할인")
                .discountRate(promotion.getPhaseADiscountRate())
                .startDate(promotion.getPhaseAStart())
                .endDate(promotion.getPhaseAEnd())
                .isCurrentPhase("A".equals(currentPhase))
                .build());
        
        phases.add(PromotionInfoResponse.PhaseInfo.builder()
                .phase("B")
                .name("얼리버드 할인")
                .discountRate(promotion.getPhaseBDiscountRate())
                .startDate(promotion.getPhaseBStart())
                .endDate(promotion.getPhaseBEnd())
                .isCurrentPhase("B".equals(currentPhase))
                .build());
        
        return phases;
    }

    /**
     * 카운트다운 정보 계산
     */
    private PromotionInfoResponse.CountdownInfo buildCountdown(Promotion promotion, String currentPhase) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDate;
        
        if ("A".equals(currentPhase)) {
            targetDate = promotion.getPhaseAEnd();
        } else if ("B".equals(currentPhase)) {
            targetDate = promotion.getPhaseBEnd();
        } else {
            // NOT_STARTED or ENDED
            targetDate = promotion.getPhaseAStart();
        }
        
        if (targetDate == null) {
            return null;
        }
        
        Duration duration = Duration.between(now, targetDate);
        if (duration.isNegative()) {
            return PromotionInfoResponse.CountdownInfo.builder()
                    .targetDate(targetDate)
                    .remainingDays(0L)
                    .remainingHours(0L)
                    .remainingMinutes(0L)
                    .remainingSeconds(0L)
                    .build();
        }
        
        long totalSeconds = duration.getSeconds();
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        return PromotionInfoResponse.CountdownInfo.builder()
                .targetDate(targetDate)
                .remainingDays(days)
                .remainingHours(hours)
                .remainingMinutes(minutes)
                .remainingSeconds(seconds)
                .build();
    }

    /**
     * 할인가 계산
     */
    private Integer calculateDiscountedPrice(Integer originalPrice, Integer discountRate) {
        double discount = originalPrice * (discountRate / 100.0);
        return (int) Math.round(originalPrice - discount);
    }

    /**
     * 할인 코드 생성 (MR2026-{PLAN}-{PHASE}{RANDOM} 형식)
     */
    private String generateDiscountCode(PlanType planType, String phase) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder("MR2026-");
        code.append(planType.name().toUpperCase()).append("-");
        code.append(phase);
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // 중복 체크
        String generatedCode = code.toString();
        if (preRegistrationRepository.findByDiscountCode(generatedCode).isPresent()) {
            return generateDiscountCode(planType, phase); // 재귀 호출로 재생성
        }
        return generatedCode;
    }

    /**
     * DTO PlanType → Entity PlanType 변환
     */
    private PreRegistration.PlanType convertToPlanType(PlanType planType) {
        return PreRegistration.PlanType.valueOf(planType.name());
    }

    /**
     * Entity → Response DTO 변환
     */
    private PreRegistrationResponse mapToResponse(PreRegistration entity) {
        return PreRegistrationResponse.builder()
                .registrationId(entity.getId().toString())
                .plan(entity.getSelectedPlan().name())
                .promotionPhase(entity.getPromotionPhase())
                .discountRate(entity.getDiscountRate())
                .discountCode(entity.getDiscountCode())
                .originalPrice(entity.getOriginalPrice())
                .discountedPrice(entity.getDiscountedPrice())
                .savings(entity.getOriginalPrice() - entity.getDiscountedPrice())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
