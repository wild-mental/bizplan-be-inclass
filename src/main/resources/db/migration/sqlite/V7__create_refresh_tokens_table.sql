-- ============================================
-- 리프레시 토큰 테이블 (SQLite 호환)
-- Version: V7
-- Description: JWT 리프레시 토큰 저장 (토큰 갱신 및 로그아웃 처리용)
-- ============================================

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    user_id TEXT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    expires_at TEXT NOT NULL,
    revoked INTEGER DEFAULT 0,  -- SQLite BOOLEAN
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens(user_id);

