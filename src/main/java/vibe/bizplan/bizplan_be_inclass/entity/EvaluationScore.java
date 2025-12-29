package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 평가 영역별 점수 엔티티
 * 6대 평가 영역(시장성, 수행능력, 핵심기술, 경제성, 실현가능성, 사회적가치)별 점수를 저장합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 6.3 - 평가 결과 조회
 */
@Entity
@Table(name = "evaluation_scores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"evaluation_id", "area_code"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EvaluationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연관 평가
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    /**
     * 평가 영역 코드
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "area_code", nullable = false, length = 15)
    private AreaCode areaCode;

    /**
     * 점수 (0-100)
     */
    @Column(name = "score", nullable = false)
    private Integer score;

    /**
     * 피드백
     */
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    /**
     * 강점 (JSON 문자열)
     */
    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    /**
     * 약점 (JSON 문자열)
     */
    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum AreaCode {
        market, ability, technology, economics, realization, social
    }
}

