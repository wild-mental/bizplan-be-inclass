package vibe.bizplan.bizplan_be_inclass.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.exception.InvalidTemplateException;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;

import java.util.UUID;

/**
 * 프로젝트 서비스
 * 프로젝트 생성 및 관리 비즈니스 로직을 담당합니다.
 * 
 * Rule 303: @Transactional(readOnly = true) at class level
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TemplateService templateService;

    /**
     * 새 프로젝트 생성
     * 
     * Logic Steps:
     * 1. 템플릿 유효성 검증
     * 2. 프로젝트 엔티티 생성
     * 3. DB 저장 및 반환
     * 
     * @param templateCode 사용할 템플릿 코드
     * @return 생성된 프로젝트 엔티티
     * @throws InvalidTemplateException 지원하지 않는 템플릿 코드인 경우
     */
    @Transactional
    public Project createProject(String templateCode) {
        // Step 1: 템플릿 유효성 검증
        if (!templateService.isValidTemplate(templateCode)) {
            throw new InvalidTemplateException(templateCode);
        }

        // Step 2: 프로젝트 엔티티 생성
        Project project = Project.createWithTemplate(templateCode);

        // Step 3: DB 저장 및 반환
        return projectRepository.save(project);
    }

    /**
     * 프로젝트 ID로 조회
     * 
     * @param projectId 조회할 프로젝트 ID
     * @return 프로젝트 엔티티
     * @throws IllegalArgumentException 프로젝트를 찾을 수 없는 경우
     */
    public Project getProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "프로젝트를 찾을 수 없습니다: " + projectId));
    }
}

