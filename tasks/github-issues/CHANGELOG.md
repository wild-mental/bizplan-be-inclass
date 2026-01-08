# GitHub Issues 변경 이력

## v1.3 (2025-11-28)

### ✅ 주요 변경사항: 완료된 Backend 이슈 파일 정리

**변경 이유**: GitHub에서 완료된 Backend 이슈들을 `completed/` 폴더로 이동하여 정리

### 완료된 작업

#### 완료된 Backend 이슈 파일 이동
- ✅ `issue-006-REQ-FUNC-001-BE-001.md` → `completed/` 폴더로 이동
  - GitHub Issue: #2 (CLOSED)
  - PR: #13 (MERGED - 2025-11-28)
  - 완료일: 2025-11-28

- ✅ `issue-008-REQ-FUNC-003-AI-001.md` → `completed/` 폴더로 이동
  - GitHub Issue: #4 (CLOSED)
  - PR: #14 (MERGED - 2025-11-28)
  - 완료일: 2025-11-28

#### 문서 업데이트
- [x] `completed/README.md` 업데이트 - Backend 완료 이슈 섹션 추가
- [x] `README.md` 업데이트 - 완료된 이슈 정보 반영 및 버전 업데이트
- [x] `CHANGELOG.md` 업데이트 - v1.3 버전 추가

### 현재 진행 상황

**완료된 이슈 (2개)**:
- Issue #2 (Task #006) - 프로젝트 생성 및 템플릿 목록 API 구현
- Issue #4 (Task #008) - 사업계획서 생성 LLM 엔진 및 프롬프트 구현

**진행 중인 이슈 (8개)**:
- Issue #3 (Task #007) - Wizard 단계별 답변 저장/조회 API 구현
- Issue #5-#11 (Task #009-#015) - 나머지 Backend 이슈들

### 파일 구조 변경

**Before (v1.2)**:
```
github-issues/
├── issue-006-REQ-FUNC-001-BE-001.md  # 진행 중
├── issue-008-REQ-FUNC-003-AI-001.md  # 진행 중
└── completed/
    └── (Frontend 이슈만 존재)
```

**After (v1.3)**:
```
github-issues/
├── issue-007-REQ-FUNC-002-BE-001.md  # 진행 중
├── issue-009-REQ-FUNC-003-BE-001.md  # 진행 중
└── completed/
    ├── issue-006-REQ-FUNC-001-BE-001.md  # ✅ 완료
    └── issue-008-REQ-FUNC-003-AI-001.md  # ✅ 완료
```

---

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

