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

