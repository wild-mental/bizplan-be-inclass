package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.entity.WizardData;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WizardDataRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("WizardDataRepository 테스트")
class WizardDataRepositoryTest {

    @Autowired
    private WizardDataRepository wizardDataRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Project testProject;
    private WizardData testWizardData;

    @BeforeEach
    void setUp() {
        User user = createTestUser();
        user = userRepository.save(user);
        testProject = createTestProject(user);
        testProject = projectRepository.save(testProject);
        testWizardData = createTestWizardData(testProject, 1);
    }

    @Test
    @DisplayName("Wizard 데이터 저장 및 조회")
    void save_and_findById() {
        WizardData saved = wizardDataRepository.save(testWizardData);
        Optional<WizardData> found = wizardDataRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getStepNumber()).isEqualTo(1);
        assertThat(found.get().getStepData()).contains("test-data");
    }

    @Test
    @DisplayName("프로젝트와 단계 번호로 조회")
    void findByProjectAndStepNumber() {
        wizardDataRepository.save(testWizardData);

        Optional<WizardData> found = wizardDataRepository.findByProjectAndStepNumber(
                testProject, 1);

        assertThat(found).isPresent();
        assertThat(found.get().getStepNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("프로젝트의 모든 Wizard 데이터 조회 (단계 순)")
    void findByProjectOrderByStepNumberAsc() {
        wizardDataRepository.save(testWizardData);

        WizardData step2 = WizardData.builder()
                .project(testProject)
                .stepNumber(2)
                .stepData("{\"step2\": \"data\"}")
                .isComplete(false)
                .build();
        wizardDataRepository.save(step2);

        List<WizardData> allSteps = wizardDataRepository
                .findByProjectOrderByStepNumberAsc(testProject);

        assertThat(allSteps).hasSize(2);
        assertThat(allSteps.get(0).getStepNumber()).isEqualTo(1);
        assertThat(allSteps.get(1).getStepNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("프로젝트의 완료된 단계 수 조회")
    void countByProjectAndIsCompleteTrue() {
        WizardData completed = WizardData.builder()
                .project(testProject)
                .stepNumber(1)
                .stepData("{\"step1\": \"completed\"}")
                .isComplete(true)
                .build();
        wizardDataRepository.save(completed);

        WizardData incomplete = WizardData.builder()
                .project(testProject)
                .stepNumber(2)
                .stepData("{\"step2\": \"incomplete\"}")
                .isComplete(false)
                .build();
        wizardDataRepository.save(incomplete);

        long completedCount = wizardDataRepository.countByProjectAndIsCompleteTrue(testProject);

        assertThat(completedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("프로젝트 ID와 단계 번호로 존재 여부 확인")
    void existsByProjectAndStepNumber() {
        wizardDataRepository.save(testWizardData);

        boolean exists = wizardDataRepository.existsByProjectAndStepNumber(testProject, 1);

        assertThat(exists).isTrue();
        assertThat(wizardDataRepository.existsByProjectAndStepNumber(testProject, 99))
                .isFalse();
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

    private WizardData createTestWizardData(Project project, int stepNumber) {
        return WizardData.builder()
                .project(project)
                .stepNumber(stepNumber)
                .stepData("{\"test\": \"test-data\"}")
                .isComplete(false)
                .build();
    }
}

