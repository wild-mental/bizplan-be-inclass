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
import vibe.bizplan.bizplan_be_inclass.dto.wizard.*;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.service.WizardService;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WizardController 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WizardController 테스트")
class WizardControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private WizardService wizardService;

    @InjectMocks
    private WizardController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("PUT /api/v1/projects/{projectId}/wizard - 성공")
    void saveWizardData_success() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        Map<String, Object> stepData = new HashMap<>();
        stepData.put("idea", "테스트 아이디어");

        WizardSaveRequest request = WizardSaveRequest.builder()
                .currentStep(1)
                .stepData(stepData)
                .isStepComplete(true)
                .build();

        WizardSaveResponse response = WizardSaveResponse.builder()
                .projectId(projectId.toString())
                .currentStep(1)
                .lastSavedAt(LocalDateTime.now())
                .progress(WizardSaveResponse.ProgressInfo.builder()
                        .currentStep(1)
                        .completedSteps(List.of(1))
                        .percentComplete(12.5)
                        .build())
                .validationWarnings(Collections.emptyList())
                .build();

        given(wizardService.saveWizardData(eq(projectId), any())).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/projects/{projectId}/wizard", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentStep").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/projects/{projectId}/wizard - 프로젝트 없음")
    void saveWizardData_projectNotFound() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        WizardSaveRequest request = WizardSaveRequest.builder()
                .currentStep(1)
                .stepData(new HashMap<>())
                .isStepComplete(false)
                .build();

        given(wizardService.saveWizardData(eq(projectId), any()))
                .willThrow(new ResourceNotFoundException("프로젝트를 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(put("/api/v1/projects/{projectId}/wizard", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/projects/{projectId}/wizard - 성공")
    void getWizardData_success() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        WizardDataResponse response = WizardDataResponse.builder()
                .projectId(projectId.toString())
                .templateId("pre-startup")
                .currentStep(1)
                .steps(new ArrayList<>())
                .lastSavedAt(LocalDateTime.now())
                .build();

        given(wizardService.getWizardData(projectId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/projects/{projectId}/wizard", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value(projectId.toString()));
    }

    @Test
    @DisplayName("POST /api/v1/projects/{projectId}/wizard/validate-budget - 성공")
    void validateBudget_success() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        BudgetValidateRequest request = BudgetValidateRequest.builder()
                .templateType("pre-startup")
                .budgetPhases(List.of(
                        BudgetValidateRequest.BudgetPhase.builder()
                                .phase(1)
                                .items(List.of(
                                        BudgetValidateRequest.BudgetItem.builder()
                                                .id("item1")
                                                .name("재료비")
                                                .amount(15000000L)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        BudgetValidateResponse response = BudgetValidateResponse.builder()
                .isValid(true)
                .summary(BudgetValidateResponse.BudgetSummary.builder()
                        .totalBudget(15000000L)
                        .phase1Total(15000000L)
                        .phase2Total(0L)
                        .phase1Remaining(5000000L)
                        .phase2Remaining(40000000L)
                        .build())
                .validations(Collections.emptyList())
                .warnings(Collections.emptyList())
                .recommendations(Collections.emptyList())
                .build();

        given(wizardService.validateBudget(eq(projectId), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/projects/{projectId}/wizard/budget/validate", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isValid").value(true));
    }
}

