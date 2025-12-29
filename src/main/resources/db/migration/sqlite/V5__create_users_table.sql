-- ============================================
-- 사용자 테이블 (SQLite 호환)
-- Version: V5
-- Description: 사용자 인증 및 프로필 정보 저장
-- ============================================

CREATE TABLE IF NOT EXISTS users (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT,
    name TEXT NOT NULL,
    phone TEXT,
    provider TEXT DEFAULT 'local' 
        CHECK (provider IN ('local', 'google', 'kakao', 'naver')),
    provider_id TEXT,
    business_category TEXT,
    marketing_consent INTEGER DEFAULT 0,  -- SQLite BOOLEAN
    email_verified INTEGER DEFAULT 0,
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_provider ON users(provider, provider_id);

