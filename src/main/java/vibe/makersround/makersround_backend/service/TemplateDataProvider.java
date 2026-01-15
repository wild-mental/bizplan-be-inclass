package vibe.makersround.makersround_backend.service;

import org.springframework.stereotype.Component;
import vibe.makersround.makersround_backend.dto.template.*;

import java.util.List;

/**
 * 프론트엔드에 제공할 템플릿/마법사 단계/PMF 질문 정적 데이터 제공자.
 * 추후 DB/관리 도구로 전환 가능하도록 별도 컴포넌트로 분리.
 */
@Component
public class TemplateDataProvider {

    public List<TemplateDto> getTemplates() {
        return List.of(
                new TemplateDto(
                        "pre-startup",
                        "예비창업패키지",
                        "창업 준비 단계에 있는 예비 창업가를 위한 사업계획서",
                        "🚀",
                        List.of("아이디어 검증 중심", "MVP 구축 계획", "초기 시장 조사", "기본 재무 설계")
                ),
                new TemplateDto(
                        "early-startup",
                        "초기창업패키지",
                        "사업을 시작한 지 1-2년 차 초기 스타트업을 위한 계획서",
                        "💼",
                        List.of("PMF 검증 전략", "성장 전략 수립", "상세 재무 분석", "투자 유치 준비")
                ),
                new TemplateDto(
                        "bank-loan",
                        "정책자금 및 은행 대출",
                        "금융기관 대출 심사를 위한 표준 사업계획서",
                        "🏦",
                        List.of("담보/신용 분석", "상환 계획 수립", "리스크 관리", "보수적 재무 예측")
                )
        );
    }

