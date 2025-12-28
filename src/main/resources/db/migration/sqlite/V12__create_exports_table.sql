-- ============================================
-- V12: Create exports table (SQLite)
-- 문서 내보내기 작업 저장
-- ============================================

CREATE TABLE IF NOT EXISTS exports (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    project_id TEXT NOT NULL,
    user_id TEXT,
    business_plan_id TEXT,
    format TEXT NOT NULL CHECK (format IN ('hwp', 'pdf', 'docx')),
    template_type TEXT,
    options TEXT,  -- JSON 문자열
    status TEXT DEFAULT 'pending' 
        CHECK (status IN ('pending', 'processing', 'completed', 'failed')),
    file_path TEXT,
    file_name TEXT,
    file_size INTEGER,
    error_message TEXT,
    expires_at TEXT,
    completed_at TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (business_plan_id) REFERENCES business_plans(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_exports_user_created ON exports(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_exports_status ON exports(status);
CREATE INDEX IF NOT EXISTS idx_exports_project ON exports(project_id);

