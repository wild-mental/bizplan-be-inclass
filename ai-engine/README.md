# AI Engine - 사업계획서 생성 LLM 엔진

> **Issue #008**: 사업계획서 생성 LLM 엔진 및 프롬프트 구현

## 개요

Wizard 답변(JSON)을 입력받아, LangChain 및 LLM(Gemini)을 사용하여 섹션별 사업계획서 초안을 생성하는 Python FastAPI 서비스입니다.

## 기술 스택

- Python 3.10+
- FastAPI
- LangChain
- Google Gemini API

## 빠른 시작

### 1. 가상환경 생성 및 의존성 설치

```bash
cd ai-engine

# 가상환경 생성
python3 -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 의존성 설치
pip install -r requirements.txt
```

### 2. 환경변수 설정

```bash
# 템플릿에서 .env 파일 생성
cp .env.example .env

# 실제 값 입력
vim .env  # 또는 선호하는 에디터 사용
```

`.env` 파일 내용:
```bash
# ============ Google Gemini API ============
# https://makersuite.google.com/app/apikey 에서 발급
GEMINI_API_KEY=your_gemini_api_key_here  # ⚠️ 필수

# ============ Application Settings ============
APP_ENV=development
LOG_LEVEL=INFO

# ============ Backend Core Communication ============
BACKEND_CORE_URL=http://localhost:8080

# ============ LLM Settings ============
MAX_TOKENS_PER_REQUEST=4096
REQUEST_TIMEOUT_SECONDS=30
```

> ⚠️ **보안 주의**: `.env` 파일은 절대 Git에 커밋하지 마세요. `.gitignore`에서 자동 제외됩니다.

### 3. 서버 실행

```bash
# 개발 서버 실행 (자동 리로드)
uvicorn app.main:app --reload --port 8001

# 또는
python -m app.main
```

### 4. API 테스트

서버 실행 후 http://localhost:8001/docs 에서 Swagger UI 확인

```bash
# 헬스체크
curl http://localhost:8001/health

# 사업계획서 생성
curl -X POST http://localhost:8001/generate \
  -H "Content-Type: application/json" \
  -d '{
    "answers": {
      "step_1_problem": {
        "q1": "기존 사업계획서 작성이 너무 복잡합니다.",
        "q2": "창업자가 핵심에 집중하지 못합니다."
      },
      "step_2_solution": {
        "q1": "AI로 자동 초안을 생성합니다.",
        "q2": "Wizard로 단계별 안내합니다."
      }
    },
    "template_type": "KSTARTUP_2025"
  }'
```

## 테스트 실행

```bash
# 전체 테스트 실행
pytest tests/ -v

# 특정 테스트 실행
pytest tests/test_generate.py -v
```

## API 명세

### GET /health
헬스체크 엔드포인트

**Response:**
```json
{
  "status": "ok",
  "service": "ai-engine",
  "llm_configured": true
}
```

### POST /generate
사업계획서 초안 생성

**Request:**
```json
{
  "answers": {
    "step_1_problem": { "q1": "...", "q2": "..." },
    "step_2_solution": { "q1": "...", "q2": "..." }
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
  "generated_at": "2025-11-28T10:10:00Z"
}
```

## 프로젝트 구조

```
ai-engine/
├── app/
│   ├── __init__.py
│   ├── main.py           # FastAPI 앱 진입점
│   ├── config.py         # 환경변수 설정
│   ├── schemas.py        # Pydantic 스키마
│   └── services/
│       ├── __init__.py
│       └── llm_service.py  # LLM 서비스 로직
├── tests/
│   ├── __init__.py
│   └── test_generate.py
├── requirements.txt
└── README.md
```

## 관련 이슈 및 요구사항 추적

### Issue 및 요구사항 매핑

