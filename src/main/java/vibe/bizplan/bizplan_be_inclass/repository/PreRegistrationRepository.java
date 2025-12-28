package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration.PlanType;
import vibe.bizplan.bizplan_be_inclass.entity.PreRegistration.RegistrationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사전 등록 Repository
 * 
 * Rule 306: Repository Layer - DB 접근 전담
 */
@Repository
public interface PreRegistrationRepository extends JpaRepository<PreRegistration, UUID> {

    /**
     * 이메일로 조회 (중복 체크용)
     */
    Optional<PreRegistration> findByEmail(String email);
    
    /**
     * 이메일 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 할인 코드로 조회
     */
    Optional<PreRegistration> findByDiscountCode(String discountCode);

    /**
     * 상태별 조회
     */
    List<PreRegistration> findByStatus(RegistrationStatus status);

    /**
     * 요금제별 조회
     */
    List<PreRegistration> findBySelectedPlan(PlanType planType);

    /**
     * 기간별 등록 수 조회
     */
    @Query("SELECT COUNT(p) FROM PreRegistration p WHERE p.registeredAt BETWEEN :start AND :end")
    Long countByRegisteredAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 요금제별 통계
     */
    @Query("SELECT p.selectedPlan, COUNT(p) FROM PreRegistration p GROUP BY p.selectedPlan")
    List<Object[]> countByPlanType();

    /**
     * 마케팅 동의율
     */
    @Query("SELECT COUNT(p) FROM PreRegistration p WHERE p.agreeMarketing = true")
    Long countMarketingAgreed();

    /**
     * 검색 (이름 또는 이메일)
     */
    @Query("SELECT p FROM PreRegistration p WHERE p.name LIKE %:keyword% OR p.email LIKE %:keyword%")
    List<PreRegistration> searchByKeyword(@Param("keyword") String keyword);
}

