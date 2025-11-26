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