    public List<WizardStepDto> getWizardSteps() {
        return List.of(
                new WizardStepDto(
                        1,
                        "문제 인식",
                        "Problem: 해결하고자 하는 문제와 타겟 고객을 정의합니다",
                        "🔍",
                        "pending",
                        List.of(
                                new WizardQuestionDto("item-name", "text", "사업 아이템명", null,
                                        "AI 기반 맞춤형 학습 플랫폼 LearnAI", true, null, null),
                                new WizardQuestionDto("item-summary", "textarea", "한 문장 요약",
                                        "투자자에게 30초 안에 설명할 수 있는 핵심 가치",
                                        "LearnAI는 학생 개개인의 학습 패턴과 취약점을 AI가 실시간 분석하여 최적화된 맞춤형 학습 경로를 제공함으로써 학습 효율을 40% 이상 향상시키는 혁신적인 에듀테크 플랫폼입니다.",
                                        true, null, null),
                                new WizardQuestionDto("problem", "textarea", "해결하고자 하는 문제",
                                        "타겟 고객이 겪고 있는 구체적인 문제 (Pain Point)",
                                        "현재 교육 시장에서 학생들은 획일화된 커리큘럼으로 인해 개인별 학습 속도와 이해도 차이를 고려받지 못하고 있습니다. 대형 학원의 일방향 수업은 상위권 학생에게는 지루하고, 하위권 학생에게는 따라가기 어려운 구조입니다. 또한 학부모는 자녀의 실질적인 학습 현황을 파악하기 어려워 적절한 교육 지원을 하지 못하고 있습니다.",
                                        true, null, null),
                                new WizardQuestionDto("problem-evidence", "textarea", "문제의 근거",
                                        "문제의 심각성을 뒷받침하는 데이터, 통계, 사례",
                                        "한국교육개발원 2024년 조사에 따르면, 중고등학생 78%가 현재 학습 방식에 불만족하며, 학부모 65%는 자녀의 실제 학습 수준을 정확히 파악하지 못한다고 응답했습니다. 사교육비 지출은 연평균 23조원에 달하지만, 학업 성취도 향상은 평균 12%에 불과합니다. 이는 맞춤형 교육의 부재로 인한 비효율적 학습의 결과입니다.",
                                        true, null, null),
                                new WizardQuestionDto("target-customer", "textarea", "타겟 고객",
                                        "구체적인 고객 페르소나와 시장 규모",
                                        "1차 타겟은 중학생 자녀를 둔 35-45세 학부모입니다. 이들은 자녀의 학업 향상에 높은 관심을 가지고 있으며, 월평균 30-50만원의 사교육비를 지출합니다. 국내 중학생 수 약 130만명, 학부모 가구 약 110만 가구가 잠재 고객입니다. 2차 타겟으로 고등학생 및 초등학교 고학년으로 확장 계획입니다.",
                                        true, null, null)
                        )
                ),
                new WizardStepDto(
                        2,
                        "시장 분석",
                        "시장 규모와 경쟁 환경을 분석합니다",
                        "📊",
                        "pending",
                        List.of(
                                new WizardQuestionDto("market-size", "textarea", "시장 규모 (TAM/SAM/SOM)",
                                        "Total Addressable Market, Serviceable Available Market, Serviceable Obtainable Market",
                                        "TAM(전체 시장): 국내 사교육 시장 약 25조원, 글로벌 에듀테크 시장 350조원입니다.\nSAM(유효 시장): 국내 온라인 교육 시장 약 5조원, AI 학습 플랫폼 시장 1조원입니다.\nSOM(목표 시장): 1년차 100억원(시장점유율 1%), 3년차 500억원(시장점유율 5%) 달성을 목표로 합니다.",
                                        true, null, null),
                                new WizardQuestionDto("market-trend", "textarea", "시장 트렌드",
                                        "최근 3-5년간 시장 변화와 향후 전망",
                                        "COVID-19 이후 온라인 교육 시장이 연평균 22% 급성장하고 있습니다. 특히 AI 기반 맞춤형 학습 서비스는 2023년 대비 2027년까지 3배 성장이 예상됩니다. 정부의 에듀테크 육성 정책으로 연간 2,000억원의 지원이 계획되어 있으며, 학부모들의 디지털 교육 수용도가 크게 높아져 온라인 학습 서비스 이용률이 65%를 넘어섰습니다.",
                                        true, null, null),
                                new WizardQuestionDto("competitors", "textarea", "경쟁사 분석",
                                        "주요 경쟁사 3-5개와 차별점",
                                        "1. 메가스터디: 강점은 브랜드 인지도와 콘텐츠 양, 약점은 개인화 부족과 높은 가격입니다.\n2. 대교 스마트러닝: 강점은 오프라인 네트워크, 약점은 AI 기술 부재와 구식 UI입니다.\n3. 클래스101(에듀): 강점은 다양한 강좌, 약점은 학습 체계성 부족입니다.\n4. Khan Academy(해외): 강점은 무료 콘텐츠, 약점은 한국 교육과정 미반영입니다.",
                                        true, null, null),
                                new WizardQuestionDto("competitive-advantage", "textarea", "경쟁 우위",
                                        "우리만의 핵심 차별화 요소",
                                        "첫째, 자체 개발한 적응형 AI 알고리즘으로 학생별 학습 경로를 실시간 최적화합니다(특허 출원 완료). 둘째, 학습 행동 데이터 기반으로 취약 영역을 0.1단원 수준까지 정밀 진단합니다. 셋째, 학부모 대시보드를 통해 자녀의 학습 현황을 실시간 공유하여 가정 내 학습 지원을 강화합니다. 넷째, 한국 교육과정 100% 연계로 내신과 수능 대비가 동시에 가능합니다.",
                                        true, null, null)
                        )
                ),
                new WizardStepDto(
                        3,
                        "실현 방안",
                        "Solution: 비즈니스 모델과 기술적 실현 가능성을 검증합니다",
                        "⚙️",
                        "pending",
                        List.of(
                                new WizardQuestionDto("solution", "textarea", "솔루션 개요",
                                        "문제를 어떻게 해결하는가?",
                                        "LearnAI 플랫폼은 학생의 학습 패턴을 AI가 분석하여 맞춤형 학습 경로를 제공합니다. 학생이 문제를 풀 때의 오답 패턴, 풀이 시간, 재시도 횟수를 분석하여 취약 단원을 정밀 진단하고, 해당 영역을 보완할 수 있는 맞춤형 문제와 강의를 자동 추천합니다. 또한 학부모 앱을 통해 자녀의 학습 진도와 성취도를 실시간으로 확인할 수 있어 효과적인 학습 지원이 가능합니다.",
                                        true, null, null),
                                new WizardQuestionDto("business-model", "textarea", "비즈니스 모델",
                                        "어떻게 수익을 창출하는가?",
                                        "B2C 구독형 SaaS 모델을 채택합니다. 기본 플랜은 월 29,000원으로 AI 맞춤 학습, 무제한 문제 풀이, 학부모 리포트를 제공합니다. 프리미엄 플랜은 월 49,000원으로 1:1 화상 튜터링과 심화 콘텐츠가 추가됩니다. 연간 구독 시 17% 할인을 적용하여 장기 구독을 유도하고, 14일 무료 체험으로 전환율을 높입니다.",
                                        true, null, null),
                                new WizardQuestionDto("revenue-streams", "textarea", "수익원",
                                        "다양한 수익 채널 (우선순위별)",
                                        "1. 구독 수익(75%): B2C 개인 구독 월 29,000원~49,000원, 예상 ARPU 35,000원입니다.\n2. B2B 라이선스(15%): 학원, 교육청 대상 기관 라이선스 제공으로 학생당 월 15,000원을 책정합니다.\n3. 프리미엄 콘텐츠(10%): 유명 강사 특강, 입시 컨설팅 등 추가 콘텐츠 판매로 건당 30,000원~100,000원을 목표로 합니다.",
                                        true, null, null),
                                new WizardQuestionDto("tech-feasibility", "textarea", "기술적 실현 가능성",
                                        "핵심 기술, 개발 현황, 기술적 장벽 및 극복 방안",
                                        "핵심 AI 알고리즘은 자체 개발 완료되어 특허 출원을 마쳤습니다. 딥러닝 기반 학습 패턴 분석 모델은 정확도 92%를 달성하였고, 현재 베타 테스트에서 사용자 만족도 4.5/5를 기록 중입니다. 기술 스택은 Python/TensorFlow 기반 AI 엔진, React Native 모바일 앱, AWS 클라우드 인프라를 사용합니다. 향후 GPT-4 연동을 통해 AI 튜터 기능을 고도화할 예정입니다.",
                                        true, null, null)
                        )
                ),
                new WizardStepDto(
                        4,
                        "사업화 전략",
                        "Scale-up: 성장 전략과 시장 진입 계획을 수립합니다",
                        "🚀",
                        "pending",
                        List.of(
                                new WizardQuestionDto("go-to-market", "textarea", "시장 진입 전략 (GTM)",
                                        "초기 시장 진입 방법과 채널",
                                        "1단계(6개월)는 수도권 중학생 대상 집중 공략으로 강남/서초/목동 학원가 제휴와 학부모 커뮤니티 마케팅을 진행합니다. 2단계(12개월)는 전국 확대로 지방 거점 도시와 온라인 채널을 통해 확산합니다. 3단계(24개월)는 해외 진출로 중화권 및 동남아 시장을 타겟으로 합니다. 초기 고객 획득은 추천 보상 프로그램과 인플루언서 협업을 통해 진행합니다.",
                                        true, null, null),
                                new WizardQuestionDto("marketing-strategy", "textarea", "마케팅 및 고객 획득 전략",
                                        "고객 획득 채널, CAC 최적화 방안",
                                        "디지털 마케팅으로 네이버/카카오 검색광고 및 인스타그램 타겟팅 광고를 집행합니다. 바이럴 마케팅으로 친구 추천 시 양쪽 1개월 무료 혜택을 제공합니다. 콘텐츠 마케팅으로 유튜브 교육 채널을 운영하여 학습법 콘텐츠로 유입을 유도합니다. 제휴 마케팅으로 학원, 교육청, 학교와 파트너십을 체결합니다. 예상 CAC는 50,000원, LTV는 420,000원으로 LTV/CAC 비율 8.4배를 목표로 합니다.",
                                        true, null, null),
                                new WizardQuestionDto("growth-strategy", "textarea", "성장 전략",
                                        "스케일업 계획, 신규 시장 확장, 다각화 전략",
                                        "1년차는 B2C 중심으로 개인 사용자 10,000명 확보에 집중합니다. 2년차는 B2B 확장으로 학원 50곳, 교육청 5곳과 계약을 체결하고 기업교육 시장에 진입합니다. 3년차는 해외 진출로 베트남, 인도네시아 시장을 타겟하며 현지 파트너와 합작 법인을 설립합니다. 동시에 성인 교육(어학, 자격증) 영역으로 서비스를 다각화하여 플랫폼 확장을 추진합니다.",
                                        true, null, null),
                                new WizardQuestionDto("milestones", "textarea", "주요 마일스톤",
                                        "향후 1-3년간 핵심 목표 및 KPI",
                                        "3개월 후: MVP 정식 출시, 베타 테스터 500명 전환, 제품-시장 적합성 검증을 완료합니다.\n6개월 후: 유료 사용자 1,000명 달성, 월 매출 3,000만원을 기록합니다.\n12개월 후: 유료 사용자 5,000명 달성, 월 매출 1.5억원, 시리즈A 30억원 투자 유치를 목표로 합니다.\n24개월 후: 유료 사용자 20,000명 달성, 월 매출 6억원, B2B 계약 50건을 달성합니다.\n36개월 후: 누적 매출 100억원, 해외 진출 완료, 시리즈B 준비를 진행합니다.",
                                        true, null, null),
                                new WizardQuestionDto("partnership", "textarea", "파트너십 및 네트워크",
                                        "핵심 파트너, 협력사, 네트워크 현황",
                                        "서울시교육청과 AI 학습 플랫폼 시범 사업 MOU를 체결하였습니다. 강남 대형 학원 3곳과 파일럿 프로그램을 진행 중입니다. 교보문고와 학습 콘텐츠 제휴 협의를 진행 중이며, AWS 스타트업 프로그램에 선정되어 1년간 클라우드 크레딧을 지원받습니다. 한국교육학술정보원(KERIS)과 데이터 연계 협력을 논의 중입니다.",
                                        false, null, null)
                        )
                ),
                new WizardStepDto(
                        5,
                        "팀 역량",
                        "Team: 팀 구성과 실행 역량을 소개합니다",
                        "👥",
                        "pending",
                        List.of(
                                new WizardQuestionDto("team-composition", "textarea", "팀 구성",
                                        "현재 팀원 구성과 역할 (직무, 경력, 전문성)",
                                        "CEO 김민수(38세): 에듀테크 분야 12년 경력, 전 메가스터디 사업본부장, 서울대 경영학 석사입니다.\nCTO 이정호(35세): AI/ML 전문가, 전 네이버 AI Lab 연구원, KAIST 컴퓨터공학 박사입니다.\nCPO 박서연(33세): UX 디자인 8년 경력, 전 토스 프로덕트 디자이너, 홍익대 디자인학과 졸업입니다.\n개발팀 3명: 백엔드 2명(평균 경력 5년), 프론트엔드 1명(경력 4년)입니다.",
                                        true, null, null),
                                new WizardQuestionDto("team-expertise", "textarea", "핵심 역량 및 경험",
                                        "해당 사업을 성공시킬 수 있는 팀의 핵심 강점",
                                        "교육 도메인 전문성으로 CEO의 12년 에듀테크 경험과 학원 운영 네트워크가 있습니다. 기술 역량으로 CTO의 AI 논문 15편 게재 및 관련 특허 3건을 보유하고 있습니다. 제품 개발 역량으로 CPO의 DAU 100만 서비스 설계 경험이 있습니다. 창업 경험으로 CEO의 전 스타트업 Exit 경험(2019년 30억 매각)이 있습니다. 산업 네트워크로 교육청, 대형 학원, 출판사 의사결정권자와의 관계가 구축되어 있습니다.",
                                        true, null, null),
                                new WizardQuestionDto("team-track-record", "textarea", "주요 성과 및 경력",
                                        "팀원들의 관련 분야 성과, 수상 이력, 프로젝트 경험",
                                        "CEO 김민수: 전 스타트업 학습앱 MAU 50만 달성, 2019년 30억에 매각하여 Exit 경험이 있습니다. 2022년 중소벤처기업부 장관상을 수상했습니다.\nCTO 이정호: AI 분야 국제 논문 15편 게재, 네이버 AI Lab에서 추천 알고리즘 개발하여 CTR 35% 향상시켰습니다. AI 관련 특허 3건을 보유하고 있습니다.\nCPO 박서연: 토스 결제 UX 설계로 전환율 28% 향상시켰으며, 2023년 한국디자인진흥원 UX 어워드를 수상했습니다.",
                                        true, null, null),
                                new WizardQuestionDto("hiring-plan", "textarea", "채용 계획",
                                        "향후 필요한 인력과 채용 계획",
                                        "6개월 내: 프론트엔드 개발자 2명, 마케터 1명 채용 예정.\n1년 내: AI 엔지니어 1명, 영업 담당 2명, 콘텐츠 기획자 1명 채용 예정.\n2년 내: 해외 사업 담당 1명, CS팀 3명 채용 예정.",
                                        false, null, null),
                                new WizardQuestionDto("advisors", "textarea", "자문단 및 멘토",
                                        "외부 전문가, 자문위원, 멘토 현황",
                                        "기술 자문으로 서울대 AI연구원 김태호 교수님이 AI 알고리즘 고도화를 지원합니다.\n경영 자문으로 전 야나두 대표 이상호 대표님이 에듀테크 사업 전략을 자문합니다.\n투자 자문으로 스프링캠프 파트너 박성민 대표님이 투자 유치 전략을 자문합니다.\n법률 자문으로 법무법인 율촌 김정현 변호사가 계약 및 IP 관련 법률 자문을 담당합니다.",
                                        false, null, null)
                        )
                ),
                new WizardStepDto(
                        6,
                        "재무 계획",
                        "재무 시뮬레이션과 손익분기점을 분석합니다. 완료 후 AI 사업계획서를 생성합니다.",
                        "💰",
                        "pending",
                        List.of(
                                new WizardQuestionDto("financial-simulation", "number", "재무 시뮬레이션",
                                        "아래 재무 계산기를 사용하여 입력해주세요",
                                        null,
                                        false,
                                        null,
                                        new QuestionValidationDto(0, null, null)
                                )
                        )
                )
        );
    }

