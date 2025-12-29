-- ============================================
-- V9: Create wizard_data table (SQLite)
-- Wizard 단계별 데이터 저장
-- ============================================

CREATE TABLE IF NOT EXISTS wizard_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id TEXT NOT NULL,
    step_number INTEGER NOT NULL,
    step_data TEXT NOT NULL,  -- JSON 문자열
    is_complete INTEGER DEFAULT 0,
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    UNIQUE (project_id, step_number)
);

CREATE INDEX IF NOT EXISTS idx_wizard_data_project ON wizard_data(project_id);
CREATE INDEX IF NOT EXISTS idx_wizard_data_project_step ON wizard_data(project_id, step_number);

