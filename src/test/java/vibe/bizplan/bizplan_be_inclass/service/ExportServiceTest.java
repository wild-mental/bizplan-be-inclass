package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vibe.bizplan.bizplan_be_inclass.dto.export.*;
import vibe.bizplan.bizplan_be_inclass.entity.Export;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.ExportRepository;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ExportService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExportService 테스트")
class ExportServiceTest {

    @Mock
    private ExportRepository exportRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ExportService exportService;
    
    private ObjectMapper objectMapper;

    private UUID projectId;
    private Project testProject;
    private User testUser;
    private ExportRequest request;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // ExportService에 실제 ObjectMapper 주입
        org.springframework.test.util.ReflectionTestUtils.setField(exportService, "objectMapper", objectMapper);
        projectId = UUID.randomUUID();
        testProject = Project.builder()
                .id(projectId)
                .name("테스트 프로젝트")
                .templateCode("pre-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("테스트 사용자")
                .provider(User.AuthProvider.local)
                .build();

        request = ExportRequest.builder()
                .format("hwp")
                .templateType("pre-startup")
                .options(ExportRequest.ExportOptions.builder()
                        .includeAppendix(true)
                        .includeCoverPage(true)
                        .build())
                .build();
    }

    @Test
    @DisplayName("내보내기 요청 성공")
    void createExport_success() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(exportRepository.save(any(Export.class))).thenAnswer(invocation -> {
            Export export = invocation.getArgument(0);
            export.setId(UUID.randomUUID());
            return export;
        });

        // when
        ExportStatusResponse response = exportService.createExport(projectId, request, testUser);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("processing");
        assertThat(response.getFormat()).isEqualTo("hwp");
        verify(exportRepository).save(any(Export.class));
    }

    @Test
    @DisplayName("내보내기 요청 실패 - 프로젝트 없음")
    void createExport_projectNotFound() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> exportService.createExport(projectId, request, testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로젝트를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("내보내기 상태 조회 성공")
    void getExportStatus_success() {
        // given
        UUID exportId = UUID.randomUUID();
        Export export = Export.builder()
                .id(exportId)
                .project(testProject)
                .user(testUser)
                .format(Export.ExportFormat.hwp)
                .status(Export.ExportStatus.processing)
                .build();

        when(exportRepository.findById(exportId)).thenReturn(Optional.of(export));

        // when
        ExportStatusResponse response = exportService.getExportStatus(exportId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getExportId()).isEqualTo(exportId.toString());
        assertThat(response.getStatus()).isEqualTo("processing");
    }

    @Test
    @DisplayName("내보내기 상태 조회 - 완료 상태")
    void getExportStatus_completed() {
        // given
        UUID exportId = UUID.randomUUID();
        Export export = Export.builder()
                .id(exportId)
                .project(testProject)
                .user(testUser)
                .format(Export.ExportFormat.pdf)
                .status(Export.ExportStatus.completed)
                .fileName("test.pdf")
                .fileSize(2621440L)
                .build();

        when(exportRepository.findById(exportId)).thenReturn(Optional.of(export));

        // when
        ExportStatusResponse response = exportService.getExportStatus(exportId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("completed");
        assertThat(response.getFileName()).isEqualTo("test.pdf");
    }

    @Test
    @DisplayName("파일 다운로드 성공")
    void downloadExport_success() {
        // given
        UUID exportId = UUID.randomUUID();
        Export export = Export.builder()
                .id(exportId)
                .project(testProject)
                .user(testUser)
                .format(Export.ExportFormat.hwp)
                .status(Export.ExportStatus.completed)
                .fileName("test.hwp")
                .build();

        when(exportRepository.findById(exportId)).thenReturn(Optional.of(export));

        // when
        var resource = exportService.downloadExport(exportId);

        // then
        assertThat(resource).isNotNull();
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 파일 미준비")
    void downloadExport_notReady() {
        // given
        UUID exportId = UUID.randomUUID();
        Export export = Export.builder()
                .id(exportId)
                .project(testProject)
                .user(testUser)
                .format(Export.ExportFormat.hwp)
                .status(Export.ExportStatus.processing)
                .build();

        when(exportRepository.findById(exportId)).thenReturn(Optional.of(export));

        // when & then
        assertThatThrownBy(() -> exportService.downloadExport(exportId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("파일이 준비되지 않았습니다");
    }
}

