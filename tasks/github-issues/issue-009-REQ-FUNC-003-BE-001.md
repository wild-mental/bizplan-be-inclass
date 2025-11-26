# [#009] 사업계획서 생성 오케스트레이션 API 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:backend`, `component:api`, `priority:Must`, `effort:M`

## Description
클라이언트의 '사업계획서 생성' 요청을 받아, Python LLM 엔진으로 생성을 위임하고 결과를 저장하는 오케스트레이션 API를 구현합니다.

## Scope
- `POST /projects/{id}/documents/business-plan:generate`
- LLM 엔진(FastAPI) 호출 (RestTemplate/WebClient)
- 결과(`BusinessPlanDocument`) 저장

### Out of Scope
- 실제 프롬프트 엔지니어링(AI 엔진에서 수행)

## Requirements
- **입력 조합**: 프로젝트의 `wizard_answers`를 조회하여 LLM 엔진에 전달할 Context 구성
- **비동기/동기**: MVP는 동기(Timeout 60s+) 또는 Polling 방식을 고려하되, 여기서는 **HTTP 동기 호출**로 단순화(추후 Async 전환)
- **저장**: 생성된 섹션별 텍스트를 `BusinessPlanDocument` 테이블에 저장

## Technical Stack
- Java 17 + Spring Boot 3.x
- WebClient (비동기 HTTP 클라이언트)
- Spring Data JPA

## API Specification

### POST /projects/{id}/documents/business-plan:generate
**Response (200 OK):**
```json
{
  "document_id": "uuid",
  "project_id": "uuid",
  "sections": {
    "problem_definition": "...",
    "solution_approach": "...",
    "market_analysis": "..."
  },
  "generated_at": "2025-11-26T10:15:00Z"
}
```

**Response (500 Internal Server Error):**
```json
{
  "error": "LLM_ENGINE_ERROR",
  "message": "AI 엔진 응답 실패"
}
```

## Implementation Steps
1. ProjectService: wizard_answers 조회
2. LlmClient: Python 엔진 API (POST /generate) 호출 구현
3. BusinessPlanDocument 엔티티 설계 및 저장 로직
4. Transaction 관리 (외부 호출 포함 주의)
5. 타임아웃 및 에러 핸들링

## Acceptance Criteria
- [ ] REQ-FUNC-002-BE-001 완료 (답변 데이터 존재)
- [ ] Python LLM 엔진(REQ-FUNC-003-AI-001) 인터페이스 정의 필요
- [ ] 새 BusinessPlanDocument 레코드가 생성됨
- [ ] LLM 엔진 실패 시 적절한 에러 응답 반환

## Dependencies
- #007 (REQ-FUNC-002-BE-001)
- #008 (REQ-FUNC-003-AI-001)

## Enables
- #010, #015

## Related Requirements
REQ-FUNC-003, REQ-FUNC-004

