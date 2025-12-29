package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.*;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.security.CustomUserDetailsService;
import vibe.bizplan.bizplan_be_inclass.service.ProjectService;
import vibe.bizplan.bizplan_be_inclass.service.TemplateService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 프로젝트 컨트롤러
 * 프로젝트 CRUD 및 템플릿 조회 API를 제공합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 4 - 프로젝트 관리 API
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "프로젝트 관리 API - CRUD 및 템플릿 조회")
public class ProjectController {

    private final ProjectService projectService;
    private final TemplateService templateService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 지원 템플릿 목록 조회
     * 
     * GET /api/v1/projects/templates
     */
    @Operation(
            summary = "지원 템플릿 목록 조회",
            description = "프로젝트 생성 시 사용 가능한 템플릿 목록을 조회합니다."
    )
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<TemplateDto>>> getTemplates() {
        List<TemplateDto> templates = templateService.getAllTemplates().stream()
                .map(t -> new TemplateDto(t.getCode(), t.getName(), t.getDescription()))
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    /**
     * 프로젝트 목록 조회 (페이징)
     * 
     * GET /api/v1/projects
     */
    @Operation(
            summary = "프로젝트 목록 조회",
            description = "현재 사용자의 프로젝트 목록을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 목록 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 항목 수") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "상태 필터") @RequestParam(required = false) String status,
            @Parameter(description = "템플릿 ID 필터") @RequestParam(required = false) String templateId) {

        User user = null;
        if (userDetails != null) {
            user = userDetailsService.getUserByEmail(userDetails.getUsername());
        }

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Project> projectPage = projectService.getProjects(user, status, templateId, pageable);

        List<ProjectResponse> projects = projectPage.getContent().stream()
                .map(p -> ProjectResponse.fromEntity(p, projectService.getTemplateName(p.getTemplateCode())))
                .toList();

        ApiResponse<List<ProjectResponse>> response = ApiResponse.success(projects);
        // 메타 정보는 별도 처리 필요 시 확장
        
        return ResponseEntity.ok(response);
    }

    /**
     * 프로젝트 상세 조회
     * 
     * GET /api/v1/projects/{projectId}
     */
    @Operation(
            summary = "프로젝트 상세 조회",
            description = "특정 프로젝트의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId) {

        Project project = projectService.getProject(projectId);
        String templateName = projectService.getTemplateName(project.getTemplateCode());
        ProjectResponse response = ProjectResponse.fromEntity(project, templateName);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 새 프로젝트 생성
     * 
     * POST /api/v1/projects
     */
    @Operation(
            summary = "새 프로젝트 생성",
            description = "선택한 템플릿을 기반으로 새로운 사업계획서 프로젝트를 생성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "프로젝트 생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "프로젝트 생성 요청 정보", required = true)
            @Valid @RequestBody CreateProjectRequest request) {
        
        User user = null;
        if (userDetails != null) {
            user = userDetailsService.getUserByEmail(userDetails.getUsername());
        }

        Project project = projectService.createProject(request, user);
        String templateName = projectService.getTemplateName(project.getTemplateCode());
        ProjectResponse response = ProjectResponse.fromEntity(project, templateName);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * 프로젝트 수정
     * 
     * PUT /api/v1/projects/{projectId}
     */
    @Operation(
            summary = "프로젝트 수정",
            description = "프로젝트의 이름, 상태, 설명 등을 수정합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "프로젝트 수정 요청 정보", required = true)
            @Valid @RequestBody UpdateProjectRequest request) {

        Project project = projectService.updateProject(projectId, request);
        String templateName = projectService.getTemplateName(project.getTemplateCode());
        ProjectResponse response = ProjectResponse.fromEntity(project, templateName);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 프로젝트 삭제
     * 
     * DELETE /api/v1/projects/{projectId}
     */
    @Operation(
            summary = "프로젝트 삭제",
            description = "프로젝트를 삭제합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "프로젝트 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId) {

        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}

