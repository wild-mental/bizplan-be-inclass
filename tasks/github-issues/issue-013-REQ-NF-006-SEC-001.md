# [#013] 데이터 저장/전송 암호화 및 보안 구성

## Labels
`epic:EPIC_3_NFR`, `type:security`, `type:backend`, `component:security`, `priority:Must`, `effort:M`

## Description
민감 데이터(사업계획서, 개인정보)의 저장 시 암호화 및 전송 구간 암호화를 적용합니다.

## Scope
- DB 컬럼 암호화 (AES-256) 또는 암호화된 파일 시스템 사용 확인
- Spring Security HTTPS 강제 설정
- 비밀번호 단방향 해시 (BCrypt)

### Out of Scope
- KMS(Key Management Service) 연동 (MVP에서는 환경변수/Secret 사용)

## Requirements
- **저장 암호화**: `wizard_answers`, `financial_model` 등 비즈니스 데이터는 암호화하여 저장 (App 레벨 컨버터 권장)
- **전송 보안**: 모든 API는 HTTPS만 허용
- **비밀번호 보안**: BCrypt 해싱 적용

## Technical Stack
- Spring Security
- JPA AttributeConverter (AES-256)
- BCrypt PasswordEncoder

## Security Requirements
- 모든 민감 데이터는 평문으로 저장되어서는 안 됨
- 보안 감사 지적 사항 0건

## Implementation Steps
1. Spring Security: PasswordEncoder(BCrypt) 빈 등록
2. JPA AttributeConverter를 이용한 AES-256 암호화 구현
3. application.yml: server.ssl.enabled=true 설정 (Self-signed for local)
4. 암호화 키 관리 (환경변수)
5. 보안 설정 테스트

## Acceptance Criteria
- [ ] DB 조회 시 민감 컬럼이 암호문으로 보임
- [ ] HTTPS로만 API 접근 가능
- [ ] 비밀번호가 BCrypt로 해싱되어 저장됨

## Dependencies
- #006 (REQ-FUNC-001-BE-001) - 프로젝트 엔티티 존재 필요

## Related Requirements
REQ-NF-006, REQ-NF-007

