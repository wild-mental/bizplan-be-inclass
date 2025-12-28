package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.export.*;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.security.CustomUserDetailsService;
import vibe.bizplan.bizplan_be_inclass.service.ExportService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 문서 내보내기 컨트롤러
 * 사업계획서 문서 내보내기 API를 제공합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 9 - 문서 내보내기 API
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Exports", description = "문서 내보내기 API")
public class ExportController {

    private final ExportService exportService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 문서 내보내기 요청
     * 
     * POST /api/v1/projects/{projectId}/export
     */
    @Operation(
            summary = "문서 내보내기 요청",
            description = "사업계획서를 HWP, PDF, DOCX 형식으로 내보냅니다. 비동기로 처리됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "202",
                    description = "내보내기 요청 접수됨",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExportStatusResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음"
            )
    })
    @PostMapping("/api/v1/projects/{projectId}/export")
    public ResponseEntity<ApiResponse<ExportStatusResponse>> createExport(
            @Parameter(description = "프로젝트 ID", required = true)
            @PathVariable UUID projectId,
            @Parameter(description = "내보내기 요청", required = true)
            @Valid @RequestBody ExportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = null;
        if (userDetails != null) {
            user = userDetailsService.getUserByEmail(userDetails.getUsername());
        }

        ExportStatusResponse response = exportService.createExport(projectId, request, user);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(response));
    }

    /**
     * 내보내기 상태 조회
     * 
     * GET /api/v1/exports/{exportId}/status
     */
    @Operation(
            summary = "내보내기 상태 조회",
            description = "내보내기 작업의 진행 상태를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상태 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExportStatusResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "내보내기를 찾을 수 없음"
            )
    })
    @GetMapping("/api/v1/exports/{exportId}/status")
    public ResponseEntity<ApiResponse<ExportStatusResponse>> getExportStatus(
            @Parameter(description = "내보내기 ID", required = true)
            @PathVariable UUID exportId) {

        ExportStatusResponse response = exportService.getExportStatus(exportId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 파일 다운로드
     * 
     * GET /api/v1/exports/{exportId}/download
     */
    @Operation(
            summary = "파일 다운로드",
            description = "완료된 내보내기 파일을 다운로드합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "파일 다운로드"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "내보내기를 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "파일이 준비되지 않았거나 만료됨"
            )
    })
    @GetMapping("/api/v1/exports/{exportId}/download")
    public ResponseEntity<Resource> downloadExport(
            @Parameter(description = "내보내기 ID", required = true)
            @PathVariable UUID exportId) {

        Resource resource = exportService.downloadExport(exportId);
        String fileName = exportService.getFileName(exportId);
        String contentType = exportService.getContentType(exportId);

        // 한글 파일명 인코딩
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }
}

