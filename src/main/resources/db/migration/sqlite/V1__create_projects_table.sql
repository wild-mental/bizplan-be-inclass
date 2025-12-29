-- ============================================
-- V1: Create projects table (SQLite)
-- Rule 303: snake_case naming, UUID PK, Audit columns
-- ============================================

CREATE TABLE IF NOT EXISTS projects (
    -- UUID PK (SQLite: TEXT 타입)
    id TEXT NOT NULL PRIMARY KEY,
    
    -- 프로젝트 정보
    template_code TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'draft' CHECK (status IN ('draft', 'completed', 'archived')),
    
    -- 감사 컬럼 (ISO 8601 형식)
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
CREATE INDEX IF NOT EXISTS idx_projects_created_at ON projects(created_at);

