-- ============================================
-- V2: Create business_plans table
-- Rule 303: snake_case naming, UUID PK, Audit columns
-- Rule 306: 3-tier architecture - Entity for Repository Layer
-- ============================================

CREATE TABLE business_plans (
    id CHAR(36) NOT NULL COMMENT 'UUID 형식의 엔티티 고유 식별자',
    business_plan_id VARCHAR(50) NOT NULL UNIQUE COMMENT '사업계획서 고유 ID (bp-2025-12-20-xxx 형식)',
    project_id CHAR(36) COMMENT '프로젝트 ID (projects 테이블 FK, nullable)',
    user_id VARCHAR(36) COMMENT '사용자 ID',
    template_type VARCHAR(20) NOT NULL COMMENT '템플릿 유형 (pre-startup, early-startup, bank-loan 등)',
    request_data_json TEXT NOT NULL COMMENT '요청 데이터 전체 (BusinessPlanGenerateRequest JSON)',
    response_sections_json TEXT NOT NULL COMMENT '응답 섹션 데이터 (BusinessPlanSection 리스트 JSON)',
    gemini_metadata_json TEXT COMMENT 'Gemini 메타데이터 (토큰 사용량, 시간 정보 등 JSON)',
    created_at TIMESTAMP NOT NULL COMMENT '레코드 생성 시각',
    updated_at TIMESTAMP NOT NULL COMMENT '레코드 수정 시각',
    
    PRIMARY KEY (id),
    INDEX idx_business_plans_business_plan_id (business_plan_id),
    INDEX idx_business_plans_project_id (project_id),
    INDEX idx_business_plans_created_at (created_at),
    
    -- Foreign Key 제약조건 (선택적 - 프로젝트가 없을 수도 있음)
    CONSTRAINT fk_business_plans_project_id 
        FOREIGN KEY (project_id) REFERENCES projects(id) 
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='사업계획서 생성 요청 및 응답 저장';
