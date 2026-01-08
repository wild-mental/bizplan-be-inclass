# 작업 의존 관계 및 이슈 문서 작성
_Exported on 2025. 11. 29. at 12시 55분 27초 GMT+9 from Cursor (2.1.39)_

---

**User**

@docs/INTEGRATED_WBS_DAG.md 에 명시된 의존 관계를 정확하게 작업의 선후 관계로 적용할 수 있는 구조로, @tasks 경로에 있는 작업의 명세들을 GitHub 원격 저장소 프로젝트에서 관리할 수 있는 형태의 Issue 문서로 작성하고 싶어.

작성된 문서는 @github-issues 경로에 작업 순서를 이슈 넘버링에 반영한 형태로 저장해줘.
그리고 이슈 수행 순서 (병렬 개발 가능한 정보까지 포함)를 명시한 문서도 별도로 하나 만들어줘.

---

**Cursor**

먼저 `tasks/` 폴더의 모든 작업 명세 파일들을 읽어서 GitHub Issue로 변환하겠습니다.

이제 모든 작업 명세를 읽었습니다. 의존 관계를 분석하여 GitHub Issue 형태로 변환하고 작업 순서를 반영한 넘버링으로 저장하겠습니다.

```markdown
# [#001] 프로젝트 생성 및 Wizard 기본 레이아웃 PoC 구현

## Labels
`epic:EPIC_0_FE_PROTOTYPE`, `type:frontend`, `type:poc`, `priority:Must`, `effort:S`

## Description
사용자가 로그인 후 '새 프로젝트 만들기'를 통해 템플릿(예비창업패키지 등)을 선택하고, Wizard의 기본 단계(Step) 네비게이션이 동작하는 화면을 구현합니다.

## Scope
- 프로젝트 생성 모달/페이지
- 템플릿 선택 UI (카드 형태)
- Wizard 레이아웃 (사이드바/상단 단계 표시 + 메인 컨텐츠 영역)
- Mock API 연동 (프로젝트 생성, 조회)

### Out of Scope
- 실제 로그인 인증(하드코딩 토큰 사용)
- 세부 폼 필드 구현(다음 Task)
- 모바일 반응형 완벽 지원

## Requirements
- **템플릿 선택**: '예비창업패키지', '초기창업패키지', '은행용' 3가지 더미 옵션 제공
- **Wizard 프레임**: 좌측에 '1. 문제정의', '2. 해결방안', '3. 시장분석' 등 챕터 목록이 보이고, 클릭 시 메인 영역이 전환
- **상태 관리**: `useProjectStore`(Zustand 등)를 통해 현재 선택된 단계와 프로젝트 메타데이터 관리

## Technical Stack
- React + Vite + TypeScript
- Tailwind CSS (스타일링)
- React Router (라우팅)
- Zustand (상태 관리)

## Implementation Steps
1. React Router로 `/projects/new` 및 `/projects/:id/wizard` 경로 설정
2. 템플릿 선택 카드 컴포넌트 구현
3. WizardLayout 컴포넌트 구현 (Sidebar + Content Area)
4. Zustand Store에 createProject 액션 구현 (Mock)

## Acceptance Criteria
- [ ] 사용자가 템플릿을 선택하면 Wizard 첫 화면으로 이동
- [ ] 사이드바 메뉴 클릭 시 URL과 메인 컨텐츠가 변경
- [ ] React+Vite 기본 프로젝트가 셋업되어 있음

## Dependencies
- None (시작점)

## Related Requirements
REQ-FUNC-001, REQ-FUNC-002

```

```markdown
# [#002] Wizard 입력 폼 및 자동저장 UI PoC 구현

## Labels
`epic:EPIC_0_FE_PROTOTYPE`, `type:frontend`, `type:poc`, `priority:Must`, `effort:S`

## Description
Wizard의 각 단계(Step) 내에서 질문에 답변을 입력하는 폼(Form)을 구현하고, 입력 시 자동저장 상태(저장 중... -> 저장됨)를 표시합니다.

## Scope
- Textarea, Input 등 기본 입력 컴포넌트
- 필수 항목(Required) 검증 UI
- Debounce 된 자동저장 Mocking
- '다음 단계' 버튼 동작 (Validation 체크)

### Out of Scope
- 복잡한 Rich Text Editor
- 서버 실제 저장(Mock으로 처리)

## Requirements
- **폼 구성**: 각 Step은 여러 개의 질문(Question)으로 구성
- **유효성 검사**: 필수 질문이 비어있을 경우 '다음' 버튼 클릭 시 에러 메시지 표시하고 포커스 이동
- **자동 저장**: 사용자가 타이핑을 멈추면 1초 뒤 '저장 중...' 표시 후 '저장됨'으로 변경 (Console 로그로 확인)

## Technical Stack
- React Hook Form (폼 상태 관리)
- Zod (스키마 검증)
- UI Components (기존 공통 컴포넌트 활용)

## Implementation Steps
1. React Hook Form을 사용하여 StepForm 컴포넌트 구현
2. AutoSaveIndicator 컴포넌트 구현 (State 기반)
3. useDebounce 훅을 사용해 입력 변경 감지 및 Mock Save 호출
4. Next 버튼 클릭 시 triggerValidation 호출 및 에러 핸들링

## Acceptance Criteria
- [ ] EPIC0-FE-001의 WizardLayout이 준비되어 있음
- [ ] 필수 항목 미입력 시 다음 단계 진행이 차단됨
- [ ] 입력 중단 시 자동으로 '저장됨' 상태로 변경됨

## Dependencies
- #001 (EPIC0-FE-001)

## Related Requirements
REQ-FUNC-002, REQ-FUNC-007, REQ-FUNC-013

```

```markdown
# [#003] 사업계획서 초안 생성 및 뷰어 UI PoC 구현

## Labels
`epic:EPIC_0_FE_PROTOTYPE`, `type:frontend`, `type:poc`, `priority:Must`, `effort:M`

## Description
입력된 데이터를 바탕으로 '초안 생성'을 요청하고, 생성된 문서(텍스트)를 화면에 보여주는 뷰어/에디터를 구현합니다.

## Scope
- '초안 생성' 버튼 및 로딩 UI (Spinner/Progress)
- 생성된 문서 뷰어 (Markdown 또는 Textarea)
- '내보내기(HWP/PDF)' 버튼 (Mock Alert)
- 섹션별 'AI 다시 쓰기' 버튼 UI

### Out of Scope
- 실제 LLM 연동(Dummy 텍스트 반환)
- 실제 HWP 변환

## Requirements
- **생성 트리거**: Wizard 마지막 단계 또는 별도 메뉴에서 '초안 생성' 버튼 제공
- **결과 표시**: 생성 완료 후, 챕터별로 구조화된 텍스트가 에디터에 채워짐
- **AI 보조**: 각 텍스트 블록 옆에 'AI 보완' 아이콘 배치 (클릭 시 예시 텍스트 변경)

## Technical Stack
- React Markdown (뷰어용) 또는 Toast UI Editor (에디터용)
- Suspense / Loading State 처리

## Implementation Steps
1. DraftGenerationPage 컴포넌트 생성
2. API 요청 시뮬레이션 (3초 딜레이 후 Dummy Text 반환)
3. DocumentViewer 컴포넌트 구현 (섹션별 편집 가능)
4. Export 버튼 클릭 이벤트 핸들러 (window.alert)

## Acceptance Criteria
- [ ] EPIC0-FE-002 완료 (입력 데이터가 있어야 함)
- [ ] 생성 버튼 클릭 시 로딩 후 결과 텍스트가 표시됨
- [ ] 내보내기 버튼 클릭 시 성공 메시지가 뜸

## Dependencies
- #002 (EPIC0-FE-002)

## Related Requirements
REQ-FUNC-003, REQ-FUNC-004, REQ-FUNC-011

```

```markdown
# [#004] 재무 입력 및 유닛 이코노믹스 시각화 UI PoC 구현

## Labels
`epic:EPIC_0_FE_PROTOTYPE`, `type:frontend`, `type:poc`, `priority:Must`, `effort:M`

## Description
매출, 비용, 고객 획득 비용(CAC) 등 재무 데이터를 입력받고, 이를 그래프(손익분기점, LTV/CAC)로 시각화합니다.

## Scope
- 재무 전용 입력 테이블 (엑셀 스타일 또는 폼)
- Chart.js / Recharts 를 활용한 그래프 렌더링
- 주요 지표(LTV, CAC, BEP) 카드 표시

### Out of Scope
- 복잡한 엑셀 수식 계산(간단한 사칙연산만 JS로 처리)

## Requirements
- **입력**: 고객 수, 객단가, 변동비, 고정비 입력 필드
- **즉시 반응**: 입력 값 변경 시 예상 매출 및 이익 그래프가 즉시 업데이트
- **경고**: LTV/CAC < 3 인 경우 경고 뱃지 표시

## Technical Stack
- Recharts (차트 라이브러리)
- React Table (선택 사항)

## Implementation Steps
1. FinancialInputForm 컴포넌트 구현
2. Recharts 설치 및 BreakEvenPointChart 컴포넌트 구현
3. 입력 값 변경에 따른 파생 변수(매출, 이익) 계산 로직 작성 (util 함수)
4. DashboardLayout에 통합

## Acceptance Criteria
- [ ] 프로젝트 생성 플로우(EPIC0-FE-001)와 독립적으로 개발 가능하나, 네비게이션 연결 필요
- [ ] 입력 값을 바꾸면 그래프가 다시 그려짐

## Dependencies
- #001 (EPIC0-FE-001) - 네비게이션 연결용

## Parallelizable With
- #002, #003, #005 (독립적 개발 가능)

## Related Requirements
REQ-FUNC-009, REQ-FUNC-012

```

```markdown
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

```

```markdown
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
- Java 21 + Spring Boot 4.0.0
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

```

```markdown
# [#007] Wizard 단계별 답변 저장/조회 API 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:backend`, `component:api`, `priority:Must`, `effort:M`

## Description
Wizard의 각 단계에서 사용자가 입력한 답변 데이터를 저장하고 불러오는 API를 구현합니다.

## Scope
- `POST /projects/{id}/wizard/steps`: 답변 저장 (Upsert)
- `GET /projects/{id}`: 프로젝트 전체 상태 및 답변 조회

### Out of Scope
- 복잡한 유효성 검사(단순 저장 위주)

## Requirements
- **데이터 구조**: `wizard_answers` 컬럼(JSONB)에 Key-Value 형태로 저장
- **부분 업데이트**: 기존 답변을 유지하면서 새로운 답변만 병합(Merge)하거나 덮어쓰기

## Technical Stack
- Java 21 + Spring Boot 4.0.0
- Spring Data JPA
- MySQL 8.x JSON 타입
- Hibernate Types (JSONB 매핑)

## API Specification

### POST /projects/{id}/wizard/steps
**Request:**
```json
{
  "step_id": "step_1_problem",
  "answers": {
    "q1": "창업 동기...",
    "q2": "해결하고자 하는 문제..."
  }
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "updated_at": "2025-11-26T10:05:00Z"
}
```

### GET /projects/{id}
**Response (200 OK):**
```json
{
  "project_id": "uuid",
  "template_code": "KSTARTUP_2025",
  "wizard_answers": {
    "step_1_problem": {
      "q1": "...",
      "q2": "..."
    }
  }
}
```

## Implementation Steps
1. Project 엔티티의 wizard_answers 필드(JSONB) 매핑 (Hibernate Types)
2. 답변 병합 로직 구현 (Service Layer)
3. Controller 엔드포인트 구현
4. 단위 테스트 작성

## Acceptance Criteria
- [ ] REQ-FUNC-001-BE-001 완료 (Project 엔티티 존재)
- [ ] DB의 JSONB 컬럼에 답변이 저장/업데이트됨
- [ ] 부분 업데이트 시 기존 답변이 유지됨

## Dependencies
- #006 (REQ-FUNC-001-BE-001)

## Enables
- #009

## Related Requirements
REQ-FUNC-002, REQ-FUNC-013

```

```markdown
# [#008] 사업계획서 생성 LLM 엔진 및 프롬프트 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:ai`, `type:backend`, `component:ai-engine`, `priority:Must`, `effort:L`

## Description
Wizard 답변(JSON)을 입력받아, LangChain 및 LLM(Gemini)을 사용하여 섹션별 사업계획서 초안을 생성하는 Python API를 구현합니다.

## Scope
- FastAPI 서버 구축
- `POST /generate`: 문서 생성 엔드포인트
- Prompt Template 관리 (섹션별 프롬프트)
- LLM Gateway(Gemini) 연동

### Out of Scope
- 복잡한 RAG(검색 증강)
- Fine-tuning

## Requirements
- **프롬프트 전략**: 'Role: 전문 컨설턴트', 'Context: 사용자 답변', 'Task: 섹션별 초안 작성' 구조의 프롬프트 체이닝
- **출력 포맷**: JSON 형태로 `{ "section_1": "...", "section_2": "..." }` 반환
- **안정성**: LLM 오류 시 재시도 로직(LangChain 기능 활용)

## Technical Stack
- Python 3.10+
- FastAPI
- LangChain
- Google Gemini API

## API Specification

### POST /generate
**Request:**
```json
{
  "answers": {
    "step_1_problem": {
      "q1": "...",
      "q2": "..."
    }
  },
  "template_type": "KSTARTUP_2025"
}
```

**Response (200 OK):**
```json
{
  "sections": {
    "problem_definition": "사업 아이템의 필요성...",
    "solution_approach": "해결 방안...",
    "market_analysis": "시장 분석..."
  },
  "generated_at": "2025-11-26T10:10:00Z"
}
```

## Implementation Steps
1. FastAPI 프로젝트 셋업
2. LangChain Gemini ChatModel 연동
3. PromptTemplate 정의 (섹션별)
4. LLMChain 구성 및 실행 로직
5. 에러 핸들링 및 재시도 로직

## Acceptance Criteria
- [ ] Google Gemini API Key 발급 및 환경변수 설정
- [ ] API 호출 시 유의미한 사업계획서 텍스트가 반환됨
- [ ] LLM 오류 시 재시도 후 실패 시 에러 응답

## Dependencies
- None (독립적으로 시작 가능)

## Enables
- #009, #011

## Parallelizable With
- #006, #007 (독립적 개발 가능)

## Related Requirements
REQ-FUNC-003, REQ-FUNC-004

```

```markdown
# [#009] 사업계획서 생성 오케스트레이션 API 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:backend`, `component:api`, `priority:Must`, `effort:M`

## Description
클라이언트의 '사업계획서 생성' 요청을 받아, Python LLM 엔진으로 생성을 위임하고 결과를 저장하는 오케스트레이션 API를 구현합니다.

## Scope
- `POST /projects/{id}/documents/business-plan:generate`
- LLM 엔진(FastAPI) 호출 (RestTemplate/WebClient)
- 결과(`BusinessPlanDocument`) 저장

### Out of Scope
- 실제 프롬프트 엔지니어링(AI 엔진에서 수행)

## Requirements
- **입력 조합**: 프로젝트의 `wizard_answers`를 조회하여 LLM 엔진에 전달할 Context 구성
- **비동기/동기**: MVP는 동기(Timeout 60s+) 또는 Polling 방식을 고려하되, 여기서는 **HTTP 동기 호출**로 단순화(추후 Async 전환)
- **저장**: 생성된 섹션별 텍스트를 `BusinessPlanDocument` 테이블에 저장

## Technical Stack
- Java 21 + Spring Boot 4.0.0
- WebClient (비동기 HTTP 클라이언트)
- Spring Data JPA

## API Specification

### POST /projects/{id}/documents/business-plan:generate
**Response (200 OK):**
```json
{
  "document_id": "uuid",
  "project_id": "uuid",
  "sections": {
    "problem_definition": "...",
    "solution_approach": "...",
    "market_analysis": "..."
  },
  "generated_at": "2025-11-26T10:15:00Z"
}
```

**Response (500 Internal Server Error):**
```json
{
  "error": "LLM_ENGINE_ERROR",
  "message": "AI 엔진 응답 실패"
}
```

## Implementation Steps
1. ProjectService: wizard_answers 조회
2. LlmClient: Python 엔진 API (POST /generate) 호출 구현
3. BusinessPlanDocument 엔티티 설계 및 저장 로직
4. Transaction 관리 (외부 호출 포함 주의)
5. 타임아웃 및 에러 핸들링

## Acceptance Criteria
- [ ] REQ-FUNC-002-BE-001 완료 (답변 데이터 존재)
- [ ] Python LLM 엔진(REQ-FUNC-003-AI-001) 인터페이스 정의 필요
- [ ] 새 BusinessPlanDocument 레코드가 생성됨
- [ ] LLM 엔진 실패 시 적절한 에러 응답 반환

## Dependencies
- #007 (REQ-FUNC-002-BE-001)
- #008 (REQ-FUNC-003-AI-001)

## Enables
- #010, #015

## Related Requirements
REQ-FUNC-003, REQ-FUNC-004

```

```markdown
# [#010] 사업계획서 HWP/PDF 내보내기 기능 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:backend`, `component:core`, `priority:Must`, `effort:L`

## Description
저장된 사업계획서 데이터를 템플릿 파일(.hwp, .docx)에 병합(Merge)하여 다운로드 가능한 파일을 생성합니다.

## Scope
- HWP 라이브러리(kr.dogfoot.hwplib 등) 또는 변환 솔루션 연동
- PDF 변환 (LibreOffice, wkhtmltopdf 등 활용)
- `GET /projects/{id}/export` API

### Out of Scope
- 완벽한 스타일링(초안 수준 포맷팅)
- 표/이미지 삽입(텍스트 위주)

## Requirements
- **HWP 필드 매핑**: 미리 준비된 HWP 템플릿의 누름틀(Field)에 데이터 삽입
- **PDF 생성**: HTML 템플릿 렌더링 후 PDF로 변환하거나, HWP를 PDF로 변환

## Technical Stack
- Java 21 + Spring Boot 4.0.0
- kr.dogfoot.hwplib (HWP 라이브러리)
- Apache PDFBox 또는 wkhtmltopdf

## API Specification

### GET /projects/{id}/export?format=hwp
**Response (200 OK):**
```
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="business-plan.hwp"

[Binary File Stream]
```

### GET /projects/{id}/export?format=pdf
**Response (200 OK):**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="business-plan.pdf"

[Binary File Stream]
```

## Implementation Steps
1. HWP 템플릿 파일 준비 및 누름틀 정의
2. Java HWP 라이브러리(hwplib) 의존성 추가
3. ExportService: 데이터-누름틀 매핑 및 파일 생성
4. Controller: 파일 다운로드 응답 처리
5. PDF 변환 로직 구현

## Acceptance Criteria
- [ ] 서버에 폰트 및 템플릿 파일이 배포되어 있음
- [ ] 생성된 HWP 파일을 열었을 때 텍스트가 올바른 위치에 들어가 있음
- [ ] PDF 파일이 정상적으로 생성되고 다운로드됨

## Dependencies
- #009 (REQ-FUNC-003-BE-001)

## Related Requirements
REQ-FUNC-011

```

```markdown
# [#011] PMF 진단 및 리포트 생성 LLM 엔진 구현

## Labels
`epic:EPIC_2_AVOID_FAILURE`, `type:ai`, `type:backend`, `component:ai-engine`, `priority:Should`, `effort:M`

## Description
사용자의 PMF 설문 응답을 분석하여 PMF 단계(Stage), 리스크, 개선 권고를 도출하는 LLM 기반 진단 엔진을 구현합니다.

## Scope
- `POST /pmf/analyze`: 진단 요청 처리
- PMF 진단 프롬프트 엔지니어링 (Persona: 스타트업 액셀러레이터 심사역)
- 응답 데이터 부족 시 예외 처리 (Rule Base + LLM)

### Out of Scope
- 진단 이력 관리(Backend DB 역할)

## Requirements
- **진단 로직**: 10개 이상의 문항 답변을 종합하여 'Problem-Solution Fit', 'Product-Market Fit', 'Scale-up' 중 단계 판정
- **리스크 추출**: 답변 내용 중 논리적 비약이나 시장성 부족 신호를 찾아 Top 3 리스크로 정리

## Technical Stack
- Python 3.10+
- FastAPI (REQ-FUNC-003-AI-001과 동일 환경 공유)
- LangChain
- Google Gemini API

## API Specification

### POST /pmf/analyze
**Request:**
```json
{
  "answers": [
    {
      "question_id": "pmf_q1",
      "answer": "고객이 매우 만족함"
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "stage": "Product-Market Fit",
  "score": 75,
  "risks": [
    "시장 규모 검증 부족",
    "경쟁사 분석 미비",
    "수익 모델 불명확"
  ],
  "recommendations": [
    "시장 조사 강화 필요",
    "경쟁사 벤치마킹 수행",
    "수익 모델 구체화"
  ]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "INSUFFICIENT_DATA",
  "message": "진단을 위해 최소 5개 이상의 답변이 필요합니다."
}
```

## Implementation Steps
1. PMF 진단용 System Prompt 작성
2. 답변 개수 부족 시 Early Return 로직 구현
3. LangChain Chain 구성 (JSON Output Parser 활용)
4. FastAPI 엔드포인트 연동
5. 에러 핸들링

## Acceptance Criteria
- [ ] REQ-FUNC-003-AI-001 환경(FastAPI/Gemini) 공용 사용
- [ ] 설문 답변 입력 시 분석된 리포트 JSON이 반환됨
- [ ] 답변 수 부족 시 적절한 에러 응답

## Dependencies
- #008 (REQ-FUNC-003-AI-001) - FastAPI 환경 공유

## Parallelizable With
- #009, #010, #012 (독립적 기능)

## Related Requirements
REQ-FUNC-008, REQ-FUNC-010

```

```markdown
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

```

```markdown
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

```

```markdown
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

```

```markdown
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

```

이제 이슈 수행 순서와 병렬 개발 가능 정보를 명시한 문서를 생성하겠습니다.

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0)
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1)
- #011 ~ #012: Special Features (EPIC 2)
- #013 ~ #015: Non-Functional Requirements (EPIC 3)

