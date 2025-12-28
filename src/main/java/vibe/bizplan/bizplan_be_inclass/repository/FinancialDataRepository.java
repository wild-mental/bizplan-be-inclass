package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.FinancialData;
import vibe.bizplan.bizplan_be_inclass.entity.Project;

import java.util.Optional;
import java.util.UUID;

/**
 * 재무 데이터 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 8 - 재무 시뮬레이션 API
 */
@Repository
public interface FinancialDataRepository extends JpaRepository<FinancialData, UUID> {

    /**
     * 프로젝트의 재무 데이터 조회
     */
    Optional<FinancialData> findByProject(Project project);

    /**
     * 프로젝트 ID로 재무 데이터 조회
     */
    Optional<FinancialData> findByProjectId(UUID projectId);

    /**
     * 프로젝트의 재무 데이터 존재 여부 확인
     */
    boolean existsByProject(Project project);
}

