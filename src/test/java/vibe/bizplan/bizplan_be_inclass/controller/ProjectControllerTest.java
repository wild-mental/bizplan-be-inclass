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
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import vibe.bizplan.bizplan_be_inclass.dto.CreateProjectRequest;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.exception.InvalidTemplateException;
import vibe.bizplan.bizplan_be_inclass.security.CustomUserDetailsService;
import vibe.bizplan.bizplan_be_inclass.service.ProjectService;
import vibe.bizplan.bizplan_be_inclass.service.TemplateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProjectController 단위 테스트
 * MockMvc를 사용한 컨트롤러 레이어 테스트
 */
@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private TemplateService templateService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        
        // @AuthenticationPrincipal을 처리하기 위한 커스텀 resolver
        HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(UserDetails.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return null; // 인증되지 않은 사용자로 테스트
            }
        };
        
        mockMvc = MockMvcBuilders
                .standaloneSetup(projectController)
                .setCustomArgumentResolvers(authPrincipalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/projects/templates - 템플릿 목록을 반환한다")
    void getTemplates_returnsTemplateList() throws Exception {
        // given
        List<TemplateService.Template> templates = List.of(
            new TemplateService.Template("pre-startup", "예비창업패키지", "설명1"),
            new TemplateService.Template("early-startup", "초기창업패키지", "설명2")
        );
        given(templateService.getAllTemplates()).willReturn(templates);

        // when & then
        mockMvc.perform(get("/api/v1/projects/templates"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].code").value("pre-startup"))
                .andExpect(jsonPath("$.data[0].name").value("예비창업패키지"));
    }

    @Test
    @DisplayName("POST /api/v1/projects - 프로젝트를 생성하고 201을 반환한다")
    void createProject_validRequest_returns201() throws Exception {
        // given
        CreateProjectRequest request = CreateProjectRequest.builder()
                .name("LearnAI")
                .templateId("pre-startup")
                .supportProgram("2026-1")
                .description("AI 기반 학습 플랫폼")
                .build();
        
        Project createdProject = Project.builder()
                .id(UUID.randomUUID())
                .name("LearnAI")
                .templateCode("pre-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        given(projectService.createProject(any(CreateProjectRequest.class), any())).willReturn(createdProject);
        given(projectService.getTemplateName("pre-startup")).willReturn("예비창업패키지");

        // when & then
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.templateId").value("pre-startup"))
                .andExpect(jsonPath("$.data.status").value("draft"));
    }

    @Test
    @DisplayName("POST /api/v1/projects - 유효하지 않은 템플릿이면 400을 반환한다")
    void createProject_invalidTemplate_returns400() throws Exception {
        // given
        CreateProjectRequest request = CreateProjectRequest.builder()
                .templateId("INVALID_CODE")
                .build();
        
        given(projectService.createProject(any(CreateProjectRequest.class), any()))
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
