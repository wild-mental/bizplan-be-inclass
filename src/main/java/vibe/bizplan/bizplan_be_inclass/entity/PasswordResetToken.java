package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 비밀번호 재설정 토큰 엔티티
 * 비밀번호 재설정을 위한 토큰을 저장합니다.
 * 
 * Rule 303: snake_case naming, UUID PK, Audit columns
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PasswordResetToken {

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
     * 재설정 토큰 값
     */
    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;

    /**
     * 토큰 만료 시각
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 토큰 사용 여부 (재설정 완료 시 true)
     */
    @Column(name = "used")
    @Builder.Default
    private Boolean used = false;

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
     * 토큰 사용 처리
     */
    public void markAsUsed() {
        this.used = true;
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean isValid() {
        return !used && LocalDateTime.now().isBefore(expiresAt);
    }
}

