-- ============================================
-- V1: Create projects table
-- Rule 303: snake_case naming, UUID PK, Audit columns
-- ============================================

CREATE TABLE projects (
    id CHAR(36) NOT NULL COMMENT 'UUID 형식의 프로젝트 고유 식별자',
    template_code VARCHAR(50) NOT NULL COMMENT '사용된 템플릿 코드',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '프로젝트 상태 (draft, completed 등)',
    created_at DATETIME NOT NULL COMMENT '레코드 생성 시각',
    updated_at DATETIME NOT NULL COMMENT '레코드 수정 시각',
    
    PRIMARY KEY (id),
    INDEX idx_projects_status (status),
    INDEX idx_projects_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='사용자가 생성한 사업계획서 프로젝트';

