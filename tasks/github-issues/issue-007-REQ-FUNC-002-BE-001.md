# [#007] Wizard 단계별 답변 저장/조회 API 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:backend`, `component:api`, `priority:Must`, `effort:M`

## Description
Wizard의 각 단계에서 사용자가 입력한 답변 데이터를 저장하고 불러오는 API를 구현합니다.

## Scope
- `POST /projects/{id}/wizard/steps`: 답변 저장 (Upsert)
- `GET /projects/{id}`: 프로젝트 전체 상태 및 답변 조회

### Out of Scope
- 복잡한 유효성 검사(단순 저장 위주)

## Requirements
- **데이터 구조**: `wizard_answers` 컬럼(JSONB)에 Key-Value 형태로 저장
- **부분 업데이트**: 기존 답변을 유지하면서 새로운 답변만 병합(Merge)하거나 덮어쓰기

## Technical Stack
- Java 21 + Spring Boot 4.0.0
- Spring Data JPA
- SQLite (JSON 지원)
- Hibernate Types (JSON 매핑)

## API Specification

### POST /projects/{id}/wizard/steps
**Request:**
```json
{
  "step_id": "step_1_problem",
  "answers": {
    "q1": "창업 동기...",
    "q2": "해결하고자 하는 문제..."
  }
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "updated_at": "2025-11-26T10:05:00Z"
}
```

### GET /projects/{id}
**Response (200 OK):**
```json
{
  "project_id": "uuid",
  "template_code": "KSTARTUP_2025",
  "wizard_answers": {
    "step_1_problem": {
      "q1": "...",
      "q2": "..."
    }
  }
}
```

## Implementation Steps
1. Project 엔티티의 wizard_answers 필드(JSONB) 매핑 (Hibernate Types)
2. 답변 병합 로직 구현 (Service Layer)
3. Controller 엔드포인트 구현
4. 단위 테스트 작성

## Acceptance Criteria
- [ ] REQ-FUNC-001-BE-001 완료 (Project 엔티티 존재)
- [ ] DB의 JSONB 컬럼에 답변이 저장/업데이트됨
- [ ] 부분 업데이트 시 기존 답변이 유지됨

## Dependencies
- #006 (REQ-FUNC-001-BE-001)

## Enables
- #009

## Related Requirements
REQ-FUNC-002, REQ-FUNC-013

