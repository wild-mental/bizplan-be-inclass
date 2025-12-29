package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.Evaluation;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EvaluationRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("EvaluationRepository 테스트")
class EvaluationRepositoryTest {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Project testProject;
    private Evaluation testEvaluation;

    @BeforeEach
    void setUp() {
        User user = createTestUser();
        user = userRepository.save(user);
        testProject = createTestProject(user);
        testProject = projectRepository.save(testProject);
        testEvaluation = createTestEvaluation(testProject);
    }

    @Test
    @DisplayName("평가 저장 및 조회")
    void save_and_findById() {
        Evaluation saved = evaluationRepository.save(testEvaluation);
        var found = evaluationRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEvaluationType())
                .isEqualTo(Evaluation.EvaluationType.demo);
        assertThat(found.get().getStatus())
                .isEqualTo(Evaluation.EvaluationStatus.pending);
    }

    @Test
    @DisplayName("프로젝트별 평가 목록 조회 (최신순)")
    void findByProjectOrderByCreatedAtDesc() {
        evaluationRepository.save(testEvaluation);

        Evaluation anotherEvaluation = Evaluation.builder()
                .project(testProject)
                .evaluationType(Evaluation.EvaluationType.full)
                .status(Evaluation.EvaluationStatus.completed)
                .inputData("{\"test\": \"data2\"}")
                .build();
        evaluationRepository.save(anotherEvaluation);

        List<Evaluation> evaluations = evaluationRepository
                .findByProjectOrderByCreatedAtDesc(testProject);

        assertThat(evaluations).hasSize(2);
        assertThat(evaluations.get(0).getCreatedAt())
                .isAfterOrEqualTo(evaluations.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("프로젝트의 최신 평가 조회")
    void findFirstByProjectOrderByCreatedAtDesc() {
        evaluationRepository.save(testEvaluation);

        Evaluation newerEvaluation = Evaluation.builder()
                .project(testProject)
                .evaluationType(Evaluation.EvaluationType.full)
                .status(Evaluation.EvaluationStatus.completed)
                .inputData("{\"test\": \"newer\"}")
                .build();
        evaluationRepository.save(newerEvaluation);

        Evaluation latest = evaluationRepository
                .findFirstByProjectOrderByCreatedAtDesc(testProject);

        assertThat(latest).isNotNull();
        assertThat(latest.getEvaluationType())
                .isEqualTo(Evaluation.EvaluationType.full);
    }

    @Test
    @DisplayName("상태별 평가 조회")
    void findByStatus() {
        evaluationRepository.save(testEvaluation);

        Evaluation completed = Evaluation.builder()
                .project(testProject)
                .evaluationType(Evaluation.EvaluationType.full)
                .status(Evaluation.EvaluationStatus.completed)
                .inputData("{\"test\": \"completed\"}")
                .build();
        evaluationRepository.save(completed);

        List<Evaluation> completedEvaluations = evaluationRepository
                .findByStatus(Evaluation.EvaluationStatus.completed);

        assertThat(completedEvaluations).hasSize(1);
        assertThat(completedEvaluations.get(0).getStatus())
                .isEqualTo(Evaluation.EvaluationStatus.completed);
    }

    private User createTestUser() {
        return User.builder()
                .email("test@example.com")
                .passwordHash("$2a$10$hashed")
                .name("테스트 사용자")
                .provider(User.AuthProvider.local)
                .build();
    }

    private Project createTestProject(User user) {
        return Project.builder()
                .user(user)
                .name("테스트 프로젝트")
                .templateCode("pre-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();
    }

    private Evaluation createTestEvaluation(Project project) {
        return Evaluation.builder()
                .project(project)
                .evaluationType(Evaluation.EvaluationType.demo)
                .status(Evaluation.EvaluationStatus.pending)
                .inputData("{\"test\": \"data\"}")
                .build();
    }
}

