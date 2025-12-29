-- ============================================
-- V3: Create pre_registrations table (SQLite)
-- PRE-SUB-FUNC-002 명세서 준수
-- ============================================

CREATE TABLE IF NOT EXISTS pre_registrations (
    -- UUID PK
    id TEXT NOT NULL PRIMARY KEY,
    
    -- 사용자 정보
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL,
    
    -- 요금제 정보 (ENUM 대신 TEXT + CHECK)
    selected_plan TEXT NOT NULL CHECK (selected_plan IN ('plus', 'pro', 'premium')),
    business_category TEXT,
    
    -- 동의 항목 (SQLite BOOLEAN은 0/1로 저장)
    marketing_consent INTEGER NOT NULL DEFAULT 0,
    
    -- 프로모션 정보
    promotion_phase TEXT NOT NULL CHECK (promotion_phase IN ('A', 'B')),
    
    -- 할인 정보
    discount_code TEXT NOT NULL UNIQUE,
    discount_rate INTEGER NOT NULL,
    original_price INTEGER NOT NULL,
    discounted_price INTEGER NOT NULL,
    
    -- 만료일 (ISO 8601 형식 TEXT)
    expires_at TEXT NOT NULL,
    
    -- 상태 관리 (ENUM 대신 TEXT + CHECK)
    status TEXT NOT NULL DEFAULT 'CONFIRMED' 
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CONVERTED')),
    
    -- 감사 컬럼 (ISO 8601 형식 TEXT)
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 인덱스 생성 (SQLite는 CREATE TABLE 외부에서)
CREATE INDEX IF NOT EXISTS idx_pre_registrations_email ON pre_registrations(email);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_status ON pre_registrations(status);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_selected_plan ON pre_registrations(selected_plan);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_discount_code ON pre_registrations(discount_code);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_created_at ON pre_registrations(created_at);
