# EPIC0-FE-005: PMF 진단 설문 및 리포트 UI PoC

## 1. 개요
- **목표**: PMF 진단을 위한 설문(객관식/주관식)을 제공하고, 진단 결과 리포트(등급, 리스크, 제언)를 보여주는 화면을 구현한다.
- **범위**:
  - PMF 진단 설문 페이지 (Wizard 형태 또는 단일 페이지)
  - 데이터 부족 경고 모달
  - 진단 결과 리포트 페이지 (Score Gauge, 리스트 뷰)
- **Out of Scope**: 실제 진단 알고리즘.

## 2. 상세 요구사항
- **설문**: 약 10개의 PMF 관련 문항 제공.
- **검증**: 5개 미만 답변 시 '진단 불가' 메시지 표시.
- **리포트**: 'Product-Solution Fit' 등의 단계 표시와 함께 '주의사항' 리스트를 카드 UI로 보여준다.

## 3. 기술 스택
- UI Components (Card, Gauge Chart 등)

---

```yaml
task_id: "EPIC0-FE-005"
title: "PMF 진단 설문 및 리포트 UI PoC 구현"
summary: >
  PMF 진단을 위한 설문 입력 UI와 결과 리포트 뷰를 구현한다.
  데이터 부족 시 예외 처리를 포함한다.
type: "functional"

epic: "EPIC_0_FE_PROTOTYPE"
req_ids: ["REQ-FUNC-008", "REQ-FUNC-010"]
agent_profile: ["frontend"]

parallelizable: true
estimated_effort: "S"
priority: "Should"

inputs:
  description: "PMF 설문 답변"
  fields:
    - name: "answers"
      type: "array"

outputs:
  description: "PMF 리포트 뷰"
  success:
    ui_state: "Report View"

steps_hint:
  - "PMFSurveyPage 컴포넌트 구현"
  - "Validation 로직 (답변 개수 체크)"
  - "PMFReportView 컴포넌트 구현 (게이지 차트, 리스크 카드)"
  - "Mock API (generatePmfReport) 연동"

preconditions:
  - "없음 (독립 페이지로 구성 가능)"

postconditions:
  - "설문 완료 시 리포트 페이지로 이동한다."

dependencies: []
```

