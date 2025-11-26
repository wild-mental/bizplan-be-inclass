# REQ-NF-012-OPS-001: 모니터링 및 로깅 파이프라인 구축

## 1. 개요
- **목표**: 시스템 운영 상태를 파악할 수 있는 구조화된 로깅과 기초 모니터링 대시보드를 구축한다.
- **범위**:
  - Logback 설정 (JSON 포맷, TraceId 포함)
  - Prometheus Actuator Endpoint 노출
  - Grafana 대시보드 (JVM, HTTP Request Rate/Error/Duration)
- **Out of Scope**: 정교한 알림 룰셋(MVP는 Error 로그 기반 기본 알림만).

## 2. 상세 요구사항
- **로깅**: `timestamp`, `level`, `trace_id`, `user_id`, `message`, `context` 필드 포함.
- **메트릭**: 문서 생성 API 호출 수, 평균 소요 시간, 에러율 시각화.

---

```yaml
task_id: "REQ-NF-012-OPS-001"
title: "구조화된 로깅 및 Prometheus/Grafana 모니터링 구축"
summary: >
  운영 가시성 확보를 위해 JSON 로그 포맷을 적용하고,
  Prometheus/Grafana 기반의 애플리케이션 모니터링을 설정한다.
type: "non_functional"
epic: "EPIC_OBSERVABILITY"
req_ids: ["REQ-NF-012", "REQ-NF-004"]
component: ["infra.monitoring"]
agent_profile: ["infra"]

category: "observability"
labels: ["logging:json", "monitoring:application"]

requirements:
  description: "장애 발생 시 1분 이내에 로그 및 메트릭으로 확인 가능해야 함."
  kpis: ["로그 인덱싱 지연 < 1min"]

steps_hint:
  - "Logback xml 설정: LogstashEncoder 등 사용해 JSON 출력"
  - "Spring Boot Actuator 및 Micrometer Prometheus 의존성 추가"
  - "docker-compose.yml에 Prometheus/Grafana 추가 및 연동"
  - "기본 대시보드(Request, Error, JVM) 구성"

preconditions:
  - "Docker 환경 권장"

postconditions:
  - "API 호출 시 JSON 로그가 남고, Grafana에서 그래프가 그려진다."

dependencies: ["REQ-FUNC-001-BE-001"]
```

