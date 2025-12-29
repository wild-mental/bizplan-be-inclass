package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vibe.bizplan.bizplan_be_inclass.dto.evaluation.*;
import vibe.bizplan.bizplan_be_inclass.entity.Evaluation;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.EvaluationRepository;
import vibe.bizplan.bizplan_be_inclass.repository.EvaluationScoreRepository;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;
import vibe.bizplan.bizplan_be_inclass.repository.UserRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EvaluationService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluationService 테스트")
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private EvaluationScoreRepository evaluationScoreRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EvaluationService evaluationService;
    
    private ObjectMapper objectMapper;

    private UUID projectId;
    private Project testProject;
    private EvaluationRequest request;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // EvaluationService에 실제 ObjectMapper 주입
        org.springframework.test.util.ReflectionTestUtils.setField(evaluationService, "objectMapper", objectMapper);
        projectId = UUID.randomUUID();
        testProject = Project.builder()
                .id(projectId)
                .name("테스트 프로젝트")
                .templateCode("pre-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();

        EvaluationRequest.InputData inputData = EvaluationRequest.InputData.builder()
                .businessName("테스트 사업")
                .businessField("IT")
                .targetMarket("스타트업")
                .problemStatement("테스트 문제")
                .solutionSummary("테스트 해결책")
                .build();

        request = EvaluationRequest.builder()
                .projectId(projectId.toString())
                .evaluationType("demo")
                .inputData(inputData)
                .build();
    }

    @Test
    @DisplayName("평가 요청 성공")
    void createEvaluation_success() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(invocation -> {
            Evaluation eval = invocation.getArgument(0);
            eval.setId(UUID.randomUUID());
            return eval;
        });

        // when
        EvaluationStatusResponse response = evaluationService.createEvaluation(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("processing");
        assertThat(response.getStages()).hasSize(6);
        verify(evaluationRepository).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("평가 요청 실패 - 프로젝트 없음")
    void createEvaluation_projectNotFound() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> evaluationService.createEvaluation(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로젝트를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("평가 상태 조회 성공")
    void getEvaluationStatus_success() {
        // given
        UUID evaluationId = UUID.randomUUID();
        Evaluation evaluation = Evaluation.builder()
                .id(evaluationId)
                .project(testProject)
                .evaluationType(Evaluation.EvaluationType.demo)
                .status(Evaluation.EvaluationStatus.processing)
                .build();

        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(evaluation));
        when(evaluationScoreRepository.findByEvaluation(any())).thenReturn(Collections.emptyList());

        // when
        EvaluationStatusResponse response = evaluationService.getEvaluationStatus(evaluationId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEvaluationId()).isEqualTo(evaluationId.toString());
        assertThat(response.getStages()).hasSize(6);
    }

    @Test
    @DisplayName("평가 결과 조회 성공")
    void getEvaluationResult_success() {
        // given
        UUID evaluationId = UUID.randomUUID();
        Evaluation evaluation = Evaluation.builder()
                .id(evaluationId)
                .project(testProject)
                .evaluationType(Evaluation.EvaluationType.demo)
                .status(Evaluation.EvaluationStatus.completed)
                .totalScore(85)
                .passRate(80)
                .build();

        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(evaluation));
        when(evaluationScoreRepository.findByEvaluation(any())).thenReturn(Collections.emptyList());

        // when
        EvaluationResultResponse response = evaluationService.getEvaluationResult(evaluationId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEvaluationId()).isEqualTo(evaluationId.toString());
        assertThat(response.getSummary().getTotalScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("평가 결과 조회 실패 - 평가 미완료")
    void getEvaluationResult_notCompleted() {
        // given
        UUID evaluationId = UUID.randomUUID();
        Evaluation evaluation = Evaluation.builder()
                .id(evaluationId)
                .project(testProject)
                .evaluationType(Evaluation.EvaluationType.demo)
                .status(Evaluation.EvaluationStatus.processing)
                .build();

        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(evaluation));

        // when & then
        assertThatThrownBy(() -> evaluationService.getEvaluationResult(evaluationId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("평가가 아직 완료되지 않았습니다");
    }
}