---

## Phase 1: Frontend PoC (EPIC 0)
**목표**: UI/UX 검증 및 사용자 플로우 확인

### Wave 1.1 (시작점)
- **#001** - EPIC0-FE-001: 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
  - 의존성: 없음
  - 병렬 가능: #005, #006 (독립적인 백엔드 작업과 병렬 가능)

### Wave 1.2 (Wave 1.1 완료 후)
**병렬 개발 가능한 작업들:**
- **#002** - EPIC0-FE-002: Wizard 입력 폼 및 자동저장 UI PoC
  - 의존성: #001
  - 병렬 가능: #004, #005
  
- **#004** - EPIC0-FE-004: 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
  - 의존성: #001 (네비게이션 연결)
  - 병렬 가능: #002, #005

- **#005** - EPIC0-FE-005: PMF 진단 설문 및 리포트 UI PoC
  - 의존성: 없음 (독립 페이지)
  - 병렬 가능: #001, #002, #004

### Wave 1.3 (Wave 1.2의 #002 완료 후)
- **#003** - EPIC0-FE-003: 사업계획서 초안 생성 및 뷰어 UI PoC
  - 의존성: #002
  - 병렬 가능: 없음 (이 단계는 UI 완결)

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization
**목표**: 최단 기간 내 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Frontend
    #001 FE-001           :f001, 2025-11-27, 3d
    #002 FE-002           :f002, after f001, 3d
    #004 FE-004           :f004, after f001, 4d
    #005 FE-005           :f005, 2025-11-27, 2d
    #003 FE-003           :f003, after f002, 4d
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 6명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #001
  - Developer 2: #005
  - Developer 3: #006
  - Developer 4: #008
  - Developer 5: #012

- **Week 1 (Day 4-7):**
  - Developer 1: #002, #004 (병렬)
  - Developer 2: #011 (준비)
  - Developer 3: #007 → #013, #014 (병렬)
  - Developer 4: #009 준비
  - Developer 5: #012 계속

- **Week 2:**
  - Developer 1: #003
  - Developer 2: #011
  - Developer 3: #013, #014 완료
  - Developer 4: #009 → #010
  - Developer 5: #015

### Strategy 2: Sequential with Limited Resources
**목표**: 2-3명의 소규모 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #001 (Frontend Base)
  Day 4-7: #006 (Backend Base)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #002 + #004 (병렬 가능 시)

Week 3:
  Day 1-3: #007 (Backend API)
  Day 4-7: #005 + #012 (병렬)

Week 4:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #003 (Frontend 완결)

Week 5:
  Day 1-3: #010 (Export) + #011 (PMF)
  Day 4-7: #013 + #014 (NFR)

Week 6:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Buffer / QA
```

### Strategy 3: Feature-First
**목표**: 기능별 완결도 우선 (데모 준비 시)

1. **Sprint 1 (Wizard Flow):** #001 → #002 → #006 → #007 → #003
2. **Sprint 2 (Document Generation):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #004 → #012, #005 → #011
4. **Sprint 4 (NFR):** #013 → #014 → #015

---

## Dependency Matrix

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #001  | -          | #002, #004 | #005, #006, #008, #012 |
| #002  | #001       | #003    | #004, #005 |
| #003  | #002       | -       | - |
| #004  | #001       | -       | #002, #005 |
| #005  | -          | -       | All |
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order

### For Backend-Focused Team
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)
7. **Phase 7**: Frontend (#001 → #002 → #003 → #004 → #005)

### For Full-Stack Team (Recommended)
1. **Sprint 1**: #001, #006, #008, #012 (병렬)
2. **Sprint 2**: #002, #004, #007, #013, #014 (병렬)
3. **Sprint 3**: #003, #009, #011 (병렬)
4. **Sprint 4**: #005, #010, #015 (병렬/순차)

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md

```

마지막으로 GitHub Issues 폴더의 README 문서를 생성하겠습니다.

```markdown
# GitHub Issues for Bizplan Backend

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── issue-001-EPIC0-FE-001.md        # Frontend PoC Issues
├── issue-002-EPIC0-FE-002.md
├── issue-003-EPIC0-FE-003.md
├── issue-004-EPIC0-FE-004.md
├── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # Backend Core Issues
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description |
|-------|-------|-------------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) |
| #011-#012 | Phase 3 | Special Features (EPIC 2) |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) |

## How to Use

### 1. Creating Issues in GitHub

각 이슈 파일을 GitHub에 등록하려면:

```bash
# GitHub CLI 사용
gh issue create -F tasks/github-issues/issue-001-EPIC0-FE-001.md

# 또는 일괄 생성 스크립트
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# 프로젝트 생성
gh project create --title "Bizplan MVP Development" --owner @me

# 이슈를 프로젝트에 추가
gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/1
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (6명 팀):**
- Wave 1: #001, #005, #006, #008, #012 (병렬)
- Wave 2: #002, #004, #007, #013, #014 (병렬)
- Wave 3: #003, #009, #011 (병렬)
- Wave 4: #010, #015 (순차)

**Small Team (2-3명):**
- Sprint 1: #001 → #002 → #003
- Sprint 2: #006 → #007 → #009
- Sprint 3: #008 → #010
- Sprint 4: #004, #005, #012 (병렬)
- Sprint 5: #011, #013, #014, #015

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation

```bash
#!/bin/bash
# scripts/create_all_issues.sh

for i in {001..015}; do
  issue_file="tasks/github-issues/issue-$i-*.md"
  if [ -f $issue_file ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0

```

완료했습니다! `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 15개의 GitHub Issue 문서와 실행 계획 문서를 생성했습니다.

## 📋 생성된 파일 요약

### GitHub Issue 문서 (15개)
**Frontend PoC (5개):**
- `issue-001-EPIC0-FE-001.md` - 프로젝트 생성 및 Wizard 레이아웃
- `issue-002-EPIC0-FE-002.md` - Wizard 입력 폼 및 자동저장
- `issue-003-EPIC0-FE-003.md` - 사업계획서 초안 생성 뷰어
- `issue-004-EPIC0-FE-004.md` - 재무 입력 및 시각화
- `issue-005-EPIC0-FE-005.md` - PMF 진단 설문 및 리포트

**Backend Core (5개):**
- `issue-006-REQ-FUNC-001-BE-001.md` - 프로젝트 생성 API
- `issue-007-REQ-FUNC-002-BE-001.md` - Wizard 답변 저장 API
- `issue-008-REQ-FUNC-003-AI-001.md` - LLM 문서 생성 엔진
- `issue-009-REQ-FUNC-003-BE-001.md` - 문서 생성 오케스트레이션
- `issue-010-REQ-FUNC-011-BE-001.md` - HWP/PDF 내보내기

**Special Features (2개):**
- `issue-011-REQ-FUNC-008-AI-001.md` - PMF 진단 LLM 엔진
- `issue-012-REQ-FUNC-012-BE-001.md` - 재무 계산 엔진

**Non-Functional (3개):**
- `issue-013-REQ-NF-006-SEC-001.md` - 데이터 암호화 및 보안
- `issue-014-REQ-NF-012-OPS-001.md` - 로깅 및 모니터링
- `issue-015-REQ-NF-001-PERF-001.md` - 성능 테스트

### 실행 계획 문서

**`ISSUE_EXECUTION_PLAN.md`** - 포함 내용:
- ✅ Phase별 Wave 구조 (병렬 개발 가능 작업 명시)
- ✅ 3가지 실행 전략 (최대 병렬화 / 소규모 팀 / 기능 우선)
- ✅ Gantt 차트 (Mermaid 다이어그램)
- ✅ 의존성 매트릭스 테이블
- ✅ Critical Path 분석
- ✅ 권장 실행 순서

**`README.md`** - 사용 가이드:
- ✅ GitHub CLI 명령어 예시
- ✅ Label/Milestone/Project 설정 방법
- ✅ 브랜치/커밋 컨벤션
- ✅ 자동화 스크립트 예시

## 🎯 주요 특징

### 1. 작업 순서 반영
이슈 번호가 의존 관계를 고려한 실행 가능 순서를 나타냅니다:
- **#001-#005**: Frontend PoC
- **#006-#010**: Backend Core & AI
- **#011-#012**: Special Features
- **#013-#015**: NFR

### 2. 병렬 개발 정보 명시
각 이슈에 다음 정보 포함:
- **Dependencies**: 선행 작업
- **Enables**: 후속 작업
- **Parallelizable With**: 동시 진행 가능 작업

### 3. 실행 전략 제시
- **최대 병렬화**: 6명 팀, 약 4주 완료
- **소규모 팀**: 2-3명 팀, 약 6주 완료
- **기능 우선**: Sprint 단위 완결형

### 4. Critical Path
```
#006 → #007 → #009 → #010 → #015
(Backend 기본 → 답변 저장 → 문서 생성 → 내보내기 → 성능 테스트)
```

## 📌 다음 단계

### GitHub에 이슈 등록
```bash
# 개별 등록
gh issue create -F tasks/github-issues/issue-001-EPIC0-FE-001.md

# 일괄 등록
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done
```

### 프로젝트 보드 구성
```bash
gh project create --title "Bizplan MVP Development" --owner @me
```

모든 파일이 `tasks/github-issues/` 폴더에 저장되었습니다!

```plaintext
#006 → #007 → #009 → #010 → #015
(Backend 기본 → 답변 저장 → 문서 생성 → 내보내기 → 성능 테스트)
```

```shellscript
# 개별 등록
gh issue create -F tasks/github-issues/issue-001-EPIC0-FE-001.md

# 일괄 등록
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done
```

```shellscript
gh project create --title "Bizplan MVP Development" --owner @me
```

---

**User**

현재 FE 작업에 해당하는 EPIC0 이슈들은 별도 프로젝트에서 모두 완수했어.

이 점을 작성된 @github-issues 하위 파일들의 내용에 반영해줘.

이후 깃허브에 이슈를 발행할 때 제외하려고 해.

---

**Cursor**

EPIC0 (Frontend PoC) 작업들이 완료되었다는 점을 반영하여 관련 파일들을 정리하고 문서를 업데이트하겠습니다.

```markdown
# Completed Issues

## EPIC 0: Frontend PoC Prototype

이 폴더의 이슈들은 별도 프로젝트에서 이미 완료되었습니다.

### Completed Issues

- **#001** - EPIC0-FE-001: 프로젝트 생성 및 Wizard 기본 레이아웃 PoC ✅
- **#002** - EPIC0-FE-002: Wizard 입력 폼 및 자동저장 UI PoC ✅
- **#003** - EPIC0-FE-003: 사업계획서 초안 생성 및 뷰어 UI PoC ✅
- **#004** - EPIC0-FE-004: 재무 입력 및 유닛 이코노믹스 시각화 UI PoC ✅
- **#005** - EPIC0-FE-005: PMF 진단 설문 및 리포트 UI PoC ✅

## Status

- **완료 시점**: 2025-11-26 이전
- **완료 프로젝트**: 별도 Frontend 프로젝트
- **GitHub Issues 발행**: 불필요 (이미 완료됨)

## Notes

Backend 작업 시작 시점에 Frontend UI/UX는 이미 준비되어 있으므로:
- Backend API는 Frontend 인터페이스에 맞춰 개발
- Mock 데이터 대신 실제 API 연동 필요
- API Contract (Request/Response 스펙)는 Frontend와 사전 합의 완료

## Impact on Backend Issues

Frontend 완료로 인한 Backend 작업 영향:
- ✅ UI 검증 완료로 API 스펙 확정
- ✅ Frontend 팀과의 통합 테스트 즉시 가능
- ✅ 사용자 시나리오 기반 개발 가능

```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## Phase 1: Frontend PoC (EPIC 0)
**목표**: UI/UX 검증 및 사용자 플로우 확인

### Wave 1.1 (시작점)
- **#001** - EPIC0-FE-001: 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
  - 의존성: 없음
  - 병렬 가능: #005, #006 (독립적인 백엔드 작업과 병렬 가능)

### Wave 1.2 (Wave 1.1 완료 후)
**병렬 개발 가능한 작업들:**
- **#002** - EPIC0-FE-002: Wizard 입력 폼 및 자동저장 UI PoC
  - 의존성: #001
  - 병렬 가능: #004, #005
  
- **#004** - EPIC0-FE-004: 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
  - 의존성: #001 (네비게이션 연결)
  - 병렬 가능: #002, #005

- **#005** - EPIC0-FE-005: PMF 진단 설문 및 리포트 UI PoC
  - 의존성: 없음 (독립 페이지)
  - 병렬 가능: #001, #002, #004

### Wave 1.3 (Wave 1.2의 #002 완료 후)
- **#003** - EPIC0-FE-003: 사업계획서 초안 생성 및 뷰어 UI PoC
  - 의존성: #002
  - 병렬 가능: 없음 (이 단계는 UI 완결)

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization
**목표**: 최단 기간 내 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Frontend
    #001 FE-001           :f001, 2025-11-27, 3d
    #002 FE-002           :f002, after f001, 3d
    #004 FE-004           :f004, after f001, 4d
    #005 FE-005           :f005, 2025-11-27, 2d
    #003 FE-003           :f003, after f002, 4d
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 6명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #001
  - Developer 2: #005
  - Developer 3: #006
  - Developer 4: #008
  - Developer 5: #012

- **Week 1 (Day 4-7):**
  - Developer 1: #002, #004 (병렬)
  - Developer 2: #011 (준비)
  - Developer 3: #007 → #013, #014 (병렬)
  - Developer 4: #009 준비
  - Developer 5: #012 계속

- **Week 2:**
  - Developer 1: #003
  - Developer 2: #011
  - Developer 3: #013, #014 완료
  - Developer 4: #009 → #010
  - Developer 5: #015

### Strategy 2: Sequential with Limited Resources
**목표**: 2-3명의 소규모 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #001 (Frontend Base)
  Day 4-7: #006 (Backend Base)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #002 + #004 (병렬 가능 시)

Week 3:
  Day 1-3: #007 (Backend API)
  Day 4-7: #005 + #012 (병렬)

Week 4:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #003 (Frontend 완결)

Week 5:
  Day 1-3: #010 (Export) + #011 (PMF)
  Day 4-7: #013 + #014 (NFR)

Week 6:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Buffer / QA
```

### Strategy 3: Feature-First
**목표**: 기능별 완결도 우선 (데모 준비 시)

1. **Sprint 1 (Wizard Flow):** #001 → #002 → #006 → #007 → #003
2. **Sprint 2 (Document Generation):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #004 → #012, #005 → #011
4. **Sprint 4 (NFR):** #013 → #014 → #015

---

## Dependency Matrix

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #001  | -          | #002, #004 | #005, #006, #008, #012 |
| #002  | #001       | #003    | #004, #005 |
| #003  | #002       | -       | - |
| #004  | #001       | -       | #002, #005 |
| #005  | -          | -       | All |
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order

### For Backend-Focused Team
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)
7. **Phase 7**: Frontend (#001 → #002 → #003 → #004 → #005)

### For Full-Stack Team (Recommended)
1. **Sprint 1**: #001, #006, #008, #012 (병렬)
2. **Sprint 2**: #002, #004, #007, #013, #014 (병렬)
3. **Sprint 3**: #003, #009, #011 (병렬)
4. **Sprint 4**: #005, #010, #015 (병렬/순차)

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization
**목표**: 최단 기간 내 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Frontend
    #001 FE-001           :f001, 2025-11-27, 3d
    #002 FE-002           :f002, after f001, 3d
    #004 FE-004           :f004, after f001, 4d
    #005 FE-005           :f005, 2025-11-27, 2d
    #003 FE-003           :f003, after f002, 4d
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 6명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #001
  - Developer 2: #005
  - Developer 3: #006
  - Developer 4: #008
  - Developer 5: #012

- **Week 1 (Day 4-7):**
  - Developer 1: #002, #004 (병렬)
  - Developer 2: #011 (준비)
  - Developer 3: #007 → #013, #014 (병렬)
  - Developer 4: #009 준비
  - Developer 5: #012 계속

- **Week 2:**
  - Developer 1: #003
  - Developer 2: #011
  - Developer 3: #013, #014 완료
  - Developer 4: #009 → #010
  - Developer 5: #015

### Strategy 2: Sequential with Limited Resources
**목표**: 2-3명의 소규모 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #001 (Frontend Base)
  Day 4-7: #006 (Backend Base)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #002 + #004 (병렬 가능 시)

Week 3:
  Day 1-3: #007 (Backend API)
  Day 4-7: #005 + #012 (병렬)

Week 4:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #003 (Frontend 완결)

Week 5:
  Day 1-3: #010 (Export) + #011 (PMF)
  Day 4-7: #013 + #014 (NFR)

Week 6:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Buffer / QA
```

### Strategy 3: Feature-First
**목표**: 기능별 완결도 우선 (데모 준비 시)

1. **Sprint 1 (Wizard Flow):** #001 → #002 → #006 → #007 → #003
2. **Sprint 2 (Document Generation):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #004 → #012, #005 → #011
4. **Sprint 4 (NFR):** #013 → #014 → #015

---

## Dependency Matrix

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #001  | -          | #002, #004 | #005, #006, #008, #012 |
| #002  | #001       | #003    | #004, #005 |
| #003  | #002       | -       | - |
| #004  | #001       | -       | #002, #005 |
| #005  | -          | -       | All |
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order

### For Backend-Focused Team
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)
7. **Phase 7**: Frontend (#001 → #002 → #003 → #004 → #005)

### For Full-Stack Team (Recommended)
1. **Sprint 1**: #001, #006, #008, #012 (병렬)
2. **Sprint 2**: #002, #004, #007, #013, #014 (병렬)
3. **Sprint 3**: #003, #009, #011 (병렬)
4. **Sprint 4**: #005, #010, #015 (병렬/순차)

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization
**목표**: 최단 기간 내 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Frontend
    #001 FE-001           :f001, 2025-11-27, 3d
    #002 FE-002           :f002, after f001, 3d
    #004 FE-004           :f004, after f001, 4d
    #005 FE-005           :f005, 2025-11-27, 2d
    #003 FE-003           :f003, after f002, 4d
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 6명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #001
  - Developer 2: #005
  - Developer 3: #006
  - Developer 4: #008
  - Developer 5: #012

- **Week 1 (Day 4-7):**
  - Developer 1: #002, #004 (병렬)
  - Developer 2: #011 (준비)
  - Developer 3: #007 → #013, #014 (병렬)
  - Developer 4: #009 준비
  - Developer 5: #012 계속

- **Week 2:**
  - Developer 1: #003
  - Developer 2: #011
  - Developer 3: #013, #014 완료
  - Developer 4: #009 → #010
  - Developer 5: #015

### Strategy 2: Sequential with Limited Resources
**목표**: 2-3명의 소규모 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #001 (Frontend Base)
  Day 4-7: #006 (Backend Base)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #002 + #004 (병렬 가능 시)

Week 3:
  Day 1-3: #007 (Backend API)
  Day 4-7: #005 + #012 (병렬)

Week 4:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #003 (Frontend 완결)

Week 5:
  Day 1-3: #010 (Export) + #011 (PMF)
  Day 4-7: #013 + #014 (NFR)

Week 6:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Buffer / QA
```

### Strategy 3: Feature-First
**목표**: 기능별 완결도 우선 (데모 준비 시)

1. **Sprint 1 (Wizard Flow):** #001 → #002 → #006 → #007 → #003
2. **Sprint 2 (Document Generation):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #004 → #012, #005 → #011
4. **Sprint 4 (NFR):** #013 → #014 → #015

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order

### For Backend-Focused Team
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)
7. **Phase 7**: Frontend (#001 → #002 → #003 → #004 → #005)

### For Full-Stack Team (Recommended)
1. **Sprint 1**: #001, #006, #008, #012 (병렬)
2. **Sprint 2**: #002, #004, #007, #013, #014 (병렬)
3. **Sprint 3**: #003, #009, #011 (병렬)
4. **Sprint 4**: #005, #010, #015 (병렬/순차)

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization
**목표**: 최단 기간 내 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Frontend
    #001 FE-001           :f001, 2025-11-27, 3d
    #002 FE-002           :f002, after f001, 3d
    #004 FE-004           :f004, after f001, 4d
    #005 FE-005           :f005, 2025-11-27, 2d
    #003 FE-003           :f003, after f002, 4d
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 6명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #001
  - Developer 2: #005
  - Developer 3: #006
  - Developer 4: #008
  - Developer 5: #012

- **Week 1 (Day 4-7):**
  - Developer 1: #002, #004 (병렬)
  - Developer 2: #011 (준비)
  - Developer 3: #007 → #013, #014 (병렬)
  - Developer 4: #009 준비
  - Developer 5: #012 계속

- **Week 2:**
  - Developer 1: #003
  - Developer 2: #011
  - Developer 3: #013, #014 완료
  - Developer 4: #009 → #010
  - Developer 5: #015

### Strategy 2: Sequential with Limited Resources
**목표**: 2-3명의 소규모 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #001 (Frontend Base)
  Day 4-7: #006 (Backend Base)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #002 + #004 (병렬 가능 시)

Week 3:
  Day 1-3: #007 (Backend API)
  Day 4-7: #005 + #012 (병렬)

Week 4:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #003 (Frontend 완결)

Week 5:
  Day 1-3: #010 (Export) + #011 (PMF)
  Day 4-7: #013 + #014 (NFR)

Week 6:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Buffer / QA
```

### Strategy 3: Feature-First
**목표**: 기능별 완결도 우선 (데모 준비 시)

1. **Sprint 1 (Wizard Flow):** #001 → #002 → #006 → #007 → #003
2. **Sprint 2 (Document Generation):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #004 → #012, #005 → #011
4. **Sprint 4 (NFR):** #013 → #014 → #015

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources
**목표**: 2-3명의 소규모 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #001 (Frontend Base)
  Day 4-7: #006 (Backend Base)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #002 + #004 (병렬 가능 시)

Week 3:
  Day 1-3: #007 (Backend API)
  Day 4-7: #005 + #012 (병렬)

Week 4:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #003 (Frontend 완결)

Week 5:
  Day 1-3: #010 (Export) + #011 (PMF)
  Day 4-7: #013 + #014 (NFR)

Week 6:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Buffer / QA
```

### Strategy 3: Feature-First
**목표**: 기능별 완결도 우선 (데모 준비 시)

1. **Sprint 1 (Wizard Flow):** #001 → #002 → #006 → #007 → #003
2. **Sprint 2 (Document Generation):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #004 → #012, #005 → #011
4. **Sprint 4 (NFR):** #013 → #014 → #015

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First
**목표**: 기능별 완결도 우선 (데모 준비 시)

1. **Sprint 1 (Wizard Flow):** #001 → #002 → #006 → #007 → #003
2. **Sprint 2 (Document Generation):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #004 → #012, #005 → #011
4. **Sprint 4 (NFR):** #013 → #014 → #015

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위**:
  - Must: #001~#010, #013 (총 11개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #001, #006, #007, #008, #009 (Wizard + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── issue-001-EPIC0-FE-001.md        # Frontend PoC Issues
├── issue-002-EPIC0-FE-002.md
├── issue-003-EPIC0-FE-003.md
├── issue-004-EPIC0-FE-004.md
├── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # Backend Core Issues
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description |
|-------|-------|-------------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) |
| #011-#012 | Phase 3 | Special Features (EPIC 2) |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) |

