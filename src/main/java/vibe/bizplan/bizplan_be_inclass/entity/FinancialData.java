package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재무 데이터 엔티티
 * 프로젝트의 재무 시뮬레이션 입력 및 결과를 저장합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 8 - 재무 시뮬레이션 API
 */
@Entity
@Table(name = "financial_data")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FinancialData {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    /**
     * 연관 프로젝트
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * 입력 데이터 (JSON 문자열)
     */
    @Column(name = "inputs", nullable = false, columnDefinition = "TEXT")
    private String inputs;

    /**
     * 연도별/월별 예측 데이터 (JSON 문자열)
     */
    @Column(name = "projections", columnDefinition = "TEXT")
    private String projections;

    /**
     * 시뮬레이션 결과 (JSON 문자열)
     */
    @Column(name = "simulation_result", columnDefinition = "TEXT")
    private String simulationResult;

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

