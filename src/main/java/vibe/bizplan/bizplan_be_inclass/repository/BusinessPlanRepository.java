package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.BusinessPlan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BusinessPlan 엔티티를 위한 Repository 인터페이스
 * 
 * Rule 306: Repository Layer는 Entity만 다룸, DTO는 사용하지 않음
 * Rule 303: Spring Data JPA 사용, Query Methods 활용
 */
@Repository
public interface BusinessPlanRepository extends JpaRepository<BusinessPlan, UUID> {

    /**
     * businessPlanId로 조회
     * 
     * @param businessPlanId 사업계획서 ID (bp-2025-12-20-xxx 형식)
     * @return BusinessPlan 엔티티 (Optional)
     */
    Optional<BusinessPlan> findByBusinessPlanId(String businessPlanId);

    /**
     * projectId로 조회 (프로젝트별 사업계획서 목록)
     * 
     * @param projectId 프로젝트 ID
     * @return BusinessPlan 리스트 (생성일시 내림차순)
     */
    @Query("SELECT bp FROM BusinessPlan bp WHERE bp.projectId = :projectId ORDER BY bp.createdAt DESC")
    List<BusinessPlan> findByProjectId(@Param("projectId") UUID projectId);

    /**
     * userId로 조회 (사용자별 사업계획서 목록)
     * 
     * @param userId 사용자 ID
     * @return BusinessPlan 리스트 (생성일시 내림차순)
     */
    @Query("SELECT bp FROM BusinessPlan bp WHERE bp.userId = :userId ORDER BY bp.createdAt DESC")
    List<BusinessPlan> findByUserId(@Param("userId") String userId);

    /**
     * templateType으로 조회
     * 
     * @param templateType 템플릿 유형
     * @return BusinessPlan 리스트 (생성일시 내림차순)
     */
    @Query("SELECT bp FROM BusinessPlan bp WHERE bp.templateType = :templateType ORDER BY bp.createdAt DESC")
    List<BusinessPlan> findByTemplateType(@Param("templateType") String templateType);
}
