# Change Management Strategy: PRD-SRS-Task

## 1. 3-Layer Traceability Structure

변경 관리는 다음 3단계 계층의 추적성(Traceability)을 기반으로 수행됩니다.

1. **PRD (Business Logic)**: `9_GPT-Gemini_Merged_PRD.md`
2. **SRS (System Requirements)**: `10_GPT-SRS-V3.md`
3. **Task Definitions (Implementation Spec)**: `tasks/**/*.md`

## 2. Change Scenarios & Workflows

### Scenario A: PRD (Business Goal) 변경 시
- **Trigger**: 시장 상황 변화, 이해관계자 요구 변경 (예: "재무 추정 시 세금 계산 로직 추가해줘")
- **Action**:
  1. PRD 업데이트 및 버전 올림 (v1.0 -> v1.1).
  2. 영향을 받는 SRS REQ-FUNC 식별 및 수정 (`REQ-FUNC-012`).
  3. 해당 SRS ID를 `req_ids`로 가지는 Task(`REQ-FUNC-012-BE-001`)를 찾아 `summary`, `inputs` 업데이트.
  4. WBS/DAG에서 영향도 파악 후 일정 재산정.

### Scenario B: SRS (Requirement Details) 변경 시
- **Trigger**: 기술적 제약 발견, 상세 기획 변경 (예: "로그인 시 이메일 인증 필수 추가")
- **Action**:
  1. SRS REQ 업데이트.
  2. 매핑된 Task(`REQ-FUNC-001-BE-001`)의 `steps_hint`, `preconditions` 수정.
  3. Task 상태를 `Pending`으로 되돌리고 에이전트 재할당.

### Scenario C: Task (Implementation) 수준 변경 시
- **Trigger**: 리팩토링, 버그 수정, 최적화 (예: "API 응답 속도 개선을 위해 캐싱 적용")
- **Action**:
  1. Task 정의서 내 `steps_hint` 또는 `implementation_note` 수정.
  2. 상위 SRS/PRD 변경이 불필요하다면, Task 버전만 올리고 작업 수행.

## 3. Version Control & Sync
- **Version Tags**:
  - PRD: `v{Major}.{Minor}`
  - SRS: `v{Major}.{Minor}` (PRD Major 버전과 동기화 권장)
  - Task Catalog: `v{Date}_{Hash}`
- **Change Log**:
  - `docs/CHANGE_LOG.md` (선택 사항) 또는 Git Commit Message에 `[PRD-Update]`, `[SRS-Sync]` 태그 사용.
- **Sync Check**:
  - 정기적으로 `docs/MVP_SCOPE_AND_MAPPING.md`와 실제 Task 파일들을 대조하여, 누락된 REQ나 삭제된 Feature의 Task가 남아있는지 점검한다.

