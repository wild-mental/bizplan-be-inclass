package vibe.bizplan.bizplan_be_inclass.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.exception.InvalidTemplateException;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * ProjectService 단위 테스트
 * 
 * Rule 301: Use JUnit 5 + Mockito
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private ProjectService projectService;

    private Project savedProject;

    @BeforeEach
    void setUp() {
        // 저장된 프로젝트 모의 객체 생성
        savedProject = Project.builder()
                .id(UUID.randomUUID())
                .templateCode("KSTARTUP_2025")
                .status("draft")
                .build();
    }

    @Test
    @DisplayName("createProject - 유효한 템플릿으로 프로젝트를 생성한다")
    void createProject_validTemplate_createsProject() {
        // given
        String templateCode = "KSTARTUP_2025";
        given(templateService.isValidTemplate(templateCode)).willReturn(true);
        given(projectRepository.save(any(Project.class))).willReturn(savedProject);

        // when
        Project result = projectService.createProject(templateCode);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTemplateCode()).isEqualTo(templateCode);
        assertThat(result.getStatus()).isEqualTo("draft");
        
        verify(templateService).isValidTemplate(templateCode);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("createProject - 유효하지 않은 템플릿이면 InvalidTemplateException을 던진다")
    void createProject_invalidTemplate_throwsException() {
        // given
        String invalidCode = "INVALID_CODE";
        given(templateService.isValidTemplate(invalidCode)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> projectService.createProject(invalidCode))
                .isInstanceOf(InvalidTemplateException.class)
                .hasMessageContaining(invalidCode);
    }

    @Test
    @DisplayName("getProject - 존재하는 프로젝트 ID로 조회한다")
    void getProject_existingId_returnsProject() {
        // given
        UUID projectId = savedProject.getId();
        given(projectRepository.findById(projectId)).willReturn(Optional.of(savedProject));

        // when
        Project result = projectService.getProject(projectId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(projectId);
    }

    @Test
    @DisplayName("getProject - 존재하지 않는 프로젝트 ID로 조회하면 예외를 던진다")
    void getProject_nonExistingId_throwsException() {
        // given
        UUID nonExistingId = UUID.randomUUID();
        given(projectRepository.findById(nonExistingId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectService.getProject(nonExistingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("프로젝트를 찾을 수 없습니다");
    }
}

