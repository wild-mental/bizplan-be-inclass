package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AI 평가 엔티티
 * 사업계획서 AI 평가 요청 및 결과를 저장합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 6 - AI 평가 API
 */
@Entity
@Table(name = "evaluations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    /**
     * 평가 대상 프로젝트
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * 평가 요청자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 평가 유형 (demo, basic, full)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_type", nullable = false, length = 10)
    private EvaluationType evaluationType;

    /**
     * 평가 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EvaluationStatus status = EvaluationStatus.pending;

    /**
     * 입력 데이터 (JSON 문자열)
     */
    @Column(name = "input_data", nullable = false, columnDefinition = "TEXT")
    private String inputData;

    /**
     * 평가 결과 (JSON 문자열)
     */
    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    /**
     * 총점
     */
    @Column(name = "total_score")
    private Integer totalScore;

    /**
     * 합격률
     */
    @Column(name = "pass_rate")
    private Integer passRate;

    /**
     * 평가 완료 시각
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

    public enum EvaluationType {
        demo, basic, full
    }

    public enum EvaluationStatus {
        pending, processing, completed, failed
    }
}

