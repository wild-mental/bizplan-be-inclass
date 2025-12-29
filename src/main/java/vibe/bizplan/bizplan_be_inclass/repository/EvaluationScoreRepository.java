package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.Evaluation;
import vibe.bizplan.bizplan_be_inclass.entity.EvaluationScore;

import java.util.List;
import java.util.Optional;

/**
 * 평가 점수 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 6 - AI 평가 API
 */
@Repository
public interface EvaluationScoreRepository extends JpaRepository<EvaluationScore, Long> {

    /**
     * 평가별 점수 목록 조회
     */
    List<EvaluationScore> findByEvaluation(Evaluation evaluation);

    /**
     * 평가 ID와 영역 코드로 점수 조회
     */
    Optional<EvaluationScore> findByEvaluationAndAreaCode(Evaluation evaluation, EvaluationScore.AreaCode areaCode);
}

