# [#006] 프로젝트 생성 및 템플릿 목록 API 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:backend`, `component:api`, `priority:Must`, `effort:M`

## Description
사용자가 프로젝트를 생성할 때 선택할 수 있는 템플릿 목록을 제공하고, 선택된 템플릿으로 프로젝트 엔티티를 생성하는 백엔드 API를 구현합니다.

## Scope
- `GET /projects/templates`: 지원 템플릿 목록 반환
- `POST /projects`: 프로젝트 생성 (Project 엔티티, 초기 메타데이터)

### Out of Scope
- 사용자 인증 로직(User ID는 토큰에서 추출 또는 Mocking)

## Requirements
- **템플릿 데이터**: 하드코딩된 목록 제공 ('KSTARTUP_2025', 'BANK_LOAN_2025' 등)
- **프로젝트 생성**: UUID 생성, `created_at` 설정, `status='draft'` 설정

## Technical Stack
- Java 17 + Spring Boot 3.x
- Spring Data JPA
- MySQL 8.x

## API Specification

### GET /projects/templates
**Response (200 OK):**
```json
[
  {
    "code": "KSTARTUP_2025",
    "name": "예비창업패키지",
    "description": "중소벤처기업부 예비창업패키지 양식"
  }
]
```

### POST /projects
**Request:**
```json
{
  "template_code": "KSTARTUP_2025"
}
```

**Response (201 Created):**
```json
{
  "project_id": "uuid",
  "template_code": "KSTARTUP_2025",
  "status": "draft",
  "created_at": "2025-11-26T10:00:00Z"
}
```

## Implementation Steps
1. TemplateService: 템플릿 목록 하드코딩 반환 로직 구현
2. ProjectEntity: JPA/Hibernate 엔티티 정의
3. ProjectController: POST /projects 엔드포인트 구현
4. DB 저장 로직 구현

## Acceptance Criteria
- [ ] MySQL DB가 실행 중
- [ ] DB에 새 Project 레코드가 생성됨
- [ ] API 호출 시 201 상태 코드와 함께 프로젝트 정보 반환

## Dependencies
- TASK-BE-INIT (프로젝트 초기 설정 - 완료 가정)

## Enables
- #007, #013, #014

## Related Requirements
REQ-FUNC-001

