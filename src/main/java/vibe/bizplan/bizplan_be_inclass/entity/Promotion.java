package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프로모션 설정 엔티티
 * 
 * Note: SQLite 호환 - TEXT 타입 사용
 */
@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Phase A
    @Column(name = "phase_a_start", nullable = false)
    private LocalDateTime phaseAStart;

    @Column(name = "phase_a_end", nullable = false)
    private LocalDateTime phaseAEnd;

    @Column(name = "phase_a_discount_rate", nullable = false)
    private Integer phaseADiscountRate;

    // Phase B
    @Column(name = "phase_b_start", nullable = false)
    private LocalDateTime phaseBStart;

    @Column(name = "phase_b_end")
    private LocalDateTime phaseBEnd;

    @Column(name = "phase_b_discount_rate", nullable = false)
    private Integer phaseBDiscountRate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // 감사 컬럼
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    /**
     * 현재 시점의 할인율 반환
     */
    public Integer getCurrentDiscountRate() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(phaseAStart) && now.isBefore(phaseAEnd)) {
            return phaseADiscountRate;
        } else if (now.isAfter(phaseBStart) && (phaseBEnd == null || now.isBefore(phaseBEnd))) {
            return phaseBDiscountRate;
        }
        return 0; // 프로모션 종료
    }

    /**
     * 현재 Phase 반환 ("A", "B", "ENDED", "NOT_STARTED")
     */
    public String getCurrentPhase() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(phaseAStart)) return "NOT_STARTED";
        if (now.isBefore(phaseAEnd)) return "A";
        if (phaseBEnd == null || now.isBefore(phaseBEnd)) return "B";
        return "ENDED";
    }

    /**
     * 프로모션 활성 상태 확인
     */
    public boolean isPromotionActive() {
        String phase = getCurrentPhase();
        return isActive && !"ENDED".equals(phase) && !"NOT_STARTED".equals(phase);
    }
}

