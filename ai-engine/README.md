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
# .env 파일 생성
cat > .env << EOF
# Google Gemini API Key (필수)
# https://aistudio.google.com/apikey 에서 발급
GEMINI_API_KEY=your-api-key-here

# 설정 (선택)
APP_ENV=development
LLM_MODEL=gemini-1.5-flash
LLM_TEMPERATURE=0.7
EOF
```

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

## 관련 이슈

- **#008**: 본 이슈 (사업계획서 생성 LLM 엔진)
- **#009**: 오케스트레이션 API (Spring Boot에서 이 서비스 호출)
- **#011**: PMF 진단 LLM 엔진 (유사 구조로 확장)

## 환경변수 목록

| 변수명 | 필수 | 기본값 | 설명 |
|--------|------|--------|------|
| `GEMINI_API_KEY` | ✅ | - | Google Gemini API Key |
| `APP_ENV` | - | `development` | 실행 환경 |
| `LOG_LEVEL` | - | `INFO` | 로그 레벨 |
| `LLM_MODEL` | - | `gemini-1.5-flash` | 사용할 Gemini 모델 |
| `LLM_TEMPERATURE` | - | `0.7` | 생성 온도 (창의성) |
| `LLM_MAX_RETRIES` | - | `3` | LLM 오류 시 재시도 횟수 |