    public List<PMFQuestionDto> getPmfQuestions() {
        return List.of(
                pmf("pmf-1", "타겟 고객이 명확하게 정의되어 있습니까?"),
                pmf("pmf-2", "고객이 겪는 문제를 직접 인터뷰를 통해 검증했습니까?"),
                pmf("pmf-3", "우리 솔루션 없이는 고객이 큰 불편을 겪습니까?"),
                pmf("pmf-4", "경쟁사 대비 명확한 차별화 포인트가 있습니까?"),
                pmf("pmf-5", "유료로 전환할 의향이 있는 얼리어답터가 있습니까?"),
                pmf("pmf-6", "고객 추천율(NPS)이 높습니까?"),
                pmf("pmf-7", "시장 규모가 충분히 크고 성장하고 있습니까?"),
                pmf("pmf-8", "수익 모델이 명확하고 검증되었습니까?"),
                pmf("pmf-9", "제품/서비스를 지속적으로 개선할 수 있는 역량이 있습니까?"),
                pmf("pmf-10", "팀이 해당 시장에 대한 전문성을 보유하고 있습니까?")
        );
    }

    private PMFQuestionDto pmf(String id, String question) {
        return new PMFQuestionDto(
                id,
                question,
                List.of(
                        new PMFQuestionOptionDto(1, "전혀 아니다"),
                        new PMFQuestionOptionDto(2, "아니다"),
                        new PMFQuestionOptionDto(3, "보통이다"),
                        new PMFQuestionOptionDto(4, "그렇다"),
                        new PMFQuestionOptionDto(5, "매우 그렇다")
                )
        );
    }
}
