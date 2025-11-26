# AI Agent Task Definition Usage Guide

## 1. Task Definition Structure
본 프로젝트의 Task 정의서는 **Human-readable Context**와 **Machine-readable Spec (YAML)**이 결합된 형태입니다.

### 파일 구조
- `tasks/functional/REQ-FUNC-XXX.md`: 기능 요구사항 구현 Task
- `tasks/non-functional/REQ-NF-XXX.md`: 비기능 요구사항(성능, 보안 등) 구현 Task
- `tasks/functional/EPIC0-FE-XXX.md`: 프론트엔드 PoC용 Task

### YAML 필드 설명
```yaml
task_id: "고유 식별자 (SRS ID 포함)"
title: "Task 제목"
summary: "요약 설명"
type: "functional | non_functional"
epic: "상위 EPIC ID"
req_ids: ["관련된 SRS REQ ID 목록"]
agent_profile: ["frontend | backend | infra | ml"] # 적합한 에이전트 유형
parallelizable: true | false # 병렬 실행 가능 여부
dependencies: ["선행 Task ID 목록"]
inputs: { description, fields } # 작업 시작 시 필요한 정보/상태
outputs: { description, success } # 작업 완료 산출물
steps_hint: ["에이전트가 참고할 구현 단계 힌트"]
```

## 2. Orchestration Strategy (How to use)

### A. 단일 Task 실행
1. 에이전트에게 `.md` 파일 전체를 Prompt로 제공합니다.
2. 에이전트는 `inputs` 정보를 확인하고, 코드베이스 내의 해당 Context를 파악합니다.
3. `steps_hint`에 따라 코드를 작성하고, `outputs` 조건(테스트 통과 등)을 만족하면 종료합니다.

### B. 병렬 실행 (Swarm Mode)
1. `docs/INTEGRATED_WBS_DAG.md`를 참고하여, 현재 완료된 Task(`dependencies`가 해결된 상태) 목록을 추출합니다.
2. `parallelizable: true`인 Task들을 필터링합니다.
3. `agent_profile`에 맞춰 Task를 분배합니다.
   - `frontend` 태그 Task -> Frontend Specialist Agent
   - `backend` 태그 Task -> Backend Specialist Agent
4. 각 에이전트의 작업 결과(GitHub PR 등)가 Merge되면, DAG 상태를 업데이트하고 다음 Task를 배정합니다.

### C. WBS/DAG 해석
- **WBS**: 기능의 계층 구조를 파악할 때 사용합니다. Epic 단위의 진행률을 체크할 수 있습니다.
- **DAG**: 작업의 순서를 파악할 때 사용합니다. 화살표(`-->`)가 가리키는 Task는 후행 Task이므로, 선행 Task가 끝나기 전에는 착수하지 않는 것이 원칙입니다.

## 3. Best Practices
- **Context 유지**: Task 실행 시 `docs/MVP_SCOPE_AND_MAPPING.md`를 함께 참조하면, SRS/PRD의 의도를 더 정확히 파악할 수 있습니다.
- **Incremental Build**: `EPIC0(UI PoC)` -> `Core API` -> `AI Integration` 순서로 빌드업하는 것이 리스크를 줄이는 길입니다.
- **Feedback Loop**: 에이전트가 Task를 수행하다가 스펙의 모호함을 발견하면, 해당 Task 정의서(Markdown)를 수정하여 PR을 올리도록 유도하십시오.

