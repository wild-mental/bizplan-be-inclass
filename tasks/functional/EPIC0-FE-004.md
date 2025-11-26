# EPIC0-FE-004: 재무 입력 및 유닛 이코노믹스 시각화 UI PoC

## 1. 개요
- **목표**: 매출, 비용, 고객 획득 비용(CAC) 등 재무 데이터를 입력받고, 이를 그래프(손익분기점, LTV/CAC)로 시각화한다.
- **범위**:
  - 재무 전용 입력 테이블 (엑셀 스타일 또는 폼)
  - Chart.js / Recharts 를 활용한 그래프 렌더링
  - 주요 지표(LTV, CAC, BEP) 카드 표시
- **Out of Scope**: 복잡한 엑셀 수식 계산(간단한 사칙연산만 JS로 처리).

## 2. 상세 요구사항
- **입력**: 고객 수, 객단가, 변동비, 고정비 입력 필드.
- **즉시 반응**: 입력 값 변경 시 예상 매출 및 이익 그래프가 즉시 업데이트되어야 한다.
- **경고**: LTV/CAC < 3 인 경우 경고 뱃지를 표시한다.

## 3. 기술 스택
- Recharts (차트 라이브러리)
- React Table (선택 사항)

---

```yaml
task_id: "EPIC0-FE-004"
title: "재무 입력 및 유닛 이코노믹스 시각화 UI PoC 구현"
summary: >
  핵심 재무 변수 입력 폼과 실시간 차트 시각화(BEP, LTV/CAC) 화면을 구현한다.
type: "functional"

epic: "EPIC_0_FE_PROTOTYPE"
req_ids: ["REQ-FUNC-009", "REQ-FUNC-012"]
agent_profile: ["frontend"]

parallelizable: true
estimated_effort: "M"
priority: "Must"

inputs:
  description: "재무 가정 변수"
  fields:
    - name: "cac"
    - name: "ltv"
    - name: "fixed_cost"

outputs:
  description: "시각화된 차트 및 지표"
  success:
    ui_state: "Interactive Charts"

steps_hint:
  - "FinancialInputForm 컴포넌트 구현"
  - "Recharts 설치 및 BreakEvenPointChart 컴포넌트 구현"
  - "입력 값 변경에 따른 파생 변수(매출, 이익) 계산 로직 작성 (util 함수)"
  - "DashboardLayout에 통합"

preconditions:
  - "프로젝트 생성 플로우(EPIC0-FE-001)와 독립적으로 개발 가능하나, 네비게이션 연결 필요"

postconditions:
  - "입력 값을 바꾸면 그래프가 다시 그려진다."

dependencies: ["EPIC0-FE-001"]
```