- **Issue #008**: 사업계획서 생성 LLM 엔진 및 프롬프트 구현
- **REQ-FUNC-003**: 사업계획서 초안 자동 생성 (SRS 4.1)
- **REQ-FUNC-004**: 섹션별 AI 작성 보조 (SRS 4.1)
- **REQ-NF-002**: 문서 생성 성능 - p95 응답시간 ≤ 10초 (SRS 4.2)

### Traceability Matrix (SRS 5. Traceability Matrix)

| Story / Feature | Requirement ID(s) | Test Case ID(s) | 구현 상태 |
|:---|:---|:---|:---|
| **F4: AI 초안 생성 + 쉬운/전문가 모드** | REQ-FUNC-003, REQ-FUNC-004 | TC-FUNC-003, TC-FUNC-004 | ✅ 완료 |
| **EPIC 1: 과제 통과 Job** | REQ-FUNC-003, REQ-FUNC-004; REQ-NF-002 | TC-FUNC-003, TC-FUNC-004; TC-NF-002 | ✅ 완료 |

### 테스트 커버리지

| 테스트 케이스 | SRS 요구사항 | 설명 | 상태 |
|:---|:---|:---|:---|
| `test_generate_includes_all_mandatory_sections` | TC-FUNC-003 | 필수 목차 누락률 0% 검증 | ✅ |
| `test_generate_template_specific_sections` | TC-FUNC-003 | 템플릿별 필수 섹션 검증 | ✅ |
| `test_generate_single_section` | TC-FUNC-004 | 섹션별 개별 생성 | ✅ |
| `test_generate_multiple_candidates` | TC-FUNC-004 | 텍스트 후보 1개 이상 반환 | ✅ |
| `test_generate_response_time_within_threshold` | TC-NF-002 | 성능 테스트 (≤ 10초) | ✅ |

**테스트 실행 결과**: 9개 테스트 모두 통과 (100%)

### 관련 이슈

- **#008**: 본 이슈 (사업계획서 생성 LLM 엔진)
- **#009**: 오케스트레이션 API (Spring Boot에서 이 서비스 호출)
- **#011**: PMF 진단 LLM 엔진 (유사 구조로 확장)

## 환경변수 목록

| 변수명 | 필수 | 기본값 | 설명 |
|--------|------|--------|------|
| `GEMINI_API_KEY` | ✅ | - | Google Gemini API Key |
| `APP_ENV` | - | `development` | 실행 환경 (development/production) |
| `LOG_LEVEL` | - | `INFO` | 로그 레벨 |
| `BACKEND_CORE_URL` | - | `http://localhost:8080` | Spring Boot 백엔드 URL |
| `MAX_TOKENS_PER_REQUEST` | - | `4096` | LLM 요청당 최대 토큰 수 |
| `REQUEST_TIMEOUT_SECONDS` | - | `30` | API 요청 타임아웃 |
| `LLM_MODEL` | - | `gemini-1.5-flash` | 사용할 Gemini 모델 |
| `LLM_TEMPERATURE` | - | `0.7` | 생성 온도 (창의성) |
| `LLM_MAX_RETRIES` | - | `3` | LLM 오류 시 재시도 횟수 |

## 🔒 보안 관리

### 환경변수 보안 원칙

```
ai-engine/
├── .env.example    # ✅ Git 포함 - 템플릿 (실제 값 없음)
├── .env            # ❌ Git 제외 - 실제 API 키 포함
└── .gitignore      # .env 파일 제외 규칙 포함
```

### 보안 체크리스트

- [ ] `.env.example`을 복사하여 `.env` 생성
- [ ] `.env` 파일에 실제 API 키 입력
- [ ] `.env` 파일이 Git에 추적되지 않는지 확인 (`git status`)
- [ ] 프로덕션 배포 시 환경변수를 서버/컨테이너에 직접 설정

### CI/CD 환경

GitHub Actions 등에서는 Repository Secrets를 사용:

```yaml
# .github/workflows/test.yml
env:
  GEMINI_API_KEY: ${{ secrets.GEMINI_API_KEY }}
```

