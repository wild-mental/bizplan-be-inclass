package vibe.makersround.makersround_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_backend.dto.businessplan.BusinessPlanGenerateResponse.BusinessPlanSection;
import vibe.makersround.makersround_backend.dto.export.*;
import vibe.makersround.makersround_backend.entity.BusinessPlan;
import vibe.makersround.makersround_backend.entity.Export;
import vibe.makersround.makersround_backend.entity.Project;
import vibe.makersround.makersround_backend.entity.User;
import vibe.makersround.makersround_backend.exception.ResourceNotFoundException;
import vibe.makersround.makersround_backend.repository.BusinessPlanRepository;
import vibe.makersround.makersround_backend.repository.ExportRepository;
import vibe.makersround.makersround_backend.repository.ProjectRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private final BusinessPlanRepository businessPlanRepository;
    private final ObjectMapper objectMapper;
    
    private static final String EXPORT_DIR = "exports";

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

        try {
            Path filePath = Paths.get(export.getFilePath());
            if (!Files.exists(filePath)) {
                throw new ResourceNotFoundException("파일을 찾을 수 없습니다: " + export.getFileName());
            }
            byte[] fileContent = Files.readAllBytes(filePath);
            return new ByteArrayResource(fileContent);
        } catch (IOException e) {
            log.error("파일 읽기 실패: exportId={}, path={}", exportId, export.getFilePath(), e);
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다", e);
        }
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

    @Transactional
    protected void processExport(UUID exportId, ExportRequest request) {
        try {
            Export export = exportRepository.findById(exportId).orElseThrow();
            Project project = export.getProject();

            List<BusinessPlanSection> sections = getBusinessPlanSections(project.getId());
            if (sections.isEmpty()) {
                throw new IllegalStateException("내보낼 사업계획서 내용이 없습니다");
            }

            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String projectName = project.getName() != null ? project.getName() : "사업계획서";
            String fileName = String.format("%s_%s.%s", projectName, dateStr, request.getFormat());

            Path exportDir = Paths.get(EXPORT_DIR, exportId.toString());
            Files.createDirectories(exportDir);
            Path filePath = exportDir.resolve(fileName);

            byte[] fileContent = switch (Export.ExportFormat.valueOf(request.getFormat())) {
                case pdf -> generatePdf(projectName, sections);
                case docx -> generateDocx(projectName, sections);
                case hwp -> generateHwpFallback(projectName, sections);
            };

            Files.write(filePath, fileContent);

            export.setFileName(fileName);
            export.setFileSize((long) fileContent.length);
            export.setFilePath(filePath.toString());
            export.setExpiresAt(LocalDateTime.now().plusDays(1));
            export.setCompletedAt(LocalDateTime.now());
            export.setStatus(Export.ExportStatus.completed);

            exportRepository.save(export);
            log.info("문서 내보내기 완료: exportId={}, fileName={}, size={}", exportId, fileName, fileContent.length);

        } catch (Exception e) {
            log.error("문서 내보내기 실패: exportId={}", exportId, e);
            handleExportFailure(exportId, e.getMessage());
        }
    }
    
    private List<BusinessPlanSection> getBusinessPlanSections(UUID projectId) {
        List<BusinessPlan> plans = businessPlanRepository.findByProjectId(projectId);
        if (plans.isEmpty()) {
            return List.of();
        }
        
        // Get the most recent one (index 0 because repository orders by createdAt DESC)
        BusinessPlan latestPlan = plans.get(0);
        
        try {
            return objectMapper.readValue(latestPlan.getResponseSectionsJson(), 
                    new TypeReference<List<BusinessPlanSection>>() {});
        } catch (JsonProcessingException e) {
            log.warn("사업계획서 JSON 파싱 실패: projectId={}", projectId, e);
            return List.of();
        }
    }
    
    private byte[] generatePdf(String title, List<BusinessPlanSection> sections) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            for (BusinessPlanSection section : sections) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);
                    
                    String sectionTitle = section.getTitle() != null ? section.getTitle() : "섹션";
                    String content = section.getContent() != null ? section.getContent() : "";
                    
                    contentStream.showText(sectionTitle);
                    contentStream.newLineAtOffset(0, -20);
                    
                    String[] lines = content.split("\n");
                    for (String line : lines) {
                        if (line.length() > 80) {
                            line = line.substring(0, 80) + "...";
                        }
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -15);
                    }
                    contentStream.endText();
                }
            }
            
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private byte[] generateDocx(String title, List<BusinessPlanSection> sections) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(title);
            titleRun.setBold(true);
            titleRun.setFontSize(24);
            
            for (BusinessPlanSection section : sections) {
                XWPFParagraph sectionTitle = document.createParagraph();
                XWPFRun sectionTitleRun = sectionTitle.createRun();
                sectionTitleRun.setText(section.getTitle() != null ? section.getTitle() : "섹션");
                sectionTitleRun.setBold(true);
                sectionTitleRun.setFontSize(16);
                
                XWPFParagraph contentParagraph = document.createParagraph();
                XWPFRun contentRun = contentParagraph.createRun();
                String content = section.getContent() != null ? section.getContent() : "";
                
                String[] lines = content.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    contentRun.setText(lines[i]);
                    if (i < lines.length - 1) {
                        contentRun.addBreak();
                    }
                }
            }
            
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private byte[] generateHwpFallback(String title, List<BusinessPlanSection> sections) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(title).append(" ===\n\n");
        
        for (BusinessPlanSection section : sections) {
            sb.append("## ").append(section.getTitle() != null ? section.getTitle() : "섹션").append("\n\n");
            sb.append(section.getContent() != null ? section.getContent() : "").append("\n\n");
        }
        
        sb.append("\n[참고: HWP 형식은 텍스트 파일로 제공됩니다. 한글 프로그램에서 열어 서식을 적용해주세요.]\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    private void handleExportFailure(UUID exportId, String errorMessage) {
        try {
            Export export = exportRepository.findById(exportId).orElse(null);
            if (export != null) {
                export.setStatus(Export.ExportStatus.failed);
                export.setErrorMessage(errorMessage);
                exportRepository.save(export);
            }
        } catch (Exception ex) {
            log.error("내보내기 실패 상태 업데이트 실패: exportId={}", exportId, ex);
        }
    }
}

