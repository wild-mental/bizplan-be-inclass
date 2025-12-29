package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.Evaluation;
import vibe.bizplan.bizplan_be_inclass.entity.EvaluationScore;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EvaluationScoreRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("EvaluationScoreRepository 테스트")
class EvaluationScoreRepositoryTest {

    @Autowired
    private EvaluationScoreRepository evaluationScoreRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Evaluation testEvaluation;
    private EvaluationScore testScore;

    @BeforeEach
    void setUp() {
        User user = createTestUser();
        user = userRepository.save(user);
        Project project = createTestProject(user);
        project = projectRepository.save(project);
        testEvaluation = createTestEvaluation(project);
        testEvaluation = evaluationRepository.save(testEvaluation);
        testScore = createTestScore(testEvaluation, EvaluationScore.AreaCode.market);
    }

    @Test
    @DisplayName("평가 점수 저장 및 조회")
    void save_and_findById() {
        EvaluationScore saved = evaluationScoreRepository.save(testScore);
        Optional<EvaluationScore> found = evaluationScoreRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getAreaCode()).isEqualTo(EvaluationScore.AreaCode.market);
        assertThat(found.get().getScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("평가별 점수 목록 조회")
    void findByEvaluation() {
        evaluationScoreRepository.save(testScore);

        EvaluationScore abilityScore = EvaluationScore.builder()
                .evaluation(testEvaluation)
                .areaCode(EvaluationScore.AreaCode.ability)
                .score(80)
                .feedback("수행능력 우수")
                .build();
        evaluationScoreRepository.save(abilityScore);

        List<EvaluationScore> scores = evaluationScoreRepository.findByEvaluation(testEvaluation);

        assertThat(scores).hasSize(2);
    }

    @Test
    @DisplayName("평가 ID와 영역 코드로 점수 조회")
    void findByEvaluationAndAreaCode() {
        evaluationScoreRepository.save(testScore);

        Optional<EvaluationScore> found = evaluationScoreRepository
                .findByEvaluationAndAreaCode(testEvaluation, EvaluationScore.AreaCode.market);

        assertThat(found).isPresent();
        assertThat(found.get().getScore()).isEqualTo(85);
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
                .status(Evaluation.EvaluationStatus.completed)
                .inputData("{\"test\": \"data\"}")
                .build();
    }

    private EvaluationScore createTestScore(Evaluation evaluation, EvaluationScore.AreaCode areaCode) {
        return EvaluationScore.builder()
                .evaluation(evaluation)
                .areaCode(areaCode)
                .score(85)
                .feedback("시장성 평가 완료")
                .build();
    }
}