## How to Use

### 1. Creating Issues in GitHub

각 이슈 파일을 GitHub에 등록하려면:

```bash
# GitHub CLI 사용
gh issue create -F tasks/github-issues/issue-001-EPIC0-FE-001.md

# 또는 일괄 생성 스크립트
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# 프로젝트 생성
gh project create --title "Bizplan MVP Development" --owner @me

# 이슈를 프로젝트에 추가
gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/1
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (6명 팀):**
- Wave 1: #001, #005, #006, #008, #012 (병렬)
- Wave 2: #002, #004, #007, #013, #014 (병렬)
- Wave 3: #003, #009, #011 (병렬)
- Wave 4: #010, #015 (순차)

**Small Team (2-3명):**
- Sprint 1: #001 → #002 → #003
- Sprint 2: #006 → #007 → #009
- Sprint 3: #008 → #010
- Sprint 4: #004, #005, #012 (병렬)
- Sprint 5: #011, #013, #014, #015

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation

```bash
#!/bin/bash
# scripts/create_all_issues.sh

for i in {001..015}; do
  issue_file="tasks/github-issues/issue-$i-*.md"
  if [ -f $issue_file ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description |
|-------|-------|-------------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) |
| #011-#012 | Phase 3 | Special Features (EPIC 2) |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) |

## How to Use

### 1. Creating Issues in GitHub

각 이슈 파일을 GitHub에 등록하려면:

```bash
# GitHub CLI 사용
gh issue create -F tasks/github-issues/issue-001-EPIC0-FE-001.md

# 또는 일괄 생성 스크립트
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# 프로젝트 생성
gh project create --title "Bizplan MVP Development" --owner @me

# 이슈를 프로젝트에 추가
gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/1
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (6명 팀):**
- Wave 1: #001, #005, #006, #008, #012 (병렬)
- Wave 2: #002, #004, #007, #013, #014 (병렬)
- Wave 3: #003, #009, #011 (병렬)
- Wave 4: #010, #015 (순차)

**Small Team (2-3명):**
- Sprint 1: #001 → #002 → #003
- Sprint 2: #006 → #007 → #009
- Sprint 3: #008 → #010
- Sprint 4: #004, #005, #012 (병렬)
- Sprint 5: #011, #013, #014, #015

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation

```bash
#!/bin/bash
# scripts/create_all_issues.sh

