"""
LLM Service
LangChain + Google Gemini를 사용한 사업계획서 생성 서비스
"""
import logging
from typing import Dict, Any

from langchain_google_genai import ChatGoogleGenerativeAI
from langchain.prompts import PromptTemplate
from langchain.schema import HumanMessage

from app.config import get_settings

logger = logging.getLogger(__name__)


# 섹션별 프롬프트 템플릿 정의
SECTION_PROMPTS = {
    "problem_definition": """
당신은 정부 지원사업 심사위원 경험이 풍부한 전문 창업 컨설턴트입니다.

## Context
사용자가 제공한 사업 아이디어 정보:
{user_answers}

## Task
위 정보를 바탕으로 "문제 정의 및 사업 아이템의 필요성" 섹션을 작성하세요.

## 작성 요구사항
1. 현재 시장/고객이 겪는 구체적인 문제점을 3가지 이상 서술
2. 기존 해결책의 한계점 분석
3. 해당 문제가 해결되지 않을 경우의 파급효과
4. 객관적 데이터나 통계를 인용할 수 있다면 포함

## 출력 형식
2-3개 문단, 총 300-500자 분량의 전문적인 서술체로 작성하세요.
""",
    
    "solution_approach": """
당신은 정부 지원사업 심사위원 경험이 풍부한 전문 창업 컨설턴트입니다.

## Context
사용자가 제공한 솔루션 정보:
{user_answers}

## Task
위 정보를 바탕으로 "해결 방안 및 사업 아이템 소개" 섹션을 작성하세요.

## 작성 요구사항
1. 제안하는 솔루션의 핵심 가치 제안(Value Proposition) 명확히 서술
2. 기존 대안 대비 차별화 포인트 3가지 이상
3. 기술적/비즈니스적 실현 가능성 근거
4. 목표 고객과 사용 시나리오

## 출력 형식
2-3개 문단, 총 300-500자 분량의 전문적인 서술체로 작성하세요.
""",
    
    "market_analysis": """
당신은 정부 지원사업 심사위원 경험이 풍부한 전문 창업 컨설턴트입니다.

## Context
사용자가 제공한 시장 및 사업 정보:
{user_answers}

## Task
위 정보를 바탕으로 "시장 분석 및 경쟁 현황" 섹션을 작성하세요.

## 작성 요구사항
1. TAM-SAM-SOM 프레임워크 기반 시장 규모 추정
2. 주요 경쟁사/대체재 분석
3. 시장 진입 전략 및 포지셔닝
4. 성장 가능성 및 트렌드

## 출력 형식
2-3개 문단, 총 300-500자 분량의 전문적인 서술체로 작성하세요.
"""
}


class LLMService:
    """LLM 기반 사업계획서 생성 서비스"""
    
    def __init__(self):
        """서비스 초기화"""
        self.settings = get_settings()
        self._llm = None
    
    @property
    def llm(self) -> ChatGoogleGenerativeAI:
        """Lazy initialization of LLM client."""
        if self._llm is None:
            if not self.settings.gemini_api_key:
                raise ValueError("GEMINI_API_KEY is not configured")
            
            self._llm = ChatGoogleGenerativeAI(
                model=self.settings.llm_model,
                google_api_key=self.settings.gemini_api_key,
                temperature=self.settings.llm_temperature,
                max_retries=self.settings.llm_max_retries,
            )
        return self._llm
    
    def is_configured(self) -> bool:
        """LLM이 설정되어 있는지 확인"""
        return bool(self.settings.gemini_api_key)
    
    async def generate_section(
        self, 
        section_key: str, 
        user_answers: Dict[str, Any]
    ) -> str:
        """
        단일 섹션 생성
        
        Args:
            section_key: 섹션 키 (problem_definition, solution_approach, market_analysis)
            user_answers: 사용자 답변 딕셔너리
            
        Returns:
            생성된 섹션 텍스트
        """
        if section_key not in SECTION_PROMPTS:
            raise ValueError(f"Unknown section: {section_key}")
        
        prompt_template = PromptTemplate(
            input_variables=["user_answers"],
            template=SECTION_PROMPTS[section_key]
        )
        
        # 사용자 답변을 문자열로 포맷팅
        answers_str = self._format_answers(user_answers)
        prompt = prompt_template.format(user_answers=answers_str)
        
        logger.info(f"Generating section: {section_key}")
        
        try:
            response = await self.llm.ainvoke([HumanMessage(content=prompt)])
            return response.content
        except Exception as e:
            logger.error(f"LLM generation failed for {section_key}: {e}")
            raise
    
    async def generate_all_sections(
        self, 
        answers: Dict[str, Any],
        template_type: str = "KSTARTUP_2025"
    ) -> Dict[str, str]:
        """
        모든 섹션 생성
        
        Args:
            answers: Wizard 단계별 사용자 답변
            template_type: 템플릿 유형
            
        Returns:
            섹션별 생성된 텍스트 딕셔너리
        """
        sections = {}
        
        # 섹션과 관련 답변 매핑
        section_answer_mapping = {
            "problem_definition": answers.get("step_1_problem", {}),
            "solution_approach": answers.get("step_2_solution", {}),
            "market_analysis": answers,  # 전체 답변 사용
        }
        
        for section_key, section_answers in section_answer_mapping.items():
            try:
                content = await self.generate_section(section_key, section_answers)
                sections[section_key] = content
                logger.info(f"Successfully generated: {section_key}")
            except Exception as e:
                logger.error(f"Failed to generate {section_key}: {e}")
                sections[section_key] = f"[생성 실패: {str(e)}]"
        
        return sections
    
    def _format_answers(self, answers: Dict[str, Any]) -> str:
        """사용자 답변을 LLM 프롬프트용 문자열로 포맷팅"""
        if not answers:
            return "(답변 없음)"
        
        lines = []
        for key, value in answers.items():
            if isinstance(value, dict):
                for sub_key, sub_value in value.items():
                    lines.append(f"- {sub_key}: {sub_value}")
            else:
                lines.append(f"- {key}: {value}")
        
        return "\n".join(lines)


# 싱글톤 인스턴스
_llm_service: LLMService | None = None


def get_llm_service() -> LLMService:
    """LLM 서비스 싱글톤 인스턴스 반환"""
    global _llm_service
    if _llm_service is None:
        _llm_service = LLMService()
    return _llm_service

