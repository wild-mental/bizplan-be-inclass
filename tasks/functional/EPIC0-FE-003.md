# EPIC0-FE-003: 사업계획서 초안 생성 및 뷰어 UI PoC

## 1. 개요
- **목표**: 입력된 데이터를 바탕으로 '초안 생성'을 요청하고, 생성된 문서(텍스트)를 화면에 보여주는 뷰어/에디터를 구현한다.
- **범위**:
  - '초안 생성' 버튼 및 로딩 UI (Spinner/Progress)
  - 생성된 문서 뷰어 (Markdown 또는 Textarea)
  - '내보내기(HWP/PDF)' 버튼 (Mock Alert)
  - 섹션별 'AI 다시 쓰기' 버튼 UI
- **Out of Scope**: 실제 LLM 연동(Dummy 텍스트 반환), 실제 HWP 변환.

## 2. 상세 요구사항
- **생성 트리거**: Wizard 마지막 단계 또는 별도 메뉴에서 '초안 생성' 버튼 제공.
- **결과 표시**: 생성 완료 후, 챕터별로 구조화된 텍스트가 에디터에 채워져야 한다.
- **AI 보조**: 각 텍스트 블록 옆에 'AI 보완' 아이콘을 배치한다 (클릭 시 예시 텍스트 변경).

## 3. 기술 스택
- React Markdown (뷰어용) 또는 Toast UI Editor (에디터용)
- Suspense / Loading State 처리

---

```yaml
task_id: "EPIC0-FE-003"
title: "사업계획서 초안 생성 및 뷰어 UI PoC 구현"
summary: >
  Wizard 데이터 기반 초안 생성 요청 UI와
  생성된 결과물을 확인/수정할 수 있는 에디터 뷰를 구현한다.
type: "functional"

epic: "EPIC_0_FE_PROTOTYPE"
req_ids: ["REQ-FUNC-003", "REQ-FUNC-004", "REQ-FUNC-011"]
agent_profile: ["frontend"]

parallelizable: true
estimated_effort: "M"
priority: "Must"

inputs:
  description: "Wizard 입력 답변 집합"
  fields:
    - name: "answers"
      type: "object"

outputs:
  description: "생성된 문서 텍스트"
  success:
    ui_state: "Document View with Dummy Content"

steps_hint:
  - "DraftGenerationPage 컴포넌트 생성"
  - "API 요청 시뮬레이션 (3초 딜레이 후 Dummy Text 반환)"
  - "DocumentViewer 컴포넌트 구현 (섹션별 편집 가능)"
  - "Export 버튼 클릭 이벤트 핸들러 (window.alert)"

preconditions:
  - "EPIC0-FE-002 완료 (입력 데이터가 있어야 함)"

postconditions:
  - "생성 버튼 클릭 시 로딩 후 결과 텍스트가 표시된다."
  - "내보내기 버튼 클릭 시 성공 메시지가 뜬다."

dependencies: ["EPIC0-FE-002"]
```

