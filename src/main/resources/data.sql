-- ============================================
-- 초기 프로모션 데이터 (SQLite 개발 환경용)
-- ============================================
-- 2026 상반기 사전 등록 프로모션
-- Phase A: 연말연시 30% 할인 (2025-12-28 ~ 2026-01-03)
-- Phase B: 공고 전 10% 할인 (2026-01-04 ~ 접수 시작일)
-- ============================================

INSERT INTO promotions (
    id, code, name, description,
    phase_a_start, phase_a_end, phase_a_discount_rate,
    phase_b_start, phase_b_end, phase_b_discount_rate,
    is_active, created_at, updated_at
) VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'pre-registration-2026-h1',
    '2026 상반기 사전 등록',
    '연말연시 30% + 공고 전 10% 할인 프로모션',
    '2025-12-20T00:00:00',
    '2026-01-03T23:59:59',
    30,
    '2026-01-04T00:00:00',
    '2026-03-01T23:59:59',
    10,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

