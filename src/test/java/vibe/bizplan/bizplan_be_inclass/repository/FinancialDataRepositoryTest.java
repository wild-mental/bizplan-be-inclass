package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.FinancialData;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FinancialDataRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FinancialDataRepository 테스트")
class FinancialDataRepositoryTest {

    @Autowired
    private FinancialDataRepository financialDataRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Project testProject;
    private FinancialData testFinancialData;

    @BeforeEach
    void setUp() {
        User user = createTestUser();
        user = userRepository.save(user);
        testProject = createTestProject(user);
        testProject = projectRepository.save(testProject);
        testFinancialData = createTestFinancialData(testProject);
    }

    @Test
    @DisplayName("재무 데이터 저장 및 조회")
    void save_and_findById() {
        FinancialData saved = financialDataRepository.save(testFinancialData);
        Optional<FinancialData> found = financialDataRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getInputs()).contains("customer");
    }

    @Test
    @DisplayName("프로젝트의 재무 데이터 조회")
    void findByProject() {
        financialDataRepository.save(testFinancialData);

        Optional<FinancialData> found = financialDataRepository.findByProject(testProject);

        assertThat(found).isPresent();
        assertThat(found.get().getProject().getId()).isEqualTo(testProject.getId());
    }

    @Test
    @DisplayName("프로젝트 ID로 재무 데이터 조회")
    void findByProjectId() {
        financialDataRepository.save(testFinancialData);

        Optional<FinancialData> found = financialDataRepository.findByProjectId(testProject.getId());

        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("프로젝트의 재무 데이터 존재 여부 확인")
    void existsByProject() {
        financialDataRepository.save(testFinancialData);

        boolean exists = financialDataRepository.existsByProject(testProject);

        assertThat(exists).isTrue();
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

    private FinancialData createTestFinancialData(Project project) {
        return FinancialData.builder()
                .project(project)
                .inputs("{\"customer\": {\"year1\": 100}}")
                .projections("{\"year1\": {\"revenue\": 1000000}}")
                .simulationResult("{\"breakEvenMonth\": 12}")
                .build();
    }
}

