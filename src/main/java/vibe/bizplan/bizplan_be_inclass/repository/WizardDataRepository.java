package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.WizardData;

import java.util.List;
import java.util.Optional;

/**
 * Wizard 데이터 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 5 - 사업계획서 작성 Wizard API
 */
@Repository
public interface WizardDataRepository extends JpaRepository<WizardData, Long> {

    /**
     * 프로젝트와 단계 번호로 Wizard 데이터 조회
     */
    Optional<WizardData> findByProjectAndStepNumber(Project project, Integer stepNumber);

    /**
     * 프로젝트의 모든 Wizard 데이터 조회 (단계 순)
     */
    List<WizardData> findByProjectOrderByStepNumberAsc(Project project);

    /**
     * 프로젝트의 완료된 단계 수 조회
     */
    long countByProjectAndIsCompleteTrue(Project project);

    /**
     * 프로젝트 ID와 단계 번호로 존재 여부 확인
     */
    boolean existsByProjectAndStepNumber(Project project, Integer stepNumber);
}

