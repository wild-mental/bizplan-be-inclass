# REQ-FUNC-008-AI-001: PMF 진단 엔진 구현

## 1. 개요
- **목표**: 사용자의 PMF 설문 응답을 분석하여 PMF 단계(Stage), 리스크, 개선 권고를 도출하는 LLM 기반 진단 엔진을 구현한다.
- **범위**:
  - `POST /pmf/analyze`: 진단 요청 처리
  - PMF 진단 프롬프트 엔지니어링 (Persona: 스타트업 액셀러레이터 심사역)
  - 응답 데이터 부족 시 예외 처리 (Rule Base + LLM)
- **Out of Scope**: 진단 이력 관리(Backend DB 역할).

## 2. 상세 요구사항
- **진단 로직**: 10개 이상의 문항 답변을 종합하여 'Problem-Solution Fit', 'Product-Market Fit', 'Scale-up' 중 단계를 판정한다.
- **리스크 추출**: 답변 내용 중 논리적 비약이나 시장성 부족 신호를 찾아 Top 3 리스크로 정리한다.

---

```yaml
task_id: "REQ-FUNC-008-AI-001"
title: "PMF 진단 및 리포트 생성 LLM 엔진 구현"
summary: >
  FastAPI 내에 PMF 진단 전용 프롬프트 파이프라인을 구축하고,
  진단 결과(단계, 리스크, 조언)를 구조화된 JSON으로 반환한다.
type: "functional"
epic: "EPIC_2_AVOID_FAILURE"
req_ids: ["REQ-FUNC-008", "REQ-FUNC-010"]
component: ["ai.engine"]
agent_profile: ["ml"]

inputs:
  description: "PMF 설문 답변 목록"
  fields:
    - name: "answers"
      type: "array"

outputs:
  description: "PMF 진단 결과"
  success:
    format: "JSON"
    fields: ["stage", "score", "risks", "recommendations"]

steps_hint:
  - "PMF 진단용 System Prompt 작성"
  - "답변 개수 부족 시 Early Return 로직 구현"
  - "LangChain Chain 구성 (JSON Output Parser 활용)"
  - "FastAPI 엔드포인트 연동"

preconditions:
  - "REQ-FUNC-003-AI-001 환경(FastAPI/Gemini) 공용 사용"

postconditions:
  - "설문 답변 입력 시 분석된 리포트 JSON이 반환된다."

dependencies: ["REQ-FUNC-003-AI-001"]
```

