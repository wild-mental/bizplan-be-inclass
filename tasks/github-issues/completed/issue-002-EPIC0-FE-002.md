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

