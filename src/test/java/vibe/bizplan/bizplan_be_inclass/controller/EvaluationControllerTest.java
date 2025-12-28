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
import vibe.bizplan.bizplan_be_inclass.dto.evaluation.*;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.service.EvaluationService;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * EvaluationController 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluationController 테스트")
class EvaluationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/evaluations - 성공")
    void createEvaluation_success() throws Exception {
        // given
        EvaluationRequest request = EvaluationRequest.builder()
                .projectId(UUID.randomUUID().toString())
                .evaluationType("demo")
                .inputData(EvaluationRequest.InputData.builder()
                        .businessName("테스트 사업")
                        .businessField("IT")
                        .targetMarket("스타트업")
                        .build())
                .build();

        EvaluationStatusResponse response = EvaluationStatusResponse.builder()
                .evaluationId(UUID.randomUUID().toString())
                .status("processing")
                .estimatedTime(30)
                .queuePosition(1)
                .stages(Collections.emptyList())
                .build();

        given(evaluationService.createEvaluation(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/evaluations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("processing"));
    }

    @Test
    @DisplayName("GET /api/v1/evaluations/{evaluationId}/status - 성공")
    void getEvaluationStatus_success() throws Exception {
        // given
        UUID evaluationId = UUID.randomUUID();
        EvaluationStatusResponse response = EvaluationStatusResponse.builder()
                .evaluationId(evaluationId.toString())
                .status("processing")
                .progress(50)
                .currentStage("market")
                .stages(Collections.emptyList())
                .build();

        given(evaluationService.getEvaluationStatus(evaluationId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/evaluations/{evaluationId}/status", evaluationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.evaluationId").value(evaluationId.toString()));
    }

    @Test
    @DisplayName("GET /api/v1/evaluations/{evaluationId}/result - 성공")
    void getEvaluationResult_success() throws Exception {
        // given
        UUID evaluationId = UUID.randomUUID();
        EvaluationResultResponse response = EvaluationResultResponse.builder()
                .evaluationId(evaluationId.toString())
                .projectId(UUID.randomUUID().toString())
                .completedAt(LocalDateTime.now())
                .summary(EvaluationResultResponse.Summary.builder()
                        .totalScore(85)
                        .grade("B")
                        .passRate(80)
                        .passRateMessage("현재 점수로는 합격 가능성이 높습니다.")
                        .build())
                .scores(Collections.emptyMap())
                .strengths(Collections.emptyList())
                .weaknesses(Collections.emptyList())
                .recommendations(Collections.emptyList())
                .accessLevel("demo")
                .build();

        given(evaluationService.getEvaluationResult(evaluationId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/evaluations/{evaluationId}/result", evaluationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.summary.totalScore").value(85));
    }

    @Test
    @DisplayName("GET /api/v1/evaluations/{evaluationId}/status - 평가 없음")
    void getEvaluationStatus_notFound() throws Exception {
        // given
        UUID evaluationId = UUID.randomUUID();
        given(evaluationService.getEvaluationStatus(evaluationId))
                .willThrow(new ResourceNotFoundException("평가를 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(get("/api/v1/evaluations/{evaluationId}/status", evaluationId))
                .andExpect(status().isNotFound());
    }
}

