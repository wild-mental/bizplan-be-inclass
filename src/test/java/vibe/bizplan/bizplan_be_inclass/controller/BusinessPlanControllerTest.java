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
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateRequest;
import vibe.bizplan.bizplan_be_inclass.dto.businessplan.BusinessPlanGenerateResponse;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.service.BusinessPlanGenerationService;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BusinessPlanController 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BusinessPlanController 테스트")
class BusinessPlanControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BusinessPlanGenerationService businessPlanGenerationService;

    @InjectMocks
    private BusinessPlanController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/business-plan/generate - 성공")
    void generate_success() throws Exception {
        // given
        String projectId = UUID.randomUUID().toString();
        BusinessPlanGenerateRequest request = new BusinessPlanGenerateRequest(
                new BusinessPlanGenerateRequest.RequestInfo("pre-startup", null, null, projectId),
                new BusinessPlanGenerateRequest.BusinessPlanData(null, null, null, null, null, null),
                new BusinessPlanGenerateRequest.GenerationOptions("professional", "detailed", "markdown", "ko", List.of("all"))
        );

        BusinessPlanGenerateResponse response = BusinessPlanGenerateResponse.builder()
                .businessPlanId("bp-2025-12-17-test")
                .projectId(projectId)
                .generatedAt(Instant.now().toString())
                .templateType("pre-startup")
                .sections(Collections.emptyList())
                .metadata(BusinessPlanGenerateResponse.GenerationMetadata.builder()
                        .totalTokens(1000)
                        .modelUsed("gemini-2.5-flash-lite")
                        .generationTimeMs(5000L)
                        .totalSections(6)
                        .wordCount(1000)
                        .characterCount(5000)
                        .promptTokens(500)
                        .completionTokens(500)
                        .build())
                .exportOptions(BusinessPlanGenerateResponse.ExportOptions.builder()
                        .availableFormats(List.of("hwp", "pdf", "docx"))
                        .build())
                .build();

        given(businessPlanGenerationService.generateBusinessPlan(
                any(BusinessPlanGenerateRequest.class),
                anyString(),
                anyString(),
                anyString(),
                anyLong()))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/business-plan/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.businessPlanId").exists())
                .andExpect(jsonPath("$.data.templateType").value("pre-startup"));
    }

    @Test
    @DisplayName("POST /api/v1/business-plan/generate - 유효성 검증 실패")
    void generate_validationFailed() throws Exception {
        // given
        // BusinessPlanController는 @Valid가 없으므로 null이어도 기본값으로 처리됨
        // 실제로는 서비스 레이어에서 예외가 발생할 수 있음
        BusinessPlanGenerateRequest request = new BusinessPlanGenerateRequest(
                null, // requestInfo가 null
                new BusinessPlanGenerateRequest.BusinessPlanData(null, null, null, null, null, null),
                new BusinessPlanGenerateRequest.GenerationOptions("professional", "detailed", "markdown", "ko", List.of("all"))
        );

        // 서비스에서 예외 발생 시뮬레이션
        given(businessPlanGenerationService.generateBusinessPlan(
                any(BusinessPlanGenerateRequest.class),
                anyString(),
                anyString(),
                anyString(),
                anyLong()))
                .willThrow(new IllegalArgumentException("requestInfo는 필수입니다"));

        // when & then
        // BusinessPlanController는 null requestInfo를 기본값으로 처리하므로
        // 실제로는 서비스 레이어에서 예외가 발생할 수 있음
        // GlobalExceptionHandler가 IllegalArgumentException을 400으로 처리
        mockMvc.perform(post("/api/v1/business-plan/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler가 IllegalArgumentException을 400으로 처리
    }
}

