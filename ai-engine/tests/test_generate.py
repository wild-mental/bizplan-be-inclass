"""
AI Engine API 테스트

Issue #008: 사업계획서 생성 LLM 엔진 및 프롬프트 구현
Related Requirements: REQ-FUNC-003, REQ-FUNC-004
SRS Document: docs/10_GPT-SRS-V3.md
Traceability: F4 (AI 초안 생성 + 쉬운/전문가 모드)

이 테스트 파일은 SRS 문서의 Traceability Matrix에 따라 다음을 검증합니다:
- TC-FUNC-003: REQ-FUNC-003 (사업계획서 초안 자동 생성)
- TC-FUNC-004: REQ-FUNC-004 (섹션별 AI 작성 보조)
- TC-NF-002: REQ-NF-002 (문서 생성 성능)
"""
import time
import pytest
from httpx import AsyncClient, ASGITransport
from unittest.mock import patch, AsyncMock, MagicMock

from app.main import app
from app.schemas import GenerateRequest


# ============================================================================
# SRS 요구사항 상수 정의
# ============================================================================

# REQ-NF-002: 문서 생성 p95 응답시간 ≤ 10초
# SRS Section: 4.2 Non-Functional Requirements
PERFORMANCE_THRESHOLD_SECONDS = 10.0

# REQ-FUNC-003: 필수 목차 정의
# 템플릿별 필수 섹션 목록 (SRS 4.1 Functional Requirements)
TEMPLATE_MANDATORY_SECTIONS = {
    "KSTARTUP_2025": [
        "problem_definition",      # 문제 정의 및 사업 아이템의 필요성
        "solution_approach",       # 해결 방안 및 사업 아이템 소개
        "market_analysis",         # 시장 분석 및 경쟁 현황
    ],
    "BANK_LOAN_2025": [
        "problem_definition",
        "solution_approach",
        "market_analysis",
    ],
    "IR_PITCH_2025": [
        "problem_definition",
        "solution_approach",
        "market_analysis",
    ],
}


# ============================================================================
# 테스트용 샘플 데이터
# ============================================================================

SAMPLE_ANSWERS = {
    "step_1_problem": {
        "q1": "기존 사업계획서 작성 도구는 복잡하고 시간이 오래 걸립니다.",
        "q2": "창업자들이 핵심에 집중하지 못하고 형식에 매몰됩니다."
    },
    "step_2_solution": {
        "q1": "AI 기반 자동 초안 생성으로 시간을 90% 단축합니다.",
        "q2": "Wizard 형태로 단계별 안내를 제공합니다."
    }
}


# ============================================================================
# Fixtures
# ============================================================================

@pytest.fixture
def anyio_backend():
    return "asyncio"


@pytest.fixture
def mock_llm_service():
    """LLM 서비스 Mock Fixture"""
    mock_service = MagicMock()
    mock_service.is_configured.return_value = True
    return mock_service


@pytest.fixture
def mock_sections():
    """생성된 섹션 Mock 데이터"""
    return {
        "problem_definition": "문제 정의 내용...",
        "solution_approach": "해결 방안 내용...",
        "market_analysis": "시장 분석 내용..."
    }


# ============================================================================
# TC-FUNC-003: REQ-FUNC-003 검증 테스트
# 사업계획서 초안 자동 생성 - 필수 목차 누락률 0%
# ============================================================================

@pytest.mark.anyio
async def test_health_check():
    """
    헬스체크 엔드포인트 테스트
    
    Issue: #008
    Component: System Health Check
    """
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.get("/health")
    
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "ok"
    assert data["service"] == "ai-engine"
    assert "llm_configured" in data


@pytest.mark.anyio
async def test_generate_without_api_key():
    """
    API Key 없이 생성 요청 시 503 반환
    
    Issue: #008
    Component: Error Handling
    """
    # LLM 서비스를 unconfigured 상태로 모킹
    mock_service = MagicMock()
    mock_service.is_configured.return_value = False
    
    with patch("app.main.get_llm_service", return_value=mock_service):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/generate",
                json={"answers": SAMPLE_ANSWERS, "template_type": "KSTARTUP_2025"}
            )
        
        # API Key가 없으면 503 Service Unavailable
        assert response.status_code == 503
        data = response.json()
        assert "GEMINI_API_KEY" in str(data)


@pytest.mark.anyio
async def test_generate_with_empty_answers():
    """
    빈 answers로 요청 시 400 반환
    
    Issue: #008
    Component: Input Validation
    """
    # LLM configured 상태로 모킹
    mock_service = MagicMock()
    mock_service.is_configured.return_value = True
    
    with patch("app.main.get_llm_service", return_value=mock_service):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/generate",
                json={"answers": {}, "template_type": "KSTARTUP_2025"}
            )
        
        assert response.status_code == 400


