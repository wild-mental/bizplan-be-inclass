package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 구독 엔티티
 * 사용자의 요금제 구독 정보를 저장합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.1 - 회원가입
 */
@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    /**
     * 구독 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 요금제 표시명 (기본, 플러스, 프로, 프리미엄)
     */
    @Column(name = "plan", nullable = false, length = 10)
    private String plan;

    /**
     * 요금제 키 (basic, plus, pro, premium)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_key", nullable = false, length = 10)
    private PlanKey planKey;

    /**
     * 원래 가격
     */
    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    /**
     * 할인된 가격
     */
    @Column(name = "discounted_price")
    private Integer discountedPrice;

    /**
     * 할인율 (%)
     */
    @Column(name = "discount_rate")
    private Integer discountRate;

    /**
     * 프로모션 페이즈 (A, B, NONE)
     */
    @Column(name = "promotion_phase", length = 5)
    private String promotionPhase;

    /**
     * 프로모션 코드
     */
    @Column(name = "promotion_code", length = 50)
    private String promotionCode;

    /**
     * 구독 시작일
     */
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * 구독 종료일
     */
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    /**
     * 구독 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.active;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 요금제 키 Enum
     */
    public enum PlanKey {
        basic, plus, pro, premium
    }

    /**
     * 구독 상태 Enum
     */
    public enum SubscriptionStatus {
        active, expired, cancelled
    }
}

