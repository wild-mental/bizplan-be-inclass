package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Wizard 데이터 엔티티
 * 각 프로젝트의 Wizard 단계별 입력 데이터를 저장합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 5 - 사업계획서 작성 Wizard API
 */
@Entity
@Table(name = "wizard_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "step_number"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WizardData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연관 프로젝트
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Wizard 단계 번호 (1-8)
     */
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    /**
     * 단계 데이터 (JSON 문자열)
     */
    @Column(name = "step_data", nullable = false, columnDefinition = "TEXT")
    private String stepData;

    /**
     * 단계 완료 여부
     */
    @Column(name = "is_complete")
    @Builder.Default
    private Boolean isComplete = false;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시각
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

