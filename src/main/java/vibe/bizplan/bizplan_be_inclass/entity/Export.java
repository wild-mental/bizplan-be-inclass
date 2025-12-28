package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 문서 내보내기 엔티티
 * 문서 내보내기 작업 상태 및 결과를 저장합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 9 - 문서 내보내기 API
 */
@Entity
@Table(name = "exports")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Export {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    /**
     * 대상 프로젝트
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * 요청자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 대상 사업계획서 ID
     */
    @Column(name = "business_plan_id")
    private String businessPlanId;

    /**
     * 출력 형식 (hwp, pdf, docx)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false, length = 10)
    private ExportFormat format;

    /**
     * 템플릿 유형
     */
    @Column(name = "template_type", length = 50)
    private String templateType;

    /**
     * 내보내기 옵션 (JSON 문자열)
     */
    @Column(name = "options", columnDefinition = "TEXT")
    private String options;

    /**
     * 작업 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ExportStatus status = ExportStatus.pending;

    /**
     * 생성된 파일 경로
     */
    @Column(name = "file_path")
    private String filePath;

    /**
     * 파일 이름
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 파일 크기 (바이트)
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 에러 메시지 (실패 시)
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 다운로드 링크 만료 시각
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * 작업 완료 시각
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ExportFormat {
        hwp, pdf, docx
    }

    public enum ExportStatus {
        pending, processing, completed, failed
    }
}

