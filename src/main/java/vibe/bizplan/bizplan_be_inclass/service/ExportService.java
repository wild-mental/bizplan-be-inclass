package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.export.*;
import vibe.bizplan.bizplan_be_inclass.entity.Export;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.ExportRepository;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 문서 내보내기 서비스
 * HWP, PDF, DOCX 형식으로 사업계획서를 내보냅니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 9 - 문서 내보내기 API
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExportService {

    private final ExportRepository exportRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;

    /**
     * 내보내기 요청
     * 
     * @param projectId 프로젝트 ID
     * @param request 내보내기 요청
     * @param user 요청자
     * @return 내보내기 상태 응답
     */
    @Transactional
    public ExportStatusResponse createExport(UUID projectId, ExportRequest request, User user) {
        log.info("문서 내보내기 요청: projectId={}, format={}", projectId, request.getFormat());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        // 내보내기 엔티티 생성
        Export export = Export.builder()
                .project(project)
                .user(user)
                .format(Export.ExportFormat.valueOf(request.getFormat()))
                .templateType(request.getTemplateType())
                .status(Export.ExportStatus.processing)
                .build();

        try {
            if (request.getOptions() != null) {
                export.setOptions(objectMapper.writeValueAsString(request.getOptions()));
            }
        } catch (JsonProcessingException e) {
            log.warn("옵션 직렬화 실패", e);
        }

        export = exportRepository.save(export);

        // 비동기 처리 시작
        final UUID exportId = export.getId();
        CompletableFuture.runAsync(() -> processExport(exportId, request));

        return ExportStatusResponse.builder()
                .exportId(export.getId().toString())
                .status("processing")
                .format(request.getFormat())
                .estimatedSize("2.5MB")
                .estimatedTime(30)
                .build();
    }

    /**
     * 내보내기 상태 조회
     * 
     * @param exportId 내보내기 ID
     * @return 내보내기 상태 응답
     */
    public ExportStatusResponse getExportStatus(UUID exportId) {
        Export export = exportRepository.findById(exportId)
                .orElseThrow(() -> new ResourceNotFoundException("내보내기를 찾을 수 없습니다: " + exportId));

        ExportStatusResponse.ExportStatusResponseBuilder builder = ExportStatusResponse.builder()
                .exportId(exportId.toString())
                .status(export.getStatus().name())
                .format(export.getFormat().name());

        if (export.getStatus() == Export.ExportStatus.completed) {
            builder
                    .fileName(export.getFileName())
                    .fileSize(export.getFileSize())
                    .downloadUrl("/api/v1/exports/" + exportId + "/download")
                    .expiresAt(export.getExpiresAt())
                    .completedAt(export.getCompletedAt());
        } else if (export.getStatus() == Export.ExportStatus.failed) {
            builder.errorMessage(export.getErrorMessage());
        } else {
            builder.estimatedTime(15);
        }

        return builder.build();
    }

    /**
     * 파일 다운로드
     * 
     * @param exportId 내보내기 ID
     * @return 파일 리소스
     */
    public Resource downloadExport(UUID exportId) {
        Export export = exportRepository.findById(exportId)
                .orElseThrow(() -> new ResourceNotFoundException("내보내기를 찾을 수 없습니다: " + exportId));

        if (export.getStatus() != Export.ExportStatus.completed) {
            throw new IllegalStateException("파일이 준비되지 않았습니다");
        }

        if (export.getExpiresAt() != null && LocalDateTime.now().isAfter(export.getExpiresAt())) {
            throw new IllegalStateException("다운로드 링크가 만료되었습니다");
        }

        // 실제로는 파일 시스템에서 파일을 읽어야 함
        // 여기서는 더미 콘텐츠 반환
        String dummyContent = "사업계획서 내용 (더미 파일)";
        return new ByteArrayResource(dummyContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 파일 이름 조회
     * 
     * @param exportId 내보내기 ID
     * @return 파일 이름
     */
    public String getFileName(UUID exportId) {
        Export export = exportRepository.findById(exportId)
                .orElseThrow(() -> new ResourceNotFoundException("내보내기를 찾을 수 없습니다: " + exportId));
        return export.getFileName();
    }

    /**
     * Content-Type 조회
     * 
     * @param exportId 내보내기 ID
     * @return Content-Type
     */
    public String getContentType(UUID exportId) {
        Export export = exportRepository.findById(exportId)
                .orElseThrow(() -> new ResourceNotFoundException("내보내기를 찾을 수 없습니다: " + exportId));

        return switch (export.getFormat()) {
            case hwp -> "application/vnd.hancom.hwp";
            case pdf -> "application/pdf";
            case docx -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        };
    }

    /**
     * 내보내기 처리 (비동기)
     */
    @Transactional
    protected void processExport(UUID exportId, ExportRequest request) {
        try {
            Thread.sleep(3000); // 시뮬레이션 지연

            Export export = exportRepository.findById(exportId).orElseThrow();
            Project project = export.getProject();

            // 파일 이름 생성
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String projectName = project.getName() != null ? project.getName() : "사업계획서";
            String fileName = String.format("%s_%s.%s", projectName, dateStr, request.getFormat());

            // 실제로는 여기서 문서 생성 로직 수행
            // HWP: Apache POI + hwp 라이브러리
            // PDF: iText 또는 Apache PDFBox
            // DOCX: Apache POI

            export.setFileName(fileName);
            export.setFileSize(2621440L); // 더미 크기 (2.5MB)
            export.setFilePath("/exports/" + exportId + "/" + fileName);
            export.setExpiresAt(LocalDateTime.now().plusDays(1));
            export.setCompletedAt(LocalDateTime.now());
            export.setStatus(Export.ExportStatus.completed);

            exportRepository.save(export);
            log.info("문서 내보내기 완료: exportId={}, fileName={}", exportId, fileName);

        } catch (Exception e) {
            log.error("문서 내보내기 실패: exportId={}", exportId, e);
            Export export = exportRepository.findById(exportId).orElse(null);
            if (export != null) {
                export.setStatus(Export.ExportStatus.failed);
                export.setErrorMessage(e.getMessage());
                exportRepository.save(export);
            }
        }
    }
}

