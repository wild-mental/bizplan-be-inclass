# REQ-FUNC-012-BE-001: 재무 추정 자동화 엔진 구현

## 1. 개요
- **목표**: 매출, 비용, 고객 수 등 핵심 변수를 입력받아 3년치 손익계산서(PL)와 현금흐름표를 자동으로 계산하는 로직을 구현한다.
- **범위**:
  - `FinancialCalculationService`: 계산 로직 코어
  - 유닛 이코노믹스(LTV, CAC, BEP) 계산 로직
  - `POST /projects/{id}/financials:generate` API
- **Out of Scope**: 복잡한 회계 처리(감가상각 등은 단순화), 세금 계산 정밀도.

## 2. 상세 요구사항
- **입력 변수**: 초기 자본금, 월 평균 객단가, 월간 마케팅 예산, CAC, 이탈률, 고정비(인건비/임대료).
- **계산 로직**:
  - 월 매출 = 활성 고객 수 * 객단가
  - 활성 고객 수 = 전월 고객 + 신규 고객 - 이탈 고객
  - 신규 고객 = 마케팅 예산 / CAC
  - 이익 = 매출 - (변동비 + 고정비)

---

```yaml
task_id: "REQ-FUNC-012-BE-001"
title: "재무 추정 및 유닛 이코노믹스 계산 엔진 구현"
summary: >
  핵심 변수 기반으로 3년치 월별 PL과 현금흐름을 계산하고,
  LTV/CAC 등 주요 지표를 산출하는 Java 로직을 구현한다.
type: "functional"
epic: "EPIC_2_AVOID_FAILURE"
req_ids: ["REQ-FUNC-012", "REQ-FUNC-009"]
component: ["backend.core"]
agent_profile: ["backend"]

inputs:
  description: "재무 가정 변수"
  fields:
    - name: "assumptions"
      type: "object"

outputs:
  description: "계산된 재무 테이블"
  success:
    fields: ["monthly_pl", "yearly_summary", "unit_economics"]

steps_hint:
  - "FinancialModel 클래스 설계 (Input/Output DTO)"
  - "CalculationService 구현 (월별 Loop 계산)"
  - "LTV, CAC, BEP 계산 메서드 구현"
  - "단위 테스트 (Excel 계산 결과와 비교 검증)"

preconditions:
  - "없음 (Pure Logic)"

postconditions:
  - "입력 변수에 따라 정확한 3년치 재무 수치가 반환된다."

dependencies: []
```

