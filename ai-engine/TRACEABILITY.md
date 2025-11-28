# Traceability Documentation

## Issue #008: 사업계획서 생성 LLM 엔진 및 프롬프트 구현

이 문서는 코드와 SRS 문서 간의 추적 가능성(Traceability)을 명시합니다.

---

## 요구사항 매핑

### SRS 요구사항 → 코드 구현

| SRS 요구사항 | 구현 위치 | 테스트 케이스 | 상태 |
|:---|:---|:---|:---|
| **REQ-FUNC-003** | `app/services/llm_service.py::generate_all_sections()` | `test_generate_includes_all_mandatory_sections` | ✅ |
| **REQ-FUNC-004** | `app/services/llm_service.py::generate_section()` | `test_generate_single_section` | ✅ |
| **REQ-NF-002** | `app/main.py::generate_business_plan()` | `test_generate_response_time_within_threshold` | ✅ |

### 테스트 케이스 → SRS 요구사항

| 테스트 케이스 ID | 테스트 함수명 | SRS 요구사항 | 설명 |
|:---|:---|:---|:---|
| **TC-FUNC-003** | `test_generate_includes_all_mandatory_sections` | REQ-FUNC-003 | 필수 목차 누락률 0% 검증 |
| **TC-FUNC-003** | `test_generate_template_specific_sections` | REQ-FUNC-003 | 템플릿별 필수 섹션 검증 |
| **TC-FUNC-003** | `test_generate_with_mocked_llm` | REQ-FUNC-003 | 기본 생성 기능 검증 |
| **TC-FUNC-004** | `test_generate_single_section` | REQ-FUNC-004 | 섹션별 개별 생성 |
| **TC-FUNC-004** | `test_generate_multiple_candidates` | REQ-FUNC-004 | 텍스트 후보 1개 이상 반환 |
| **TC-NF-002** | `test_generate_response_time_within_threshold` | REQ-NF-002 | 성능 테스트 (≤ 10초) |

---

## 코드 주석 추적성

### 파일별 추적성 주석 위치

1. **`tests/test_generate.py`**
   - 파일 상단: Issue #008, Related Requirements, SRS Document 참조
   - 각 테스트 함수: SRS 요구사항 ID, Acceptance Criteria, Traceability 정보

2. **`app/services/llm_service.py`**
   - 파일 상단: Issue #008, Related Requirements, Traceability 정보
   - `generate_section()`: REQ-FUNC-004, TC-FUNC-004
   - `generate_all_sections()`: REQ-FUNC-003, TC-FUNC-003

3. **`app/main.py`**
   - 파일 상단: Issue #008, Related Requirements, API Endpoint 참조
   - `generate_business_plan()`: REQ-FUNC-003, REQ-FUNC-004, TC-FUNC-003, TC-FUNC-004

---

## SRS 문서 참조

- **SRS 문서**: `docs/10_GPT-SRS-V3.md`
- **Section 4.1**: Functional Requirements (REQ-FUNC-003, REQ-FUNC-004)
- **Section 4.2**: Non-Functional Requirements (REQ-NF-002)
- **Section 5**: Traceability Matrix
- **Section 6.1**: API Endpoint List

---

## Issue 문서 참조

- **Issue 문서**: `tasks/github-issues/completed/issue-008-REQ-FUNC-003-AI-001.md`
- **GitHub Issue**: [#4](https://github.com/wild-mental/bizplan-be-inclass/issues/4)
- **Pull Request**: [#14](https://github.com/wild-mental/bizplan-be-inclass/pull/14)

---

## 검증 방법

### 테스트 실행

```bash
cd ai-engine
source venv/bin/activate
pytest tests/test_generate.py -v
```

### 추적성 확인

1. **코드에서 SRS 요구사항 찾기**: `grep -r "REQ-FUNC-003" ai-engine/`
2. **테스트에서 요구사항 확인**: `grep -r "TC-FUNC-003" ai-engine/tests/`
3. **SRS 문서에서 구현 확인**: `docs/10_GPT-SRS-V3.md` Section 5 참조

---

## 변경 이력

- **2025-11-28**: 초기 추적성 문서 작성
  - 모든 테스트 케이스에 SRS 요구사항 ID 명시
  - 코드 주석에 Traceability 정보 추가
  - README에 Traceability Matrix 추가

