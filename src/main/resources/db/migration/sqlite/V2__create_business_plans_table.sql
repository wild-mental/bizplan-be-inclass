-- ============================================
-- V2: Create business_plans table (SQLite)
-- Rule 303: snake_case naming, UUID PK, Audit columns
-- Rule 306: 3-tier architecture - Entity for Repository Layer
-- ============================================

CREATE TABLE IF NOT EXISTS business_plans (
    -- UUID PK (SQLite: TEXT 타입)
    id TEXT NOT NULL PRIMARY KEY,
    
    -- 사업계획서 정보
    business_plan_id TEXT NOT NULL UNIQUE,
    project_id TEXT,
    user_id TEXT,
    template_type TEXT NOT NULL,
    
    -- JSON 데이터
    request_data_json TEXT NOT NULL,
    response_sections_json TEXT NOT NULL,
    gemini_metadata_json TEXT,
    
    -- 감사 컬럼 (ISO 8601 형식)
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    
    -- FK 제약조건 (SQLite)
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_business_plans_business_plan_id ON business_plans(business_plan_id);
CREATE INDEX IF NOT EXISTS idx_business_plans_project_id ON business_plans(project_id);
CREATE INDEX IF NOT EXISTS idx_business_plans_created_at ON business_plans(created_at);

