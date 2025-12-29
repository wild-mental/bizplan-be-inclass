package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 엔티티
 * 회원가입, 로그인 등 인증에 사용되는 사용자 정보를 저장합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 2 - 인증 API
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    /**
     * 사용자 고유 식별자 (UUID)
     */
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    /**
     * 이메일 (로그인 ID)
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 비밀번호 해시 (BCrypt)
     */
    @Column(name = "password_hash")
    private String passwordHash;

    /**
     * 사용자 이름
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 전화번호
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 인증 제공자 (local, google, kakao, naver)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 10)
    @Builder.Default
    private AuthProvider provider = AuthProvider.local;

    /**
     * 소셜 로그인 시 제공자에서 발급한 ID
     */
    @Column(name = "provider_id")
    private String providerId;

    /**
     * 사업 분야
     */
    @Column(name = "business_category", length = 50)
    private String businessCategory;

    /**
     * 마케팅 수신 동의 여부
     */
    @Column(name = "marketing_consent")
    @Builder.Default
    private Boolean marketingConsent = false;

    /**
     * 이메일 인증 여부
     */
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * 레코드 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 레코드 수정 시각
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

    /**
     * 인증 제공자 Enum
     */
    public enum AuthProvider {
        local, google, kakao, naver
    }
}

