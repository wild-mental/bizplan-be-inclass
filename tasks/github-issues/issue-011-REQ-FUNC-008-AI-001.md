# [#011] PMF 진단 및 리포트 생성 LLM 엔진 구현

## Labels
`epic:EPIC_2_AVOID_FAILURE`, `type:ai`, `type:backend`, `component:ai-engine`, `priority:Should`, `effort:M`

## Description
사용자의 PMF 설문 응답을 분석하여 PMF 단계(Stage), 리스크, 개선 권고를 도출하는 LLM 기반 진단 엔진을 구현합니다.

## Scope
- `POST /pmf/analyze`: 진단 요청 처리
- PMF 진단 프롬프트 엔지니어링 (Persona: 스타트업 액셀러레이터 심사역)
- 응답 데이터 부족 시 예외 처리 (Rule Base + LLM)

### Out of Scope
- 진단 이력 관리(Backend DB 역할)

## Requirements
- **진단 로직**: 10개 이상의 문항 답변을 종합하여 'Problem-Solution Fit', 'Product-Market Fit', 'Scale-up' 중 단계 판정
- **리스크 추출**: 답변 내용 중 논리적 비약이나 시장성 부족 신호를 찾아 Top 3 리스크로 정리

## Technical Stack
- Python 3.10+
- FastAPI (REQ-FUNC-003-AI-001과 동일 환경 공유)
- LangChain
- Google Gemini API

## API Specification

### POST /pmf/analyze
**Request:**
```json
{
  "answers": [
    {
      "question_id": "pmf_q1",
      "answer": "고객이 매우 만족함"
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "stage": "Product-Market Fit",
  "score": 75,
  "risks": [
    "시장 규모 검증 부족",
    "경쟁사 분석 미비",
    "수익 모델 불명확"
  ],
  "recommendations": [
    "시장 조사 강화 필요",
    "경쟁사 벤치마킹 수행",
    "수익 모델 구체화"
  ]
}
```

**Response (400 Bad Request):**
```json
{
  "error": "INSUFFICIENT_DATA",
  "message": "진단을 위해 최소 5개 이상의 답변이 필요합니다."
}
```

## Implementation Steps
1. PMF 진단용 System Prompt 작성
2. 답변 개수 부족 시 Early Return 로직 구현
3. LangChain Chain 구성 (JSON Output Parser 활용)
4. FastAPI 엔드포인트 연동
5. 에러 핸들링

## Acceptance Criteria
- [ ] REQ-FUNC-003-AI-001 환경(FastAPI/Gemini) 공용 사용
- [ ] 설문 답변 입력 시 분석된 리포트 JSON이 반환됨
- [ ] 답변 수 부족 시 적절한 에러 응답

## Dependencies
- #008 (REQ-FUNC-003-AI-001) - FastAPI 환경 공유

## Parallelizable With
- #009, #010, #012 (독립적 기능)

## Related Requirements
REQ-FUNC-008, REQ-FUNC-010

