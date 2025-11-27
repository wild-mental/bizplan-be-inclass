package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.Project;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 Repository
 * Spring Data JPA가 구현체를 자동 생성합니다.
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    
    /**
     * 상태별 프로젝트 조회
     * 
     * @param status 프로젝트 상태 (draft, completed 등)
     * @return 해당 상태의 프로젝트 목록
     */
    List<Project> findByStatus(String status);
    
    /**
     * 템플릿 코드별 프로젝트 조회
     * 
     * @param templateCode 템플릿 코드
     * @return 해당 템플릿으로 생성된 프로젝트 목록
     */
    List<Project> findByTemplateCode(String templateCode);
}

