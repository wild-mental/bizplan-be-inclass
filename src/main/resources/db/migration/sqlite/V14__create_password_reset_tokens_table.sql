-- ============================================
-- 비밀번호 재설정 토큰 테이블 (SQLite 호환)
-- Version: V14
-- Description: 비밀번호 재설정을 위한 토큰 저장
-- ============================================

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    user_id TEXT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    expires_at TEXT NOT NULL,
    used INTEGER DEFAULT 0,  -- SQLite BOOLEAN
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);

