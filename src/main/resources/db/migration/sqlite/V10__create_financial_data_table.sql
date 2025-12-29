-- ============================================
-- V10: Create financial_data table (SQLite)
-- 재무 시뮬레이션 데이터 저장
-- ============================================

CREATE TABLE IF NOT EXISTS financial_data (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    project_id TEXT NOT NULL,
    inputs TEXT NOT NULL,  -- JSON 문자열
    projections TEXT,  -- JSON 문자열
    simulation_result TEXT,  -- JSON 문자열
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_financial_data_project ON financial_data(project_id);

