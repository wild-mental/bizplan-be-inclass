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
import java.util.*;

/**
 * 사전 등록 서비스
 * 
 * Rule 306: Service Layer - 비즈니스 로직 처리
 * - Repository 호출 및 데이터 조합
 * - DTO ↔ Entity 변환
 * - 트랜잭션 관리
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
     * 
     * @param request 사전 등록 요청 DTO
     * @return 사전 등록 응답 DTO
     * @throws DuplicateEmailException 이메일 중복 시
     * @throws PromotionEndedException 프로모션 종료 시
     */
    @Transactional
    public PreRegistrationResponse register(PreRegistrationRequest request) {
        log.info("사전 등록 요청: email={}, plan={}", request.getEmail(), request.getSelectedPlan());

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
        PlanType planType = request.getSelectedPlan();
        Integer originalPrice = ORIGINAL_PRICES.get(planType);
        Integer discountedPrice = calculateDiscountedPrice(originalPrice, discountRate);

        // 4. 할인 코드 생성
        String discountCode = generateDiscountCode();

        // 5. Entity 생성 및 저장
        PreRegistration entity = PreRegistration.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .selectedPlan(convertToPlanType(planType))
                .businessCategory(request.getBusinessCategory())
                .agreeTerms(request.getAgreeTerms())
                .agreeMarketing(request.getAgreeMarketing() != null ? request.getAgreeMarketing() : false)
                .discountCode(discountCode)
                .discountRate(discountRate)
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .build();

        PreRegistration saved = preRegistrationRepository.save(entity);
        log.info("사전 등록 완료: id={}, discountCode={}", saved.getId(), discountCode);

        // 6. 응답 생성
        return mapToResponse(saved);
    }

    /**
     * 이메일 중복 체크
     * 
     * @param email 확인할 이메일
     * @return 이메일 체크 응답 DTO
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
     * 
     * @param id 등록 ID (UUID)
     * @return 사전 등록 응답 DTO
     * @throws ResourceNotFoundException 등록 정보가 없을 때
     */
    public PreRegistrationResponse getById(String id) {
        PreRegistration entity = preRegistrationRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("등록 정보를 찾을 수 없습니다: " + id));
        return mapToResponse(entity);
    }

    /**
     * 할인 코드로 조회
     * 
     * @param discountCode 할인 코드
     * @return 사전 등록 응답 DTO
     * @throws ResourceNotFoundException 유효하지 않은 할인 코드
     */
    public PreRegistrationResponse getByDiscountCode(String discountCode) {
        PreRegistration entity = preRegistrationRepository.findByDiscountCode(discountCode)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 할인 코드입니다: " + discountCode));
        return mapToResponse(entity);
    }

    /**
     * 현재 프로모션 정보 조회
     * 
     * @return 프로모션 정보 응답 DTO
     */
    public PromotionInfoResponse getPromotionInfo() {
        Promotion promotion = getActivePromotion();
        String currentPhase = promotion.getCurrentPhase();
        Integer discountRate = promotion.getCurrentDiscountRate();

        // 요금제별 가격 정보 계산
        Map<String, PromotionInfoResponse.PriceInfo> prices = new HashMap<>();
        for (Map.Entry<PlanType, Integer> entry : ORIGINAL_PRICES.entrySet()) {
            Integer original = entry.getValue();
            Integer discounted = calculateDiscountedPrice(original, discountRate);
            prices.put(entry.getKey().name(), PromotionInfoResponse.PriceInfo.builder()
                    .original(original)
                    .discounted(discounted)
                    .saved(original - discounted)
                    .build());
        }

        return PromotionInfoResponse.builder()
                .isActive(!"ENDED".equals(currentPhase) && !"NOT_STARTED".equals(currentPhase))
                .currentPhase(currentPhase)
                .discountRate(discountRate)
                .phaseAEnd(promotion.getPhaseAEnd())
                .phaseBEnd(promotion.getPhaseBEnd())
                .prices(prices)
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
     * 할인가 계산
     */
    private Integer calculateDiscountedPrice(Integer originalPrice, Integer discountRate) {
        double discount = originalPrice * (discountRate / 100.0);
        return (int) Math.round(originalPrice - discount);
    }

    /**
     * 할인 코드 생성 (MR2026-XXXXXX 형식)
     */
    private String generateDiscountCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder("MR2026-");
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // 중복 체크
        String generatedCode = code.toString();
        if (preRegistrationRepository.findByDiscountCode(generatedCode).isPresent()) {
            return generateDiscountCode(); // 재귀 호출로 재생성
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
                .id(entity.getId().toString())
                .discountCode(entity.getDiscountCode())
                .discountRate(entity.getDiscountRate())
                .selectedPlan(entity.getSelectedPlan().name())
                .originalPrice(entity.getOriginalPrice())
                .discountedPrice(entity.getDiscountedPrice())
                .savedAmount(entity.getOriginalPrice() - entity.getDiscountedPrice())
                .registeredAt(entity.getRegisteredAt())
                .status(entity.getStatus().name())
                .build();
    }
}

