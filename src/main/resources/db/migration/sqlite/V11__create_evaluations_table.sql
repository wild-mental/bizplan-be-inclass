-- ============================================
-- V11: Create evaluations table (SQLite)
-- AI 평가 데이터 저장
-- ============================================

CREATE TABLE IF NOT EXISTS evaluations (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    project_id TEXT NOT NULL,
    user_id TEXT,
    evaluation_type TEXT NOT NULL 
        CHECK (evaluation_type IN ('demo', 'basic', 'full')),
    status TEXT DEFAULT 'pending' 
        CHECK (status IN ('pending', 'processing', 'completed', 'failed')),
    input_data TEXT NOT NULL,  -- JSON 문자열
    result TEXT,  -- JSON 문자열
    total_score INTEGER,
    pass_rate INTEGER,
    completed_at TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_evaluations_project_created ON evaluations(project_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_evaluations_user ON evaluations(user_id);
CREATE INDEX IF NOT EXISTS idx_evaluations_status ON evaluations(status);

-- ============================================
-- 평가 영역별 점수 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS evaluation_scores (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    evaluation_id TEXT NOT NULL,
    area_code TEXT NOT NULL 
        CHECK (area_code IN ('market', 'ability', 'technology', 'economics', 'realization', 'social')),
    score INTEGER NOT NULL,
    feedback TEXT,
    strengths TEXT,  -- JSON 문자열
    weaknesses TEXT,  -- JSON 문자열
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (evaluation_id) REFERENCES evaluations(id) ON DELETE CASCADE,
    UNIQUE (evaluation_id, area_code)
);

CREATE INDEX IF NOT EXISTS idx_evaluation_scores_eval ON evaluation_scores(evaluation_id);

