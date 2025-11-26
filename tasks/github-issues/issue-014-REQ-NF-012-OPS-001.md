# [#014] 구조화된 로깅 및 Prometheus/Grafana 모니터링 구축

## Labels
`epic:EPIC_3_NFR`, `type:observability`, `type:infra`, `component:monitoring`, `priority:Should`, `effort:M`

## Description
시스템 운영 상태를 파악할 수 있는 구조화된 로깅과 기초 모니터링 대시보드를 구축합니다.

## Scope
- Logback 설정 (JSON 포맷, TraceId 포함)
- Prometheus Actuator Endpoint 노출
- Grafana 대시보드 (JVM, HTTP Request Rate/Error/Duration)

### Out of Scope
- 정교한 알림 룰셋(MVP는 Error 로그 기반 기본 알림만)

## Requirements
- **로깅**: `timestamp`, `level`, `trace_id`, `user_id`, `message`, `context` 필드 포함
- **메트릭**: 문서 생성 API 호출 수, 평균 소요 시간, 에러율 시각화
- **SLO**: 장애 발생 시 1분 이내에 로그 및 메트릭으로 확인 가능

## Technical Stack
- Logback + Logstash Encoder (JSON 로그)
- Spring Boot Actuator
- Micrometer + Prometheus
- Grafana

## Observability Requirements
- 로그 인덱싱 지연 < 1min
- API 호출 추적 가능
- 에러 발생 시 즉시 확인 가능

## Implementation Steps
1. Logback xml 설정: LogstashEncoder 등 사용해 JSON 출력
2. Spring Boot Actuator 및 Micrometer Prometheus 의존성 추가
3. docker-compose.yml에 Prometheus/Grafana 추가 및 연동
4. 기본 대시보드(Request, Error, JVM) 구성
5. 알림 설정 (선택 사항)

## Acceptance Criteria
- [ ] Docker 환경 권장
- [ ] API 호출 시 JSON 로그가 남음
- [ ] Grafana에서 그래프가 그려짐
- [ ] 에러 로그가 구조화되어 저장됨

## Dependencies
- #006 (REQ-FUNC-001-BE-001) - API 존재 필요

## Related Requirements
REQ-NF-012, REQ-NF-004

