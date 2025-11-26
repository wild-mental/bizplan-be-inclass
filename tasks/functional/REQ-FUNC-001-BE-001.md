# REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API

## 1. 개요
- **목표**: 사용자가 프로젝트를 생성할 때 선택할 수 있는 템플릿 목록을 제공하고, 선택된 템플릿으로 프로젝트 엔티티를 생성하는 백엔드 API를 구현한다.
- **범위**:
  - `GET /projects/templates`: 지원 템플릿 목록 반환
  - `POST /projects`: 프로젝트 생성 (Project 엔티티, 초기 메타데이터)
- **Out of Scope**: 사용자 인증 로직(User ID는 토큰에서 추출 또는 Mocking).

## 2. 상세 요구사항
- **템플릿 데이터**: 하드코딩된 목록 제공 ('KSTARTUP_2025', 'BANK_LOAN_2025' 등).
- **프로젝트 생성**: UUID 생성, `created_at` 설정, `status='draft'` 설정.

---

```yaml
task_id: "REQ-FUNC-001-BE-001"
title: "프로젝트 생성 및 템플릿 목록 API 구현"
summary: >
  템플릿 목록 조회 및 신규 프로젝트 생성 REST API를 구현한다.
type: "functional"
epic: "EPIC_1_PASS_THE_TEST"
req_ids: ["REQ-FUNC-001"]
component: ["backend.api"]
agent_profile: ["backend"]

inputs:
  description: "프로젝트 생성 요청"
  fields:
    - name: "template_code"
      type: "string"
      required: true

outputs:
  description: "생성된 프로젝트 정보"
  success:
    http_status: 201
    body_fields: ["project_id", "template_code", "status", "created_at"]

steps_hint:
  - "TemplateService: 템플릿 목록 하드코딩 반환 로직 구현"
  - "ProjectEntity: JPA/Hibernate 엔티티 정의"
  - "ProjectController: POST /projects 엔드포인트 구현"
  - "DB 저장 로직 구현"

preconditions:
  - "MySQL DB가 실행 중이어야 한다."

postconditions:
  - "DB에 새 Project 레코드가 생성된다."

dependencies: ["TASK-BE-INIT"]
```

