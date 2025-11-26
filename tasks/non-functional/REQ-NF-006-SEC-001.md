# REQ-NF-006-SEC-001: 데이터 암호화 및 보안 설정

## 1. 개요
- **목표**: 민감 데이터(사업계획서, 개인정보)의 저장 시 암호화 및 전송 구간 암호화를 적용한다.
- **범위**:
  - DB 컬럼 암호화 (AES-256) 또는 암호화된 파일 시스템 사용 확인
  - Spring Security HTTPS 강제 설정
  - 비밀번호 단방향 해시 (BCrypt)
- **Out of Scope**: KMS(Key Management Service) 연동 (MVP에서는 환경변수/Secret 사용).

## 2. 상세 요구사항
- **저장 암호화**: `wizard_answers`, `financial_model` 등 비즈니스 데이터는 암호화하여 저장한다(선택: DB 레벨 or App 레벨). MVP는 App 레벨 컨버터 권장.
- **전송 보안**: 모든 API는 HTTPS만 허용.

---

```yaml
task_id: "REQ-NF-006-SEC-001"
title: "데이터 저장/전송 암호화 및 보안 구성"
summary: >
  사용자 비밀번호 BCrypt 해싱, 중요 데이터 AES 암호화 저장, 
  HTTPS 강제 설정을 통해 기본 보안 요구사항을 충족한다.
type: "non_functional"
epic: "EPIC_SECURITY"
req_ids: ["REQ-NF-006", "REQ-NF-007"]
component: ["backend.security"]
agent_profile: ["backend", "infra"]

category: "security"
labels: ["security:encryption", "security:auth"]

requirements:
  description: "모든 민감 데이터는 평문으로 저장되어서는 안 된다."
  kpis: ["보안 감사 지적 사항 0건"]

steps_hint:
  - "Spring Security: PasswordEncoder(BCrypt) 빈 등록"
  - "JPA AttributeConverter를 이용한 AES-256 암호화 구현"
  - "application.yml: server.ssl.enabled=true 설정 (Self-signed for local)"

preconditions:
  - "없음"

postconditions:
  - "DB 조회 시 민감 컬럼이 암호문으로 보여야 한다."

dependencies: ["REQ-FUNC-001-BE-001"]
```

