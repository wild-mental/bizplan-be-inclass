# [#005] PMF 진단 설문 및 리포트 UI PoC 구현

## Labels
`epic:EPIC_0_FE_PROTOTYPE`, `type:frontend`, `type:poc`, `priority:Should`, `effort:S`

## Description
PMF 진단을 위한 설문(객관식/주관식)을 제공하고, 진단 결과 리포트(등급, 리스크, 제언)를 보여주는 화면을 구현합니다.

## Scope
- PMF 진단 설문 페이지 (Wizard 형태 또는 단일 페이지)
- 데이터 부족 경고 모달
- 진단 결과 리포트 페이지 (Score Gauge, 리스트 뷰)

### Out of Scope
- 실제 진단 알고리즘

## Requirements
- **설문**: 약 10개의 PMF 관련 문항 제공
- **검증**: 5개 미만 답변 시 '진단 불가' 메시지 표시
- **리포트**: 'Product-Solution Fit' 등의 단계 표시와 함께 '주의사항' 리스트를 카드 UI로 표시

## Technical Stack
- UI Components (Card, Gauge Chart 등)

## Implementation Steps
1. PMFSurveyPage 컴포넌트 구현
2. Validation 로직 (답변 개수 체크)
3. PMFReportView 컴포넌트 구현 (게이지 차트, 리스크 카드)
4. Mock API (generatePmfReport) 연동

## Acceptance Criteria
- [ ] 설문 완료 시 리포트 페이지로 이동함
- [ ] 답변 수 부족 시 진단 불가 메시지 표시

## Dependencies
- None (독립 페이지로 구성 가능)

## Parallelizable With
- #001, #002, #003, #004 (독립적 개발 가능)

## Related Requirements
REQ-FUNC-008, REQ-FUNC-010

