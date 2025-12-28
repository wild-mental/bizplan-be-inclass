package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.Promotion;

import java.util.Optional;
import java.util.UUID;

/**
 * 프로모션 Repository
 * 
 * Rule 306: Repository Layer - DB 접근 전담
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    /**
     * 프로모션 코드로 조회
     */
    Optional<Promotion> findByCode(String code);
    
    /**
     * 활성화된 프로모션 조회
     */
    Optional<Promotion> findByIsActiveTrue();
}

