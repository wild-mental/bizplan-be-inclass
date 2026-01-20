-- V15__create_ab_test_tables.sql
-- A/B 테스트 기능을 위한 테이블 생성

-- 실험(Experiment) 테이블
CREATE TABLE ab_experiments (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    target_page TEXT,
    target_element TEXT,
    status TEXT NOT NULL DEFAULT 'draft',
    traffic_percentage INTEGER DEFAULT 100,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 변형(Variant) 테이블
CREATE TABLE ab_variants (
    id TEXT PRIMARY KEY,
    experiment_id TEXT NOT NULL,
    name TEXT NOT NULL,
    weight INTEGER DEFAULT 50,
    content_json TEXT NOT NULL,
    is_control INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (experiment_id) REFERENCES ab_experiments(id) ON DELETE CASCADE
);

-- 할당(Assignment) 테이블
CREATE TABLE ab_assignments (
    id TEXT PRIMARY KEY,
    experiment_id TEXT NOT NULL,
    variant_id TEXT NOT NULL,
    visitor_id TEXT NOT NULL,
    user_id TEXT,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (visitor_id, experiment_id),
    FOREIGN KEY (experiment_id) REFERENCES ab_experiments(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES ab_variants(id) ON DELETE CASCADE
);

-- 전환(Conversion) 테이블
CREATE TABLE ab_conversions (
    id TEXT PRIMARY KEY,
    assignment_id TEXT NOT NULL,
    event_type TEXT NOT NULL,
    event_data_json TEXT,
    converted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignment_id) REFERENCES ab_assignments(id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_ab_experiments_status ON ab_experiments(status);
CREATE INDEX idx_ab_experiments_target_page ON ab_experiments(target_page);
CREATE INDEX idx_ab_variants_experiment_id ON ab_variants(experiment_id);
CREATE INDEX idx_ab_assignments_visitor_experiment ON ab_assignments(visitor_id, experiment_id);
CREATE INDEX idx_ab_assignments_experiment_id ON ab_assignments(experiment_id);
CREATE INDEX idx_ab_conversions_assignment_id ON ab_conversions(assignment_id);
CREATE INDEX idx_ab_conversions_event_type ON ab_conversions(event_type);
