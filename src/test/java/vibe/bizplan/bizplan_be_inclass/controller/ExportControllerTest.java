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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import vibe.bizplan.bizplan_be_inclass.dto.export.*;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.security.CustomUserDetailsService;
import vibe.bizplan.bizplan_be_inclass.service.ExportService;

import java.security.Principal;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ExportController 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExportController 테스트")
class ExportControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ExportService exportService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private ExportController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        
        // @AuthenticationPrincipal을 처리하기 위한 ArgumentResolver
        HandlerMethodArgumentResolver authenticationPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
            }

            @Override
            public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                                        org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                                        org.springframework.web.context.request.NativeWebRequest webRequest,
                                        org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                return org.springframework.security.core.userdetails.User.builder()
                        .username("test@example.com")
                        .password("password")
                        .authorities(Collections.emptyList())
                        .build();
            }
        };
        
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(authenticationPrincipalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/projects/{projectId}/export - 성공")
    void createExport_success() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        ExportRequest request = ExportRequest.builder()
                .format("hwp")
                .templateType("pre-startup")
                .options(ExportRequest.ExportOptions.builder()
                        .includeAppendix(true)
                        .includeCoverPage(true)
                        .build())
                .build();

        ExportStatusResponse response = ExportStatusResponse.builder()
                .exportId(UUID.randomUUID().toString())
                .status("processing")
                .format("hwp")
                .estimatedSize("2.5MB")
                .estimatedTime(30)
                .build();

        User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("테스트 사용자")
                .provider(User.AuthProvider.local)
                .build();

        // ExportController는 @AuthenticationPrincipal을 사용하므로
        // ArgumentResolver가 자동으로 UserDetails를 주입
        given(userDetailsService.getUserByEmail("test@example.com")).willReturn(testUser);
        given(exportService.createExport(eq(projectId), any(), eq(testUser))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/projects/{projectId}/export", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("processing"));
    }

    @Test
    @DisplayName("GET /api/v1/exports/{exportId}/status - 성공")
    void getExportStatus_success() throws Exception {
        // given
        UUID exportId = UUID.randomUUID();
        ExportStatusResponse response = ExportStatusResponse.builder()
                .exportId(exportId.toString())
                .status("completed")
                .format("pdf")
                .fileName("test.pdf")
                .fileSize(2621440L)
                .downloadUrl("/api/v1/exports/" + exportId + "/download")
                .completedAt(LocalDateTime.now())
                .build();

        given(exportService.getExportStatus(exportId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/exports/{exportId}/status", exportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("completed"));
    }

    @Test
    @DisplayName("GET /api/v1/exports/{exportId}/status - 내보내기 없음")
    void getExportStatus_notFound() throws Exception {
        // given
        UUID exportId = UUID.randomUUID();
        given(exportService.getExportStatus(exportId))
                .willThrow(new ResourceNotFoundException("내보내기를 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(get("/api/v1/exports/{exportId}/status", exportId))
                .andExpect(status().isNotFound());
    }
}

