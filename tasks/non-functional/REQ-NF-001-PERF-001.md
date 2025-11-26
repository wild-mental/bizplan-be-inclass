# REQ-NF-001-PERF-001: 주요 API 성능 테스트 환경 구축

## 1. 개요
- **목표**: Wizard 단계 전환, 문서 생성 등 주요 시나리오에 대한 부하 테스트 스크립트를 작성하고, p95 응답시간 목표(REQ-NF-001, 002) 달성 여부를 검증한다.
- **범위**:
  - k6 부하 테스트 스크립트 작성
  - CI 파이프라인 연동 (선택 사항)
  - 성능 리포트 생성
- **Out of Scope**: 극한의 DDoS 테스트.

## 2. 상세 요구사항
- **목표치**:
  - Wizard 단계 전환: p95 ≤ 800ms
  - 문서 생성: p95 ≤ 10s (Async Polling 포함)
- **시나리오**: "로그인 -> 프로젝트 생성 -> Wizard 답변 5개 입력 -> 초안 생성" 흐름 반복.

---

```yaml
task_id: "REQ-NF-001-PERF-001"
title: "API 성능 목표 검증을 위한 k6 부하 테스트 구현"
summary: >
  REQ-NF-001, 002 성능 목표(Wizard 800ms, Gen 10s)를 검증하기 위한 
  k6 테스트 스크립트와 실행 환경을 구축한다.
type: "non_functional"
epic: "EPIC_PERFORMANCE"
req_ids: ["REQ-NF-001", "REQ-NF-002", "REQ-NF-009"]
component: ["test.perf"]
agent_profile: ["infra", "qa"]

category: "performance"
labels: ["performance:latency", "performance:load"]

requirements:
  description: "동시 접속 1,000명(가정) 상황에서도 p95 목표를 준수해야 함."
  kpis:
    - "Wizard API p95 < 800ms"
    - "Generation API p95 < 10s"

steps_hint:
  - "k6 설치 및 기본 시나리오 스크립트(script.js) 작성"
  - "가상 유저(VU) 램프업 설정"
  - "Threshold(임계값) 설정 (p95 > 800ms 시 Fail)"
  - "테스트 실행 및 리포트 분석"

preconditions:
  - "백엔드 API 서버가 배포되어 있거나 로컬 실행 가능해야 한다."

postconditions:
  - "부하 테스트 결과 리포트가 생성되고, 성능 병목 구간이 식별된다."

dependencies: ["REQ-FUNC-003-BE-001"]
```

