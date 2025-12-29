-- ============================================
-- 구독 테이블 (SQLite 호환)
-- Version: V6
-- Description: 사용자 요금제 구독 정보 저장
-- ============================================

CREATE TABLE IF NOT EXISTS subscriptions (
    id TEXT NOT NULL PRIMARY KEY,  -- UUID
    user_id TEXT NOT NULL,
    plan TEXT NOT NULL CHECK (plan IN ('기본', '플러스', '프로', '프리미엄')),
    plan_key TEXT NOT NULL CHECK (plan_key IN ('basic', 'plus', 'pro', 'premium')),
    original_price INTEGER NOT NULL,
    discounted_price INTEGER,
    discount_rate INTEGER,
    promotion_phase TEXT CHECK (promotion_phase IN ('A', 'B', 'NONE')),
    promotion_code TEXT,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    status TEXT DEFAULT 'active' CHECK (status IN ('active', 'expired', 'cancelled')),
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_subscriptions_user_status ON subscriptions(user_id, status);

