# REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현

## 1. 개요
- **목표**: Wizard 답변(JSON)을 입력받아, LangChain 및 LLM(Gemini)을 사용하여 섹션별 사업계획서 초안을 생성하는 Python API를 구현한다.
- **범위**:
  - FastAPI 서버 구축
  - `POST /generate`: 문서 생성 엔드포인트
  - Prompt Template 관리 (섹션별 프롬프트)
  - LLM Gateway(Gemini) 연동
- **Out of Scope**: 복잡한 RAG(검색 증강), Fine-tuning.

## 2. 상세 요구사항
- **프롬프트 전략**: 'Role: 전문 컨설턴트', 'Context: 사용자 답변', 'Task: 섹션별 초안 작성' 구조의 프롬프트 체이닝.
- **출력 포맷**: JSON 형태로 `{ "section_1": "...", "section_2": "..." }` 반환.
- **안정성**: LLM 오류 시 재시도 로직(LangChain 기능 활용).

---

```yaml
task_id: "REQ-FUNC-003-AI-001"
title: "사업계획서 생성 LLM 엔진 및 프롬프트 구현"
summary: >
  FastAPI + LangChain 기반으로, 입력된 정보를 바탕으로 
  사업계획서 초안을 생성하는 LLM 파이프라인을 구현한다.
type: "functional"
epic: "EPIC_1_PASS_THE_TEST"
req_ids: ["REQ-FUNC-003", "REQ-FUNC-004"]
component: ["ai.engine"]
agent_profile: ["ml", "backend"]

inputs:
  description: "사용자 답변 Context"
  fields:
    - name: "answers"
      type: "object"
    - name: "template_type"
      type: "string"

outputs:
  description: "구조화된 문서 초안"
  success:
    format: "JSON"

steps_hint:
  - "FastAPI 프로젝트 셋업"
  - "LangChain Gemini ChatModel 연동"
  - "PromptTemplate 정의 (섹션별)"
  - "LLMChain 구성 및 실행 로직"

preconditions:
  - "Google Gemini API Key 발급 및 환경변수 설정"

postconditions:
  - "API 호출 시 유의미한 사업계획서 텍스트가 반환된다."

dependencies: []
```