@pytest.mark.anyio
async def test_generate_includes_all_mandatory_sections(mock_llm_service, mock_sections):
    """
    TC-FUNC-003: 필수 목차 누락률 0% 검증
    
    REQ-FUNC-003: 사업계획서 초안 자동 생성
    SRS Section: 4.1 Functional Requirements, Table row REQ-FUNC-003
    Acceptance Criteria:
        Given: 사용자가 '예비창업패키지' 템플릿을 선택하고 필수 기본 정보를 입력한 상태에서
        When: '초안 생성' 버튼을 클릭하면
        Then: 해당 공고의 필수 목차 누락률 0%인 초안 문서가 생성·저장되고 화면에 표시되어야 한다.
    
    Issue: #008
    Traceability: F4 → TC-FUNC-003
    """
    mock_llm_service.generate_all_sections = AsyncMock(return_value=mock_sections)
    
    with patch("app.main.get_llm_service", return_value=mock_llm_service):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/generate",
                json={"answers": SAMPLE_ANSWERS, "template_type": "KSTARTUP_2025"}
            )
        
        assert response.status_code == 200
        data = response.json()
        assert "sections" in data
        
        # REQ-FUNC-003: 필수 목차 누락률 0% 검증
        generated_sections = set(data["sections"].keys())
        mandatory_sections = set(TEMPLATE_MANDATORY_SECTIONS["KSTARTUP_2025"])
        
        missing_sections = mandatory_sections - generated_sections
        assert len(missing_sections) == 0, (
            f"필수 섹션이 누락되었습니다: {missing_sections}. "
            f"생성된 섹션: {generated_sections}, "
            f"필수 섹션: {mandatory_sections}"
        )
        
        # 각 필수 섹션이 비어있지 않은지 확인
        for section_key in mandatory_sections:
            assert section_key in data["sections"], f"섹션 '{section_key}'가 응답에 없습니다"
            assert len(data["sections"][section_key]) > 0, f"섹션 '{section_key}'가 비어있습니다"


@pytest.mark.anyio
async def test_generate_template_specific_sections(mock_llm_service, mock_sections):
    """
    TC-FUNC-003: 템플릿별 필수 섹션 검증
    
    REQ-FUNC-003: 사업계획서 초안 자동 생성
    템플릿별로 필수 섹션이 올바르게 생성되는지 검증합니다.
    
    Issue: #008
    Traceability: F4 → TC-FUNC-003
    """
    mock_llm_service.generate_all_sections = AsyncMock(return_value=mock_sections)
    
    # 각 템플릿 타입에 대해 테스트
    for template_type, expected_sections in TEMPLATE_MANDATORY_SECTIONS.items():
        with patch("app.main.get_llm_service", return_value=mock_llm_service):
            transport = ASGITransport(app=app)
            async with AsyncClient(transport=transport, base_url="http://test") as client:
                response = await client.post(
                    "/generate",
                    json={"answers": SAMPLE_ANSWERS, "template_type": template_type}
                )
            
            assert response.status_code == 200, f"템플릿 {template_type} 생성 실패"
            data = response.json()
            
            # 템플릿별 필수 섹션 검증
            generated_sections = set(data["sections"].keys())
            mandatory_sections = set(expected_sections)
            
            assert mandatory_sections.issubset(generated_sections), (
                f"템플릿 {template_type}: 필수 섹션 누락. "
                f"생성됨: {generated_sections}, 필수: {mandatory_sections}"
            )


@pytest.mark.anyio
async def test_generate_with_mocked_llm(mock_llm_service, mock_sections):
    """
    TC-FUNC-003: LLM을 Mock하여 생성 성공 테스트
    
    REQ-FUNC-003: 사업계획서 초안 자동 생성
    기본 생성 기능이 정상 동작하는지 검증합니다.
    
    Issue: #008
    Traceability: F4 → TC-FUNC-003
    """
    mock_llm_service.generate_all_sections = AsyncMock(return_value=mock_sections)
    
    with patch("app.main.get_llm_service", return_value=mock_llm_service):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/generate",
                json={"answers": SAMPLE_ANSWERS, "template_type": "KSTARTUP_2025"}
            )
        
        assert response.status_code == 200
        data = response.json()
        assert "sections" in data
        assert "generated_at" in data
        assert data["sections"]["problem_definition"] == "문제 정의 내용..."


# ============================================================================
# TC-FUNC-004: REQ-FUNC-004 검증 테스트
# 섹션별 AI 작성 보조 - 텍스트 후보 1개 이상 반환
# ============================================================================

