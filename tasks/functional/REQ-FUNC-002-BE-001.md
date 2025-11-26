# REQ-FUNC-002-BE-001: Wizard 단계별 저장 API

## 1. 개요
- **목표**: Wizard의 각 단계에서 사용자가 입력한 답변 데이터를 저장하고 불러오는 API를 구현한다.
- **범위**:
  - `POST /projects/{id}/wizard/steps`: 답변 저장 (Upsert)
  - `GET /projects/{id}`: 프로젝트 전체 상태 및 답변 조회
- **Out of Scope**: 복잡한 유효성 검사(단순 저장 위주).

## 2. 상세 요구사항
- **데이터 구조**: `wizard_answers` 컬럼(JSONB)에 Key-Value 형태로 저장한다.
- **부분 업데이트**: 기존 답변을 유지하면서 새로운 답변만 병합(Merge)하거나 덮어쓴다.

---

```yaml
task_id: "REQ-FUNC-002-BE-001"
title: "Wizard 단계별 답변 저장/조회 API 구현"
summary: >
  Wizard 입력 데이터를 JSONB 형태로 저장하고 조회하는 API를 구현한다.
type: "functional"
epic: "EPIC_1_PASS_THE_TEST"
req_ids: ["REQ-FUNC-002", "REQ-FUNC-013"]
component: ["backend.api"]
agent_profile: ["backend"]

inputs:
  description: "단계별 답변 데이터"
  fields:
    - name: "step_id"
      type: "string"
    - name: "answers"
      type: "object" # JSON

outputs:
  description: "업데이트된 프로젝트 정보"
  success:
    http_status: 200

steps_hint:
  - "Project 엔티티의 wizard_answers 필드(JSONB) 매핑 (Hibernate Types)"
  - "답변 병합 로직 구현 (Service Layer)"
  - "Controller 엔드포인트 구현"

preconditions:
  - "REQ-FUNC-001-BE-001 완료 (Project 엔티티 존재)"

postconditions:
  - "DB의 JSONB 컬럼에 답변이 저장/업데이트된다."

dependencies: ["REQ-FUNC-001-BE-001"]
```

