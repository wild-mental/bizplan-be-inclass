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
 * @see PRE-SUB-FUNC-002.md Section 4 - 프로젝트 관리 API
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Project {

    /**
     * 프로젝트 고유 식별자 (UUID)
     */
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    /**
     * 프로젝트 소유자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 프로젝트 이름
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * 사용된 템플릿 코드 (예: pre-startup, early-startup, policy-fund)
     */
    @Column(name = "template_code", nullable = false, length = 50)
    private String templateCode;

    /**
     * 지원 프로그램 (예: 2026-1)
     */
    @Column(name = "support_program", length = 50)
    private String supportProgram;

    /**
     * 프로젝트 설명
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 프로젝트 상태 (draft, in_progress, completed, archived)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.draft;

    /**
     * 현재 Wizard 단계
     */
    @Column(name = "current_step")
    @Builder.Default
    private Integer currentStep = 1;

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
                .status(ProjectStatus.draft)
                .currentStep(1)
                .build();
    }

    /**
     * 프로젝트 상태 Enum
     */
    public enum ProjectStatus {
        draft, in_progress, completed, archived
    }
}

