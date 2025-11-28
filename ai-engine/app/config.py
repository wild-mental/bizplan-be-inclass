"""
AI Engine Configuration
환경변수 기반 설정 관리
"""
import os
from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    """Application settings loaded from environment variables."""
    
    # Application
    app_name: str = "BizPlan AI Engine"
    app_env: str = "development"
    log_level: str = "INFO"
    
    # Google Gemini API
    gemini_api_key: str = ""
    
    # LLM Settings
    llm_model: str = "gemini-1.5-flash"
    llm_temperature: float = 0.7
    llm_max_retries: int = 3
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        extra = "ignore"


@lru_cache
def get_settings() -> Settings:
    """Get cached settings instance."""
    return Settings()

