package vibe.bizplan.bizplan_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사업계획서 생성 요청 및 응답을 저장하는 엔티티
 * 
 * 저장 내용:
 * - 요청 데이터 (BusinessPlanGenerateRequest 전체)
 * - 응답 데이터 (생성된 섹션들)
 * - Gemini 메타데이터 (토큰 사용량, 시간 정보 등)
 * 
 * Rule 303: snake_case naming, UUID PK, Audit columns
 * Rule 306: Entity는 Repository Layer에서만 사용, Service에서 DTO 변환
 */
@Entity
@Table(name = "business_plans", indexes = {
    @Index(name = "idx_business_plans_business_plan_id", columnList = "business_plan_id"),
    @Index(name = "idx_business_plans_project_id", columnList = "project_id"),
    @Index(name = "idx_business_plans_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BusinessPlan {

    /**
     * 엔티티 고유 식별자 (UUID)
     */
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private UUID id;

    /**
     * 사업계획서 고유 ID (bp-2025-12-20-xxx 형식)
     * API 응답에 사용되는 ID
     */
    @Column(name = "business_plan_id", nullable = false, unique = true, length = 50)
    private String businessPlanId;

    /**
     * 프로젝트 ID (projects 테이블과의 관계)
     * nullable: true (프로젝트 없이도 생성 가능)
     */
    @Column(name = "project_id", columnDefinition = "CHAR(36)")
    private UUID projectId;

    /**
     * 사용자 ID
     */
    @Column(name = "user_id", length = 36)
    private String userId;

    /**
     * 템플릿 유형 (pre-startup, early-startup, bank-loan 등)
     */
    @Column(name = "template_type", nullable = false, length = 20)
    private String templateType;

    /**
     * 요청 데이터 전체 (BusinessPlanGenerateRequest JSON)
     * Rule 303: TEXT 타입 사용
     */
    @Column(name = "request_data_json", columnDefinition = "TEXT", nullable = false)
    private String requestDataJson;

    /**
     * 응답 섹션 데이터 (BusinessPlanSection 리스트 JSON)
     * Rule 303: TEXT 타입 사용
     */
    @Column(name = "response_sections_json", columnDefinition = "TEXT", nullable = false)
    private String responseSectionsJson;

    /**
     * Gemini 메타데이터 (토큰 사용량, 시간 정보 등 JSON)
     * Rule 303: TEXT 타입 사용
     */
    @Column(name = "gemini_metadata_json", columnDefinition = "TEXT")
    private String geminiMetadataJson;

    /**
     * 레코드 생성 시각
     * Rule 303: Audit column
     */
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * 레코드 수정 시각
     * Rule 303: Audit column
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
}
