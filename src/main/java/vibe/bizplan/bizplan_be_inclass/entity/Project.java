package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프로젝트 엔티티
 * 사용자가 생성한 사업계획서 프로젝트를 나타냅니다.
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Project {

    /**
     * 프로젝트 고유 식별자 (UUID)
     */
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private UUID id;

    /**
     * 사용된 템플릿 코드 (예: KSTARTUP_2025, BANK_LOAN_2025)
     */
    @Column(name = "template_code", nullable = false, length = 50)
    private String templateCode;

    /**
     * 프로젝트 상태 (draft, completed 등)
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "draft";

    /**
     * 레코드 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * 레코드 수정 시각
     */
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    /**
     * 엔티티 저장 전 호출 - 생성/수정 시각 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 수정 전 호출 - 수정 시각 자동 갱신
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프로젝트 생성을 위한 정적 팩토리 메서드
     * 
     * @param templateCode 사용할 템플릿 코드
     * @return 새로운 Project 인스턴스 (status=draft)
     */
    public static Project createWithTemplate(String templateCode) {
        return Project.builder()
                .templateCode(templateCode)
                .status("draft")
                .build();
    }
}

