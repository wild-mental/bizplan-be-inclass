package vibe.bizplan.bizplan_be_inclass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vibe.bizplan.bizplan_be_inclass.dto.CreateProjectRequest;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.exception.InvalidTemplateException;
import vibe.bizplan.bizplan_be_inclass.service.ProjectService;
import vibe.bizplan.bizplan_be_inclass.service.TemplateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProjectController 단위 테스트
 * MockMvc를 사용한 컨트롤러 레이어 테스트
 * 
 * Rule 301: Use Mockito for mocking dependencies
 */
@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization
        
        // MockMvc 수동 설정 with GlobalExceptionHandler
        mockMvc = MockMvcBuilders
                .standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/projects/templates - 템플릿 목록을 반환한다")
    void getTemplates_returnsTemplateList() throws Exception {
        // given
        List<TemplateService.Template> templates = List.of(
            new TemplateService.Template("KSTARTUP_2025", "예비창업패키지", "설명1"),
            new TemplateService.Template("BANK_LOAN_2025", "은행 대출용", "설명2")
        );
        given(templateService.getAllTemplates()).willReturn(templates);

        // when & then
        mockMvc.perform(get("/api/v1/projects/templates"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].code").value("KSTARTUP_2025"))
                .andExpect(jsonPath("$.data[0].name").value("예비창업패키지"));
    }

    @Test
    @DisplayName("POST /api/v1/projects - 프로젝트를 생성하고 201을 반환한다")
    void createProject_validRequest_returns201() throws Exception {
        // given
        CreateProjectRequest request = new CreateProjectRequest("KSTARTUP_2025");
        
        Project createdProject = Project.builder()
                .id(UUID.randomUUID())
                .templateCode("KSTARTUP_2025")
                .status("draft")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        given(projectService.createProject("KSTARTUP_2025")).willReturn(createdProject);

        // when & then
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").isNotEmpty())
                .andExpect(jsonPath("$.data.templateCode").value("KSTARTUP_2025"))
                .andExpect(jsonPath("$.data.status").value("draft"));
    }

    @Test
    @DisplayName("POST /api/v1/projects - 유효하지 않은 템플릿이면 400을 반환한다")
    void createProject_invalidTemplate_returns400() throws Exception {
        // given
        CreateProjectRequest request = new CreateProjectRequest("INVALID_CODE");
        given(projectService.createProject(anyString()))
                .willThrow(new InvalidTemplateException("INVALID_CODE"));

        // when & then
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_TEMPLATE"))
                .andExpect(jsonPath("$.error.message").exists());
    }
}
