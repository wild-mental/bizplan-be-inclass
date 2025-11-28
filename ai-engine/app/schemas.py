"""
API Request/Response Schemas
Pydantic 모델 정의
"""
from datetime import datetime
from typing import Dict, Any, Optional
from pydantic import BaseModel, Field


class GenerateRequest(BaseModel):
    """사업계획서 생성 요청 스키마"""
    
    answers: Dict[str, Any] = Field(
        ...,
        description="Wizard 단계별 사용자 답변 (JSON)",
        json_schema_extra={
            "example": {
                "step_1_problem": {
                    "q1": "기존 사업계획서 작성 도구는 복잡하고 시간이 오래 걸립니다.",
                    "q2": "창업자들이 핵심에 집중하지 못하고 형식에 매몰됩니다."
                },
                "step_2_solution": {
                    "q1": "AI 기반 자동 초안 생성으로 시간을 90% 단축합니다.",
                    "q2": "Wizard 형태로 단계별 안내를 제공합니다."
                }
            }
        }
    )
    
    template_type: str = Field(
        default="KSTARTUP_2025",
        description="사용할 템플릿 유형",
        json_schema_extra={"example": "KSTARTUP_2025"}
    )


class GenerateResponse(BaseModel):
    """사업계획서 생성 응답 스키마"""
    
    sections: Dict[str, str] = Field(
        ...,
        description="생성된 섹션별 내용"
    )
    
    generated_at: datetime = Field(
        default_factory=datetime.utcnow,
        description="생성 시각 (UTC)"
    )


class ErrorResponse(BaseModel):
    """에러 응답 스키마"""
    
    error: str = Field(..., description="에러 타입")
    message: str = Field(..., description="에러 메시지")
    detail: Optional[str] = Field(None, description="상세 정보")


class HealthResponse(BaseModel):
    """헬스체크 응답 스키마"""
    
    status: str = "ok"
    service: str = "ai-engine"
    llm_configured: bool = False

