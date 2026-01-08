package vibe.makersround.makersround_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.makersround.makersround_backend.entity.Evaluation;
import vibe.makersround.makersround_backend.entity.Project;

import java.util.List;
import java.util.UUID;

/**
 * AI 평가 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 6 - AI 평가 API
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {

    /**
     * 프로젝트별 평가 목록 조회 (최신순)
     */
    List<Evaluation> findByProjectOrderByCreatedAtDesc(Project project);

    /**
     * 프로젝트의 최신 평가 조회
     */
    Evaluation findFirstByProjectOrderByCreatedAtDesc(Project project);

    /**
     * 상태별 평가 조회
     */
    List<Evaluation> findByStatus(Evaluation.EvaluationStatus status);
}