for i in {001..015}; do
  issue_file="tasks/github-issues/issue-$i-*.md"
  if [ -f $issue_file ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

각 이슈 파일을 GitHub에 등록하려면:

```bash
# GitHub CLI 사용
gh issue create -F tasks/github-issues/issue-001-EPIC0-FE-001.md

# 또는 일괄 생성 스크립트
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# 프로젝트 생성
gh project create --title "Bizplan MVP Development" --owner @me

# 이슈를 프로젝트에 추가
gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/1
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (6명 팀):**
- Wave 1: #001, #005, #006, #008, #012 (병렬)
- Wave 2: #002, #004, #007, #013, #014 (병렬)
- Wave 3: #003, #009, #011 (병렬)
- Wave 4: #010, #015 (순차)

**Small Team (2-3명):**
- Sprint 1: #001 → #002 → #003
- Sprint 2: #006 → #007 → #009
- Sprint 3: #008 → #010
- Sprint 4: #004, #005, #012 (병렬)
- Sprint 5: #011, #013, #014, #015

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation

```bash
#!/bin/bash
# scripts/create_all_issues.sh

for i in {001..015}; do
  issue_file="tasks/github-issues/issue-$i-*.md"
  if [ -f $issue_file ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**Backend 이슈만 등록** (Frontend는 이미 완료됨):

```bash
# GitHub CLI를 사용한 개별 등록
gh issue create -F tasks/github-issues/issue-006-REQ-FUNC-001-BE-001.md

# Backend 이슈만 일괄 생성 (completed 폴더 제외)
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done

# 또는 번호 범위 지정
for i in {006..015}; do
  file=$(ls tasks/github-issues/issue-0$i-*.md 2>/dev/null)
  if [ -f "$file" ]; then
    gh issue create -F "$file"
  fi
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# 프로젝트 생성
gh project create --title "Bizplan MVP Development" --owner @me

# 이슈를 프로젝트에 추가
gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/1
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (6명 팀):**
- Wave 1: #001, #005, #006, #008, #012 (병렬)
- Wave 2: #002, #004, #007, #013, #014 (병렬)
- Wave 3: #003, #009, #011 (병렬)
- Wave 4: #010, #015 (순차)

**Small Team (2-3명):**
- Sprint 1: #001 → #002 → #003
- Sprint 2: #006 → #007 → #009
- Sprint 3: #008 → #010
- Sprint 4: #004, #005, #012 (병렬)
- Sprint 5: #011, #013, #014, #015

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation

```bash
#!/bin/bash
# scripts/create_all_issues.sh

for i in {001..015}; do
  issue_file="tasks/github-issues/issue-$i-*.md"
  if [ -f $issue_file ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**Backend 이슈만 등록** (Frontend는 이미 완료됨):

```bash
# GitHub CLI를 사용한 개별 등록
gh issue create -F tasks/github-issues/issue-006-REQ-FUNC-001-BE-001.md

# Backend 이슈만 일괄 생성 (completed 폴더 제외)
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done

# 또는 번호 범위 지정
for i in {006..015}; do
  file=$(ls tasks/github-issues/issue-0$i-*.md 2>/dev/null)
  if [ -f "$file" ]; then
    gh issue create -F "$file"
  fi
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (6명 팀):**
- Wave 1: #001, #005, #006, #008, #012 (병렬)
- Wave 2: #002, #004, #007, #013, #014 (병렬)
- Wave 3: #003, #009, #011 (병렬)
- Wave 4: #010, #015 (순차)

**Small Team (2-3명):**
- Sprint 1: #001 → #002 → #003
- Sprint 2: #006 → #007 → #009
- Sprint 3: #008 → #010
- Sprint 4: #004, #005, #012 (병렬)
- Sprint 5: #011, #013, #014, #015

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation

```bash
#!/bin/bash
# scripts/create_all_issues.sh

for i in {001..015}; do
  issue_file="tasks/github-issues/issue-$i-*.md"
  if [ -f $issue_file ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**Backend 이슈만 등록** (Frontend는 이미 완료됨):

```bash
# GitHub CLI를 사용한 개별 등록
gh issue create -F tasks/github-issues/issue-006-REQ-FUNC-001-BE-001.md

# Backend 이슈만 일괄 생성 (completed 폴더 제외)
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done

# 또는 번호 범위 지정
for i in {006..015}; do
  file=$(ls tasks/github-issues/issue-0$i-*.md 2>/dev/null)
  if [ -f "$file" ]; then
    gh issue create -F "$file"
  fi
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation

```bash
#!/bin/bash
# scripts/create_all_issues.sh

for i in {001..015}; do
  issue_file="tasks/github-issues/issue-$i-*.md"
  if [ -f $issue_file ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**Backend 이슈만 등록** (Frontend는 이미 완료됨):

```bash
# GitHub CLI를 사용한 개별 등록
gh issue create -F tasks/github-issues/issue-006-REQ-FUNC-001-BE-001.md

# Backend 이슈만 일괄 생성 (completed 폴더 제외)
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done

# 또는 번호 범위 지정
for i in {006..015}; do
  file=$(ls tasks/github-issues/issue-0$i-*.md 2>/dev/null)
  if [ -f "$file" ]; then
    gh issue create -F "$file"
  fi
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation (Backend Only)

```bash
#!/bin/bash
# scripts/create_backend_issues.sh

# Backend 이슈만 생성 (#006-#015)
for i in {006..015}; do
  issue_file=$(ls tasks/github-issues/issue-$i-*.md 2>/dev/null)
  if [ -f "$issue_file" ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done

echo "✅ Backend issues (#006-#015) created successfully"
echo "ℹ️  Frontend issues (#001-#005) skipped (already completed)"
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
2. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
3. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
4. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
5. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**Backend 이슈만 등록** (Frontend는 이미 완료됨):

```bash
# GitHub CLI를 사용한 개별 등록
gh issue create -F tasks/github-issues/issue-006-REQ-FUNC-001-BE-001.md

# Backend 이슈만 일괄 생성 (completed 폴더 제외)
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done

# 또는 번호 범위 지정
for i in {006..015}; do
  file=$(ls tasks/github-issues/issue-0$i-*.md 2>/dev/null)
  if [ -f "$file" ]; then
    gh issue create -F "$file"
  fi
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation (Backend Only)

```bash
#!/bin/bash
# scripts/create_backend_issues.sh

# Backend 이슈만 생성 (#006-#015)
for i in {006..015}; do
  issue_file=$(ls tasks/github-issues/issue-$i-*.md 2>/dev/null)
  if [ -f "$issue_file" ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done

echo "✅ Backend issues (#006-#015) created successfully"
echo "ℹ️  Frontend issues (#001-#005) skipped (already completed)"
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **Frontend 통합**: Frontend가 완료되었으므로 API Contract를 준수하며 개발
2. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
3. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
4. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
5. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
6. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록
7. **통합 테스트**: 각 API 완료 시 Frontend와 즉시 통합 테스트 진행

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.0


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**Backend 이슈만 등록** (Frontend는 이미 완료됨):

```bash
# GitHub CLI를 사용한 개별 등록
gh issue create -F tasks/github-issues/issue-006-REQ-FUNC-001-BE-001.md

# Backend 이슈만 일괄 생성 (completed 폴더 제외)
for file in tasks/github-issues/issue-*.md; do
  gh issue create -F "$file"
done

# 또는 번호 범위 지정
for i in {006..015}; do
  file=$(ls tasks/github-issues/issue-0$i-*.md 2>/dev/null)
  if [ -f "$file" ]; then
    gh issue create -F "$file"
  fi
done
```

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation (Backend Only)

```bash
#!/bin/bash
# scripts/create_backend_issues.sh

# Backend 이슈만 생성 (#006-#015)
for i in {006..015}; do
  issue_file=$(ls tasks/github-issues/issue-$i-*.md 2>/dev/null)
  if [ -f "$issue_file" ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done

echo "✅ Backend issues (#006-#015) created successfully"
echo "ℹ️  Frontend issues (#001-#005) skipped (already completed)"
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **Frontend 통합**: Frontend가 완료되었으므로 API Contract를 준수하며 개발
2. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
3. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
4. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
5. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
6. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록
7. **통합 테스트**: 각 API 완료 시 Frontend와 즉시 통합 테스트 진행

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.1 (Frontend EPIC0 완료 반영)


```

마지막으로 Backend 이슈 일괄 생성을 위한 스크립트를 만들어드리겠습니다.

```shellscript
#!/bin/bash

# Bizplan Backend Issues 일괄 생성 스크립트
# Frontend PoC (#001-#005)는 이미 완료되어 제외됨
# Backend 이슈만 생성 (#006-#015)

set -e

echo "================================================"
echo "Bizplan Backend Issues 생성 시작"
echo "================================================"
echo ""
echo "✅ Frontend PoC (#001-#005): 이미 완료됨 - 건너뜀"
echo "🔄 Backend Issues (#006-#015): 생성 중..."
echo ""

# GitHub CLI 설치 확인
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI (gh) not installed"
    echo "Install: https://cli.github.com/"
    exit 1
fi

# GitHub 로그인 확인
if ! gh auth status &> /dev/null; then
    echo "❌ Error: Not logged in to GitHub"
    echo "Run: gh auth login"
    exit 1
fi

# 스크립트 실행 위치 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Backend 이슈 생성
CREATED=0
FAILED=0

for i in {006..015}; do
    issue_file=$(ls issue-$i-*.md 2>/dev/null | head -1)
    
    if [ -f "$issue_file" ]; then
        issue_name=$(basename "$issue_file" .md)
        echo "[$((i-5))/10] Creating: $issue_name"
        
        if gh issue create -F "$issue_file" > /dev/null 2>&1; then
            echo "    ✅ Success"
            ((CREATED++))
        else
            echo "    ❌ Failed"
            ((FAILED++))
        fi
        
        # API rate limiting 방지
        sleep 2
    else
        echo "[$((i-5))/10] ⚠️  File not found: issue-$i-*.md"
        ((FAILED++))
    fi
done

echo ""
echo "================================================"
echo "Backend Issues 생성 완료"
echo "================================================"
echo "✅ 생성 성공: $CREATED 개"
if [ $FAILED -gt 0 ]; then
    echo "❌ 생성 실패: $FAILED 개"
fi
echo ""
echo "다음 단계:"
echo "1. GitHub에서 생성된 이슈 확인"
echo "2. 라벨 추가: gh issue edit <NUM> --add-label 'epic:...,type:...'"
echo "3. 마일스톤 설정: gh issue edit <NUM> --milestone 'Phase 2'"
echo "4. 프로젝트 보드 추가: gh project item-add <PROJECT_ID> --url <ISSUE_URL>"
echo ""
echo "자세한 내용: README.md 참조"
echo ""

```

```markdown
# GitHub Issues 변경 이력

## v1.1 (2025-11-26)

### ✅ 주요 변경사항: Frontend PoC 완료 반영

**변경 이유**: EPIC0 (Frontend PoC) 작업들이 별도 프로젝트에서 완료됨

### 파일 구조 변경

#### 이동된 파일 (5개)
완료된 Frontend 이슈들을 `completed/` 폴더로 이동:
- `issue-001-EPIC0-FE-001.md` → `completed/issue-001-EPIC0-FE-001.md`
- `issue-002-EPIC0-FE-002.md` → `completed/issue-002-EPIC0-FE-002.md`
- `issue-003-EPIC0-FE-003.md` → `completed/issue-003-EPIC0-FE-003.md`
- `issue-004-EPIC0-FE-004.md` → `completed/issue-004-EPIC0-FE-004.md`
- `issue-005-EPIC0-FE-005.md` → `completed/issue-005-EPIC0-FE-005.md`

#### 추가된 파일 (2개)
- `completed/README.md` - 완료된 Frontend 이슈 설명
- `create_backend_issues.sh` - Backend 이슈 일괄 생성 스크립트

### 문서 업데이트

#### ISSUE_EXECUTION_PLAN.md
- [x] Status Update 섹션 추가 (Frontend 완료 명시)
- [x] Phase 1 (Frontend PoC) 완료 상태로 표시
- [x] Gantt 차트에서 Frontend 완료 표시
- [x] 인력 배치 계획 Backend 중심으로 재구성 (6명 → 4-5명)
- [x] Sequential Strategy Backend 중심으로 재작성
- [x] Feature-First Strategy Backend 중심으로 재작성
- [x] Dependency Matrix에서 Frontend 이슈 제거
- [x] MVP 최소 범위 업데이트 (11개 → 6개)
- [x] 우선순위 재정의 (Backend 기준)

#### README.md
- [x] Status Update 섹션 추가
- [x] 폴더 구조 업데이트 (completed 폴더 반영)
- [x] Issue Numbering Convention에 완료 상태 추가
- [x] Creating Issues in GitHub 섹션 Backend 전용으로 수정
- [x] Creating Project Board Backend 전용으로 수정
- [x] Quick Reference Backend 전용으로 재작성
- [x] Bulk Issue Creation 스크립트 Backend 전용으로 수정
- [x] Best Practices에 Frontend 통합 관련 항목 추가
- [x] Version 1.1로 업데이트

### 영향 받은 이슈들

#### 변경 불필요한 이슈 (#006-#015)
Backend 이슈들은 원래부터 Frontend와 독립적이므로 내용 변경 불필요:
- #006-#010: Core Backend & AI (EPIC 1)
- #011-#012: Special Features (EPIC 2)
- #013-#015: Non-Functional Requirements (EPIC 3)

**이유**: 
- Backend API는 Frontend 완료 여부와 무관하게 API Contract 기반으로 개발
- 의존성 그래프에서 Frontend → Backend 의존성 없음
- 통합 테스트 시점만 앞당겨질 뿐, 개발 범위/내용 동일

### 실행 계획 변경 사항

#### Before (v1.0)
```
Phase 1: Frontend PoC (#001-#005) → 약 2주
Phase 2: Backend Core (#006-#010) → 약 3주
Phase 3: Special Features (#011-#012) → 약 2주
Phase 4: NFR (#013-#015) → 약 1주
Total: 약 8주
```

#### After (v1.1)
```
✅ Phase 1: Frontend PoC - 완료
Phase 2: Backend Core (#006-#010) → 약 3주
Phase 3: Special Features (#011-#012) → 약 2주  
Phase 4: NFR (#013-#015) → 약 1주
Total: 약 6주 (Frontend 통합 테스트 포함)
```

### GitHub Issues 생성 방법

#### v1.0 (전체 생성)
```bash
for i in {001..015}; do
  gh issue create -F "issue-$i-*.md"
done
```

#### v1.1 (Backend만 생성)
```bash
# 방법 1: 스크립트 사용 (권장)
./create_backend_issues.sh

# 방법 2: 수동 생성
for i in {006..015}; do
  gh issue create -F "issue-0$i-*.md"
done
```

### 마이그레이션 가이드

기존에 v1.0 기반으로 작업 중이었다면:

1. **Frontend 이슈 처리**:
   ```bash
   # GitHub에 이미 생성된 #001-#005 이슈가 있다면
   for i in {1..5}; do
     gh issue close $i --comment "✅ 별도 프로젝트에서 완료됨"
   done
   ```

2. **Backend 이슈 재확인**:
   - #006-#015 이슈들의 내용은 변경 없음
   - Dependencies 섹션 확인 (Frontend 의존성 없음)

3. **프로젝트 보드 업데이트**:
   - Frontend 이슈들을 "Completed" 컬럼으로 이동
   - 또는 프로젝트에서 제거

### 테스트 체크리스트

- [x] Frontend 이슈 파일 5개가 `completed/` 폴더로 이동됨
- [x] Backend 이슈 파일 10개가 루트에 남아있음
- [x] `completed/README.md` 생성됨
- [x] `create_backend_issues.sh` 생성 및 실행 권한 설정됨
- [x] `ISSUE_EXECUTION_PLAN.md` 업데이트됨
- [x] `README.md` 업데이트됨
- [x] Dependency Matrix에 Frontend 이슈 제거됨
- [x] 모든 문서에 v1.1 버전 명시됨

---

## v1.0 (2025-11-26)

### 초기 버전
- 15개 이슈 생성 (Frontend 5개, Backend 10개)
- INTEGRATED_WBS_DAG.md 기반 의존성 그래프 작성
- 3가지 실행 전략 제시
- Gantt 차트 및 Dependency Matrix 작성

---

## 참고 링크
- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md)
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md)
- [README.md](./README.md)

```

완료했습니다! Frontend PoC (EPIC0) 완료를 반영하여 모든 문서를 업데이트했습니다.

## 📋 변경 사항 요약

### ✅ 파일 이동
Frontend 이슈 5개를 `completed/` 폴더로 이동:
- `issue-001` ~ `issue-005` (EPIC0-FE-001 ~ EPIC0-FE-005)

### 📝 새로 추가된 파일
1. **`completed/README.md`** - 완료된 Frontend 이슈 설명
2. **`create_backend_issues.sh`** - Backend 이슈 일괄 생성 스크립트
3. **`CHANGELOG.md`** - 변경 이력 문서

### 🔄 업데이트된 문서

#### `ISSUE_EXECUTION_PLAN.md`
- ✅ Frontend 완료 상태 표시
- Phase 1 (Frontend) 완료 섹션 추가
- Backend 중심 실행 전략으로 재구성:
  - 최대 병렬화: 6명 → 4-5명 Backend 팀
  - 소규모 팀: Frontend 단계 제거
  - Gantt 차트에 Frontend 완료 표시
- Dependency Matrix에서 Frontend 이슈 제거
- MVP 최소 범위: 11개 → 6개 (Backend만)

#### `README.md`
- Frontend 완료 상태 배너 추가
- 폴더 구조 업데이트 (completed 폴더 반영)
- Backend 이슈만 생성하는 가이드로 변경
- Quick Reference Backend 전용으로 재작성
- Best Practices에 "Frontend 통합" 항목 추가

## 📂 현재 구조

```
github-issues/
├── README.md                          # Backend 중심으로 업데이트됨
├── ISSUE_EXECUTION_PLAN.md           # Backend 중심으로 업데이트됨
├── CHANGELOG.md                       # ✨ NEW: 변경 이력
├── create_backend_issues.sh           # ✨ NEW: Backend 이슈 생성 스크립트
├── completed/                         # ✨ NEW: 완료된 이슈 보관
│   ├── README.md
│   ├── issue-001-EPIC0-FE-001.md    # ✅ COMPLETED
│   ├── issue-002-EPIC0-FE-002.md    # ✅ COMPLETED
│   ├── issue-003-EPIC0-FE-003.md    # ✅ COMPLETED
│   ├── issue-004-EPIC0-FE-004.md    # ✅ COMPLETED
│   └── issue-005-EPIC0-FE-005.md    # ✅ COMPLETED
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 IN SCOPE
├── issue-007-REQ-FUNC-002-BE-001.md # 🔄 IN SCOPE
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 IN SCOPE
├── issue-009-REQ-FUNC-003-BE-001.md # 🔄 IN SCOPE
├── issue-010-REQ-FUNC-011-BE-001.md # 🔄 IN SCOPE
├── issue-011-REQ-FUNC-008-AI-001.md # 🔄 IN SCOPE
├── issue-012-REQ-FUNC-012-BE-001.md # 🔄 IN SCOPE
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 IN SCOPE
├── issue-014-REQ-NF-012-OPS-001.md  # 🔄 IN SCOPE
└── issue-015-REQ-NF-001-PERF-001.md # 🔄 IN SCOPE
```

## 🚀 GitHub에 이슈 등록하기

### 방법 1: 스크립트 사용 (권장)
```bash
cd tasks/github-issues
./create_backend_issues.sh
```

### 방법 2: 수동 생성
```bash
cd tasks/github-issues

# Backend 이슈만 생성 (#006-#015)
for i in {006..015}; do
  issue_file=$(ls issue-$i-*.md 2>/dev/null | head -1)
  [ -f "$issue_file" ] && gh issue create -F "$issue_file"
  sleep 2
done
```

## 📊 업데이트된 실행 계획

### Critical Path (Backend)
```
#006 → #007 → #009 → #010 → #015
(약 18일, 병렬화 시 약 20일)
```

### 권장 실행 순서 (Backend 팀)
```
Sprint 1 (Week 1):
  #006 (BE Core) + #008 (AI) + #012 (Financial) 병렬

Sprint 2 (Week 2):
  #007 (Wizard API) + #013, #014 (NFR) 병렬

Sprint 3 (Week 3):
  #009 (Orchestration) → #010 (Export)

Sprint 4 (Week 4):
  #011 (PMF) + #015 (Performance) + Frontend 통합 테스트
```

## ✨ 주요 개선 사항

1. **명확한 범위**: Frontend 완료로 Backend 작업만 집중
2. **빠른 통합**: API 완성 즉시 Frontend와 통합 테스트 가능
3. **API Contract 확정**: Frontend 검증 완료로 API 스펙 변경 위험 감소
4. **리소스 최적화**: Backend 개발자 4-5명으로 효율적 작업 분배 가능

Frontend가 완료되었으므로 Backend API 개발에만 집중하시면 됩니다! 🎯

```plaintext
github-issues/
├── README.md                          # Backend 중심으로 업데이트됨
├── ISSUE_EXECUTION_PLAN.md           # Backend 중심으로 업데이트됨
├── CHANGELOG.md                       # ✨ NEW: 변경 이력
├── create_backend_issues.sh           # ✨ NEW: Backend 이슈 생성 스크립트
├── completed/                         # ✨ NEW: 완료된 이슈 보관
│   ├── README.md
│   ├── issue-001-EPIC0-FE-001.md    # ✅ COMPLETED
│   ├── issue-002-EPIC0-FE-002.md    # ✅ COMPLETED
│   ├── issue-003-EPIC0-FE-003.md    # ✅ COMPLETED
│   ├── issue-004-EPIC0-FE-004.md    # ✅ COMPLETED
│   └── issue-005-EPIC0-FE-005.md    # ✅ COMPLETED
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 IN SCOPE
├── issue-007-REQ-FUNC-002-BE-001.md # 🔄 IN SCOPE
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 IN SCOPE
├── issue-009-REQ-FUNC-003-BE-001.md # 🔄 IN SCOPE
├── issue-010-REQ-FUNC-011-BE-001.md # 🔄 IN SCOPE
├── issue-011-REQ-FUNC-008-AI-001.md # 🔄 IN SCOPE
├── issue-012-REQ-FUNC-012-BE-001.md # 🔄 IN SCOPE
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 IN SCOPE
├── issue-014-REQ-NF-012-OPS-001.md  # 🔄 IN SCOPE
└── issue-015-REQ-NF-001-PERF-001.md # 🔄 IN SCOPE
```

```shellscript
cd tasks/github-issues
./create_backend_issues.sh
```

```shellscript
cd tasks/github-issues

# Backend 이슈만 생성 (#006-#015)
for i in {006..015}; do
  issue_file=$(ls issue-$i-*.md 2>/dev/null | head -1)
  [ -f "$issue_file" ] && gh issue create -F "$issue_file"
  sleep 2
done
```

```plaintext
#006 → #007 → #009 → #010 → #015
(약 18일, 병렬화 시 약 20일)
```

```plaintext
Sprint 1 (Week 1):
  #006 (BE Core) + #008 (AI) + #012 (Financial) 병렬

Sprint 2 (Week 2):
  #007 (Wizard API) + #013, #014 (NFR) 병렬

Sprint 3 (Week 3):
  #009 (Orchestration) → #010 (Export)

Sprint 4 (Week 4):
  #011 (PMF) + #015 (Performance) + Frontend 통합 테스트
```

---

**User**

이제 현재 원격 저장소에 지금까지의 내용을 커밋하고 푸시해줘.

깃허브 작업 관련 룰을 지켜서 수행해줘.

---

**Cursor**

Git 작업 관련 룰을 확인하고 적용하여 커밋과 푸시를 진행하겠습니다.

Git 작업 룰을 확인했습니다. 규칙에 따라 진행하겠습니다:

1. **브랜치 전략**: 작업용 브랜치 생성 필요
2. **커밋 메시지**: Conventional Commits 형식 적용
3. **안전 검증**: 변경사항 확인 후 커밋

먼저 현재 상태를 확인하겠습니다.

현재 main 브랜치에 있습니다. 룰에 따라 작업용 브랜치를 생성하고 커밋하겠습니다.

커밋이 완료되었습니다! 이제 원격 저장소 연결을 확인하고 push하겠습니다.

Push가 완료되었습니다! 이제 룰에 따라 Draft PR을 생성하겠습니다.

완료했습니다! ✨

## 📝 Git 작업 완료 요약

### ✅ 수행된 작업

1. **브랜치 생성**
   - `docs/github-issues-backend-scope` 브랜치 생성
   - 룰에 따라 main 브랜치에 직접 커밋하지 않음

2. **커밋**
   - Conventional Commits 형식 준수
   - Type: `docs` (문서 작업)
   - Scope: `github-issues`
   - 21개 파일, 2,300줄 추가

3. **Push**
   - 원격 저장소 연결 확인 완료
   - 브랜치 push 완료: `origin/docs/github-issues-backend-scope`

4. **Draft PR 생성**
   - PR #1 생성: https://github.com/wild-mental/makersround-backend/pull/1
   - 상세한 변경사항 설명 포함
   - 다음 단계 안내 포함

### 📊 커밋 내용

```
docs(github-issues): create GitHub issues and update for backend scope

- Create 15 GitHub issue templates based on INTEGRATED_WBS_DAG.md
- Move completed Frontend PoC issues (#001-#005) to completed/ folder
- Update execution plan to focus on Backend development (#006-#015)
- Add backend issue creation script (create_backend_issues.sh)
- Add comprehensive documentation (README, CHANGELOG, ISSUE_EXECUTION_PLAN)
```

### 📁 추가된 파일들

**문서 (4개)**:
- `tasks/github-issues/README.md`
- `tasks/github-issues/ISSUE_EXECUTION_PLAN.md`
- `tasks/github-issues/CHANGELOG.md`
- `tasks/github-issues/completed/README.md`

**Frontend 이슈 (5개)** - 완료됨:
- `completed/issue-001` ~ `issue-005`

**Backend 이슈 (10개)** - 작업 대상:
- `issue-006` ~ `issue-015`

**스크립트 (1개)**:
- `create_backend_issues.sh`

**프로젝트 README**:
- `README.md` (루트)

### 🎯 다음 단계

1. **PR 리뷰 & 승인**
   ```bash
   # PR 확인
   gh pr view 1
   ```

2. **Merge**
   ```bash
   # PR 승인 후 merge
   gh pr merge 1
   ```

3. **GitHub Issues 생성**
   ```bash
   # main 브랜치로 이동
   git checkout main
   git pull
   
   # Backend 이슈 일괄 생성
   cd tasks/github-issues
   ./create_backend_issues.sh
   ```

4. **개발 시작**
   - Critical Path: #006 → #007 → #009 → #010 → #015
   - 병렬 가능: #006 + #008 + #012

모든 Git 작업이 룰에 따라 안전하게 완료되었습니다! 🎉

```plaintext
docs(github-issues): create GitHub issues and update for backend scope

- Create 15 GitHub issue templates based on INTEGRATED_WBS_DAG.md
- Move completed Frontend PoC issues (#001-#005) to completed/ folder
- Update execution plan to focus on Backend development (#006-#015)
- Add backend issue creation script (create_backend_issues.sh)
- Add comprehensive documentation (README, CHANGELOG, ISSUE_EXECUTION_PLAN)
```

```shellscript
   # PR 확인
   gh pr view 1
```

```shellscript
   # PR 승인 후 merge
   gh pr merge 1
```

```shellscript
   # main 브랜치로 이동
   git checkout main
   git pull
   
   # Backend 이슈 일괄 생성
   cd tasks/github-issues
   ./create_backend_issues.sh
```

---

**User**

이제 메인브랜치로 돌아가서 원격 저장소에 이슈 목록에 따른 실제 깃허브 이슈를 발행해줘. 관련 규칙을 참조해줘.

---

**Cursor**

GitHub Issue 발행 규칙을 확인하고 진행하겠습니다.

규칙을 확인했습니다. 이제 순서대로 진행하겠습니다.

PR이 이미 merge되었습니다! 최신 변경사항을 pull하고 이슈를 생성하겠습니다.

이제 GitHub Issues를 생성하겠습니다. 규칙에 따라 labels를 포함해서 생성하도록 스크립트를 개선하겠습니다.

```shellscript
#!/bin/bash

# Bizplan Backend Issues 일괄 생성 스크립트
# Frontend PoC (#001-#005)는 이미 완료되어 제외됨
# Backend 이슈만 생성 (#006-#015)

set -e

echo "================================================"
echo "Bizplan Backend Issues 생성 시작"
echo "================================================"
echo ""
echo "✅ Frontend PoC (#001-#005): 이미 완료됨 - 건너뜀"
echo "🔄 Backend Issues (#006-#015): 생성 중..."
echo ""

# GitHub CLI 설치 확인
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI (gh) not installed"
    echo "Install: https://cli.github.com/"
    exit 1
fi

# GitHub 로그인 확인
if ! gh auth status &> /dev/null; then
    echo "❌ Error: Not logged in to GitHub"
    echo "Run: gh auth login"
    exit 1
fi

# 스크립트 실행 위치 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Backend 이슈 생성
CREATED=0
FAILED=0

for i in {006..015}; do
    issue_file=$(ls issue-$i-*.md 2>/dev/null | head -1)
    
    if [ -f "$issue_file" ]; then
        issue_name=$(basename "$issue_file" .md)
        echo "[$((i-5))/10] Creating: $issue_name"
        
        # Extract labels from issue file
        labels=$(grep "^## Labels" -A 1 "$issue_file" | tail -1 | sed 's/`//g' | tr ',' '\n' | xargs | tr ' ' ',')
        
        # Create issue with labels
        if [ -n "$labels" ]; then
            if gh issue create -F "$issue_file" --label "$labels" > /dev/null 2>&1; then
                echo "    ✅ Success (with labels: $labels)"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        else
            # Fallback: create without labels
            if gh issue create -F "$issue_file" > /dev/null 2>&1; then
                echo "    ✅ Success (no labels found)"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        fi
        
        # API rate limiting 방지
        sleep 2
    else
        echo "[$((i-5))/10] ⚠️  File not found: issue-$i-*.md"
        ((FAILED++))
    fi
done

echo ""
echo "================================================"
echo "Backend Issues 생성 완료"
echo "================================================"
echo "✅ 생성 성공: $CREATED 개"
if [ $FAILED -gt 0 ]; then
    echo "❌ 생성 실패: $FAILED 개"
fi
echo ""
echo "다음 단계:"
echo "1. GitHub에서 생성된 이슈 확인"
echo "2. 라벨 추가: gh issue edit <NUM> --add-label 'epic:...,type:...'"
echo "3. 마일스톤 설정: gh issue edit <NUM> --milestone 'Phase 2'"
echo "4. 프로젝트 보드 추가: gh project item-add <PROJECT_ID> --url <ISSUE_URL>"
echo ""
echo "자세한 내용: README.md 참조"
echo ""


```

스크립트를 개선했습니다. 이제 Backend Issues를 생성하겠습니다.

파일명 패턴에 문제가 있습니다. 수정하겠습니다.

```shellscript
#!/bin/bash

# Bizplan Backend Issues 일괄 생성 스크립트
# Frontend PoC (#001-#005)는 이미 완료되어 제외됨
# Backend 이슈만 생성 (#006-#015)

set -e

echo "================================================"
echo "Bizplan Backend Issues 생성 시작"
echo "================================================"
echo ""
echo "✅ Frontend PoC (#001-#005): 이미 완료됨 - 건너뜀"
echo "🔄 Backend Issues (#006-#015): 생성 중..."
echo ""

# GitHub CLI 설치 확인
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI (gh) not installed"
    echo "Install: https://cli.github.com/"
    exit 1
fi

# GitHub 로그인 확인
if ! gh auth status &> /dev/null; then
    echo "❌ Error: Not logged in to GitHub"
    echo "Run: gh auth login"
    exit 1
fi

# 스크립트 실행 위치 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Backend 이슈 생성
CREATED=0
FAILED=0

for i in {6..15}; do
    # Format as 3-digit number (006, 007, etc.)
    issue_num=$(printf "%03d" $i)
    issue_file=$(ls issue-$issue_num-*.md 2>/dev/null | head -1)
    
    if [ -f "$issue_file" ]; then
        issue_name=$(basename "$issue_file" .md)
        echo "[$((i-5))/10] Creating: $issue_name"
        
        # Extract labels from issue file
        labels=$(grep "^## Labels" -A 1 "$issue_file" | tail -1 | sed 's/`//g' | tr ',' '\n' | xargs | tr ' ' ',')
        
        # Create issue with labels
        if [ -n "$labels" ]; then
            if gh issue create -F "$issue_file" --label "$labels" > /dev/null 2>&1; then
                echo "    ✅ Success (with labels: $labels)"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        else
            # Fallback: create without labels
            if gh issue create -F "$issue_file" > /dev/null 2>&1; then
                echo "    ✅ Success (no labels found)"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        fi
        
        # API rate limiting 방지
        sleep 2
    else
        echo "[$((i-5))/10] ⚠️  File not found: issue-$i-*.md"
        ((FAILED++))
    fi
done

echo ""
echo "================================================"
echo "Backend Issues 생성 완료"
echo "================================================"
echo "✅ 생성 성공: $CREATED 개"
if [ $FAILED -gt 0 ]; then
    echo "❌ 생성 실패: $FAILED 개"
fi
echo ""
echo "다음 단계:"
echo "1. GitHub에서 생성된 이슈 확인"
echo "2. 라벨 추가: gh issue edit <NUM> --add-label 'epic:...,type:...'"
echo "3. 마일스톤 설정: gh issue edit <NUM> --milestone 'Phase 2'"
echo "4. 프로젝트 보드 추가: gh project item-add <PROJECT_ID> --url <ISSUE_URL>"
echo ""
echo "자세한 내용: README.md 참조"
echo ""


```

```shellscript
#!/bin/bash

# Bizplan Backend Issues 일괄 생성 스크립트
# Frontend PoC (#001-#005)는 이미 완료되어 제외됨
# Backend 이슈만 생성 (#006-#015)

set -e

echo "================================================"
echo "Bizplan Backend Issues 생성 시작"
echo "================================================"
echo ""
echo "✅ Frontend PoC (#001-#005): 이미 완료됨 - 건너뜀"
echo "🔄 Backend Issues (#006-#015): 생성 중..."
echo ""

# GitHub CLI 설치 확인
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI (gh) not installed"
    echo "Install: https://cli.github.com/"
    exit 1
fi

# GitHub 로그인 확인
if ! gh auth status &> /dev/null; then
    echo "❌ Error: Not logged in to GitHub"
    echo "Run: gh auth login"
    exit 1
fi

# 스크립트 실행 위치 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Backend 이슈 생성
CREATED=0
FAILED=0

for i in {6..15}; do
    # Format as 3-digit number (006, 007, etc.)
    issue_num=$(printf "%03d" $i)
    issue_file=$(ls issue-$issue_num-*.md 2>/dev/null | head -1)
    
    if [ -f "$issue_file" ]; then
        issue_name=$(basename "$issue_file" .md)
        echo "[$((i-5))/10] Creating: $issue_name"
        
        # Extract labels from issue file
        labels=$(grep "^## Labels" -A 1 "$issue_file" | tail -1 | sed 's/`//g' | tr ',' '\n' | xargs | tr ' ' ',')
        
        # Create issue with labels
        if [ -n "$labels" ]; then
            if gh issue create -F "$issue_file" --label "$labels" > /dev/null 2>&1; then
                echo "    ✅ Success (with labels: $labels)"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        else
            # Fallback: create without labels
            if gh issue create -F "$issue_file" > /dev/null 2>&1; then
                echo "    ✅ Success (no labels found)"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        fi
        
        # API rate limiting 방지
        sleep 2
    else
        echo "[$((i-5))/10] ⚠️  File not found: issue-$issue_num-*.md"
        ((FAILED++))
    fi
done

echo ""
echo "================================================"
echo "Backend Issues 생성 완료"
echo "================================================"
echo "✅ 생성 성공: $CREATED 개"
if [ $FAILED -gt 0 ]; then
    echo "❌ 생성 실패: $FAILED 개"
fi
echo ""
echo "다음 단계:"
echo "1. GitHub에서 생성된 이슈 확인"
echo "2. 라벨 추가: gh issue edit <NUM> --add-label 'epic:...,type:...'"
echo "3. 마일스톤 설정: gh issue edit <NUM> --milestone 'Phase 2'"
echo "4. 프로젝트 보드 추가: gh project item-add <PROJECT_ID> --url <ISSUE_URL>"
echo ""
echo "자세한 내용: README.md 참조"
echo ""


```

파일은 찾았지만 생성에 실패했습니다. 에러 원인을 확인하기 위해 직접 명령어를 실행해보겠습니다.

이슈 생성 시 title과 body를 별도로 제공해야 합니다. 스크립트를 수정하겠습니다.

```shellscript
#!/bin/bash

# Bizplan Backend Issues 일괄 생성 스크립트
# Frontend PoC (#001-#005)는 이미 완료되어 제외됨
# Backend 이슈만 생성 (#006-#015)

set -e

echo "================================================"
echo "Bizplan Backend Issues 생성 시작"
echo "================================================"
echo ""
echo "✅ Frontend PoC (#001-#005): 이미 완료됨 - 건너뜀"
echo "🔄 Backend Issues (#006-#015): 생성 중..."
echo ""

# GitHub CLI 설치 확인
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI (gh) not installed"
    echo "Install: https://cli.github.com/"
    exit 1
fi

# GitHub 로그인 확인
if ! gh auth status &> /dev/null; then
    echo "❌ Error: Not logged in to GitHub"
    echo "Run: gh auth login"
    exit 1
fi

# 스크립트 실행 위치 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Backend 이슈 생성
CREATED=0
FAILED=0

for i in {6..15}; do
    # Format as 3-digit number (006, 007, etc.)
    issue_num=$(printf "%03d" $i)
    issue_file=$(ls issue-$issue_num-*.md 2>/dev/null | head -1)
    
    if [ -f "$issue_file" ]; then
        issue_name=$(basename "$issue_file" .md)
        echo "[$((i-5))/10] Creating: $issue_name"
        
        # Extract title from first line (format: # [#NNN] Title)
        title=$(head -1 "$issue_file" | sed 's/^# //')
        
        # Extract body (everything after first line)
        body=$(tail -n +3 "$issue_file")
        
        # Extract labels from issue file
        labels=$(grep "^## Labels" -A 1 "$issue_file" | tail -1 | sed 's/`//g' | tr ',' '\n' | xargs | tr ' ' ',')
        
        # Create issue with title, body, and labels
        if [ -n "$labels" ]; then
            if echo "$body" | gh issue create --title "$title" --body-file - --label "$labels" > /dev/null 2>&1; then
                echo "    ✅ Success"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        else
            # Fallback: create without labels
            if echo "$body" | gh issue create --title "$title" --body-file - > /dev/null 2>&1; then
                echo "    ✅ Success (no labels)"
                ((CREATED++))
            else
                echo "    ❌ Failed"
                ((FAILED++))
            fi
        fi
        
        # API rate limiting 방지
        sleep 2
    else
        echo "[$((i-5))/10] ⚠️  File not found: issue-$issue_num-*.md"
        ((FAILED++))
    fi
done

echo ""
echo "================================================"
echo "Backend Issues 생성 완료"
echo "================================================"
echo "✅ 생성 성공: $CREATED 개"
if [ $FAILED -gt 0 ]; then
    echo "❌ 생성 실패: $FAILED 개"
fi
echo ""
echo "다음 단계:"
echo "1. GitHub에서 생성된 이슈 확인"
echo "2. 라벨 추가: gh issue edit <NUM> --add-label 'epic:...,type:...'"
echo "3. 마일스톤 설정: gh issue edit <NUM> --milestone 'Phase 2'"
echo "4. 프로젝트 보드 추가: gh project item-add <PROJECT_ID> --url <ISSUE_URL>"
echo ""
echo "자세한 내용: README.md 참조"
echo ""


```

---

**User**

해당 스크립트로 하지 말고 니가 에이전트로서 직접 gh 명령을 수행해줘.

---

**Cursor**

네, 직접 GitHub CLI로 이슈를 생성하겠습니다. 먼저 첫 번째 이슈 파일을 확인하겠습니다.

이제 각 이슈를 순차적으로 생성하겠습니다.

라벨이 존재하지 않아 실패했습니다. 먼저 라벨 없이 이슈를 생성하겠습니다.

좋습니다! 첫 번째 이슈가 생성되었습니다. 나머지 이슈들도 계속 생성하겠습니다.

훌륭합니다! 이제 나머지 NFR 이슈들을 생성하겠습니다.

완료되었습니다! 모든 Backend 이슈가 성공적으로 생성되었습니다. 확인해보겠습니다.

완료되었습니다! ✨

## 🎉 GitHub Issues 생성 완료

### ✅ 생성된 Backend Issues (10개)

| GitHub Issue # | Task ID | Title | Status |
|----------------|---------|-------|--------|
| #2 | #006 | 프로젝트 생성 및 템플릿 목록 API 구현 | ✅ OPEN |
| #3 | #007 | Wizard 단계별 답변 저장/조회 API 구현 | ✅ OPEN |
| #4 | #008 | 사업계획서 생성 LLM 엔진 및 프롬프트 구현 | ✅ OPEN |
| #5 | #009 | 사업계획서 생성 오케스트레이션 API 구현 | ✅ OPEN |
| #6 | #010 | 사업계획서 HWP/PDF 내보내기 기능 구현 | ✅ OPEN |
| #7 | #011 | PMF 진단 및 리포트 생성 LLM 엔진 구현 | ✅ OPEN |
| #8 | #012 | 재무 추정 및 유닛 이코노믹스 계산 엔진 구현 | ✅ OPEN |
| #9 | #013 | 데이터 저장/전송 암호화 및 보안 구성 | ✅ OPEN |
| #10 | #014 | 구조화된 로깅 및 Prometheus/Grafana 모니터링 구축 | ✅ OPEN |
| #11 | #015 | API 성능 목표 검증을 위한 k6 부하 테스트 구현 | ✅ OPEN |

### 📊 분류

**Core Backend & AI (EPIC 1):**
- #2 (006) - 프로젝트 생성 API
- #3 (007) - Wizard 답변 저장 API
- #4 (008) - LLM 문서 생성 엔진
- #5 (009) - 문서 생성 오케스트레이션
- #6 (010) - HWP/PDF 내보내기

**Special Features (EPIC 2):**
- #7 (011) - PMF 진단 LLM 엔진
- #8 (012) - 재무 계산 엔진

**Non-Functional Requirements (EPIC 3):**
- #9 (013) - 보안 구성
- #10 (014) - 모니터링 구축
- #11 (015) - 성능 테스트

### 🎯 Critical Path

```
#2 (006) → #3 (007) → #5 (009) → #6 (010) → #11 (015)
```

### 🚀 다음 단계

1. **GitHub에서 확인**
   ```bash
   # 브라우저에서 확인
   gh issue list --web
   ```

2. **Labels 추가** (선택 사항)
   ```bash
   # 예시: Issue #2에 라벨 추가
   gh label create "epic:EPIC_1_PASS_THE_TEST" --description "Core Backend & AI" --color "0366d6"
   gh label create "type:backend" --description "Backend development" --color "5319e7"
   gh label create "priority:Must" --description "Must have feature" --color "d73a4a"
   
   # 이슈에 라벨 추가
   gh issue edit 2 --add-label "type:backend,priority:Must"
   ```

3. **Milestone 설정** (선택 사항)
   ```bash
   # Milestone 생성
   gh milestone create "Phase 2: Backend Core" --due-date 2025-12-31
   
   # 이슈에 milestone 할당
   gh issue edit 2 --milestone "Phase 2: Backend Core"
   ```

4. **프로젝트 보드 생성** (선택 사항)
   ```bash
   # GitHub Projects V2 생성
   gh project create --title "Bizplan Backend Development" --owner @me
   ```

5. **작업 시작**
   - 첫 번째 작업: Issue #2 (프로젝트 생성 API)
   - 병렬 가능: Issue #4 (AI 엔진), Issue #8 (재무 계산)

### 📝 참고 문서

- **실행 계획**: `tasks/github-issues/ISSUE_EXECUTION_PLAN.md`
- **사용 가이드**: `tasks/github-issues/README.md`
- **변경 이력**: `tasks/github-issues/CHANGELOG.md`

모든 Backend 이슈가 성공적으로 생성되었습니다! 🎊

```plaintext
#2 (006) → #3 (007) → #5 (009) → #6 (010) → #11 (015)
```

```shellscript
   # 브라우저에서 확인
   gh issue list --web
```

```shellscript
   # 예시: Issue #2에 라벨 추가
   gh label create "epic:EPIC_1_PASS_THE_TEST" --description "Core Backend & AI" --color "0366d6"
   gh label create "type:backend" --description "Backend development" --color "5319e7"
   gh label create "priority:Must" --description "Must have feature" --color "d73a4a"
   
   # 이슈에 라벨 추가
   gh issue edit 2 --add-label "type:backend,priority:Must"
```

```shellscript
   # Milestone 생성
   gh milestone create "Phase 2: Backend Core" --due-date 2025-12-31
   
   # 이슈에 milestone 할당
   gh issue edit 2 --milestone "Phase 2: Backend Core"
```

```shellscript
   # GitHub Projects V2 생성
   gh project create --title "Bizplan Backend Development" --owner @me
```

---

**User**

https://github.com/users/wild-mental/projects/10/views/4  프로젝트의 로드맵 뷰에서 각 이슈들에 대한 일정까지 확인할 수 있게 시작일 종료일을 필드로 추가해줘.

---

**Cursor**

GitHub Projects의 로드맵 뷰에 시작일과 종료일 필드를 추가하고 각 이슈에 일정을 설정하겠습니다.

먼저 프로젝트 정보를 확인하겠습니다.

프로젝트를 찾았습니다! 이제 프로젝트의 필드를 확인하겠습니다.

좋습니다! 프로젝트에 이미 "Start date"와 "Target date" 필드가 있습니다. 이제 각 이슈에 일정을 설정하겠습니다.

먼저 프로젝트에 이슈들이 추가되어 있는지 확인하겠습니다.

완벽합니다! 모든 이슈가 프로젝트에 있습니다. 이제 ISSUE_EXECUTION_PLAN.md의 실행 전략에 따라 각 이슈에 일정을 설정하겠습니다.

오늘(2025-11-26)을 기준으로 설정하겠습니다:

`--owner` 플래그가 필요 없었습니다. project-id에 Node ID를 사용하여 다시 설정하겠습니다.

좋습니다! 계속해서 나머지 이슈들의 일정을 설정하겠습니다.

완료되었습니다! 이제 설정된 일정을 확인하겠습니다.

간단하게 웹 브라우저로 확인하도록 안내하겠습니다.

완료되었습니다! ✨

## 📅 GitHub Projects 로드맵 일정 설정 완료

모든 Backend 이슈에 시작일(Start date)과 종료일(Target date)이 설정되었습니다.

### 📊 설정된 일정 요약

#### Wave 1: 병렬 시작 (2025-11-27 시작)
| Issue | Task | 시작일 | 종료일 | 기간 |
|-------|------|--------|--------|------|
| #2 | [#006] 프로젝트 생성 API | 2025-11-27 | 2025-11-29 | 3일 |
| #4 | [#008] LLM 문서 생성 엔진 | 2025-11-27 | 2025-12-01 | 5일 |
| #8 | [#012] 재무 계산 엔진 | 2025-11-27 | 2025-12-01 | 5일 |

#### Wave 2: 의존성 작업 (2025-11-30 시작)
| Issue | Task | 시작일 | 종료일 | 기간 |
|-------|------|--------|--------|------|
| #3 | [#007] Wizard 답변 저장 API | 2025-11-30 | 2025-12-02 | 3일 |
| #9 | [#013] 보안 구성 | 2025-11-30 | 2025-12-02 | 3일 |
| #10 | [#014] 모니터링 구축 | 2025-11-30 | 2025-12-02 | 3일 |

#### Wave 3: 통합 작업 (2025-12-02~03 시작)
| Issue | Task | 시작일 | 종료일 | 기간 |
|-------|------|--------|--------|------|
| #7 | [#011] PMF 진단 엔진 | 2025-12-02 | 2025-12-05 | 4일 |
| #5 | [#009] 문서 생성 오케스트레이션 | 2025-12-03 | 2025-12-06 | 4일 |

#### Wave 4: 완료 작업 (2025-12-07 시작)
| Issue | Task | 시작일 | 종료일 | 기간 |
|-------|------|--------|--------|------|
| #6 | [#010] HWP/PDF 내보내기 | 2025-12-07 | 2025-12-11 | 5일 |
| #11 | [#015] 성능 테스트 | 2025-12-07 | 2025-12-09 | 3일 |

### 🎯 Critical Path (최장 경로)
```
#2 (006) → #3 (007) → #5 (009) → #6 (010)
11/27      11/30      12/03      12/07      → 12/11 완료
```

### 📈 타임라인 요약
- **시작**: 2025년 11월 27일 (수)
- **완료**: 2025년 12월 11일 (수)
- **총 기간**: 약 2주 (실제 작업일 기준)
- **병렬 작업**: 최대 3개 이슈 동시 진행 가능

### 🔗 확인 방법

1. **로드맵 뷰에서 확인**:
   - [https://github.com/users/wild-mental/projects/10/views/4](https://github.com/users/wild-mental/projects/10/views/4)
   - 타임라인 형태로 각 이슈의 일정을 시각적으로 확인 가능

2. **테이블 뷰에서 확인**:
   - Projects에서 "Table" 뷰 선택
   - "Start date"와 "Target date" 컬럼에서 날짜 확인

### 💡 활용 팁

1. **일정 변경**: 이슈를 드래그하여 시작/종료일 조정 가능
2. **의존성 확인**: 로드맵 뷰에서 작업 순서를 시각적으로 파악
3. **진행 상태 추적**: Status 필드로 각 이슈의 진행 상태 관리
4. **팀 할당**: Assignees 필드로 담당자 배정

모든 일정이 성공적으로 설정되었습니다! 🎉

```plaintext
#2 (006) → #3 (007) → #5 (009) → #6 (010)
11/27      11/30      12/03      12/07      → 12/11 완료
```

---

**User**

@tasks/github-issues/ISSUE_EXECUTION_PLAN.md 에 현재 정리한 내용을 완전한 결정사항으로 반영하며 업데이트해주고, 

앞으로도 지금 진행한 것처럼 이슈 관리를 shell script가 아니라 에이전트의 gh 명령 직접 제어를 통해서 수행할 수 있도록 rule 에 명시해줘.

필요 없어진 .sh 파일을 제거하고 해당 스크립트를 사용하라고 가이드 되어 있는 문서가 있다면 해당 부분들을 함께 수정해줘.

---

**Cursor**

네, 현재 진행한 내용을 반영하여 문서들을 업데이트하고 규칙을 정리하겠습니다.

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update (2025-11-26)
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

**Backend Issues (EPIC 1-3) - CREATED & SCHEDULED**
- GitHub Issues #2 ~ #11 생성 완료 (2025-11-26)
- GitHub Projects 로드맵에 일정 설정 완료
- 시작일: 2025-11-27 (수)
- 예상 완료일: 2025-12-11 (수)

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점)
**병렬 개발 가능한 작업들:**
- **#006** - REQ-FUNC-001-BE-001: 프로젝트 생성 및 템플릿 목록 API
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #008, #012
  - 활성화: #007, #013, #014

- **#008** - REQ-FUNC-003-AI-001: 사업계획서 생성 LLM 엔진 구현
  - 의존성: 없음
  - 병렬 가능: #006, #012
  - 활성화: #009, #011

- **#012** - REQ-FUNC-012-BE-001: 재무 추정 및 유닛 이코노믹스 계산 엔진
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #006, #008 (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update (2025-11-26)
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

**Backend Issues (EPIC 1-3) - CREATED & SCHEDULED**
- GitHub Issues #2 ~ #11 생성 완료 (2025-11-26)
- GitHub Projects 로드맵에 일정 설정 완료
- 시작일: 2025-11-27 (수)
- 예상 완료일: 2025-12-11 (수)

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점: 2025-11-27)
**병렬 개발 가능한 작업들:**
- **#2 (006)** - 프로젝트 생성 및 템플릿 목록 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/2
  - 일정: 2025-11-27 ~ 2025-11-29 (3일)
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #4 (008), #8 (012)
  - 활성화: #3 (007), #9 (013), #10 (014)

- **#4 (008)** - 사업계획서 생성 LLM 엔진 구현
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/4
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음
  - 병렬 가능: #2 (006), #8 (012)
  - 활성화: #5 (009), #7 (011)

- **#8 (012)** - 재무 추정 및 유닛 이코노믹스 계산 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/8
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #2 (006), #4 (008) (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #006 완료 후)
- **#007** - REQ-FUNC-002-BE-001: Wizard 단계별 답변 저장/조회 API
  - 의존성: #006
  - 병렬 가능: #008, #012 (진행 중인 작업)
  - 활성화: #009

### Wave 2.3 (Wave 2.2의 #007 + Wave 2.1의 #008 완료 후)
- **#009** - REQ-FUNC-003-BE-001: 사업계획서 생성 오케스트레이션 API
  - 의존성: #007, #008
  - 병렬 가능: #012 (진행 중일 경우)
  - 활성화: #010, #015

### Wave 2.4 (Wave 2.3의 #009 완료 후)
- **#010** - REQ-FUNC-011-BE-001: HWP/PDF 내보내기 기능
  - 의존성: #009
  - 병렬 가능: #011 (AI 작업), #012 (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update (2025-11-26)
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

**Backend Issues (EPIC 1-3) - CREATED & SCHEDULED**
- GitHub Issues #2 ~ #11 생성 완료 (2025-11-26)
- GitHub Projects 로드맵에 일정 설정 완료
- 시작일: 2025-11-27 (수)
- 예상 완료일: 2025-12-11 (수)

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점: 2025-11-27)
**병렬 개발 가능한 작업들:**
- **#2 (006)** - 프로젝트 생성 및 템플릿 목록 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/2
  - 일정: 2025-11-27 ~ 2025-11-29 (3일)
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #4 (008), #8 (012)
  - 활성화: #3 (007), #9 (013), #10 (014)

- **#4 (008)** - 사업계획서 생성 LLM 엔진 구현
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/4
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음
  - 병렬 가능: #2 (006), #8 (012)
  - 활성화: #5 (009), #7 (011)

- **#8 (012)** - 재무 추정 및 유닛 이코노믹스 계산 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/8
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #2 (006), #4 (008) (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #2 완료 후: 2025-11-30)
- **#3 (007)** - Wizard 단계별 답변 저장/조회 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/3
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #4 (008), #8 (012) (진행 중인 작업)
  - 활성화: #5 (009)

**병렬 작업 (NFR):**
- **#9 (013)** - 데이터 저장/전송 암호화 및 보안 구성
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/9
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #10 (014)

- **#10 (014)** - 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/10
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #9 (013)

### Wave 2.3 (Wave 2.2의 #3 + Wave 2.1의 #4 완료 후: 2025-12-03)
- **#5 (009)** - 사업계획서 생성 오케스트레이션 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/5
  - 일정: 2025-12-03 ~ 2025-12-06 (4일)
  - 의존성: #3 (007), #4 (008)
  - 병렬 가능: #8 (012) (진행 중일 경우)
  - 활성화: #6 (010), #11 (015)

### Wave 2.4 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#6 (010)** - HWP/PDF 내보내기 기능
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/6
  - 일정: 2025-12-07 ~ 2025-12-11 (5일)
  - 의존성: #5 (009)
  - 병렬 가능: #7 (011) (AI 작업), #8 (012) (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #008 완료 후)
- **#011** - REQ-FUNC-008-AI-001: PMF 진단 및 리포트 생성 LLM 엔진
  - 의존성: #008 (FastAPI 환경 공유)
  - 병렬 가능: #009, #010, #012

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #006 완료 후)
**병렬 개발 가능한 작업들:**
- **#013** - REQ-NF-006-SEC-001: 데이터 저장/전송 암호화 및 보안 구성
  - 의존성: #006
  - 병렬 가능: #014

- **#014** - REQ-NF-012-OPS-001: 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - 의존성: #006
  - 병렬 가능: #013

### Wave 4.2 (Wave 2.3의 #009 완료 후)
- **#015** - REQ-NF-001-PERF-001: API 성능 목표 검증을 위한 k6 부하 테스트
  - 의존성: #009 (주요 API 구현 완료 필요)
  - 병렬 가능: #010, #011, #012, #013, #014

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update (2025-11-26)
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

**Backend Issues (EPIC 1-3) - CREATED & SCHEDULED**
- GitHub Issues #2 ~ #11 생성 완료 (2025-11-26)
- GitHub Projects 로드맵에 일정 설정 완료
- 시작일: 2025-11-27 (수)
- 예상 완료일: 2025-12-11 (수)

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점: 2025-11-27)
**병렬 개발 가능한 작업들:**
- **#2 (006)** - 프로젝트 생성 및 템플릿 목록 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/2
  - 일정: 2025-11-27 ~ 2025-11-29 (3일)
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #4 (008), #8 (012)
  - 활성화: #3 (007), #9 (013), #10 (014)

- **#4 (008)** - 사업계획서 생성 LLM 엔진 구현
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/4
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음
  - 병렬 가능: #2 (006), #8 (012)
  - 활성화: #5 (009), #7 (011)

- **#8 (012)** - 재무 추정 및 유닛 이코노믹스 계산 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/8
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #2 (006), #4 (008) (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #2 완료 후: 2025-11-30)
- **#3 (007)** - Wizard 단계별 답변 저장/조회 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/3
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #4 (008), #8 (012) (진행 중인 작업)
  - 활성화: #5 (009)

**병렬 작업 (NFR):**
- **#9 (013)** - 데이터 저장/전송 암호화 및 보안 구성
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/9
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #10 (014)

- **#10 (014)** - 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/10
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #9 (013)

### Wave 2.3 (Wave 2.2의 #3 + Wave 2.1의 #4 완료 후: 2025-12-03)
- **#5 (009)** - 사업계획서 생성 오케스트레이션 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/5
  - 일정: 2025-12-03 ~ 2025-12-06 (4일)
  - 의존성: #3 (007), #4 (008)
  - 병렬 가능: #8 (012) (진행 중일 경우)
  - 활성화: #6 (010), #11 (015)

### Wave 2.4 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#6 (010)** - HWP/PDF 내보내기 기능
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/6
  - 일정: 2025-12-07 ~ 2025-12-11 (5일)
  - 의존성: #5 (009)
  - 병렬 가능: #7 (011) (AI 작업), #8 (012) (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #4 완료 후: 2025-12-02)
- **#7 (011)** - PMF 진단 및 리포트 생성 LLM 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/7
  - 일정: 2025-12-02 ~ 2025-12-05 (4일)
  - 의존성: #4 (008) (FastAPI 환경 공유)
  - 병렬 가능: #5 (009), #6 (010), #8 (012)

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #2 완료 후: 2025-11-30)
**이미 Wave 2.2에서 병렬 처리됨** ✅
- #9 (013) - 보안 구성 (2025-11-30 ~ 2025-12-02)
- #10 (014) - 모니터링 구축 (2025-11-30 ~ 2025-12-02)

### Wave 4.2 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#11 (015)** - API 성능 목표 검증을 위한 k6 부하 테스트
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/11
  - 일정: 2025-12-07 ~ 2025-12-09 (3일)
  - 의존성: #5 (009) (주요 API 구현 완료 필요)
  - 병렬 가능: #6 (010), #7 (011), #8 (012)

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| Issue | Depends On | Enables | Parallelizable With |
|-------|------------|---------|---------------------|
| #006  | BE-INIT    | #007, #013, #014 | #008, #012 |
| #007  | #006       | #009    | #008, #012 |
| #008  | -          | #009, #011 | #006, #007, #012 |
| #009  | #007, #008 | #010, #015 | #012 |
| #010  | #009       | -       | #011, #012 |
| #011  | #008       | -       | #009, #010, #012 |
| #012  | -          | -       | All |
| #013  | #006       | -       | #014 |
| #014  | #006       | -       | #013 |
| #015  | #009       | -       | #010, #011, #012, #013, #014 |

**Note**: Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update (2025-11-26)
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

**Backend Issues (EPIC 1-3) - CREATED & SCHEDULED**
- GitHub Issues #2 ~ #11 생성 완료 (2025-11-26)
- GitHub Projects 로드맵에 일정 설정 완료
- 시작일: 2025-11-27 (수)
- 예상 완료일: 2025-12-11 (수)

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점: 2025-11-27)
**병렬 개발 가능한 작업들:**
- **#2 (006)** - 프로젝트 생성 및 템플릿 목록 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/2
  - 일정: 2025-11-27 ~ 2025-11-29 (3일)
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #4 (008), #8 (012)
  - 활성화: #3 (007), #9 (013), #10 (014)

- **#4 (008)** - 사업계획서 생성 LLM 엔진 구현
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/4
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음
  - 병렬 가능: #2 (006), #8 (012)
  - 활성화: #5 (009), #7 (011)

- **#8 (012)** - 재무 추정 및 유닛 이코노믹스 계산 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/8
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #2 (006), #4 (008) (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #2 완료 후: 2025-11-30)
- **#3 (007)** - Wizard 단계별 답변 저장/조회 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/3
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #4 (008), #8 (012) (진행 중인 작업)
  - 활성화: #5 (009)

**병렬 작업 (NFR):**
- **#9 (013)** - 데이터 저장/전송 암호화 및 보안 구성
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/9
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #10 (014)

- **#10 (014)** - 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/10
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #9 (013)

### Wave 2.3 (Wave 2.2의 #3 + Wave 2.1의 #4 완료 후: 2025-12-03)
- **#5 (009)** - 사업계획서 생성 오케스트레이션 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/5
  - 일정: 2025-12-03 ~ 2025-12-06 (4일)
  - 의존성: #3 (007), #4 (008)
  - 병렬 가능: #8 (012) (진행 중일 경우)
  - 활성화: #6 (010), #11 (015)

### Wave 2.4 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#6 (010)** - HWP/PDF 내보내기 기능
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/6
  - 일정: 2025-12-07 ~ 2025-12-11 (5일)
  - 의존성: #5 (009)
  - 병렬 가능: #7 (011) (AI 작업), #8 (012) (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #4 완료 후: 2025-12-02)
- **#7 (011)** - PMF 진단 및 리포트 생성 LLM 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/7
  - 일정: 2025-12-02 ~ 2025-12-05 (4일)
  - 의존성: #4 (008) (FastAPI 환경 공유)
  - 병렬 가능: #5 (009), #6 (010), #8 (012)

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #2 완료 후: 2025-11-30)
**이미 Wave 2.2에서 병렬 처리됨** ✅
- #9 (013) - 보안 구성 (2025-11-30 ~ 2025-12-02)
- #10 (014) - 모니터링 구축 (2025-11-30 ~ 2025-12-02)

### Wave 4.2 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#11 (015)** - API 성능 목표 검증을 위한 k6 부하 테스트
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/11
  - 일정: 2025-12-07 ~ 2025-12-09 (3일)
  - 의존성: #5 (009) (주요 API 구현 완료 필요)
  - 병렬 가능: #6 (010), #7 (011), #8 (012)

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| GitHub Issue | Task ID | Depends On | Enables | Parallelizable With | Schedule |
|--------------|---------|------------|---------|---------------------|----------|
| #2 | 006 | BE-INIT | #3, #9, #10 | #4, #8 | 11/27-11/29 |
| #3 | 007 | #2 | #5 | #4, #8 | 11/30-12/02 |
| #4 | 008 | - | #5, #7 | #2, #3, #8 | 11/27-12/01 |
| #5 | 009 | #3, #4 | #6, #11 | #8 | 12/03-12/06 |
| #6 | 010 | #5 | - | #7, #8 | 12/07-12/11 |
| #7 | 011 | #4 | - | #5, #6, #8 | 12/02-12/05 |
| #8 | 012 | - | - | All | 11/27-12/01 |
| #9 | 013 | #2 | - | #10 | 11/30-12/02 |
| #10 | 014 | #2 | - | #9 | 11/30-12/02 |
| #11 | 015 | #5 | - | #6, #7, #8 | 12/07-12/09 |

**Notes**: 
- Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨
- GitHub Issue 번호는 실제 저장소의 Issue 번호
- Task ID는 문서상의 원래 번호
- Schedule은 GitHub Projects 로드맵에 설정된 실제 일정

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#006 → #007 → #009 → #010 → #015
```

**예상 소요 기간 (순차 실행 시):**
- #006: 3일
- #007: 3일
- #009: 4일 (AI #008과 동기화 필요)
- #010: 5일
- #015: 3일
- **Total: 18일** (약 3.5주)

**병렬화 시 최단 기간:**
- Wave 1: 5일 (#008 AI 엔진이 가장 긴 작업)
- Wave 2: 3일 (#007)
- Wave 3: 4일 (#009)
- Wave 4: 5일 (#010)
- Wave 5: 3일 (#015)
- **Total: 20일** (약 4주) - 약간의 대기 시간 포함

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update (2025-11-26)
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

**Backend Issues (EPIC 1-3) - CREATED & SCHEDULED**
- GitHub Issues #2 ~ #11 생성 완료 (2025-11-26)
- GitHub Projects 로드맵에 일정 설정 완료
- 시작일: 2025-11-27 (수)
- 예상 완료일: 2025-12-11 (수)

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점: 2025-11-27)
**병렬 개발 가능한 작업들:**
- **#2 (006)** - 프로젝트 생성 및 템플릿 목록 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/2
  - 일정: 2025-11-27 ~ 2025-11-29 (3일)
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #4 (008), #8 (012)
  - 활성화: #3 (007), #9 (013), #10 (014)

- **#4 (008)** - 사업계획서 생성 LLM 엔진 구현
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/4
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음
  - 병렬 가능: #2 (006), #8 (012)
  - 활성화: #5 (009), #7 (011)

- **#8 (012)** - 재무 추정 및 유닛 이코노믹스 계산 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/8
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #2 (006), #4 (008) (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #2 완료 후: 2025-11-30)
- **#3 (007)** - Wizard 단계별 답변 저장/조회 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/3
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #4 (008), #8 (012) (진행 중인 작업)
  - 활성화: #5 (009)

**병렬 작업 (NFR):**
- **#9 (013)** - 데이터 저장/전송 암호화 및 보안 구성
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/9
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #10 (014)

- **#10 (014)** - 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/10
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #9 (013)

### Wave 2.3 (Wave 2.2의 #3 + Wave 2.1의 #4 완료 후: 2025-12-03)
- **#5 (009)** - 사업계획서 생성 오케스트레이션 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/5
  - 일정: 2025-12-03 ~ 2025-12-06 (4일)
  - 의존성: #3 (007), #4 (008)
  - 병렬 가능: #8 (012) (진행 중일 경우)
  - 활성화: #6 (010), #11 (015)

### Wave 2.4 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#6 (010)** - HWP/PDF 내보내기 기능
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/6
  - 일정: 2025-12-07 ~ 2025-12-11 (5일)
  - 의존성: #5 (009)
  - 병렬 가능: #7 (011) (AI 작업), #8 (012) (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #4 완료 후: 2025-12-02)
- **#7 (011)** - PMF 진단 및 리포트 생성 LLM 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/7
  - 일정: 2025-12-02 ~ 2025-12-05 (4일)
  - 의존성: #4 (008) (FastAPI 환경 공유)
  - 병렬 가능: #5 (009), #6 (010), #8 (012)

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #2 완료 후: 2025-11-30)
**이미 Wave 2.2에서 병렬 처리됨** ✅
- #9 (013) - 보안 구성 (2025-11-30 ~ 2025-12-02)
- #10 (014) - 모니터링 구축 (2025-11-30 ~ 2025-12-02)

### Wave 4.2 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#11 (015)** - API 성능 목표 검증을 위한 k6 부하 테스트
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/11
  - 일정: 2025-12-07 ~ 2025-12-09 (3일)
  - 의존성: #5 (009) (주요 API 구현 완료 필요)
  - 병렬 가능: #6 (010), #7 (011), #8 (012)

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| GitHub Issue | Task ID | Depends On | Enables | Parallelizable With | Schedule |
|--------------|---------|------------|---------|---------------------|----------|
| #2 | 006 | BE-INIT | #3, #9, #10 | #4, #8 | 11/27-11/29 |
| #3 | 007 | #2 | #5 | #4, #8 | 11/30-12/02 |
| #4 | 008 | - | #5, #7 | #2, #3, #8 | 11/27-12/01 |
| #5 | 009 | #3, #4 | #6, #11 | #8 | 12/03-12/06 |
| #6 | 010 | #5 | - | #7, #8 | 12/07-12/11 |
| #7 | 011 | #4 | - | #5, #6, #8 | 12/02-12/05 |
| #8 | 012 | - | - | All | 11/27-12/01 |
| #9 | 013 | #2 | - | #10 | 11/30-12/02 |
| #10 | 014 | #2 | - | #9 | 11/30-12/02 |
| #11 | 015 | #5 | - | #6, #7, #8 | 12/07-12/09 |

**Notes**: 
- Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨
- GitHub Issue 번호는 실제 저장소의 Issue 번호
- Task ID는 문서상의 원래 번호
- Schedule은 GitHub Projects 로드맵에 설정된 실제 일정

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#2 (006) → #3 (007) → #5 (009) → #6 (010)
11/27      11/30      12/03      12/07      → 12/11 완료
```

**실제 설정된 일정:**
- 시작일: 2025-11-27 (수)
- 완료일: 2025-12-11 (수)
- 총 기간: **15일 (약 3주)**
- 실 작업일: 약 10-12일 (주말 제외)

**예상 소요 기간 (순차 실행 시):**
- #2 (006): 3일
- #3 (007): 3일
- #5 (009): 4일 (AI #4(008)과 동기화 필요)
- #6 (010): 5일
- #11 (015): 3일
- **Total: 18일** (약 3.5주)

**병렬화를 통한 실제 일정 (확정):**
- Wave 1 (11/27-12/01): 5일 - #2, #4, #8 병렬
- Wave 2 (11/30-12/02): 3일 - #3, #9, #10 병렬
- Wave 3 (12/02-12/06): 5일 - #5, #7 병렬
- Wave 4 (12/07-12/11): 5일 - #6, #11 병렬
- **Total: 15일** (약 3주) - 병렬화로 3일 단축

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## Version History
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
# GitHub Issues Execution Plan

## Overview
이 문서는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue들의 실행 순서와 병렬 개발 가능 정보를 명시합니다.

## ✅ Status Update (2025-11-26)
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- UI/UX 검증 완료로 Backend API 개발 즉시 착수 가능
- 완료된 이슈 파일들은 `completed/` 폴더로 이동됨

**Backend Issues (EPIC 1-3) - CREATED & SCHEDULED**
- GitHub Issues #2 ~ #11 생성 완료 (2025-11-26)
- GitHub Projects 로드맵에 일정 설정 완료
- 시작일: 2025-11-27 (수)
- 예상 완료일: 2025-12-11 (수)

## Issue Numbering Strategy
이슈 번호는 실행 가능 순서를 반영하여 할당되었습니다:
- #001 ~ #005: Frontend PoC (EPIC 0) ✅ **COMPLETED**
- #006 ~ #010: Core Backend & AI Implementation (EPIC 1) 🔄 **IN SCOPE**
- #011 ~ #012: Special Features (EPIC 2) 🔄 **IN SCOPE**
- #013 ~ #015: Non-Functional Requirements (EPIC 3) 🔄 **IN SCOPE**

---

## ✅ Phase 1: Frontend PoC (EPIC 0) - COMPLETED

**상태**: 별도 프로젝트에서 완료됨 (2025-11-26 이전)

완료된 작업:
- ✅ #001 - 프로젝트 생성 및 Wizard 기본 레이아웃 PoC
- ✅ #002 - Wizard 입력 폼 및 자동저장 UI PoC
- ✅ #003 - 사업계획서 초안 생성 및 뷰어 UI PoC
- ✅ #004 - 재무 입력 및 유닛 이코노믹스 시각화 UI PoC
- ✅ #005 - PMF 진단 설문 및 리포트 UI PoC

**결과**:
- Frontend UI/UX 검증 완료
- API Contract 확정
- Backend 개발 즉시 착수 가능

---

## Phase 2: Core Backend & AI Implementation (EPIC 1)
**목표**: 핵심 API 및 AI 파이프라인 구축

### Wave 2.1 (시작점: 2025-11-27)
**병렬 개발 가능한 작업들:**
- **#2 (006)** - 프로젝트 생성 및 템플릿 목록 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/2
  - 일정: 2025-11-27 ~ 2025-11-29 (3일)
  - 의존성: TASK-BE-INIT (프로젝트 초기 설정 완료 가정)
  - 병렬 가능: #4 (008), #8 (012)
  - 활성화: #3 (007), #9 (013), #10 (014)

- **#4 (008)** - 사업계획서 생성 LLM 엔진 구현
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/4
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음
  - 병렬 가능: #2 (006), #8 (012)
  - 활성화: #5 (009), #7 (011)

- **#8 (012)** - 재무 추정 및 유닛 이코노믹스 계산 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/8
  - 일정: 2025-11-27 ~ 2025-12-01 (5일)
  - 의존성: 없음 (Pure Logic)
  - 병렬 가능: #2 (006), #4 (008) (모든 작업과 병렬 가능)

### Wave 2.2 (Wave 2.1의 #2 완료 후: 2025-11-30)
- **#3 (007)** - Wizard 단계별 답변 저장/조회 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/3
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #4 (008), #8 (012) (진행 중인 작업)
  - 활성화: #5 (009)

**병렬 작업 (NFR):**
- **#9 (013)** - 데이터 저장/전송 암호화 및 보안 구성
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/9
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #10 (014)

- **#10 (014)** - 구조화된 로깅 및 Prometheus/Grafana 모니터링
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/10
  - 일정: 2025-11-30 ~ 2025-12-02 (3일)
  - 의존성: #2 (006)
  - 병렬 가능: #9 (013)

### Wave 2.3 (Wave 2.2의 #3 + Wave 2.1의 #4 완료 후: 2025-12-03)
- **#5 (009)** - 사업계획서 생성 오케스트레이션 API
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/5
  - 일정: 2025-12-03 ~ 2025-12-06 (4일)
  - 의존성: #3 (007), #4 (008)
  - 병렬 가능: #8 (012) (진행 중일 경우)
  - 활성화: #6 (010), #11 (015)

### Wave 2.4 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#6 (010)** - HWP/PDF 내보내기 기능
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/6
  - 일정: 2025-12-07 ~ 2025-12-11 (5일)
  - 의존성: #5 (009)
  - 병렬 가능: #7 (011) (AI 작업), #8 (012) (진행 중일 경우)

---

## Phase 3: Special Features (EPIC 2)
**목표**: PMF 진단 등 부가 기능 개발

### Wave 3.1 (Wave 2.1의 #4 완료 후: 2025-12-02)
- **#7 (011)** - PMF 진단 및 리포트 생성 LLM 엔진
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/7
  - 일정: 2025-12-02 ~ 2025-12-05 (4일)
  - 의존성: #4 (008) (FastAPI 환경 공유)
  - 병렬 가능: #5 (009), #6 (010), #8 (012)

---

## Phase 4: Non-Functional Requirements (EPIC 3)
**목표**: 보안, 모니터링, 성능 검증

### Wave 4.1 (Wave 2.1의 #2 완료 후: 2025-11-30)
**이미 Wave 2.2에서 병렬 처리됨** ✅
- #9 (013) - 보안 구성 (2025-11-30 ~ 2025-12-02)
- #10 (014) - 모니터링 구축 (2025-11-30 ~ 2025-12-02)

### Wave 4.2 (Wave 2.3의 #5 완료 후: 2025-12-07)
- **#11 (015)** - API 성능 목표 검증을 위한 k6 부하 테스트
  - GitHub Issue: https://github.com/wild-mental/makersround-backend/issues/11
  - 일정: 2025-12-07 ~ 2025-12-09 (3일)
  - 의존성: #5 (009) (주요 API 구현 완료 필요)
  - 병렬 가능: #6 (010), #7 (011), #8 (012)

---

## Execution Strategies

### Strategy 1: Maximum Parallelization (Backend)
**목표**: 최단 기간 내 Backend 개발 완료 (팀 리소스 충분 시)

```mermaid
gantt
    title Backend Maximum Parallelization Strategy
    dateFormat YYYY-MM-DD
    section Prerequisites
    Frontend Completed    :done, prereq, 2025-11-01, 2025-11-26
    
    section Backend Core
    #006 BE-001           :b001, 2025-11-27, 3d
    #007 BE-002           :b002, after b001, 3d
    #009 BE-003           :b003, after b002, 4d
    #010 BE-011           :b004, after b003, 5d
    
    section AI Engine
    #008 AI-001           :a001, 2025-11-27, 5d
    #011 AI-008           :a002, after a001, 4d
    
    section Financial
    #012 BE-012           :b005, 2025-11-27, 5d
    
    section NFR
    #013 SEC-001          :n001, after b001, 3d
    #014 OPS-001          :n002, after b001, 3d
    #015 PERF-001         :n003, after b003, 3d
```

**인력 배치 (최대 4-5명 병렬):**
- **Week 1 (Day 1-3):**
  - Developer 1: #006 (Backend Core)
  - Developer 2: #008 (AI Engine)
  - Developer 3: #012 (Financial)

- **Week 1 (Day 4-7):**
  - Developer 1: #007 → #013, #014 (병렬)
  - Developer 2: #009 준비 (AI와 협업)
  - Developer 3: #012 계속

- **Week 2:**
  - Developer 1: #013, #014 완료 → Frontend 통합 테스트
  - Developer 2: #009 → #010
  - Developer 3: #011 (PMF) → #015 (Performance Test)

### Strategy 2: Sequential with Limited Resources (Backend)
**목표**: 2-3명의 소규모 Backend 팀으로 안정적 개발

```
Week 1:
  Day 1-3: #006 (Backend Base)
  Day 4-7: #007 (Wizard API)

Week 2:
  Day 1-3: #008 (AI Engine)
  Day 4-7: #012 (Financial, 병렬 가능 시)

Week 3:
  Day 1-3: #009 (Orchestration)
  Day 4-7: #010 (Export)

Week 4:
  Day 1-3: #011 (PMF) + #013 (Security, 병렬)
  Day 4-7: #014 (Monitoring)

Week 5:
  Day 1-3: #015 (Performance Test)
  Day 4-7: Frontend 통합 테스트 & QA
```

### Strategy 3: Feature-First (Backend)
**목표**: 기능별 완결도 우선 (데모/통합 테스트 준비 시)

1. **Sprint 1 (Core Backend API):** #006 → #007
2. **Sprint 2 (Document Generation Pipeline):** #008 → #009 → #010
3. **Sprint 3 (Financial & PMF):** #012, #011 (병렬)
4. **Sprint 4 (NFR & Integration):** #013 → #014 → #015 → Frontend 통합 테스트

---

## Dependency Matrix (Backend Issues Only)

| GitHub Issue | Task ID | Depends On | Enables | Parallelizable With | Schedule |
|--------------|---------|------------|---------|---------------------|----------|
| #2 | 006 | BE-INIT | #3, #9, #10 | #4, #8 | 11/27-11/29 |
| #3 | 007 | #2 | #5 | #4, #8 | 11/30-12/02 |
| #4 | 008 | - | #5, #7 | #2, #3, #8 | 11/27-12/01 |
| #5 | 009 | #3, #4 | #6, #11 | #8 | 12/03-12/06 |
| #6 | 010 | #5 | - | #7, #8 | 12/07-12/11 |
| #7 | 011 | #4 | - | #5, #6, #8 | 12/02-12/05 |
| #8 | 012 | - | - | All | 11/27-12/01 |
| #9 | 013 | #2 | - | #10 | 11/30-12/02 |
| #10 | 014 | #2 | - | #9 | 11/30-12/02 |
| #11 | 015 | #5 | - | #6, #7, #8 | 12/07-12/09 |

**Notes**: 
- Frontend 이슈(#001-#005)는 이미 완료되어 의존성에서 제외됨
- GitHub Issue 번호는 실제 저장소의 Issue 번호
- Task ID는 문서상의 원래 번호
- Schedule은 GitHub Projects 로드맵에 설정된 실제 일정

---

## Critical Path Analysis

**최단 완료 경로 (Critical Path):**
```
#2 (006) → #3 (007) → #5 (009) → #6 (010)
11/27      11/30      12/03      12/07      → 12/11 완료
```

**실제 설정된 일정:**
- 시작일: 2025-11-27 (수)
- 완료일: 2025-12-11 (수)
- 총 기간: **15일 (약 3주)**
- 실 작업일: 약 10-12일 (주말 제외)

**예상 소요 기간 (순차 실행 시):**
- #2 (006): 3일
- #3 (007): 3일
- #5 (009): 4일 (AI #4(008)과 동기화 필요)
- #6 (010): 5일
- #11 (015): 3일
- **Total: 18일** (약 3.5주)

**병렬화를 통한 실제 일정 (확정):**
- Wave 1 (11/27-12/01): 5일 - #2, #4, #8 병렬
- Wave 2 (11/30-12/02): 3일 - #3, #9, #10 병렬
- Wave 3 (12/02-12/06): 5일 - #5, #7 병렬
- Wave 4 (12/07-12/11): 5일 - #6, #11 병렬
- **Total: 15일** (약 3주) - 병렬화로 3일 단축

---

## Recommended Execution Order (Backend Only)

### ✅ Prerequisites
Frontend PoC (#001-#005) 완료됨 → Backend 개발 즉시 시작 가능

### For Backend-Focused Team (Recommended)
1. **Phase 1**: #006 → #007 (Core Backend Setup)
2. **Phase 2**: #008 (AI Engine, 병렬 시작 가능)
3. **Phase 3**: #009 → #010 (Document Pipeline)
4. **Phase 4**: #012 (Financial, 병렬 가능)
5. **Phase 5**: #013, #014 (Security & Monitoring, 병렬)
6. **Phase 6**: #015 (Performance Test)

### For Small Backend Team (2-3명)
1. **Sprint 1**: #006 → #007 (Core Backend Setup)
2. **Sprint 2**: #008 (AI Engine) + #012 (Financial, 병렬)
3. **Sprint 3**: #009 → #010 (Document Pipeline)
4. **Sprint 4**: #011 (PMF) + #013, #014 (NFR, 병렬)
5. **Sprint 5**: #015 (Performance Test) + Frontend 통합 테스트

---

## Notes
- **병렬 개발 시 주의사항**:
  - API 인터페이스(Contract)를 사전에 정의 (#007 ↔ #009, #008 ↔ #009)
  - Mock/Stub을 활용하여 의존성 없이 개발 진행
  - 통합 테스트는 각 Wave 완료 후 수행

- **리스크 관리**:
  - #008 (AI Engine): LLM 연동 시 예상치 못한 이슈 가능 → 버퍼 2일 추가 권장
  - #010 (HWP Export): 라이브러리 호환성 이슈 → PoC 선행 권장
  - #012 (Financial): 복잡한 비즈니스 로직 → 단위 테스트 철저히

- **우선순위** (Backend):
  - Must: #006~#010, #013 (총 6개)
  - Should: #011, #014, #015 (총 3개)
  - MVP 최소 범위: #006, #007, #008, #009 (Core API + Document Generation)

---

## GitHub Project Integration

### Project Information
- **Project Name**: MakersRound-Backend-Project
- **Project ID**: `PVT_kwHOBWaOeM4BJJCo`
- **Roadmap View**: https://github.com/users/wild-mental/projects/10/views/4

### Created Issues
All 10 backend issues have been created and scheduled:
- Issues #2-#11 created on 2025-11-26
- Start/Target dates set in GitHub Projects
- Issues tracked in Project Roadmap view

### Issue Management
**Important**: Issue creation and management should be performed by AI Agent using GitHub CLI directly, not through shell scripts. See `.cursor/rules/202-github-issue-handling.mdc` for guidelines.

---

## Version History
- v1.2 (2025-11-26): Added actual schedule, GitHub Project integration, and confirmed dates
- v1.1 (2025-11-26): Updated to reflect EPIC0 (Frontend) completion - Backend focus only
- v1.0 (2025-11-26): Initial execution plan based on INTEGRATED_WBS_DAG.md


```

```markdown
---
description: Guidelines for managing GitHub issues, milestones, projects, and Pull Requests using CLI tools
globs: tasks/**/*.md, .github/**/*.yml
alwaysApply: false
---
# GitHub Issue & Project Management

## Context
- Applies when creating issues from task documents
- Applies when managing project roadmaps via GitHub CLI
- **Applies when creating Pull Requests (refer to Rule 200 for content requirements)**
- **AI Agent must perform issue management directly using `gh` commands, not shell scripts**

## Rules

### 1. Issue Creation (Direct CLI)
- **AI Agent performs**: Read task markdown files and create issues using `gh` CLI directly
- Extract title from first line: `# [#NNN] Title`
- Extract body from rest of content: `tail -n +3 file.md`
- Create issue: `gh issue create --title "..." --body "..."`
- **Do NOT** create shell scripts for batch issue creation
- Create issues one by one with proper error handling

**Example:**
```bash
gh issue create \
  --title "[#006] 프로젝트 생성 및 템플릿 목록 API 구현" \
  --body "$(tail -n +3 issue-006-REQ-FUNC-001-BE-001.md)"
```

### 2. Labels Management
- Labels may not exist in repository initially
- Create issues first without labels if they fail
- Add labels later if needed: `gh label create "name" --description "..." --color "..."`
- Labels are optional for issue creation

### 3. Project & Roadmap Integration
- **List projects**: `gh project list --owner <username>`
- **Get field IDs**: `gh project field-list <number> --owner <username>`
- **Use Project V2 Node IDs** for item manipulation (not integer IDs)
- **Required Fields**: `Start Date` and `End Date` (Type: `DATE`)
- **Set dates**: 
  ```bash
  gh project item-edit \
    --id <ItemID> \
    --project-id <ProjectNodeID> \
    --field-id <FieldID> \
    --date YYYY-MM-DD
  ```

### 4. Batch Operations
- **AI Agent performs batch operations** by iterating through multiple `gh` commands
- Use sequential execution with proper error handling
- Add delays (`sleep 2`) between API calls to avoid rate limiting
- Log success/failure for each operation

### 5. Workflow Integration
- Create issue → Add to project → Set schedule dates
- Verify project item IDs after adding issues
- Use `gh project item-list` to get item IDs for date setting

## Examples

<example what="Creating single issue">
# AI Agent directly executes
gh issue create \
  --title "[#006] API Implementation" \
  --body "$(tail -n +3 tasks/issue-006.md)"
</example>

<example what="Setting schedule dates">
# Get project items first
gh project item-list 10 --owner wild-mental --format json | jq -r '.items[] | "\(.id)\t\(.content.number)"'

# Set start and target dates
gh project item-edit \
  --id PVTI_xxx \
  --project-id PVT_kwHOBWaOeM4BJJCo \
  --field-id PVTF_xxx_START \
  --date 2025-11-27

gh project item-edit \
  --id PVTI_xxx \
  --project-id PVT_kwHOBWaOeM4BJJCo \
  --field-id PVTF_xxx_TARGET \
  --date 2025-11-30
</example>

<example what="Batch issue creation by AI Agent">
# AI Agent iterates through issue files
for i in {006..015}; do
  issue_num=$(printf "%03d" $i)
  issue_file="issue-$issue_num-*.md"
  
  if [ -f $issue_file ]; then
    title=$(head -1 "$issue_file" | sed 's/^# //')
    gh issue create --title "$title" --body "$(tail -n +3 $issue_file)"
    sleep 2  # Rate limiting
  fi
done
</example>

<bad-example what="Using shell scripts">
# ❌ Don't create separate .sh files
./create_backend_issues.sh

# ✅ Instead: AI Agent executes gh commands directly
</bad-example>

<bad-example what="Using integer ID for projects">
# ❌ Wrong: Using integer project ID
gh project item-edit --project-id 7 ...

# ✅ Correct: Using Node ID
gh project item-edit --project-id PVT_kwHOBWaOeM4BJJCo ...
</bad-example>

## AI Agent Responsibilities
1. Read task specification files from `tasks/github-issues/`
2. Execute `gh` commands directly for each issue
3. Handle errors and provide feedback
4. Set project schedules based on execution plan
5. Verify successful creation and report status
6. **Never delegate to shell scripts** - perform operations directly

```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**⚠️ Important**: Issues should be created by AI Agent using direct `gh` commands, not shell scripts.

**Backend 이슈 등록 (AI Agent 방식)**:

AI Agent가 다음과 같이 직접 수행:
```bash
# 개별 이슈 생성
cd tasks/github-issues
gh issue create \
  --title "[#006] 프로젝트 생성 및 템플릿 목록 API 구현" \
  --body "$(tail -n +3 issue-006-REQ-FUNC-001-BE-001.md)"

# AI Agent가 반복문으로 처리 (스크립트 파일 없이)
for i in {006..015}; do
  issue_num=$(printf "%03d" $i)
  issue_file=$(ls issue-$issue_num-*.md 2>/dev/null | head -1)
  if [ -f "$issue_file" ]; then
    title=$(head -1 "$issue_file" | sed 's/^# //')
    gh issue create --title "$title" --body "$(tail -n +3 $issue_file)"
    sleep 2  # API rate limiting
  fi
done
```

**현재 상태 (2025-11-26)**:
- ✅ 모든 Backend 이슈 생성 완료 (Issues #2-#11)
- ✅ GitHub Projects에 추가 완료
- ✅ 로드맵 일정 설정 완료

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## Automation Scripts

### Bulk Issue Creation (Backend Only)

```bash
#!/bin/bash
# scripts/create_backend_issues.sh

# Backend 이슈만 생성 (#006-#015)
for i in {006..015}; do
  issue_file=$(ls tasks/github-issues/issue-$i-*.md 2>/dev/null)
  if [ -f "$issue_file" ]; then
    echo "Creating issue from $issue_file"
    gh issue create -F "$issue_file"
    sleep 2  # API rate limiting
  fi
done

echo "✅ Backend issues (#006-#015) created successfully"
echo "ℹ️  Frontend issues (#001-#005) skipped (already completed)"
```

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **Frontend 통합**: Frontend가 완료되었으므로 API Contract를 준수하며 개발
2. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
3. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
4. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
5. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
6. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록
7. **통합 테스트**: 각 API 완료 시 Frontend와 즉시 통합 테스트 진행

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.1 (Frontend EPIC0 완료 반영)


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # 🔄 Backend Core Issues (현재 범위)
├── issue-007-REQ-FUNC-002-BE-001.md
├── issue-008-REQ-FUNC-003-AI-001.md # 🔄 AI Engine Issues
├── issue-009-REQ-FUNC-003-BE-001.md
├── issue-010-REQ-FUNC-011-BE-001.md
├── issue-011-REQ-FUNC-008-AI-001.md
├── issue-012-REQ-FUNC-012-BE-001.md
├── issue-013-REQ-NF-006-SEC-001.md  # 🔄 Non-Functional Issues
├── issue-014-REQ-NF-012-OPS-001.md
└── issue-015-REQ-NF-001-PERF-001.md
```

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**⚠️ Important**: Issues should be created by AI Agent using direct `gh` commands, not shell scripts.

**Backend 이슈 등록 (AI Agent 방식)**:

AI Agent가 다음과 같이 직접 수행:
```bash
# 개별 이슈 생성
cd tasks/github-issues
gh issue create \
  --title "[#006] 프로젝트 생성 및 템플릿 목록 API 구현" \
  --body "$(tail -n +3 issue-006-REQ-FUNC-001-BE-001.md)"

# AI Agent가 반복문으로 처리 (스크립트 파일 없이)
for i in {006..015}; do
  issue_num=$(printf "%03d" $i)
  issue_file=$(ls issue-$issue_num-*.md 2>/dev/null | head -1)
  if [ -f "$issue_file" ]; then
    title=$(head -1 "$issue_file" | sed 's/^# //')
    gh issue create --title "$title" --body "$(tail -n +3 $issue_file)"
    sleep 2  # API rate limiting
  fi
done
```

**현재 상태 (2025-11-26)**:
- ✅ 모든 Backend 이슈 생성 완료 (Issues #2-#11)
- ✅ GitHub Projects에 추가 완료
- ✅ 로드맵 일정 설정 완료

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## AI Agent Automation

### Issue Management Guidelines

**⚠️ No Shell Scripts**: Issue management is performed directly by AI Agent using `gh` CLI commands.

**AI Agent Responsibilities**:
1. Read task files from `tasks/github-issues/`
2. Execute `gh issue create` for each issue
3. Add issues to GitHub Projects
4. Set start/target dates on project roadmap
5. Handle errors and provide feedback
6. Verify successful operations

**Example AI Agent Workflow**:
```bash
# 1. Create issue
gh issue create --title "..." --body "$(tail -n +3 issue-file.md)"

# 2. Get project item ID
gh project item-list 10 --owner wild-mental --format json

# 3. Set schedule dates
gh project item-edit --id PVTI_xxx --project-id PVT_xxx --field-id PVTF_xxx --date YYYY-MM-DD
```

See `.cursor/rules/202-github-issue-handling.mdc` for detailed guidelines.

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **Frontend 통합**: Frontend가 완료되었으므로 API Contract를 준수하며 개발
2. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
3. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
4. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
5. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
6. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록
7. **통합 테스트**: 각 API 완료 시 Frontend와 즉시 통합 테스트 진행

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.1 (Frontend EPIC0 완료 반영)


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── CHANGELOG.md                       # 변경 이력
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # ✅ Backend Core Issues (GitHub #2)
├── issue-007-REQ-FUNC-002-BE-001.md # ✅ (GitHub #3)
├── issue-008-REQ-FUNC-003-AI-001.md # ✅ AI Engine Issues (GitHub #4)
├── issue-009-REQ-FUNC-003-BE-001.md # ✅ (GitHub #5)
├── issue-010-REQ-FUNC-011-BE-001.md # ✅ (GitHub #6)
├── issue-011-REQ-FUNC-008-AI-001.md # ✅ (GitHub #7)
├── issue-012-REQ-FUNC-012-BE-001.md # ✅ (GitHub #8)
├── issue-013-REQ-NF-006-SEC-001.md  # ✅ Non-Functional Issues (GitHub #9)
├── issue-014-REQ-NF-012-OPS-001.md  # ✅ (GitHub #10)
└── issue-015-REQ-NF-001-PERF-001.md # ✅ (GitHub #11)
```

**Note**: All backend issues have been created (GitHub #2-#11) and scheduled in [GitHub Projects](https://github.com/users/wild-mental/projects/10/views/4).

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**⚠️ Important**: Issues should be created by AI Agent using direct `gh` commands, not shell scripts.

**Backend 이슈 등록 (AI Agent 방식)**:

AI Agent가 다음과 같이 직접 수행:
```bash
# 개별 이슈 생성
cd tasks/github-issues
gh issue create \
  --title "[#006] 프로젝트 생성 및 템플릿 목록 API 구현" \
  --body "$(tail -n +3 issue-006-REQ-FUNC-001-BE-001.md)"

# AI Agent가 반복문으로 처리 (스크립트 파일 없이)
for i in {006..015}; do
  issue_num=$(printf "%03d" $i)
  issue_file=$(ls issue-$issue_num-*.md 2>/dev/null | head -1)
  if [ -f "$issue_file" ]; then
    title=$(head -1 "$issue_file" | sed 's/^# //')
    gh issue create --title "$title" --body "$(tail -n +3 $issue_file)"
    sleep 2  # API rate limiting
  fi
done
```

**현재 상태 (2025-11-26)**:
- ✅ 모든 Backend 이슈 생성 완료 (Issues #2-#11)
- ✅ GitHub Projects에 추가 완료
- ✅ 로드맵 일정 설정 완료

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## AI Agent Automation

### Issue Management Guidelines

**⚠️ No Shell Scripts**: Issue management is performed directly by AI Agent using `gh` CLI commands.

**AI Agent Responsibilities**:
1. Read task files from `tasks/github-issues/`
2. Execute `gh issue create` for each issue
3. Add issues to GitHub Projects
4. Set start/target dates on project roadmap
5. Handle errors and provide feedback
6. Verify successful operations

**Example AI Agent Workflow**:
```bash
# 1. Create issue
gh issue create --title "..." --body "$(tail -n +3 issue-file.md)"

# 2. Get project item ID
gh project item-list 10 --owner wild-mental --format json

# 3. Set schedule dates
gh project item-edit --id PVTI_xxx --project-id PVT_xxx --field-id PVTF_xxx --date YYYY-MM-DD
```

See `.cursor/rules/202-github-issue-handling.mdc` for detailed guidelines.

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **Frontend 통합**: Frontend가 완료되었으므로 API Contract를 준수하며 개발
2. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
3. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
4. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
5. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
6. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록
7. **통합 테스트**: 각 API 완료 시 Frontend와 즉시 통합 테스트 진행

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

**Last Updated**: 2025-11-26  
**Version**: 1.1 (Frontend EPIC0 완료 반영)


```

```markdown
# GitHub Issues for Bizplan Backend

## ✅ Status Update
**Frontend PoC (EPIC 0) - COMPLETED**
- #001 ~ #005 이슈들은 별도 프로젝트에서 완료됨
- 완료된 이슈 파일들은 `completed/` 폴더로 이동
- **이 폴더의 이슈들은 Backend 작업에 집중합니다 (#006-#015)**

## Overview
이 폴더에는 `docs/INTEGRATED_WBS_DAG.md`의 의존 관계를 기반으로 작성된 GitHub Issue 형태의 작업 명세가 포함되어 있습니다.

## Structure

```
github-issues/
├── README.md                          # 이 파일
├── ISSUE_EXECUTION_PLAN.md           # 이슈 실행 순서 및 병렬 개발 전략
├── CHANGELOG.md                       # 변경 이력
├── completed/                         # ✅ 완료된 이슈들
│   ├── README.md                     # 완료 이슈 설명
│   ├── issue-001-EPIC0-FE-001.md    # Frontend PoC Issues (완료)
│   ├── issue-002-EPIC0-FE-002.md
│   ├── issue-003-EPIC0-FE-003.md
│   ├── issue-004-EPIC0-FE-004.md
│   └── issue-005-EPIC0-FE-005.md
├── issue-006-REQ-FUNC-001-BE-001.md # ✅ Backend Core Issues (GitHub #2)
├── issue-007-REQ-FUNC-002-BE-001.md # ✅ (GitHub #3)
├── issue-008-REQ-FUNC-003-AI-001.md # ✅ AI Engine Issues (GitHub #4)
├── issue-009-REQ-FUNC-003-BE-001.md # ✅ (GitHub #5)
├── issue-010-REQ-FUNC-011-BE-001.md # ✅ (GitHub #6)
├── issue-011-REQ-FUNC-008-AI-001.md # ✅ (GitHub #7)
├── issue-012-REQ-FUNC-012-BE-001.md # ✅ (GitHub #8)
├── issue-013-REQ-NF-006-SEC-001.md  # ✅ Non-Functional Issues (GitHub #9)
├── issue-014-REQ-NF-012-OPS-001.md  # ✅ (GitHub #10)
└── issue-015-REQ-NF-001-PERF-001.md # ✅ (GitHub #11)
```

**Note**: All backend issues have been created (GitHub #2-#11) and scheduled in [GitHub Projects](https://github.com/users/wild-mental/projects/10/views/4).

## Issue Numbering Convention

이슈 번호는 의존 관계를 고려한 실행 가능 순서를 반영합니다:

| Range | Phase | Description | Status |
|-------|-------|-------------|--------|
| #001-#005 | Phase 1 | Frontend PoC (EPIC 0) | ✅ **COMPLETED** |
| #006-#010 | Phase 2 | Core Backend & AI (EPIC 1) | 🔄 **IN SCOPE** |
| #011-#012 | Phase 3 | Special Features (EPIC 2) | 🔄 **IN SCOPE** |
| #013-#015 | Phase 4 | Non-Functional Requirements (EPIC 3) | 🔄 **IN SCOPE** |

## How to Use

### 1. Creating Issues in GitHub

**⚠️ Important**: Issues should be created by AI Agent using direct `gh` commands, not shell scripts.

**Backend 이슈 등록 (AI Agent 방식)**:

AI Agent가 다음과 같이 직접 수행:
```bash
# 개별 이슈 생성
cd tasks/github-issues
gh issue create \
  --title "[#006] 프로젝트 생성 및 템플릿 목록 API 구현" \
  --body "$(tail -n +3 issue-006-REQ-FUNC-001-BE-001.md)"

# AI Agent가 반복문으로 처리 (스크립트 파일 없이)
for i in {006..015}; do
  issue_num=$(printf "%03d" $i)
  issue_file=$(ls issue-$issue_num-*.md 2>/dev/null | head -1)
  if [ -f "$issue_file" ]; then
    title=$(head -1 "$issue_file" | sed 's/^# //')
    gh issue create --title "$title" --body "$(tail -n +3 $issue_file)"
    sleep 2  # API rate limiting
  fi
done
```

**현재 상태 (2025-11-26)**:
- ✅ 모든 Backend 이슈 생성 완료 (Issues #2-#11)
- ✅ GitHub Projects에 추가 완료
- ✅ 로드맵 일정 설정 완료

### 2. Adding Labels

각 이슈 파일 상단에 명시된 라벨을 추가:

```bash
# 예시: #001 이슈에 라벨 추가
gh issue edit 1 --add-label "epic:EPIC_0_FE_PROTOTYPE,type:frontend,priority:Must,effort:S"
```

### 3. Setting Milestones

Phase별로 마일스톤 설정:

```bash
# 마일스톤 생성
gh milestone create "Phase 1: Frontend PoC" --due-date 2025-12-15
gh milestone create "Phase 2: Core Backend & AI" --due-date 2026-01-15
gh milestone create "Phase 3: Special Features" --due-date 2026-01-31
gh milestone create "Phase 4: NFR" --due-date 2026-02-15

# 이슈에 마일스톤 할당
gh issue edit 1 --milestone "Phase 1: Frontend PoC"
```

### 4. Creating Project Board

GitHub Projects V2를 사용한 칸반 보드 구성:

```bash
# Backend 프로젝트 생성
gh project create --title "Bizplan Backend Development" --owner @me

# Backend 이슈들만 프로젝트에 추가 (#006-#015)
for i in {6..15}; do
  gh project item-add <PROJECT_ID> --url https://github.com/<OWNER>/<REPO>/issues/$i
done
```

### 5. Tracking Dependencies

각 이슈의 "Dependencies" 섹션을 참고하여:

```markdown
## Dependencies
- #006 (REQ-FUNC-001-BE-001)
- #008 (REQ-FUNC-003-AI-001)
```

GitHub에서 Tasklist 형태로 관리:

```markdown
## Blocked By
- [ ] #006
- [ ] #008
```

## Execution Strategies

자세한 실행 전략은 `ISSUE_EXECUTION_PLAN.md`를 참조하세요.

### Quick Reference (Backend Only)

**Prerequisites:**
- ✅ Frontend PoC (#001-#005) 완료됨
- ✅ API Contract 확정됨

**Critical Path (순차 실행 필수):**
```
#006 → #007 → #009 → #010 → #015
```

**Maximum Parallelization (4-5명 Backend 팀):**
- Wave 1: #006, #008, #012 (병렬)
- Wave 2: #007, #013, #014 (병렬)
- Wave 3: #009, #011 (병렬)
- Wave 4: #010 → #015 (순차)

**Small Backend Team (2-3명):**
- Sprint 1: #006 → #007
- Sprint 2: #008 + #012 (병렬)
- Sprint 3: #009 → #010
- Sprint 4: #011 + #013 + #014 (병렬)
- Sprint 5: #015 + Frontend 통합 테스트

## Issue Template Structure

각 이슈는 다음 구조를 따릅니다:

```markdown
# [#NNN] Task Title

## Labels
`epic:XXX`, `type:YYY`, `priority:ZZZ`

## Description
간략한 설명

## Scope
- 포함 항목
- Out of Scope

## Requirements
상세 요구사항

## Technical Stack
사용 기술

## API Specification (해당 시)
Request/Response 예시

## Implementation Steps
1. Step 1
2. Step 2

## Acceptance Criteria
- [ ] 완료 조건 1
- [ ] 완료 조건 2

## Dependencies
- #XXX (Issue Title)

## Parallelizable With (해당 시)
- #YYY

## Related Requirements
REQ-FUNC-XXX
```

## Label Taxonomy

### Epic Labels
- `epic:EPIC_0_FE_PROTOTYPE` - Frontend PoC
- `epic:EPIC_1_PASS_THE_TEST` - Core Features
- `epic:EPIC_2_AVOID_FAILURE` - Special Features
- `epic:EPIC_3_NFR` - Non-Functional Requirements

### Type Labels
- `type:frontend` - React/TypeScript 작업
- `type:backend` - Spring Boot/Java 작업
- `type:ai` - Python/LangChain 작업
- `type:poc` - Proof of Concept
- `type:security` - 보안 관련
- `type:testing` - 테스트 관련
- `type:infra` - 인프라/DevOps

### Component Labels
- `component:api` - REST API 개발
- `component:core` - 비즈니스 로직
- `component:ai-engine` - AI/LLM 엔진
- `component:security` - 보안 컴포넌트
- `component:monitoring` - 모니터링/로깅
- `component:test` - 테스트 인프라

### Priority Labels
- `priority:Must` - MVP 필수 기능
- `priority:Should` - 권장 기능
- `priority:Could` - 선택 기능

### Effort Labels
- `effort:S` - Small (1-3일)
- `effort:M` - Medium (3-5일)
- `effort:L` - Large (5-10일)

## Integration with Workflow

### Branch Naming
이슈 번호를 브랜치명에 포함:

```bash
git checkout -b feature/#001-project-wizard-layout
git checkout -b feature/#006-project-api
git checkout -b fix/#009-llm-timeout
```

### Commit Convention
이슈 번호를 커밋 메시지에 포함:

```bash
git commit -m "feat(#001): implement project creation modal"
git commit -m "fix(#009): add timeout handling for LLM calls"
```

### Pull Request Template

```markdown
## Related Issue
Closes #XXX

## Changes
- 변경 사항 1
- 변경 사항 2

## Testing
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Manual Testing

## Screenshots (if applicable)
```

## AI Agent Automation

### Issue Management Guidelines

**⚠️ No Shell Scripts**: Issue management is performed directly by AI Agent using `gh` CLI commands.

**AI Agent Responsibilities**:
1. Read task files from `tasks/github-issues/`
2. Execute `gh issue create` for each issue
3. Add issues to GitHub Projects
4. Set start/target dates on project roadmap
5. Handle errors and provide feedback
6. Verify successful operations

**Example AI Agent Workflow**:
```bash
# 1. Create issue
gh issue create --title "..." --body "$(tail -n +3 issue-file.md)"

# 2. Get project item ID
gh project item-list 10 --owner wild-mental --format json

# 3. Set schedule dates
gh project item-edit --id PVTI_xxx --project-id PVT_xxx --field-id PVTF_xxx --date YYYY-MM-DD
```

See `.cursor/rules/202-github-issue-handling.mdc` for detailed guidelines.

### Issue Status Sync

```bash
#!/bin/bash
# scripts/sync_issue_status.sh

# 완료된 이슈를 자동으로 닫기
gh issue list --state open --json number,title,labels | \
  jq -r '.[] | select(.labels[].name == "status:done") | .number' | \
  xargs -I {} gh issue close {}
```

## Best Practices

1. **Frontend 통합**: Frontend가 완료되었으므로 API Contract를 준수하며 개발
2. **의존성 확인**: 이슈 시작 전 Dependencies 섹션 확인
3. **병렬 작업**: Parallelizable With 섹션을 참고하여 효율적으로 작업 분배
4. **Acceptance Criteria**: 모든 항목 체크 후 이슈 종료
5. **블로커 관리**: Blocked 상태 이슈는 즉시 팀에 공유
6. **문서 업데이트**: 구현 중 발견된 제약사항은 이슈에 코멘트로 기록
7. **통합 테스트**: 각 API 완료 시 Frontend와 즉시 통합 테스트 진행

## References

- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md) - 원본 WBS 및 의존성 그래프
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md) - 상세 실행 계획
- [AI_AGENT_TASKS_USAGE_GUIDE.md](../../docs/AI_AGENT_TASKS_USAGE_GUIDE.md) - AI Agent 작업 가이드

## Questions?

Issues 관련 질문이 있으면:
1. `ISSUE_EXECUTION_PLAN.md`의 FAQ 섹션 확인
2. GitHub Discussions에 질문 등록
3. 프로젝트 리드에게 문의

---

## Current Status (2025-11-26)

### Completed
- ✅ All 10 backend issues created (GitHub #2-#11)
- ✅ Issues added to GitHub Projects
- ✅ Schedule dates set in roadmap view
- ✅ Project timeline: 2025-11-27 ~ 2025-12-11

### GitHub Resources
- **Issues**: https://github.com/wild-mental/makersround-backend/issues
- **Project Board**: https://github.com/users/wild-mental/projects/10
- **Roadmap View**: https://github.com/users/wild-mental/projects/10/views/4

---

**Last Updated**: 2025-11-26  
**Version**: 1.2 (Issues created, scheduled, AI Agent automation established)


```

```markdown
# GitHub Issues 변경 이력

## v1.2 (2025-11-26)

### 🎉 주요 변경사항: Issues 생성 완료 및 일정 설정

**변경 이유**: Backend Issues 생성 완료 및 GitHub Projects 로드맵 일정 설정 완료

### 완료된 작업

#### GitHub Issues 생성 (10개)
- Issue #2 (Task 006) - 프로젝트 생성 API
- Issue #3 (Task 007) - Wizard 답변 저장 API
- Issue #4 (Task 008) - LLM 문서 생성 엔진
- Issue #5 (Task 009) - 문서 생성 오케스트레이션
- Issue #6 (Task 010) - HWP/PDF 내보내기
- Issue #7 (Task 011) - PMF 진단 엔진
- Issue #8 (Task 012) - 재무 계산 엔진
- Issue #9 (Task 013) - 보안 구성
- Issue #10 (Task 014) - 모니터링 구축
- Issue #11 (Task 015) - 성능 테스트

#### GitHub Projects 일정 설정
- Start Date 및 Target Date 필드 설정
- 전체 일정: 2025-11-27 ~ 2025-12-11 (약 3주)
- Roadmap View에서 시각적 확인 가능

#### 프로세스 개선
- **Shell Script 제거**: `create_backend_issues.sh` 삭제
- **AI Agent 직접 제어**: Issue 관리를 AI Agent가 `gh` 명령어로 직접 수행
- **Rule 업데이트**: `.cursor/rules/202-github-issue-handling.mdc` 대폭 개선

### 문서 업데이트

#### ISSUE_EXECUTION_PLAN.md
- [x] 실제 생성된 GitHub Issue 번호 반영 (#2-#11)
- [x] 확정된 일정 추가 (2025-11-27 ~ 2025-12-11)
- [x] GitHub Projects 정보 추가
- [x] Dependency Matrix에 Schedule 컬럼 추가
- [x] Critical Path에 실제 날짜 반영
- [x] Version 1.2로 업데이트

#### .cursor/rules/202-github-issue-handling.mdc
- [x] AI Agent 직접 제어 방식 명시
- [x] Shell Script 사용 금지 규칙 추가
- [x] Labels 관리 가이드라인 추가
- [x] Batch Operations 수행 방법 설명
- [x] 상세한 예제 코드 추가
- [x] AI Agent Responsibilities 섹션 신설

#### README.md
- [x] 현재 상태 섹션 추가 (Issues 생성 완료)
- [x] Shell Script 관련 내용 제거
- [x] AI Agent Automation 섹션으로 대체
- [x] GitHub Resources 링크 추가
- [x] Version 1.2로 업데이트

#### 파일 삭제
- [x] `create_backend_issues.sh` 제거 (더 이상 사용하지 않음)

### 새로운 워크플로우

**Before (v1.1)**:
```bash
# Shell script 실행
./create_backend_issues.sh
```

**After (v1.2)**:
```bash
# AI Agent가 직접 gh 명령어 실행
for i in {006..015}; do
  gh issue create --title "..." --body "..."
  sleep 2
done
```

### GitHub Project 정보

- **Project Name**: MakersRound-Backend-Project
- **Project ID**: `PVT_kwHOBWaOeM4BJJCo` (Node ID)
- **Number**: 10
- **Roadmap View**: https://github.com/users/wild-mental/projects/10/views/4

### 설정된 일정

| Wave | Issues | 기간 | 작업 |
|------|--------|------|------|
| Wave 1 | #2, #4, #8 | 11/27-12/01 | Backend Core, AI, Financial (병렬) |
| Wave 2 | #3, #9, #10 | 11/30-12/02 | Wizard API, Security, Monitoring (병렬) |
| Wave 3 | #5, #7 | 12/02-12/06 | Orchestration, PMF (병렬) |
| Wave 4 | #6, #11 | 12/07-12/11 | Export, Performance Test (병렬) |

---

## v1.1 (2025-11-26)

### ✅ 주요 변경사항: Frontend PoC 완료 반영

**변경 이유**: EPIC0 (Frontend PoC) 작업들이 별도 프로젝트에서 완료됨

### 파일 구조 변경

#### 이동된 파일 (5개)
완료된 Frontend 이슈들을 `completed/` 폴더로 이동:
- `issue-001-EPIC0-FE-001.md` → `completed/issue-001-EPIC0-FE-001.md`
- `issue-002-EPIC0-FE-002.md` → `completed/issue-002-EPIC0-FE-002.md`
- `issue-003-EPIC0-FE-003.md` → `completed/issue-003-EPIC0-FE-003.md`
- `issue-004-EPIC0-FE-004.md` → `completed/issue-004-EPIC0-FE-004.md`
- `issue-005-EPIC0-FE-005.md` → `completed/issue-005-EPIC0-FE-005.md`

#### 추가된 파일 (2개)
- `completed/README.md` - 완료된 Frontend 이슈 설명
- `create_backend_issues.sh` - Backend 이슈 일괄 생성 스크립트

### 문서 업데이트

#### ISSUE_EXECUTION_PLAN.md
- [x] Status Update 섹션 추가 (Frontend 완료 명시)
- [x] Phase 1 (Frontend PoC) 완료 상태로 표시
- [x] Gantt 차트에서 Frontend 완료 표시
- [x] 인력 배치 계획 Backend 중심으로 재구성 (6명 → 4-5명)
- [x] Sequential Strategy Backend 중심으로 재작성
- [x] Feature-First Strategy Backend 중심으로 재작성
- [x] Dependency Matrix에서 Frontend 이슈 제거
- [x] MVP 최소 범위 업데이트 (11개 → 6개)
- [x] 우선순위 재정의 (Backend 기준)

#### README.md
- [x] Status Update 섹션 추가
- [x] 폴더 구조 업데이트 (completed 폴더 반영)
- [x] Issue Numbering Convention에 완료 상태 추가
- [x] Creating Issues in GitHub 섹션 Backend 전용으로 수정
- [x] Creating Project Board Backend 전용으로 수정
- [x] Quick Reference Backend 전용으로 재작성
- [x] Bulk Issue Creation 스크립트 Backend 전용으로 수정
- [x] Best Practices에 Frontend 통합 관련 항목 추가
- [x] Version 1.1로 업데이트

### 영향 받은 이슈들

#### 변경 불필요한 이슈 (#006-#015)
Backend 이슈들은 원래부터 Frontend와 독립적이므로 내용 변경 불필요:
- #006-#010: Core Backend & AI (EPIC 1)
- #011-#012: Special Features (EPIC 2)
- #013-#015: Non-Functional Requirements (EPIC 3)

**이유**: 
- Backend API는 Frontend 완료 여부와 무관하게 API Contract 기반으로 개발
- 의존성 그래프에서 Frontend → Backend 의존성 없음
- 통합 테스트 시점만 앞당겨질 뿐, 개발 범위/내용 동일

### 실행 계획 변경 사항

#### Before (v1.0)
```
Phase 1: Frontend PoC (#001-#005) → 약 2주
Phase 2: Backend Core (#006-#010) → 약 3주
Phase 3: Special Features (#011-#012) → 약 2주
Phase 4: NFR (#013-#015) → 약 1주
Total: 약 8주
```

#### After (v1.1)
```
✅ Phase 1: Frontend PoC - 완료
Phase 2: Backend Core (#006-#010) → 약 3주
Phase 3: Special Features (#011-#012) → 약 2주  
Phase 4: NFR (#013-#015) → 약 1주
Total: 약 6주 (Frontend 통합 테스트 포함)
```

### GitHub Issues 생성 방법

#### v1.0 (전체 생성)
```bash
for i in {001..015}; do
  gh issue create -F "issue-$i-*.md"
done
```

#### v1.1 (Backend만 생성)
```bash
# 방법 1: 스크립트 사용 (권장)
./create_backend_issues.sh

# 방법 2: 수동 생성
for i in {006..015}; do
  gh issue create -F "issue-0$i-*.md"
done
```

### 마이그레이션 가이드

기존에 v1.0 기반으로 작업 중이었다면:

1. **Frontend 이슈 처리**:
   ```bash
   # GitHub에 이미 생성된 #001-#005 이슈가 있다면
   for i in {1..5}; do
     gh issue close $i --comment "✅ 별도 프로젝트에서 완료됨"
   done
   ```

2. **Backend 이슈 재확인**:
   - #006-#015 이슈들의 내용은 변경 없음
   - Dependencies 섹션 확인 (Frontend 의존성 없음)

3. **프로젝트 보드 업데이트**:
   - Frontend 이슈들을 "Completed" 컬럼으로 이동
   - 또는 프로젝트에서 제거

### 테스트 체크리스트

- [x] Frontend 이슈 파일 5개가 `completed/` 폴더로 이동됨
- [x] Backend 이슈 파일 10개가 루트에 남아있음
- [x] `completed/README.md` 생성됨
- [x] `create_backend_issues.sh` 생성 및 실행 권한 설정됨
- [x] `ISSUE_EXECUTION_PLAN.md` 업데이트됨
- [x] `README.md` 업데이트됨
- [x] Dependency Matrix에 Frontend 이슈 제거됨
- [x] 모든 문서에 v1.1 버전 명시됨

---

## v1.0 (2025-11-26)

### 초기 버전
- 15개 이슈 생성 (Frontend 5개, Backend 10개)
- INTEGRATED_WBS_DAG.md 기반 의존성 그래프 작성
- 3가지 실행 전략 제시
- Gantt 차트 및 Dependency Matrix 작성

---

## 참고 링크
- [INTEGRATED_WBS_DAG.md](../../docs/INTEGRATED_WBS_DAG.md)
- [ISSUE_EXECUTION_PLAN.md](./ISSUE_EXECUTION_PLAN.md)
- [README.md](./README.md)


```

완료되었습니다! 이제 변경사항을 커밋하고 푸시하겠습니다.

완료되었습니다! ✨

## 📋 작업 완료 요약

### ✅ 완료된 작업

#### 1. ISSUE_EXECUTION_PLAN.md 업데이트 (v1.2)
- **실제 GitHub Issue 번호** 반영 (#2-#11)
- **확정된 일정** 추가 (2025-11-27 ~ 2025-12-11)
- **Dependency Matrix** 에 Schedule 컬럼 추가
- **Critical Path** 에 실제 날짜 반영
- **GitHub Projects 정보** 추가

#### 2. Rule 업데이트 (.cursor/rules/202-github-issue-handling.mdc)
**주요 변경사항:**
```markdown
- AI Agent가 gh 명령어로 직접 제어
- Shell Script 사용 금지 명시
- Labels 관리 가이드라인 추가
- Batch Operations 수행 방법 설명
- AI Agent Responsibilities 섹션 신설
```

**새로운 원칙:**
- ❌ Shell Script 생성 금지
- ✅ AI Agent가 직접 `gh` 명령어 실행
- ✅ 실시간 에러 처리 및 피드백
- ✅ 더 유연하고 유지보수 쉬운 접근

#### 3. 파일 제거
- ❌ `create_backend_issues.sh` 삭제 (더 이상 불필요)

#### 4. 문서 업데이트
- **README.md**: AI Agent Automation 섹션으로 대체
- **CHANGELOG.md**: v1.2 변경사항 상세 기록
- 모든 문서에 실제 완료 상태 반영

### 📊 현재 상태

**GitHub Issues (생성 완료)**:
| GitHub Issue | Task ID | Title | Schedule |
|--------------|---------|-------|----------|
| #2 | 006 | 프로젝트 생성 API | 11/27-11/29 |
| #3 | 007 | Wizard 답변 저장 API | 11/30-12/02 |
| #4 | 008 | LLM 문서 생성 엔진 | 11/27-12/01 |
| #5 | 009 | 문서 생성 오케스트레이션 | 12/03-12/06 |
| #6 | 010 | HWP/PDF 내보내기 | 12/07-12/11 |
| #7 | 011 | PMF 진단 엔진 | 12/02-12/05 |
| #8 | 012 | 재무 계산 엔진 | 11/27-12/01 |
| #9 | 013 | 보안 구성 | 11/30-12/02 |
| #10 | 014 | 모니터링 구축 | 11/30-12/02 |
| #11 | 015 | 성능 테스트 | 12/07-12/09 |

**프로젝트 타임라인**:
- 시작: 2025-11-27 (수)
- 완료: 2025-12-11 (수)
- 기간: 약 3주 (병렬화로 단축)

### 🔄 Git 작업

**커밋**:
```bash
docs(github-issues): update workflow to AI Agent direct control and add actual schedules

- Update ISSUE_EXECUTION_PLAN.md with actual GitHub Issue numbers and schedules
- Enhance .cursor/rules/202-github-issue-handling.mdc for AI Agent operations
- Remove create_backend_issues.sh (replaced by AI Agent direct control)
- Update README.md and CHANGELOG.md

Version: v1.2
```

**Pull Request**:
- PR #12: https://github.com/wild-mental/makersround-backend/pull/12
- Draft PR 생성 완료
- 5 files changed, 398 insertions(+), 206 deletions(-)

### 📚 주요 문서 링크

- **ISSUE_EXECUTION_PLAN.md**: `tasks/github-issues/ISSUE_EXECUTION_PLAN.md`
- **Rule**: `.cursor/rules/202-github-issue-handling.mdc`
- **README**: `tasks/github-issues/README.md`
- **CHANGELOG**: `tasks/github-issues/CHANGELOG.md`

### 🎯 다음 단계

1. **PR 리뷰 & Merge**
   ```bash
   gh pr view 12
   gh pr merge 12
   ```

2. **개발 시작**
   - Critical Path: #2 → #3 → #5 → #6
   - Wave 1 (11/27): #2, #4, #8 병렬 시작

3. **향후 Issue 관리**
   - AI Agent가 Rule에 따라 직접 `gh` 명령어 실행
   - Shell Script 없이 유연하게 대응
   - 실시간 피드백으로 문제 즉시 해결

모든 작업이 완료되었습니다! 🎉

```markdown
- AI Agent가 gh 명령어로 직접 제어
- Shell Script 사용 금지 명시
- Labels 관리 가이드라인 추가
- Batch Operations 수행 방법 설명
- AI Agent Responsibilities 섹션 신설
```

```shellscript
docs(github-issues): update workflow to AI Agent direct control and add actual schedules

- Update ISSUE_EXECUTION_PLAN.md with actual GitHub Issue numbers and schedules
- Enhance .cursor/rules/202-github-issue-handling.mdc for AI Agent operations
- Remove create_backend_issues.sh (replaced by AI Agent direct control)
- Update README.md and CHANGELOG.md

Version: v1.2
```

```shellscript
   gh pr view 12
   gh pr merge 12
```

