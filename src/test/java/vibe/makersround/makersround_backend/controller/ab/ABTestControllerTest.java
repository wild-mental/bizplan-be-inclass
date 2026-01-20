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
import vibe.makersround.makersround_backend.controller.ABTestController;
import vibe.makersround.makersround_backend.dto.ab.request.ConversionRequest;
import vibe.makersround.makersround_backend.dto.ab.response.ActiveExperimentsResponse;
import vibe.makersround.makersround_backend.dto.ab.response.AssignedExperimentDto;
import vibe.makersround.makersround_backend.dto.ab.response.ConversionResponse;
import vibe.makersround.makersround_backend.exception.GlobalExceptionHandler;
import vibe.makersround.makersround_backend.service.ABTestService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ABTestController 테스트")
class ABTestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ABTestService abTestService;

    @InjectMocks
    private ABTestController controller;

    private String testVisitorId;
    private String testExperimentId;
    private String testVariantId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        
        testVisitorId = UUID.randomUUID().toString();
        testExperimentId = UUID.randomUUID().toString();
        testVariantId = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("활성 실험 조회 - 성공")
    void getActiveExperiments_success() throws Exception {
        ActiveExperimentsResponse response = ActiveExperimentsResponse.builder()
                .experiments(Arrays.asList(
                        AssignedExperimentDto.builder()
                                .experimentId(testExperimentId)
                                .experimentName("makers-section-order")
                                .variantId(testVariantId)
                                .variantName("control")
                                .content(Map.of("order", Arrays.asList("M", "A", "K", "E", "R", "S")))
                                .build()
                ))
                .visitorId(testVisitorId)
                .build();

        when(abTestService.getActiveExperiments(anyString(), anyString()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/ab-tests/active")
                        .param("page", "landing")
                        .param("visitor_id", testVisitorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.visitorId").value(testVisitorId))
                .andExpect(jsonPath("$.data.experiments").isArray())
                .andExpect(jsonPath("$.data.experiments[0].experimentName").value("makers-section-order"));
    }

    @Test
    @DisplayName("활성 실험 조회 - 빈 결과")
    void getActiveExperiments_emptyResult() throws Exception {
        ActiveExperimentsResponse response = ActiveExperimentsResponse.builder()
                .experiments(List.of())
                .visitorId(testVisitorId)
                .build();

        when(abTestService.getActiveExperiments(anyString(), anyString()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/ab-tests/active")
                        .param("page", "nonexistent-page")
                        .param("visitor_id", testVisitorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.experiments").isEmpty());
    }

    @Test
    @DisplayName("전환 기록 - 성공")
    void recordConversion_success() throws Exception {
        String assignmentId = UUID.randomUUID().toString();
        String conversionId = UUID.randomUUID().toString();

        ConversionResponse response = ConversionResponse.builder()
                .conversionId(conversionId)
                .assignmentId(assignmentId)
                .eventType("cta_click")
                .convertedAt("2025-01-15T14:30:00")
                .build();

        when(abTestService.recordConversion(any(ConversionRequest.class)))
                .thenReturn(response);

        ConversionRequest request = ConversionRequest.builder()
                .experimentId(testExperimentId)
                .variantId(testVariantId)
                .visitorId(testVisitorId)
                .eventType("cta_click")
                .eventData(Map.of("button", "start_free", "position", "hero"))
                .build();

        mockMvc.perform(post("/api/v1/ab-tests/conversions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conversionId").value(conversionId))
                .andExpect(jsonPath("$.data.eventType").value("cta_click"));
    }

    @Test
    @DisplayName("전환 기록 - 할당 없음 오류")
    void recordConversion_assignmentNotFound() throws Exception {
        when(abTestService.recordConversion(any(ConversionRequest.class)))
                .thenThrow(new IllegalArgumentException("Assignment not found for visitor"));

        ConversionRequest request = ConversionRequest.builder()
                .experimentId(testExperimentId)
                .variantId(testVariantId)
                .visitorId(testVisitorId)
                .eventType("cta_click")
                .build();

        mockMvc.perform(post("/api/v1/ab-tests/conversions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("활성 실험 조회 - 필수 파라미터 누락")
    void getActiveExperiments_missingRequiredParam() throws Exception {
        mockMvc.perform(get("/api/v1/ab-tests/active")
                        .param("page", "landing"))
                .andExpect(status().isBadRequest());
    }
}
