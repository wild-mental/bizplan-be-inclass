-- ============================================
-- 이메일 인증 토큰 테이블 (SQLite 호환)
-- Version: V13
-- Description: 이메일 인증을 위한 토큰 저장
-- ============================================

CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    user_id TEXT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    expires_at TEXT NOT NULL,
    used INTEGER DEFAULT 0,  -- SQLite BOOLEAN
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_email_verification_tokens_user_id ON email_verification_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_email_verification_tokens_token ON email_verification_tokens(token);
CREATE INDEX IF NOT EXISTS idx_email_verification_tokens_expires_at ON email_verification_tokens(expires_at);

