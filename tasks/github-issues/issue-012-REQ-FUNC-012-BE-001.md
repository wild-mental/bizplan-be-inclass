# [#012] 재무 추정 및 유닛 이코노믹스 계산 엔진 구현

## Labels
`epic:EPIC_2_AVOID_FAILURE`, `type:backend`, `component:core`, `priority:Must`, `effort:L`

## Description
매출, 비용, 고객 수 등 핵심 변수를 입력받아 3년치 손익계산서(PL)와 현금흐름표를 자동으로 계산하는 로직을 구현합니다.

## Scope
- `FinancialCalculationService`: 계산 로직 코어
- 유닛 이코노믹스(LTV, CAC, BEP) 계산 로직
- `POST /projects/{id}/financials:generate` API

### Out of Scope
- 복잡한 회계 처리(감가상각 등은 단순화)
- 세금 계산 정밀도

## Requirements
- **입력 변수**: 초기 자본금, 월 평균 객단가, 월간 마케팅 예산, CAC, 이탈률, 고정비(인건비/임대료)
- **계산 로직**:
  - 월 매출 = 활성 고객 수 * 객단가
  - 활성 고객 수 = 전월 고객 + 신규 고객 - 이탈 고객
  - 신규 고객 = 마케팅 예산 / CAC
  - 이익 = 매출 - (변동비 + 고정비)

## Technical Stack
- Java 21 + Spring Boot 4.0.0
- Pure Java Logic (비즈니스 로직 중심)

## API Specification

### POST /projects/{id}/financials:generate
**Request:**
```json
{
  "assumptions": {
    "initial_capital": 50000000,
    "avg_price_per_customer": 30000,
    "monthly_marketing_budget": 2000000,
    "cac": 50000,
    "churn_rate": 0.05,
    "fixed_costs": {
      "salary": 10000000,
      "rent": 2000000
    }
  }
}
```

**Response (200 OK):**
```json
{
  "monthly_pl": [
    {
      "month": 1,
      "revenue": 1200000,
      "costs": 12500000,
      "profit": -11300000,
      "active_customers": 40
    }
  ],
  "yearly_summary": [
    {
      "year": 1,
      "total_revenue": 50000000,
      "total_costs": 150000000,
      "net_profit": -100000000
    }
  ],
  "unit_economics": {
    "ltv": 600000,
    "cac": 50000,
    "ltv_cac_ratio": 12.0,
    "break_even_point_month": 15
  }
}
```

## Implementation Steps
1. FinancialModel 클래스 설계 (Input/Output DTO)
2. CalculationService 구현 (월별 Loop 계산)
3. LTV, CAC, BEP 계산 메서드 구현
4. 단위 테스트 (Excel 계산 결과와 비교 검증)
5. Controller 엔드포인트 구현

## Acceptance Criteria
- [ ] 입력 변수에 따라 정확한 3년치 재무 수치가 반환됨
- [ ] Excel로 계산한 결과와 일치함 (단위 테스트)
- [ ] LTV/CAC 비율이 정확히 계산됨

## Dependencies
- None (Pure Logic, 독립적으로 개발 가능)

## Parallelizable With
- #006, #007, #008, #009, #010, #011 (모든 작업과 병렬 가능)

## Related Requirements
REQ-FUNC-012, REQ-FUNC-009

