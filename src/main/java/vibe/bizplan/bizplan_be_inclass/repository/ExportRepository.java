package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.Export;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * 문서 내보내기 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 9 - 문서 내보내기 API
 */
@Repository
public interface ExportRepository extends JpaRepository<Export, UUID> {

    /**
     * 사용자별 내보내기 목록 조회 (최신순)
     */
    List<Export> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 프로젝트별 내보내기 목록 조회
     */
    List<Export> findByProjectOrderByCreatedAtDesc(Project project);

    /**
     * 상태별 내보내기 조회
     */
    List<Export> findByStatus(Export.ExportStatus status);
}

