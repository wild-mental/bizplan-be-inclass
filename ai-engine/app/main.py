"""
AI Engine - FastAPI Application
사업계획서 생성 LLM 엔진 메인 애플리케이션
"""
import logging
from datetime import datetime, timezone
from contextlib import asynccontextmanager

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from app.config import get_settings
from app.schemas import (
    GenerateRequest, 
    GenerateResponse, 
    ErrorResponse,
    HealthResponse,
)
from app.services.llm_service import get_llm_service

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan handler."""
    settings = get_settings()
    logger.info(f"Starting {settings.app_name} in {settings.app_env} mode")
    
    # LLM 설정 확인
    llm_service = get_llm_service()
    if llm_service.is_configured():
        logger.info("✅ LLM service configured")
    else:
        logger.warning("⚠️ GEMINI_API_KEY not set - LLM features disabled")
    
    yield
    
    logger.info("Shutting down AI Engine")


# FastAPI 앱 생성
app = FastAPI(
    title="BizPlan AI Engine",
    description="사업계획서 생성 LLM 엔진 - Issue #008",
    version="0.1.0",
    lifespan=lifespan,
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 개발 환경용
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health", response_model=HealthResponse, tags=["System"])
async def health_check():
    """헬스체크 엔드포인트"""
    llm_service = get_llm_service()
    return HealthResponse(
        status="ok",
        service="ai-engine",
        llm_configured=llm_service.is_configured()
    )


@app.post(
    "/generate",
    response_model=GenerateResponse,
    responses={
        400: {"model": ErrorResponse, "description": "잘못된 요청"},
        500: {"model": ErrorResponse, "description": "서버 오류"},
        503: {"model": ErrorResponse, "description": "LLM 서비스 불가"},
    },
    tags=["Generation"],
    summary="사업계획서 초안 생성",
    description="Wizard 답변을 기반으로 사업계획서 섹션별 초안을 생성합니다."
)
async def generate_business_plan(request: GenerateRequest):
    """
    사업계획서 초안 생성 엔드포인트
    
    - **answers**: Wizard 단계별 사용자 답변 (JSON)
    - **template_type**: 사용할 템플릿 유형 (기본: KSTARTUP_2025)
    """
    llm_service = get_llm_service()
    
    # LLM 설정 확인
    if not llm_service.is_configured():
        raise HTTPException(
            status_code=503,
            detail={
                "error": "LLM_NOT_CONFIGURED",
                "message": "GEMINI_API_KEY가 설정되지 않았습니다.",
                "detail": "환경변수 GEMINI_API_KEY를 설정해주세요."
            }
        )
    
    # 입력 검증
    if not request.answers:
        raise HTTPException(
            status_code=400,
            detail={
                "error": "INVALID_REQUEST",
                "message": "answers 필드가 비어있습니다.",
                "detail": "최소 하나 이상의 답변을 입력해주세요."
            }
        )
    
    try:
        logger.info(f"Generating business plan for template: {request.template_type}")
        
        # 섹션 생성
        sections = await llm_service.generate_all_sections(
            answers=request.answers,
            template_type=request.template_type
        )
        
        logger.info(f"Successfully generated {len(sections)} sections")
        
        return GenerateResponse(
            sections=sections,
            generated_at=datetime.now(timezone.utc)
        )
        
    except ValueError as e:
        logger.error(f"Validation error: {e}")
        raise HTTPException(
            status_code=400,
            detail={
                "error": "VALIDATION_ERROR",
                "message": str(e),
            }
        )
    except Exception as e:
        logger.error(f"Generation failed: {e}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail={
                "error": "GENERATION_FAILED",
                "message": "사업계획서 생성 중 오류가 발생했습니다.",
                "detail": str(e)
            }
        )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8001, reload=True)

