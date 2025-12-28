package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vibe.bizplan.bizplan_be_inclass.dto.wizard.*;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.entity.WizardData;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;
import vibe.bizplan.bizplan_be_inclass.repository.WizardDataRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * WizardService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WizardService 테스트")
class WizardServiceTest {

    @Mock
    private WizardDataRepository wizardDataRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private WizardService wizardService;
    
    private ObjectMapper objectMapper;
    private UUID projectId;
    private Project testProject;
    private WizardSaveRequest saveRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // WizardService에 실제 ObjectMapper 주입
        org.springframework.test.util.ReflectionTestUtils.setField(wizardService, "objectMapper", objectMapper);
        projectId = UUID.randomUUID();
        testProject = Project.builder()
                .id(projectId)
                .name("테스트 프로젝트")
                .templateCode("pre-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();

        Map<String, Object> stepData = new HashMap<>();
        stepData.put("idea", "테스트 아이디어");
        stepData.put("target", "테스트 타겟");

        saveRequest = WizardSaveRequest.builder()
                .currentStep(1)
                .stepData(stepData)
                .isStepComplete(true)
                .build();
    }

    @Test
    @DisplayName("Wizard 데이터 저장 성공")
    void saveWizardData_success() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(wizardDataRepository.findByProjectAndStepNumber(any(), anyInt()))
                .thenReturn(Optional.empty());
        when(wizardDataRepository.save(any(WizardData.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(wizardDataRepository.findByProjectOrderByStepNumberAsc(any()))
                .thenReturn(Collections.emptyList());

        // when
        WizardSaveResponse response = wizardService.saveWizardData(projectId, saveRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCurrentStep()).isEqualTo(1);
        assertThat(response.getProjectId()).isEqualTo(projectId.toString());
        verify(wizardDataRepository).save(any(WizardData.class));
    }

    @Test
    @DisplayName("Wizard 데이터 저장 실패 - 프로젝트 없음")
    void saveWizardData_projectNotFound() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> wizardService.saveWizardData(projectId, saveRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로젝트를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("Wizard 데이터 조회 성공")
    void getWizardData_success() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(wizardDataRepository.findByProjectOrderByStepNumberAsc(any()))
                .thenReturn(Collections.emptyList());

        // when
        WizardDataResponse response = wizardService.getWizardData(projectId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getProjectId()).isEqualTo(projectId.toString());
        assertThat(response.getSteps()).hasSize(8);
    }

    @Test
    @DisplayName("자금 집행계획 검증 성공")
    void validateBudget_success() {
        // given
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

        // when
        BudgetValidateResponse response = wizardService.validateBudget(projectId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsValid()).isTrue();
        assertThat(response.getSummary().getPhase1Total()).isEqualTo(15000000L);
    }

    @Test
    @DisplayName("자금 집행계획 검증 실패 - 1단계 한도 초과")
    void validateBudget_phase1Exceeded() {
        // given
        BudgetValidateRequest request = BudgetValidateRequest.builder()
                .templateType("pre-startup")
                .budgetPhases(List.of(
                        BudgetValidateRequest.BudgetPhase.builder()
                                .phase(1)
                                .items(List.of(
                                        BudgetValidateRequest.BudgetItem.builder()
                                                .id("item1")
                                                .name("재료비")
                                                .amount(25000000L) // 20,000,000 초과
                                                .build()
                                ))
                                .build()
                ))
                .build();

        // when
        BudgetValidateResponse response = wizardService.validateBudget(projectId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsValid()).isFalse();
    }
}

