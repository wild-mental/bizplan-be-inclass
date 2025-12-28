package vibe.bizplan.bizplan_be_inclass.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * 문서 내보내기 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 9.1 - 문서 내보내기 요청
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "문서 내보내기 요청")
public class ExportRequest {

    @NotBlank(message = "출력 형식은 필수입니다")
    @Pattern(regexp = "^(hwp|pdf|docx)$", message = "지원하지 않는 형식입니다")
    @Schema(description = "출력 형식", example = "hwp", allowableValues = {"hwp", "pdf", "docx"})
    private String format;

    @Schema(description = "템플릿 유형", example = "2026_예비창업패키지")
    private String templateType;

    @Schema(description = "내보내기 옵션")
    private ExportOptions options;

    @Schema(description = "포함할 섹션 목록 (all = 전체)")
    @Builder.Default
    private List<String> sections = List.of("all");

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExportOptions {
        @Builder.Default
        private Boolean maskPersonalInfo = true;
        @Builder.Default
        private Boolean includeAppendix = true;
        @Builder.Default
        private Boolean includeCoverPage = true;
        @Builder.Default
        private Boolean pageNumbering = true;
        @Builder.Default
        private Boolean watermark = false;
    }
}

