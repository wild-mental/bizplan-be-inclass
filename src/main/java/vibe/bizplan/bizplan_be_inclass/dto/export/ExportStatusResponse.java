package vibe.bizplan.bizplan_be_inclass.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 문서 내보내기 상태 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 9.2 - 내보내기 상태 확인
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "문서 내보내기 상태 응답")
public class ExportStatusResponse {

    @Schema(description = "내보내기 ID")
    private String exportId;

    @Schema(description = "작업 상태", example = "completed")
    private String status;

    @Schema(description = "출력 형식", example = "hwp")
    private String format;

    @Schema(description = "파일 이름")
    private String fileName;

    @Schema(description = "파일 크기 (바이트)")
    private Long fileSize;

    @Schema(description = "예상 파일 크기 (처리 중일 때)")
    private String estimatedSize;

    @Schema(description = "예상 소요 시간 (초)")
    private Integer estimatedTime;

    @Schema(description = "다운로드 URL")
    private String downloadUrl;

    @Schema(description = "다운로드 링크 만료 시각")
    private LocalDateTime expiresAt;

    @Schema(description = "작업 완료 시각")
    private LocalDateTime completedAt;

    @Schema(description = "에러 메시지 (실패 시)")
    private String errorMessage;
}

