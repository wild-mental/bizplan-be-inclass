"""
AI Engine API 테스트
Issue #008 - 사업계획서 생성 LLM 엔진 테스트
"""
import pytest
from httpx import AsyncClient, ASGITransport
from unittest.mock import patch, AsyncMock, MagicMock

from app.main import app
from app.schemas import GenerateRequest


# 테스트용 샘플 데이터
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


@pytest.fixture
def anyio_backend():
    return "asyncio"


@pytest.mark.anyio
async def test_health_check():
    """헬스체크 엔드포인트 테스트"""
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
    """API Key 없이 생성 요청 시 503 반환"""
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
    """빈 answers로 요청 시 400 반환"""
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
async def test_generate_with_mocked_llm():
    """LLM을 Mock하여 생성 성공 테스트"""
    mock_sections = {
        "problem_definition": "문제 정의 내용...",
        "solution_approach": "해결 방안 내용...",
        "market_analysis": "시장 분석 내용..."
    }
    
    # AsyncMock으로 서비스 전체를 모킹
    mock_service = MagicMock()
    mock_service.is_configured.return_value = True
    mock_service.generate_all_sections = AsyncMock(return_value=mock_sections)
    
    with patch("app.main.get_llm_service", return_value=mock_service):
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


if __name__ == "__main__":
    pytest.main([__file__, "-v"])

