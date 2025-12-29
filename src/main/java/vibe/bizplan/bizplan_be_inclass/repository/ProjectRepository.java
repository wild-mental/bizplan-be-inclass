package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 Repository
 * 
 * @see PRE-SUB-FUNC-002.md Section 4 - 프로젝트 관리 API
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    
    /**
     * 사용자별 프로젝트 목록 조회 (페이징)
     */
    Page<Project> findByUserOrderByUpdatedAtDesc(User user, Pageable pageable);

    /**
     * 사용자별 상태별 프로젝트 목록 조회 (페이징)
     */
    Page<Project> findByUserAndStatusOrderByUpdatedAtDesc(
            User user, 
            Project.ProjectStatus status, 
            Pageable pageable);

    /**
     * 사용자별 템플릿별 프로젝트 목록 조회 (페이징)
     */
    Page<Project> findByUserAndTemplateCodeOrderByUpdatedAtDesc(
            User user, 
            String templateCode, 
            Pageable pageable);

    /**
     * 상태별 프로젝트 조회
     */
    List<Project> findByStatus(Project.ProjectStatus status);
    
    /**
     * 템플릿 코드별 프로젝트 조회
     */
    List<Project> findByTemplateCode(String templateCode);

    /**
     * 사용자별 프로젝트 수 조회
     */
    long countByUser(User user);

    /**
     * 사용자별 상태별 프로젝트 수 조회
     */
    long countByUserAndStatus(User user, Project.ProjectStatus status);
}

