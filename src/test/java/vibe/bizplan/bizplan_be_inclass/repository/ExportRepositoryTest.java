package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.Export;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ExportRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ExportRepository 테스트")
class ExportRepositoryTest {

    @Autowired
    private ExportRepository exportRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Project testProject;
    private Export testExport;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUser = userRepository.save(testUser);
        testProject = createTestProject(testUser);
        testProject = projectRepository.save(testProject);
        testExport = createTestExport(testProject, testUser);
    }

    @Test
    @DisplayName("내보내기 저장 및 조회")
    void save_and_findById() {
        Export saved = exportRepository.save(testExport);
        var found = exportRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFormat()).isEqualTo(Export.ExportFormat.hwp);
        assertThat(found.get().getStatus()).isEqualTo(Export.ExportStatus.pending);
    }

    @Test
    @DisplayName("사용자별 내보내기 목록 조회 (최신순)")
    void findByUserOrderByCreatedAtDesc() {
        exportRepository.save(testExport);

        Export anotherExport = Export.builder()
                .project(testProject)
                .user(testUser)
                .format(Export.ExportFormat.pdf)
                .status(Export.ExportStatus.completed)
                .build();
        exportRepository.save(anotherExport);

        List<Export> exports = exportRepository.findByUserOrderByCreatedAtDesc(testUser);

        assertThat(exports).hasSize(2);
        assertThat(exports.get(0).getCreatedAt())
                .isAfterOrEqualTo(exports.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("프로젝트별 내보내기 목록 조회")
    void findByProjectOrderByCreatedAtDesc() {
        exportRepository.save(testExport);

        Export anotherExport = Export.builder()
                .project(testProject)
                .user(testUser)
                .format(Export.ExportFormat.docx)
                .status(Export.ExportStatus.processing)
                .build();
        exportRepository.save(anotherExport);

        List<Export> exports = exportRepository.findByProjectOrderByCreatedAtDesc(testProject);

        assertThat(exports).hasSize(2);
    }

    @Test
    @DisplayName("상태별 내보내기 조회")
    void findByStatus() {
        exportRepository.save(testExport);

        Export completed = Export.builder()
                .project(testProject)
                .user(testUser)
                .format(Export.ExportFormat.pdf)
                .status(Export.ExportStatus.completed)
                .build();
        exportRepository.save(completed);

        List<Export> completedExports = exportRepository
                .findByStatus(Export.ExportStatus.completed);

        assertThat(completedExports).hasSize(1);
        assertThat(completedExports.get(0).getStatus())
                .isEqualTo(Export.ExportStatus.completed);
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

    private Export createTestExport(Project project, User user) {
        return Export.builder()
                .project(project)
                .user(user)
                .format(Export.ExportFormat.hwp)
                .status(Export.ExportStatus.pending)
                .build();
    }
}

