-- ============================================
-- V4: Create promotions table (SQLite)
-- 프로모션 설정 관리 (동적 종료일 업데이트용)
-- ============================================

CREATE TABLE IF NOT EXISTS promotions (
    -- UUID PK
    id TEXT NOT NULL PRIMARY KEY,
    
    -- 프로모션 정보
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    description TEXT,
    
    -- Phase A 설정 (ISO 8601 형식)
    phase_a_start TEXT NOT NULL,
    phase_a_end TEXT NOT NULL,
    phase_a_discount_rate INTEGER NOT NULL DEFAULT 30,
    
    -- Phase B 설정
    phase_b_start TEXT NOT NULL,
    phase_b_end TEXT,
    phase_b_discount_rate INTEGER NOT NULL DEFAULT 10,
    
    -- 상태 (SQLite BOOLEAN = INTEGER)
    is_active INTEGER NOT NULL DEFAULT 1,
    
    -- 감사 컬럼
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_promotions_code ON promotions(code);
CREATE INDEX IF NOT EXISTS idx_promotions_is_active ON promotions(is_active);

-- 초기 프로모션 데이터 삽입
-- Note: SQLite는 UUID() 함수가 없으므로 하드코딩된 UUID 사용
INSERT INTO promotions (
    id, code, name, description,
    phase_a_start, phase_a_end, phase_a_discount_rate,
    phase_b_start, phase_b_end, phase_b_discount_rate,
    is_active
) VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'pre-registration-2026-h1',
    '2026 상반기 사전 등록',
    '연말연시 30% + 공고 전 10% 할인 프로모션',
    '2025-12-28T00:00:00',
    '2026-01-03T23:59:59',
    30,
    '2026-01-04T00:00:00',
    '2026-03-01T23:59:59',
    10,
    1
);

