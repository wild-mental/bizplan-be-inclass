package vibe.bizplan.bizplan_be_inclass.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.CreateProjectRequest;
import vibe.bizplan.bizplan_be_inclass.dto.UpdateProjectRequest;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.exception.InvalidTemplateException;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;

import java.util.UUID;

/**
 * 프로젝트 서비스
 * 프로젝트 생성 및 관리 비즈니스 로직을 담당합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 4 - 프로젝트 관리 API
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TemplateService templateService;

    /**
     * 새 프로젝트 생성
     * 
     * @param request 프로젝트 생성 요청
     * @param user 프로젝트 소유자
     * @return 생성된 프로젝트 엔티티
     */
    @Transactional
    public Project createProject(CreateProjectRequest request, User user) {
        log.info("프로젝트 생성 시도: templateId={}, user={}", request.getTemplateCode(), 
                user != null ? user.getEmail() : "anonymous");

        // 템플릿 유효성 검증
        if (!templateService.isValidTemplate(request.getTemplateCode())) {
            throw new InvalidTemplateException(request.getTemplateCode());
        }

        // 프로젝트 엔티티 생성
        Project project = Project.builder()
                .user(user)
                .name(request.getName())
                .templateCode(request.getTemplateCode())
                .supportProgram(request.getSupportProgram())
                .description(request.getDescription())
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();

        return projectRepository.save(project);
    }

    /**
     * 새 프로젝트 생성 (템플릿 코드만으로 - 기존 호환성)
     */
    @Transactional
    public Project createProject(String templateCode) {
        if (!templateService.isValidTemplate(templateCode)) {
            throw new InvalidTemplateException(templateCode);
        }

        Project project = Project.createWithTemplate(templateCode);
        return projectRepository.save(project);
    }

    /**
     * 프로젝트 목록 조회 (페이징)
     * 
     * @param user 프로젝트 소유자
     * @param status 필터링할 상태 (nullable)
     * @param templateId 필터링할 템플릿 ID (nullable)
     * @param pageable 페이징 정보
     * @return 프로젝트 페이지
     */
    public Page<Project> getProjects(User user, String status, String templateId, Pageable pageable) {
        if (user == null) {
            // 인증되지 않은 사용자는 빈 결과 반환
            return Page.empty(pageable);
        }

        if (status != null && !status.isEmpty()) {
            Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status);
            return projectRepository.findByUserAndStatusOrderByUpdatedAtDesc(user, projectStatus, pageable);
        }

        if (templateId != null && !templateId.isEmpty()) {
            return projectRepository.findByUserAndTemplateCodeOrderByUpdatedAtDesc(user, templateId, pageable);
        }

        return projectRepository.findByUserOrderByUpdatedAtDesc(user, pageable);
    }

    /**
     * 프로젝트 ID로 조회
     * 
     * @param projectId 조회할 프로젝트 ID
     * @return 프로젝트 엔티티
     */
    public Project getProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "프로젝트를 찾을 수 없습니다: " + projectId));
    }

    /**
     * 프로젝트 수정
     * 
     * @param projectId 수정할 프로젝트 ID
     * @param request 수정 요청
     * @return 수정된 프로젝트
     */
    @Transactional
    public Project updateProject(UUID projectId, UpdateProjectRequest request) {
        Project project = getProject(projectId);

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            project.setStatus(Project.ProjectStatus.valueOf(request.getStatus()));
        }

        return projectRepository.save(project);
    }

    /**
     * 프로젝트 삭제
     * 
     * @param projectId 삭제할 프로젝트 ID
     */
    @Transactional
    public void deleteProject(UUID projectId) {
        Project project = getProject(projectId);
        projectRepository.delete(project);
        log.info("프로젝트 삭제 완료: {}", projectId);
    }

    /**
     * 템플릿 이름 조회
     */
    public String getTemplateName(String templateCode) {
        return templateService.getTemplate(templateCode)
                .map(t -> t.getName())
                .orElse(null);
    }
}

