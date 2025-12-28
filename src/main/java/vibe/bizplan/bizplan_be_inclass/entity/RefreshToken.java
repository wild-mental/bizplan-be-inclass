package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 리프레시 토큰 엔티티
 * JWT 리프레시 토큰을 저장하여 토큰 갱신 및 로그아웃 처리에 사용합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.4 - 토큰 갱신
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    /**
     * 토큰 소유자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 리프레시 토큰 값
     */
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    /**
     * 토큰 만료 시각
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 토큰 폐기 여부 (로그아웃 시 true)
     */
    @Column(name = "revoked")
    @Builder.Default
    private Boolean revoked = false;

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
     * 토큰 폐기
     */
    public void revoke() {
        this.revoked = true;
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean isValid() {
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }
}

