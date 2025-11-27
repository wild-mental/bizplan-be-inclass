package vibe.bizplan.bizplan_be_inclass.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.CreateProjectRequest;
import vibe.bizplan.bizplan_be_inclass.dto.ProjectResponse;
import vibe.bizplan.bizplan_be_inclass.dto.TemplateDto;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.service.ProjectService;
import vibe.bizplan.bizplan_be_inclass.service.TemplateService;

import java.util.List;

/**
 * 프로젝트 컨트롤러
 * 프로젝트 생성 및 템플릿 조회 API를 제공합니다.
 * 
 * Rule 304: /api/v1/ prefix for all endpoints
 * Rule 301: Constructor Injection via @RequiredArgsConstructor
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final TemplateService templateService;

    /**
     * 지원 템플릿 목록 조회
     * 
     * GET /api/v1/projects/templates
     * 
     * @return 지원되는 템플릿 목록
     */
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<TemplateDto>>> getTemplates() {
        // TemplateService에서 목록 조회 후 DTO로 변환
        List<TemplateDto> templates = templateService.getAllTemplates().stream()
                .map(t -> new TemplateDto(t.getCode(), t.getName(), t.getDescription()))
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    /**
     * 새 프로젝트 생성
     * 
     * POST /api/v1/projects
     * 
     * Logic Steps:
     * 1. Request Body 검증 (@Valid)
     * 2. ProjectService.createProject() 호출
     * 3. Entity -> DTO 변환
     * 4. 201 Created 응답 반환
     * 
     * @param request 프로젝트 생성 요청 (templateCode 필수)
     * @return 생성된 프로젝트 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        
        // Service 호출하여 프로젝트 생성
        Project project = projectService.createProject(request.getTemplateCode());
        
        // Entity -> DTO 변환
        ProjectResponse response = ProjectResponse.fromEntity(project);
        
        // 201 Created 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}

