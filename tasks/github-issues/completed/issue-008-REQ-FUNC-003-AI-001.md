# [#008] 사업계획서 생성 LLM 통합 및 프롬프트 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:ai`, `type:backend`, `component:backend`, `priority:Must`, `effort:L`

## Description
Wizard 답변(JSON)을 입력받아, Spring Boot 서비스에서 Google Gemini API를 직접 호출하여 섹션별 사업계획서 초안을 생성하는 기능을 구현합니다.

## Scope
- Spring Boot 서비스에서 Gemini API 직접 호출
- `POST /generate`: 문서 생성 엔드포인트
- Prompt Template 관리 (섹션별 프롬프트)
- Spring AI를 통한 Gemini API 연동

### Out of Scope
- 복잡한 RAG(검색 증강)
- Fine-tuning
- 별도 AI 엔진 서비스

## Requirements
- **프롬프트 전략**: 'Role: 전문 컨설턴트', 'Context: 사용자 답변', 'Task: 섹션별 초안 작성' 구조의 프롬프트 구성
- **출력 포맷**: JSON 형태로 `{ "section_1": "...", "section_2": "..." }` 반환
- **안정성**: LLM 오류 시 재시도 로직 구현

## Technical Stack
- Java 21 + Spring Boot 4.0.0
- Spring AI
- Google Gemini API (외부 API 직접 호출)

## API Specification

### POST /generate
**Request:**
```json
{
  "answers": {
    "step_1_problem": {
      "q1": "...",
      "q2": "..."
    }
  },
  "template_type": "KSTARTUP_2025"
}
```

**Response (200 OK):**
```json
{
  "sections": {
    "problem_definition": "사업 아이템의 필요성...",
    "solution_approach": "해결 방안...",
    "market_analysis": "시장 분석..."
  },
  "generated_at": "2025-11-26T10:10:00Z"
}
```

## Implementation Steps
1. Spring AI Gemini ChatModel 설정
2. Gemini API 직접 호출 구현
3. PromptTemplate 정의 (섹션별)
4. Service 레이어에서 Gemini API 호출 로직 구현
5. 에러 핸들링 및 재시도 로직

## Acceptance Criteria
- [ ] Google Gemini API Key 발급 및 환경변수 설정
- [ ] API 호출 시 유의미한 사업계획서 텍스트가 반환됨
- [ ] LLM 오류 시 재시도 후 실패 시 에러 응답

## Dependencies
- None (독립적으로 시작 가능)

## Enables
- #009, #011

## Parallelizable With
- #006, #007 (독립적 개발 가능)

## Related Requirements
REQ-FUNC-003, REQ-FUNC-004

