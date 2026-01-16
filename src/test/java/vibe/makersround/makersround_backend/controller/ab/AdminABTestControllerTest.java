package vibe.makersround.makersround_backend.controller.ab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vibe.makersround.makersround_backend.dto.ab.request.ExperimentCreateRequest;
import vibe.makersround.makersround_backend.dto.ab.request.ExperimentUpdateRequest;
import vibe.makersround.makersround_backend.dto.ab.response.ExperimentResponse;
import vibe.makersround.makersround_backend.dto.ab.response.ExperimentStatsResponse;
import vibe.makersround.makersround_backend.controller.AdminABTestController;
import vibe.makersround.makersround_backend.exception.GlobalExceptionHandler;
import vibe.makersround.makersround_backend.service.ABTestService;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AdminABTestController 테스트")
class AdminABTestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ABTestService abTestService;

    @InjectMocks
    private AdminABTestController controller;

    private String testExperimentId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        testExperimentId = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("전체 실험 목록 조회")
    void getAllExperiments_success() throws Exception {
        ExperimentResponse exp1 = createTestExperimentResponse("exp-1", "makers-section-order", "running");
        ExperimentResponse exp2 = createTestExperimentResponse("exp-2", "hero-cta-text", "draft");

        when(abTestService.getAllExperiments())
                .thenReturn(Arrays.asList(exp1, exp2));

        mockMvc.perform(get("/api/v1/admin/ab-tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("실험 상세 조회")
    void getExperimentById_success() throws Exception {
        ExperimentResponse response = createTestExperimentResponse(testExperimentId, "makers-section-order", "running");

        when(abTestService.getExperimentById(testExperimentId))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/ab-tests/{id}", testExperimentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testExperimentId))
                .andExpect(jsonPath("$.data.name").value("makers-section-order"));
    }

    @Test
    @DisplayName("실험 상세 조회 - 존재하지 않는 ID")
    void getExperimentById_notFound() throws Exception {
        when(abTestService.getExperimentById(anyString()))
                .thenThrow(new IllegalArgumentException("Experiment not found"));

        mockMvc.perform(get("/api/v1/admin/ab-tests/{id}", "non-existent-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실험 생성")
    void createExperiment_success() throws Exception {
        ExperimentCreateRequest request = ExperimentCreateRequest.builder()
                .name("new-experiment")
                .description("Test experiment")
                .targetPage("landing")
                .targetElement("hero_cta")
                .trafficPercentage(100)
                .variants(Arrays.asList(
                        ExperimentCreateRequest.VariantCreateRequest.builder()
                                .name("control")
                                .weight(50)
                                .isControl(true)
                                .content(Map.of("text", "Get Started"))
                                .build(),
                        ExperimentCreateRequest.VariantCreateRequest.builder()
                                .name("treatment")
                                .weight(50)
                                .isControl(false)
                                .content(Map.of("text", "Start Free"))
                                .build()
                ))
                .build();

        ExperimentResponse response = createTestExperimentResponse(testExperimentId, "new-experiment", "draft");

        when(abTestService.createExperiment(any(ExperimentCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/ab-tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testExperimentId));
    }

    @Test
    @DisplayName("실험 생성 - 중복 이름 오류")
    void createExperiment_duplicateName() throws Exception {
        ExperimentCreateRequest request = ExperimentCreateRequest.builder()
                .name("existing-experiment")
                .targetPage("landing")
                .build();

        when(abTestService.createExperiment(any(ExperimentCreateRequest.class)))
                .thenThrow(new IllegalArgumentException("Experiment with name already exists"));

        mockMvc.perform(post("/api/v1/admin/ab-tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실험 수정")
    void updateExperiment_success() throws Exception {
        ExperimentUpdateRequest request = ExperimentUpdateRequest.builder()
                .description("Updated description")
                .trafficPercentage(50)
                .build();

        ExperimentResponse response = createTestExperimentResponse(testExperimentId, "test-experiment", "draft");

        when(abTestService.updateExperiment(anyString(), any(ExperimentUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/admin/ab-tests/{id}", testExperimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("실험 삭제")
    void deleteExperiment_success() throws Exception {
        doNothing().when(abTestService).deleteExperiment(testExperimentId);

        mockMvc.perform(delete("/api/v1/admin/ab-tests/{id}", testExperimentId))
                .andExpect(status().isOk());

        verify(abTestService).deleteExperiment(testExperimentId);
    }

    @Test
    @DisplayName("실험 시작")
    void startExperiment_success() throws Exception {
        ExperimentResponse response = createTestExperimentResponse(testExperimentId, "test-experiment", "running");

        when(abTestService.startExperiment(testExperimentId))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/ab-tests/{id}/start", testExperimentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("running"));
    }

    @Test
    @DisplayName("실험 중지")
    void stopExperiment_success() throws Exception {
        ExperimentResponse response = createTestExperimentResponse(testExperimentId, "test-experiment", "paused");

        when(abTestService.stopExperiment(testExperimentId))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/ab-tests/{id}/stop", testExperimentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("paused"));
    }

    @Test
    @DisplayName("실험 통계 조회")
    void getExperimentStats_success() throws Exception {
        ExperimentStatsResponse stats = ExperimentStatsResponse.builder()
                .experimentId(testExperimentId)
                .experimentName("test-experiment")
                .status("running")
                .totalAssignments(1000L)
                .variants(Arrays.asList(
                        ExperimentStatsResponse.VariantStatsDto.builder()
                                .variantId(UUID.randomUUID().toString())
                                .name("control")
                                .isControl(true)
                                .assignments(500L)
                                .conversions(50L)
                                .conversionRate(10.0)
                                .build(),
                        ExperimentStatsResponse.VariantStatsDto.builder()
                                .variantId(UUID.randomUUID().toString())
                                .name("treatment")
                                .isControl(false)
                                .assignments(500L)
                                .conversions(75L)
                                .conversionRate(15.0)
                                .uplift("+50.0%")
                                .build()
                ))
                .statisticalSignificance(0.95)
                .winner("treatment")
                .build();

        when(abTestService.getExperimentStats(testExperimentId))
                .thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/ab-tests/{id}/stats", testExperimentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAssignments").value(1000))
                .andExpect(jsonPath("$.data.winner").value("treatment"))
                .andExpect(jsonPath("$.data.variants.length()").value(2));
    }

    private ExperimentResponse createTestExperimentResponse(String id, String name, String status) {
        return ExperimentResponse.builder()
                .id(id)
                .name(name)
                .description("Test experiment")
                .targetPage("landing")
                .targetElement("hero_cta")
                .status(status)
                .trafficPercentage(100)
                .createdAt("2025-01-15T10:00:00")
                .updatedAt("2025-01-15T10:00:00")
                .variants(Arrays.asList(
                        ExperimentResponse.VariantDto.builder()
                                .id(UUID.randomUUID().toString())
                                .name("control")
                                .weight(50)
                                .isControl(true)
                                .content(Map.of("text", "Original"))
                                .build(),
                        ExperimentResponse.VariantDto.builder()
                                .id(UUID.randomUUID().toString())
                                .name("treatment")
                                .weight(50)
                                .isControl(false)
                                .content(Map.of("text", "Variant"))
                                .build()
                ))
                .build();
    }
}
