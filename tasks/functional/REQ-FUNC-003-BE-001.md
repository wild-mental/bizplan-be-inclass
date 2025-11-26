# REQ-FUNC-003-BE-001: 사업계획서 생성 요청 처리 및 오케스트레이션

## 1. 개요
- **목표**: 클라이언트의 '사업계획서 생성' 요청을 받아, Python LLM 엔진으로 생성을 위임하고 결과를 저장하는 오케스트레이션 API를 구현한다.
- **범위**:
  - `POST /projects/{id}/documents/business-plan:generate`
  - LLM 엔진(FastAPI) 호출 (RestTemplate/WebClient)
  - 결과(`BusinessPlanDocument`) 저장
- **Out of Scope**: 실제 프롬프트 엔지니어링(AI 엔진에서 수행).

## 2. 상세 요구사항
- **입력 조합**: 프로젝트의 `wizard_answers`를 조회하여 LLM 엔진에 전달할 Context를 구성한다.
- **비동기/동기**: MVP는 동기(Timeout 60s+) 또는 Polling 방식을 고려하되, 여기서는 **HTTP 동기 호출**로 단순화(추후 Async 전환).
- **저장**: 생성된 섹션별 텍스트를 `BusinessPlanDocument` 테이블에 저장한다.

---

```yaml
task_id: "REQ-FUNC-003-BE-001"
title: "사업계획서 생성 오케스트레이션 API 구현"
summary: >
  Wizard 답변을 조합하여 AI 엔진에 생성을 요청하고, 
  반환된 초안을 DB에 저장하는 백엔드 로직을 구현한다.
type: "functional"
epic: "EPIC_1_PASS_THE_TEST"
req_ids: ["REQ-FUNC-003", "REQ-FUNC-004"]
component: ["backend.api"]
agent_profile: ["backend"]

inputs:
  description: "문서 생성 요청"
  fields:
    - name: "project_id"
      type: "uuid"

outputs:
  description: "생성된 문서 ID 및 내용"
  success:
    http_status: 200

steps_hint:
  - "ProjectService: wizard_answers 조회"
  - "LlmClient: Python 엔진 API (POST /generate) 호출 구현"
  - "BusinessPlanDocument 엔티티 설계 및 저장 로직"
  - "Transaction 관리 (외부 호출 포함 주의)"

preconditions:
  - "REQ-FUNC-002-BE-001 완료 (답변 데이터 존재)"
  - "Python LLM 엔진(REQ-FUNC-003-AI-001) 인터페이스 정의 필요"

postconditions:
  - "새 BusinessPlanDocument 레코드가 생성된다."

dependencies: ["REQ-FUNC-002-BE-001", "REQ-FUNC-003-AI-001"]
```

