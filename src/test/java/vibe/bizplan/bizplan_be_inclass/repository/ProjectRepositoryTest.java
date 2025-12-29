package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProjectRepository 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ProjectRepository 테스트")
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUser = userRepository.save(testUser);
        testProject = createTestProject(testUser);
    }

    @Test
    @DisplayName("프로젝트 저장 및 조회")
    void save_and_findById() {
        Project saved = projectRepository.save(testProject);
        var found = projectRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTemplateCode()).isEqualTo("pre-startup");
        assertThat(found.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("사용자별 프로젝트 목록 조회 (페이징)")
    void findByUserOrderByUpdatedAtDesc() {
        projectRepository.save(testProject);

        Project anotherProject = Project.builder()
                .user(testUser)
                .name("프로젝트 2")
                .templateCode("early-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();
        projectRepository.save(anotherProject);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = projectRepository.findByUserOrderByUpdatedAtDesc(testUser, pageable);

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("사용자별 상태별 프로젝트 조회")
    void findByUserAndStatusOrderByUpdatedAtDesc() {
        projectRepository.save(testProject);

        Project completedProject = Project.builder()
                .user(testUser)
                .name("완료된 프로젝트")
                .templateCode("pre-startup")
                .status(Project.ProjectStatus.completed)
                .currentStep(8)
                .build();
        projectRepository.save(completedProject);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = projectRepository.findByUserAndStatusOrderByUpdatedAtDesc(
                testUser, Project.ProjectStatus.completed, pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getStatus())
                .isEqualTo(Project.ProjectStatus.completed);
    }

    @Test
    @DisplayName("사용자별 프로젝트 수 조회")
    void countByUser() {
        projectRepository.save(testProject);

        Project anotherProject = Project.builder()
                .user(testUser)
                .name("프로젝트 2")
                .templateCode("early-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();
        projectRepository.save(anotherProject);

        long count = projectRepository.countByUser(testUser);

        assertThat(count).isEqualTo(2);
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
}

