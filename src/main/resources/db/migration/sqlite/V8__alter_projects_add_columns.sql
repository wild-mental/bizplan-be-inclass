-- ============================================
-- V8: Alter projects table - add new columns
-- 프로젝트 테이블에 새로운 컬럼 추가
-- ============================================

-- 사용자 ID (user_id)
ALTER TABLE projects ADD COLUMN user_id TEXT;

-- 프로젝트 이름
ALTER TABLE projects ADD COLUMN name TEXT;

-- 지원 프로그램
ALTER TABLE projects ADD COLUMN support_program TEXT;

-- 프로젝트 설명
ALTER TABLE projects ADD COLUMN description TEXT;

-- 현재 Wizard 단계
ALTER TABLE projects ADD COLUMN current_step INTEGER DEFAULT 1;

-- 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id);
CREATE INDEX IF NOT EXISTS idx_projects_user_status ON projects(user_id, status);

