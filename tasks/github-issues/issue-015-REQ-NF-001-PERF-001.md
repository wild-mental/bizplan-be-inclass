# [#015] API 성능 목표 검증을 위한 k6 부하 테스트 구현

## Labels
`epic:EPIC_3_NFR`, `type:performance`, `type:testing`, `component:test`, `priority:Should`, `effort:M`

## Description
Wizard 단계 전환, 문서 생성 등 주요 시나리오에 대한 부하 테스트 스크립트를 작성하고, p95 응답시간 목표 달성 여부를 검증합니다.

## Scope
- k6 부하 테스트 스크립트 작성
- CI 파이프라인 연동 (선택 사항)
- 성능 리포트 생성

### Out of Scope
- 극한의 DDoS 테스트

## Requirements
- **목표치**:
  - Wizard 단계 전환: p95 ≤ 800ms
  - 문서 생성: p95 ≤ 10s (Async Polling 포함)
- **시나리오**: "로그인 -> 프로젝트 생성 -> Wizard 답변 5개 입력 -> 초안 생성" 흐름 반복

## Technical Stack
- k6 (부하 테스트 도구)
- Docker (테스트 환경)

## Performance Requirements
- 동시 접속 1,000명(가정) 상황에서도 p95 목표 준수
- Wizard API p95 < 800ms
- Generation API p95 < 10s

## Implementation Steps
1. k6 설치 및 기본 시나리오 스크립트(script.js) 작성
2. 가상 유저(VU) 램프업 설정
3. Threshold(임계값) 설정 (p95 > 800ms 시 Fail)
4. 테스트 실행 및 리포트 분석
5. 병목 지점 식별 및 개선 방안 문서화

## Test Scenarios

### Scenario 1: Wizard Step Transition
```javascript
export default function() {
  // POST /projects/{id}/wizard/steps
  // Target: p95 < 800ms
}
```

### Scenario 2: Document Generation
```javascript
export default function() {
  // POST /projects/{id}/documents/business-plan:generate
  // Target: p95 < 10s
}
```

## Acceptance Criteria
- [ ] 백엔드 API 서버가 배포되어 있거나 로컬 실행 가능
- [ ] 부하 테스트 결과 리포트가 생성됨
- [ ] 성능 병목 구간이 식별됨
- [ ] p95 목표 달성 여부 확인

## Dependencies
- #009 (REQ-FUNC-003-BE-001) - 주요 API 구현 완료 필요

## Related Requirements
REQ-NF-001, REQ-NF-002, REQ-NF-009

