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

