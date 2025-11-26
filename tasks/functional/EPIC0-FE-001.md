# EPIC0-FE-001: 프로젝트 생성 및 Wizard 기본 레이아웃 PoC

## 1. 개요
- **목표**: 사용자가 로그인 후 '새 프로젝트 만들기'를 통해 템플릿(예비창업패키지 등)을 선택하고, Wizard의 기본 단계(Step) 네비게이션이 동작하는 화면을 구현한다.
- **범위**:
  - 프로젝트 생성 모달/페이지
  - 템플릿 선택 UI (카드 형태)
  - Wizard 레이아웃 (사이드바/상단 단계 표시 + 메인 컨텐츠 영역)
  - Mock API 연동 (프로젝트 생성, 조회)
- **Out of Scope**: 실제 로그인 인증(하드코딩 토큰 사용), 세부 폼 필드 구현(다음 Task), 모바일 반응형 완벽 지원.

## 2. 상세 요구사항
- **템플릿 선택**: '예비창업패키지', '초기창업패키지', '은행용' 3가지 더미 옵션을 제공한다.
- **Wizard 프레임**: 좌측에 '1. 문제정의', '2. 해결방안', '3. 시장분석' 등 챕터 목록이 보이고, 클릭 시 메인 영역이 전환되어야 한다.
- **상태 관리**: `useProjectStore`(Zustand 등)를 통해 현재 선택된 단계와 프로젝트 메타데이터를 관리한다.

## 3. 기술 스택 및 도구
- React + Vite + TypeScript
- Tailwind CSS (스타일링)
- React Router (라우팅)
- Zustand (상태 관리)

---

```yaml
task_id: "EPIC0-FE-001"
title: "프로젝트 생성 및 Wizard 기본 레이아웃 PoC 구현"
summary: >
  로그인 후 템플릿 선택을 통해 프로젝트를 생성하고, 
  단계별 이동이 가능한 Wizard UI의 골격을 구현한다.
type: "functional"

epic: "EPIC_0_FE_PROTOTYPE"
req_ids: ["REQ-FUNC-001", "REQ-FUNC-002"]
agent_profile: ["frontend"]

parallelizable: true
estimated_effort: "S"
priority: "Must"

inputs:
  description: "템플릿 목록 Mock 데이터"
  fields:
    - name: "templates"
      type: "array"
      example: [{id: "KSTARTUP_2025", name: "예비창업패키지"}]

outputs:
  description: "프로젝트 생성 후 Wizard 화면 진입"
  success:
    screen: "WizardMainPage"
    state: "currentStepIndex=0"

steps_hint:
  - "React Router로 /projects/new 및 /projects/:id/wizard 경로 설정"
  - "템플릿 선택 카드 컴포넌트 구현"
  - "WizardLayout 컴포넌트 구현 (Sidebar + Content Area)"
  - "Zustand Store에 createProject 액션 구현 (Mock)"

preconditions:
  - "React+Vite 기본 프로젝트가 셋업되어 있어야 한다."

postconditions:
  - "사용자가 템플릿을 선택하면 Wizard 첫 화면으로 이동한다."
  - "사이드바 메뉴 클릭 시 URL과 메인 컨텐츠가 변경된다."

dependencies: []
```