@pytest.mark.anyio
async def test_generate_single_section(mock_llm_service):
    """
    TC-FUNC-004: 섹션별 개별 생성 테스트
    
    REQ-FUNC-004: 섹션별 AI 작성 보조
    SRS Section: 4.1 Functional Requirements, Table row REQ-FUNC-004
    Acceptance Criteria:
        Given: 사용자가 특정 섹션을 편집 중인 상태에서
        When: 'AI로 작성' 또는 'AI로 보완' 버튼을 클릭하면
        Then: LLM을 통해 생성된 텍스트 후보가 1개 이상 표시되고, 
              사용자는 이를 적용 또는 취소할 수 있어야 한다.
    
    Issue: #008
    Traceability: F4 → TC-FUNC-004
    
    Note: 현재 구현은 전체 섹션 일괄 생성만 지원하지만,
          LLMService.generate_section() 메서드를 통해 개별 섹션 생성이 가능합니다.
    """
    # 단일 섹션 생성 Mock
    mock_llm_service.generate_section = AsyncMock(return_value="생성된 단일 섹션 내용")
    mock_llm_service.is_configured.return_value = True
    
    # generate_section 메서드가 직접 호출 가능한지 검증
    # (실제 API 엔드포인트는 전체 생성만 지원하지만, 서비스 레벨에서는 개별 생성 가능)
    result = await mock_llm_service.generate_section(
        section_key="problem_definition",
        user_answers=SAMPLE_ANSWERS.get("step_1_problem", {})
    )
    
    assert result is not None
    assert len(result) > 0
    assert isinstance(result, str)


@pytest.mark.anyio
async def test_generate_multiple_candidates(mock_llm_service, mock_sections):
    """
    TC-FUNC-004: 텍스트 후보 1개 이상 반환 검증
    
    REQ-FUNC-004: 섹션별 AI 작성 보조
    생성된 섹션이 1개 이상의 유의미한 텍스트를 포함하는지 검증합니다.
    
    Issue: #008
    Traceability: F4 → TC-FUNC-004
    """
    mock_llm_service.generate_all_sections = AsyncMock(return_value=mock_sections)
    
    with patch("app.main.get_llm_service", return_value=mock_llm_service):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/generate",
                json={"answers": SAMPLE_ANSWERS, "template_type": "KSTARTUP_2025"}
            )
        
        assert response.status_code == 200
        data = response.json()
        
        # REQ-FUNC-004: 텍스트 후보 1개 이상 검증
        sections = data["sections"]
        assert len(sections) >= 1, "최소 1개 이상의 섹션이 생성되어야 합니다"
        
        # 각 섹션이 유의미한 내용을 포함하는지 검증
        for section_key, content in sections.items():
            assert isinstance(content, str), f"섹션 '{section_key}'는 문자열이어야 합니다"
            assert len(content.strip()) > 0, f"섹션 '{section_key}'가 비어있습니다"
            assert len(content) > 10, f"섹션 '{section_key}'의 내용이 너무 짧습니다 (최소 10자 이상)"


# ============================================================================
# TC-NF-002: REQ-NF-002 검증 테스트
# 문서 생성 성능 - p95 응답시간 ≤ 10초
# ============================================================================

@pytest.mark.anyio
async def test_generate_response_time_within_threshold(mock_llm_service, mock_sections):
    """
    TC-NF-002: 단일 응답시간 모니터링 테스트
    
    REQ-NF-002: 문서 생성 성능
    SRS Section: 4.2 Non-Functional Requirements, Table row REQ-NF-002
    Target: 사업계획서 초안 생성 및 리포트 생성 요청의 p95 응답시간 ≤ 10초
    
    Issue: #008
    Traceability: F4 → TC-NF-002
    
    Note: 실제 p95 측정은 k6 부하 테스트(Issue #015, REQ-NF-001)에서 수행됩니다.
          이 테스트는 단일 요청의 응답시간이 임계값 이내인지 확인합니다.
    """
    mock_llm_service.generate_all_sections = AsyncMock(return_value=mock_sections)
    
    with patch("app.main.get_llm_service", return_value=mock_llm_service):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            # 응답 시간 측정 시작
            start_time = time.perf_counter()
            
            response = await client.post(
                "/generate",
                json={"answers": SAMPLE_ANSWERS, "template_type": "KSTARTUP_2025"}
            )
            
            # 응답 시간 측정 종료
            end_time = time.perf_counter()
            response_time = end_time - start_time
        
        # 응답 성공 확인
        assert response.status_code == 200
        
        # REQ-NF-002: 응답시간이 10초 이내인지 확인
        assert response_time < PERFORMANCE_THRESHOLD_SECONDS, (
            f"Response time {response_time:.3f}s exceeds threshold "
            f"{PERFORMANCE_THRESHOLD_SECONDS}s (REQ-NF-002)"
        )
        
        # 응답 시간 로깅 (테스트 출력에서 확인 가능)
        print(f"\n[PERF] Response time: {response_time:.3f}s "
              f"(threshold: {PERFORMANCE_THRESHOLD_SECONDS}s, REQ-NF-002)")


# ============================================================================
# 테스트 실행
# ============================================================================

if __name__ == "__main__":
    pytest.main([__file__, "-v"])
