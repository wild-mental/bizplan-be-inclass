package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사전 등록 엔티티
 * 
 * Rule 303: snake_case naming, UUID PK, Audit columns
 * Rule 306: Entity는 Repository Layer에서만 사용
 * 
 * Note: SQLite 호환을 위해 TEXT 타입 사용
 */
@Entity
@Table(name = "pre_registrations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PreRegistration {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    // 사용자 정보
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // 요금제 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_plan", nullable = false, length = 10)
    private PlanType selectedPlan;

    @Column(name = "business_category", length = 50)
    private String businessCategory;

    // 동의 항목
    @Column(name = "agree_terms", nullable = false)
    private Boolean agreeTerms;

    @Column(name = "agree_marketing", nullable = false)
    private Boolean agreeMarketing;

    // 할인 정보
    @Column(name = "discount_code", nullable = false, unique = true, length = 20)
    private String discountCode;

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discounted_price", nullable = false)
    private Integer discountedPrice;

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.CONFIRMED;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

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
        this.registeredAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상태 변경 메서드
     */
    public void updateStatus(RegistrationStatus newStatus) {
        this.status = newStatus;
    }

    // Enum 정의
    public enum PlanType {
        plus, pro, premium
    }

    public enum RegistrationStatus {
        PENDING, CONFIRMED, CANCELLED, CONVERTED
    }
}

