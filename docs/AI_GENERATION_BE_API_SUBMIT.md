# AI 사업계획서 생성 백엔드 API 요청 스펙

## 개요

이 문서는 프론트엔드에서 백엔드 AI 사업계획서 생성 API를 호출할 때 전송하는 JSON 데이터 형식을 정의합니다.

- **API Endpoint**: `POST /api/v1/business-plan/generate`
- **Content-Type**: `application/json`
- **인증**: Bearer Token (JWT)

---

## 1. 요청 JSON 스키마 (전체)

```json
{
  "requestInfo": {
    "templateType": "pre-startup",
    "generatedAt": "2025-12-17T12:30:00.000Z",
    "userId": "user-uuid-here",
    "projectId": "project-uuid-here"
  },

  "businessPlanData": {
    "step1_problemRecognition": {
      "itemName": "AI 기반 맞춤형 학습 플랫폼 LearnAI",
      "itemSummary": "LearnAI는 학생 개개인의 학습 패턴과 취약점을 AI가 실시간 분석하여 최적화된 맞춤형 학습 경로를 제공함으로써 학습 효율을 40% 이상 향상시키는 혁신적인 에듀테크 플랫폼입니다.",
      "problem": "현재 교육 시장에서 학생들은 획일화된 커리큘럼으로 인해 개인별 학습 속도와 이해도 차이를 고려받지 못하고 있습니다. 대형 학원의 일방향 수업은 상위권 학생에게는 지루하고, 하위권 학생에게는 따라가기 어려운 구조입니다. 또한 학부모는 자녀의 실질적인 학습 현황을 파악하기 어려워 적절한 교육 지원을 하지 못하고 있습니다.",
      "problemEvidence": "한국교육개발원 2024년 조사에 따르면, 중고등학생 78%가 현재 학습 방식에 불만족하며, 학부모 65%는 자녀의 실제 학습 수준을 정확히 파악하지 못한다고 응답했습니다. 사교육비 지출은 연평균 23조원에 달하지만, 학업 성취도 향상은 평균 12%에 불과합니다. 이는 맞춤형 교육의 부재로 인한 비효율적 학습의 결과입니다.",
      "targetCustomer": "1차 타겟은 중학생 자녀를 둔 35-45세 학부모입니다. 이들은 자녀의 학업 향상에 높은 관심을 가지고 있으며, 월평균 30-50만원의 사교육비를 지출합니다. 국내 중학생 수 약 130만명, 학부모 가구 약 110만 가구가 잠재 고객입니다. 2차 타겟으로 고등학생 및 초등학교 고학년으로 확장 계획입니다."
    },

    "step2_marketAnalysis": {
      "marketSize": "TAM(전체 시장): 국내 사교육 시장 약 25조원, 글로벌 에듀테크 시장 350조원입니다.\nSAM(유효 시장): 국내 온라인 교육 시장 약 5조원, AI 학습 플랫폼 시장 1조원입니다.\nSOM(목표 시장): 1년차 100억원(시장점유율 1%), 3년차 500억원(시장점유율 5%) 달성을 목표로 합니다.",
      "marketTrend": "COVID-19 이후 온라인 교육 시장이 연평균 22% 급성장하고 있습니다. 특히 AI 기반 맞춤형 학습 서비스는 2023년 대비 2027년까지 3배 성장이 예상됩니다. 정부의 에듀테크 육성 정책으로 연간 2,000억원의 지원이 계획되어 있으며, 학부모들의 디지털 교육 수용도가 크게 높아져 온라인 학습 서비스 이용률이 65%를 넘어섰습니다.",
      "competitors": "1. 메가스터디: 강점은 브랜드 인지도와 콘텐츠 양, 약점은 개인화 부족과 높은 가격입니다.\n2. 대교 스마트러닝: 강점은 오프라인 네트워크, 약점은 AI 기술 부재와 구식 UI입니다.\n3. 클래스101(에듀): 강점은 다양한 강좌, 약점은 학습 체계성 부족입니다.\n4. Khan Academy(해외): 강점은 무료 콘텐츠, 약점은 한국 교육과정 미반영입니다.",
      "competitiveAdvantage": "첫째, 자체 개발한 적응형 AI 알고리즘으로 학생별 학습 경로를 실시간 최적화합니다(특허 출원 완료). 둘째, 학습 행동 데이터 기반으로 취약 영역을 0.1단원 수준까지 정밀 진단합니다. 셋째, 학부모 대시보드를 통해 자녀의 학습 현황을 실시간 공유하여 가정 내 학습 지원을 강화합니다. 넷째, 한국 교육과정 100% 연계로 내신과 수능 대비가 동시에 가능합니다."
    },

    "step3_solutionFeasibility": {
      "solution": "LearnAI 플랫폼은 학생의 학습 패턴을 AI가 분석하여 맞춤형 학습 경로를 제공합니다. 학생이 문제를 풀 때의 오답 패턴, 풀이 시간, 재시도 횟수를 분석하여 취약 단원을 정밀 진단하고, 해당 영역을 보완할 수 있는 맞춤형 문제와 강의를 자동 추천합니다. 또한 학부모 앱을 통해 자녀의 학습 진도와 성취도를 실시간으로 확인할 수 있어 효과적인 학습 지원이 가능합니다.",
      "businessModel": "B2C 구독형 SaaS 모델을 채택합니다. 기본 플랜은 월 29,000원으로 AI 맞춤 학습, 무제한 문제 풀이, 학부모 리포트를 제공합니다. 프리미엄 플랜은 월 49,000원으로 1:1 화상 튜터링과 심화 콘텐츠가 추가됩니다. 연간 구독 시 17% 할인을 적용하여 장기 구독을 유도하고, 14일 무료 체험으로 전환율을 높입니다.",
      "revenueStreams": "1. 구독 수익(75%): B2C 개인 구독 월 29,000원~49,000원, 예상 ARPU 35,000원입니다.\n2. B2B 라이선스(15%): 학원, 교육청 대상 기관 라이선스 제공으로 학생당 월 15,000원을 책정합니다.\n3. 프리미엄 콘텐츠(10%): 유명 강사 특강, 입시 컨설팅 등 추가 콘텐츠 판매로 건당 30,000원~100,000원을 목표로 합니다.",
      "techFeasibility": "핵심 AI 알고리즘은 자체 개발 완료되어 특허 출원을 마쳤습니다. 딥러닝 기반 학습 패턴 분석 모델은 정확도 92%를 달성하였고, 현재 베타 테스트에서 사용자 만족도 4.5/5를 기록 중입니다. 기술 스택은 Python/TensorFlow 기반 AI 엔진, React Native 모바일 앱, AWS 클라우드 인프라를 사용합니다. 향후 GPT-4 연동을 통해 AI 튜터 기능을 고도화할 예정입니다."
    },

    "step4_commercializationStrategy": {
      "goToMarket": "1단계(6개월)는 수도권 중학생 대상 집중 공략으로 강남/서초/목동 학원가 제휴와 학부모 커뮤니티 마케팅을 진행합니다. 2단계(12개월)는 전국 확대로 지방 거점 도시와 온라인 채널을 통해 확산합니다. 3단계(24개월)는 해외 진출로 중화권 및 동남아 시장을 타겟으로 합니다. 초기 고객 획득은 추천 보상 프로그램과 인플루언서 협업을 통해 진행합니다.",
      "marketingStrategy": "디지털 마케팅으로 네이버/카카오 검색광고 및 인스타그램 타겟팅 광고를 집행합니다. 바이럴 마케팅으로 친구 추천 시 양쪽 1개월 무료 혜택을 제공합니다. 콘텐츠 마케팅으로 유튜브 교육 채널을 운영하여 학습법 콘텐츠로 유입을 유도합니다. 제휴 마케팅으로 학원, 교육청, 학교와 파트너십을 체결합니다. 예상 CAC는 50,000원, LTV는 420,000원으로 LTV/CAC 비율 8.4배를 목표로 합니다.",
      "growthStrategy": "1년차는 B2C 중심으로 개인 사용자 10,000명 확보에 집중합니다. 2년차는 B2B 확장으로 학원 50곳, 교육청 5곳과 계약을 체결하고 기업교육 시장에 진입합니다. 3년차는 해외 진출로 베트남, 인도네시아 시장을 타겟하며 현지 파트너와 합작 법인을 설립합니다. 동시에 성인 교육(어학, 자격증) 영역으로 서비스를 다각화하여 플랫폼 확장을 추진합니다.",
      "milestones": "3개월 후: MVP 정식 출시, 베타 테스터 500명 전환, 제품-시장 적합성 검증을 완료합니다.\n6개월 후: 유료 사용자 1,000명 달성, 월 매출 3,000만원을 기록합니다.\n12개월 후: 유료 사용자 5,000명 달성, 월 매출 1.5억원, 시리즈A 30억원 투자 유치를 목표로 합니다.\n24개월 후: 유료 사용자 20,000명 달성, 월 매출 6억원, B2B 계약 50건을 달성합니다.\n36개월 후: 누적 매출 100억원, 해외 진출 완료, 시리즈B 준비를 진행합니다.",
      "partnership": "서울시교육청과 AI 학습 플랫폼 시범 사업 MOU를 체결하였습니다. 강남 대형 학원 3곳(청담어학원, 대치씨앤씨, 목동하이스트)과 파일럿 프로그램을 진행 중입니다. 교보문고와 학습 콘텐츠 제휴 협의를 진행 중이며, AWS 스타트업 프로그램에 선정되어 1년간 클라우드 크레딧을 지원받습니다. 한국교육학술정보원(KERIS)과 데이터 연계 협력을 논의 중입니다."
    },

    "step5_teamCapability": {
      "teamComposition": "CEO 김민수(38세): 에듀테크 분야 12년 경력, 전 메가스터디 사업본부장, 서울대 경영학 석사입니다.\nCTO 이정호(35세): AI/ML 전문가, 전 네이버 AI Lab 연구원, KAIST 컴퓨터공학 박사입니다.\nCPO 박서연(33세): UX 디자인 8년 경력, 전 토스 프로덕트 디자이너, 홍익대 디자인학과 졸업입니다.\n개발팀 3명: 백엔드 2명(평균 경력 5년), 프론트엔드 1명(경력 4년)입니다.",
      "teamExpertise": "교육 도메인 전문성으로 CEO의 12년 에듀테크 경험과 학원 운영 네트워크가 있습니다. 기술 역량으로 CTO의 AI 논문 15편 게재 및 관련 특허 3건을 보유하고 있습니다. 제품 개발 역량으로 CPO의 DAU 100만 서비스 설계 경험이 있습니다. 창업 경험으로 CEO의 전 스타트업 Exit 경험(2019년 30억 매각)이 있습니다. 산업 네트워크로 교육청, 대형 학원, 출판사 의사결정권자와의 관계가 구축되어 있습니다.",
      "teamTrackRecord": "CEO 김민수: 전 스타트업 학습앱 MAU 50만 달성, 2019년 30억에 매각하여 Exit 경험이 있습니다. 2022년 중소벤처기업부 장관상을 수상했습니다.\nCTO 이정호: AI 분야 국제 논문 15편 게재, 네이버 AI Lab에서 추천 알고리즘 개발하여 CTR 35% 향상시켰습니다. AI 관련 특허 3건을 보유하고 있습니다.\nCPO 박서연: 토스 결제 UX 설계로 전환율 28% 향상시켰으며, 2023년 한국디자인진흥원 UX 어워드를 수상했습니다.",
      "hiringPlan": "6개월 내: 프론트엔드 개발자 2명(연봉 6,000만원~7,000만원), 마케터 1명(연봉 5,000만원)을 채용 예정입니다.\n1년 내: AI 엔지니어 1명(연봉 8,000만원), 영업 담당 2명(연봉 5,000만원), 콘텐츠 기획자 1명(연봉 4,500만원)을 채용 예정입니다.\n2년 내: 해외 사업 담당 1명, CS팀 3명을 채용하여 총 인원을 현재 6명에서 17명으로 확대할 계획입니다.",
      "advisors": "기술 자문으로 서울대 AI연구원 김태호 교수님이 AI 알고리즘 고도화를 지원합니다.\n경영 자문으로 전 야나두 대표 이상호 대표님이 에듀테크 사업 전략을 자문합니다.\n투자 자문으로 스프링캠프 파트너 박성민 대표님이 투자 유치 전략을 자문합니다.\n법률 자문으로 법무법인 율촌 김정현 변호사가 계약 및 IP 관련 법률 자문을 담당합니다."
    },

    "step6_financialPlan": {
      "inputs": {
        "initialCustomers": 100,
        "pricePerCustomer": 35000,
        "customerAcquisitionCost": 50000,
        "monthlyFixedCosts": 15000000,
        "variableCostRate": 0.1,
        "monthlyChurnRate": 0.05
      },
      "calculatedMetrics": {
        "monthlyRevenue": 3500000,
        "yearlyRevenue": 42000000,
        "ltv": 420000,
        "ltvCacRatio": 8.4,
        "breakEvenCustomers": 500,
        "breakEvenMonths": 18,
        "grossMargin": 0.9
      }
    }
  },

  "generationOptions": {
    "tone": "professional",
    "targetLength": "detailed",
    "outputFormat": "markdown",
    "language": "ko",
    "sections": [
      "executive_summary",
      "problem_analysis",
      "market_analysis",
      "solution_overview",
      "business_model",
      "go_to_market",
      "team_introduction",
      "financial_projection",
      "risk_analysis",
      "conclusion"
    ]
  }
}
```

---

## 2. 필드 상세 설명

### 2.1 requestInfo (요청 메타데이터)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `templateType` | string | ✅ | 템플릿 유형: `pre-startup`, `early-startup`, `bank-loan` |
| `generatedAt` | string (ISO 8601) | ✅ | 요청 시간 |
| `userId` | string (UUID) | ✅ | 사용자 고유 ID |
| `projectId` | string (UUID) | ✅ | 프로젝트 고유 ID |

### 2.2 businessPlanData (사업계획서 입력 데이터)

#### step1_problemRecognition (문제 인식)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `itemName` | string | ✅ | 사업 아이템명 |
| `itemSummary` | string | ✅ | 한 문장 요약 (핵심 가치 제안) |
| `problem` | string | ✅ | 해결하고자 하는 문제 (Pain Point) |
| `problemEvidence` | string | ✅ | 문제의 근거 (데이터, 통계, 사례) |
| `targetCustomer` | string | ✅ | 타겟 고객 (페르소나, 시장 규모) |

#### step2_marketAnalysis (시장 분석)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `marketSize` | string | ✅ | 시장 규모 (TAM/SAM/SOM) |
| `marketTrend` | string | ✅ | 시장 트렌드 (3-5년 변화, 전망) |
| `competitors` | string | ✅ | 경쟁사 분석 (3-5개 경쟁사) |
| `competitiveAdvantage` | string | ✅ | 경쟁 우위 (핵심 차별화 요소) |

#### step3_solutionFeasibility (실현 방안)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `solution` | string | ✅ | 솔루션 개요 |
| `businessModel` | string | ✅ | 비즈니스 모델 |
| `revenueStreams` | string | ✅ | 수익원 (우선순위별) |
| `techFeasibility` | string | ✅ | 기술적 실현 가능성 |

#### step4_commercializationStrategy (사업화 전략)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `goToMarket` | string | ✅ | 시장 진입 전략 (GTM) |
| `marketingStrategy` | string | ✅ | 마케팅 및 고객 획득 전략 |
| `growthStrategy` | string | ✅ | 성장 전략 |
| `milestones` | string | ✅ | 주요 마일스톤 (1-3년) |
| `partnership` | string | ❌ | 파트너십 및 네트워크 |

#### step5_teamCapability (팀 역량)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `teamComposition` | string | ✅ | 팀 구성 (직무, 경력, 전문성) |
| `teamExpertise` | string | ✅ | 핵심 역량 및 경험 |
| `teamTrackRecord` | string | ✅ | 주요 성과 및 경력 |
| `hiringPlan` | string | ❌ | 채용 계획 |
| `advisors` | string | ❌ | 자문단 및 멘토 |

#### step6_financialPlan (재무 계획)

**inputs (입력값)**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `initialCustomers` | number | ✅ | 초기 고객 수 |
| `pricePerCustomer` | number | ✅ | 고객당 가격 (원/월) |
| `customerAcquisitionCost` | number | ✅ | 고객 획득 비용 (CAC) |
| `monthlyFixedCosts` | number | ✅ | 월 고정비용 |
| `variableCostRate` | number | ✅ | 변동비율 (0-1) |
| `monthlyChurnRate` | number | ✅ | 월 이탈률 (0-1) |

**calculatedMetrics (계산된 지표)**

| 필드 | 타입 | 설명 |
|------|------|------|
| `monthlyRevenue` | number | 월 매출 |
| `yearlyRevenue` | number | 연 매출 |
| `ltv` | number | 고객 생애 가치 |
| `ltvCacRatio` | number | LTV/CAC 비율 |
| `breakEvenCustomers` | number | 손익분기점 고객 수 |
| `breakEvenMonths` | number | 손익분기점 도달 개월 |
| `grossMargin` | number | 매출총이익률 |

### 2.3 generationOptions (생성 옵션)

| 필드 | 타입 | 필수 | 설명 | 허용값 |
|------|------|------|------|--------|
| `tone` | string | ✅ | 문서 톤앤매너 | `professional`, `casual`, `formal` |
| `targetLength` | string | ✅ | 문서 분량 | `brief`, `standard`, `detailed` |
| `outputFormat` | string | ✅ | 출력 형식 | `markdown`, `html`, `plain` |
| `language` | string | ✅ | 출력 언어 | `ko`, `en` |
| `sections` | string[] | ✅ | 생성할 섹션 목록 | 아래 참조 |

**sections 허용값:**
- `executive_summary` - 요약
- `problem_analysis` - 문제 분석
- `market_analysis` - 시장 분석
- `solution_overview` - 솔루션 개요
- `business_model` - 비즈니스 모델
- `go_to_market` - 시장 진입 전략
- `team_introduction` - 팀 소개
- `financial_projection` - 재무 전망
- `risk_analysis` - 리스크 분석
- `conclusion` - 결론

---

## 3. TypeScript 인터페이스

```typescript
// ============================================
// API 요청 타입 정의
// ============================================

/** 템플릿 유형 */
type TemplateType = 'pre-startup' | 'early-startup' | 'bank-loan';

/** 문서 톤앤매너 */
type ToneType = 'professional' | 'casual' | 'formal';

/** 문서 분량 */
type LengthType = 'brief' | 'standard' | 'detailed';

/** 출력 형식 */
type OutputFormatType = 'markdown' | 'html' | 'plain';

/** 생성 가능 섹션 */
type SectionType =
  | 'executive_summary'
  | 'problem_analysis'
  | 'market_analysis'
  | 'solution_overview'
  | 'business_model'
  | 'go_to_market'
  | 'team_introduction'
  | 'financial_projection'
  | 'risk_analysis'
  | 'conclusion';

/** 요청 메타데이터 */
interface RequestInfo {
  templateType: TemplateType;
  generatedAt: string;
  userId: string;
  projectId: string;
}

/** 1단계: 문제 인식 */
interface Step1ProblemRecognition {
  itemName: string;
  itemSummary: string;
  problem: string;
  problemEvidence: string;
  targetCustomer: string;
}

/** 2단계: 시장 분석 */
interface Step2MarketAnalysis {
  marketSize: string;
  marketTrend: string;
  competitors: string;
  competitiveAdvantage: string;
}

/** 3단계: 실현 방안 */
interface Step3SolutionFeasibility {
  solution: string;
  businessModel: string;
  revenueStreams: string;
  techFeasibility: string;
}

/** 4단계: 사업화 전략 */
interface Step4CommercializationStrategy {
  goToMarket: string;
  marketingStrategy: string;
  growthStrategy: string;
  milestones: string;
  partnership?: string;
}

/** 5단계: 팀 역량 */
interface Step5TeamCapability {
  teamComposition: string;
  teamExpertise: string;
  teamTrackRecord: string;
  hiringPlan?: string;
  advisors?: string;
}

/** 재무 입력값 */
interface FinancialInputs {
  initialCustomers: number;
  pricePerCustomer: number;
  customerAcquisitionCost: number;
  monthlyFixedCosts: number;
  variableCostRate: number;
  monthlyChurnRate: number;
}

/** 계산된 재무 지표 */
interface CalculatedMetrics {
  monthlyRevenue: number;
  yearlyRevenue: number;
  ltv: number;
  ltvCacRatio: number;
  breakEvenCustomers: number;
  breakEvenMonths: number;
  grossMargin: number;
}

/** 6단계: 재무 계획 */
interface Step6FinancialPlan {
  inputs: FinancialInputs;
  calculatedMetrics: CalculatedMetrics;
}

/** 사업계획서 데이터 */
interface BusinessPlanData {
  step1_problemRecognition: Step1ProblemRecognition;
  step2_marketAnalysis: Step2MarketAnalysis;
  step3_solutionFeasibility: Step3SolutionFeasibility;
  step4_commercializationStrategy: Step4CommercializationStrategy;
  step5_teamCapability: Step5TeamCapability;
  step6_financialPlan: Step6FinancialPlan;
}

/** 생성 옵션 */
interface GenerationOptions {
  tone: ToneType;
  targetLength: LengthType;
  outputFormat: OutputFormatType;
  language: 'ko' | 'en';
  sections: SectionType[];
}

/** AI 사업계획서 생성 요청 */
interface BusinessPlanGenerationRequest {
  requestInfo: RequestInfo;
  businessPlanData: BusinessPlanData;
  generationOptions: GenerationOptions;
}
```

---

## 4. 응답 JSON 스키마 (전체)

```json
{
  "success": true,
  "data": {
    "businessPlanId": "bp-2025-12-17-uuid-here",
    "projectId": "project-uuid-here",
    "generatedAt": "2025-12-17T12:35:00.000Z",
    "templateType": "pre-startup",
    "sections": [
      {
        "id": "section-1",
        "title": "1. 사업 개요",
        "content": "### 1.1 사업 아이템\n\n**AI 기반 맞춤형 학습 플랫폼 \"LearnAI\"**\n\nLearnAI는 학생 개개인의 학습 패턴, 강점, 약점을 AI가 분석하여 최적화된 학습 경로를 제공하는 혁신적인 에듀테크 플랫폼입니다.\n\n### 1.2 핵심 가치 제안\n\n- **개인화 학습 경로**: 학생별 맞춤 커리큘럼 자동 생성\n- **실시간 취약점 분석**: AI 기반 학습 패턴 분석 및 개선 방안 제시\n- **학부모 대시보드**: 자녀의 학습 현황을 실시간으로 확인\n\n### 1.3 비전\n\n2027년까지 국내 1위 AI 학습 플랫폼으로 성장하여 100만 명의 학생에게 맞춤형 교육 기회를 제공합니다.",
        "order": 1
      },
      {
        "id": "section-2",
        "title": "2. 시장 분석",
        "content": "### 2.1 시장 규모\n\n**TAM (Total Addressable Market)**\n- 국내 전체 교육 시장: 약 25조 원 (2024년 기준)\n\n**SAM (Serviceable Available Market)**\n- 온라인 교육 시장: 약 5조 원\n- 중고등학생 대상 온라인 교육: 약 2조 원\n\n**SOM (Serviceable Obtainable Market)**\n- 1년차 목표: 100억 원 (시장 점유율 0.5%)\n- 3년차 목표: 500억 원 (시장 점유율 2.5%)\n\n### 2.2 시장 트렌드\n\n1. **에듀테크 시장 급성장**: COVID-19 이후 온라인 교육 시장이 연평균 22% 성장\n2. **개인화 교육 수요 증가**: 학생별 맞춤 교육에 대한 학부모 니즈 확대\n3. **AI 기술 활용 확대**: 교육 분야 AI 도입이 글로벌 트렌드로 자리잡음\n\n### 2.3 경쟁 분석\n\n| 경쟁사 | 강점 | 약점 | 우리의 차별점 |\n|--------|------|------|---------------|\n| A사(메가스터디) | 브랜드 인지도 | 개인화 부족 | AI 기반 맞춤 학습 |\n| B사(대교) | 오프라인 네트워크 | 온라인 전환 미흡 | 완전한 온라인 플랫폼 |\n| C사(해외 플랫폼) | 글로벌 기술력 | 한국 교육과정 미반영 | 한국형 커리큘럼 |",
        "order": 2
      },
      {
        "id": "section-3",
        "title": "3. 사업 모델",
        "content": "### 3.1 비즈니스 모델\n\n**B2C 구독형 SaaS 모델**\n\n- 월 구독료: 29,000원\n- 연간 구독 할인: 290,000원 (17% 할인)\n- 무료 체험: 14일\n\n### 3.2 수익원\n\n1. **구독 수익 (75%)**\n   - 기본 플랜: 월 29,000원\n   - 프리미엄 플랜: 월 49,000원 (1:1 화상 과외 포함)\n\n2. **기업 제휴 (15%)**\n   - 학원, 교육청 대상 B2B 라이선스\n\n3. **프리미엄 콘텐츠 (10%)**\n   - 유명 강사 특강, 입시 컨설팅\n\n### 3.3 고객 획득 전략\n\n1. **바이럴 마케팅**\n   - 추천 보상 프로그램: 친구 초대 시 1개월 무료\n   - SNS 인플루언서 협업\n\n2. **콘텐츠 마케팅**\n   - 유튜브 교육 콘텐츠\n   - 블로그 입시 정보 제공\n\n3. **파트너십**\n   - 학원/교육청 제휴\n   - 학습지/교재 업체 협력",
        "order": 3
      },
      {
        "id": "section-4",
        "title": "4. 재무 계획",
        "content": "### 4.1 초기 투자 계획\n\n**총 필요 자금: 5억 원**\n\n- 기술 개발: 2억 원 (40%)\n- 마케팅: 1.5억 원 (30%)\n- 운영비: 1억 원 (20%)\n- 예비비: 0.5억 원 (10%)\n\n### 4.2 손익 전망\n\n| 구분 | 1년차 | 2년차 | 3년차 |\n|------|-------|-------|-------|\n| 매출 | 3억 원 | 15억 원 | 50억 원 |\n| 영업비용 | 5억 원 | 12억 원 | 35억 원 |\n| 영업이익 | -2억 원 | 3억 원 | 15억 원 |\n\n### 4.3 주요 가정\n\n- 월 평균 신규 가입자: 1년차 200명 → 3년차 1,500명\n- 이탈률 (Churn Rate): 월 5%\n- 객단가 (ARPU): 35,000원\n- CAC (고객획득비용): 50,000원\n- LTV (생애가치): 420,000원 (평균 12개월 구독 가정)\n- **LTV/CAC 비율: 8.4** (우수)\n\n### 4.4 손익분기점\n\n- BEP 달성 시점: 창업 후 18개월\n- BEP 고객 수: 약 1,200명 (누적)",
        "order": 4
      },
      {
        "id": "section-5",
        "title": "5. 실행 계획",
        "content": "### 5.1 주요 마일스톤\n\n**Phase 1: MVP 개발 (3개월)**\n- 핵심 AI 알고리즘 개발\n- 베타 테스터 100명 모집\n- 피드백 기반 개선\n\n**Phase 2: 정식 출시 (6개월)**\n- 마케팅 캠페인 시작\n- 첫 1,000명 유저 확보\n- 월 구독 수익 3천만 원 달성\n\n**Phase 3: 성장 가속화 (12개월)**\n- 기업 제휴 확대\n- 누적 유저 10,000명\n- 시리즈 A 투자 유치 (30억 원)\n\n### 5.2 팀 구성\n\n- **CEO (1명)**: 전략 및 경영 총괄\n- **CTO (1명)**: 기술 개발 총괄\n- **AI 엔지니어 (2명)**: 알고리즘 개발\n- **백엔드 개발자 (2명)**: 서버 및 인프라\n- **프론트엔드 개발자 (2명)**: 웹/앱 UI 개발\n- **마케터 (2명)**: 콘텐츠 및 성장 마케팅\n\n### 5.3 리스크 관리\n\n| 리스크 | 대응 방안 |\n|--------|-----------|\n| 기술 개발 지연 | 애자일 개발 방법론 도입, 외주 개발 병행 |\n| 고객 확보 부진 | 무료 체험 확대, 바이럴 이벤트 강화 |\n| 경쟁 심화 | 지속적 기술 혁신, 고객 락인 전략 |\n| 법적 규제 | 법률 자문, 개인정보보호 강화 |",
        "order": 5
      },
      {
        "id": "section-6",
        "title": "6. 결론",
        "content": "### 6.1 핵심 요약\n\nLearnAI는 AI 기술을 활용하여 학생 개개인에게 최적화된 학습 경험을 제공하는 혁신적인 에듀테크 플랫폼입니다.\n\n**핵심 경쟁력**\n1. 차별화된 AI 기술력\n2. 검증된 비즈니스 모델\n3. 명확한 시장 기회\n4. 우수한 팀 역량\n\n**투자 포인트**\n- 연평균 22% 성장하는 시장\n- 8.4배의 우수한 LTV/CAC 비율\n- 18개월 내 손익분기점 달성 예정\n- 3년 내 누적 매출 70억 원 전망\n\n### 6.2 향후 비전\n\n교육의 본질인 \"개인 맞춤 성장\"을 기술로 구현하여, 모든 학생이 자신의 잠재력을 최대한 발휘할 수 있는 세상을 만들겠습니다.",
        "order": 6
      }
    ],
    "metadata": {
      "totalSections": 6,
      "wordCount": 3847,
      "characterCount": 12450,
      "generationTimeMs": 4500,
      "modelUsed": "gpt-4-turbo",
      "promptTokens": 2500,
      "completionTokens": 4200,
      "totalTokens": 6700
    },
    "exportOptions": {
      "availableFormats": ["pdf", "hwp", "docx", "markdown"],
      "downloadUrls": {
        "pdf": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/pdf",
        "hwp": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/hwp",
        "docx": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/docx",
        "markdown": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/markdown"
      }
    }
  },
  "error": null
}
```

### 4.1 응답 필드 상세 설명

#### 최상위 필드

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | boolean | 요청 성공 여부 |
| `data` | object \| null | 응답 데이터 (실패 시 null) |
| `error` | object \| null | 에러 정보 (성공 시 null) |

#### data 객체

| 필드 | 타입 | 설명 |
|------|------|------|
| `businessPlanId` | string | 생성된 사업계획서 고유 ID |
| `projectId` | string | 프로젝트 ID |
| `generatedAt` | string (ISO 8601) | 생성 시간 |
| `templateType` | string | 사용된 템플릿 유형 |
| `sections` | array | 사업계획서 섹션 배열 |
| `metadata` | object | 생성 메타데이터 |
| `exportOptions` | object | 내보내기 옵션 |

#### sections 배열 요소

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | string | 섹션 고유 ID |
| `title` | string | 섹션 제목 |
| `content` | string | 마크다운 형식 콘텐츠 |
| `order` | number | 섹션 순서 (1부터 시작) |

#### metadata 객체

| 필드 | 타입 | 설명 |
|------|------|------|
| `totalSections` | number | 총 섹션 수 |
| `wordCount` | number | 총 단어 수 |
| `characterCount` | number | 총 글자 수 |
| `generationTimeMs` | number | 생성 소요 시간 (밀리초) |
| `modelUsed` | string | 사용된 AI 모델 |
| `promptTokens` | number | 입력 토큰 수 |
| `completionTokens` | number | 출력 토큰 수 |
| `totalTokens` | number | 총 토큰 수 |

#### exportOptions 객체

| 필드 | 타입 | 설명 |
|------|------|------|
| `availableFormats` | string[] | 사용 가능한 내보내기 형식 |
| `downloadUrls` | object | 형식별 다운로드 URL |

### 4.2 TypeScript 응답 타입

```typescript
/** 사업계획서 섹션 */
interface BusinessPlanSection {
  id: string;
  title: string;
  content: string;
  order: number;
}

/** 생성 메타데이터 */
interface GenerationMetadata {
  totalSections: number;
  wordCount: number;
  characterCount: number;
  generationTimeMs: number;
  modelUsed: string;
  promptTokens: number;
  completionTokens: number;
  totalTokens: number;
}

/** 내보내기 옵션 */
interface ExportOptions {
  availableFormats: ('pdf' | 'hwp' | 'docx' | 'markdown')[];
  downloadUrls: {
    pdf: string;
    hwp: string;
    docx: string;
    markdown: string;
  };
}

/** 생성된 사업계획서 데이터 */
interface GeneratedBusinessPlanData {
  businessPlanId: string;
  projectId: string;
  generatedAt: string;
  templateType: 'pre-startup' | 'early-startup' | 'bank-loan';
  sections: BusinessPlanSection[];
  metadata: GenerationMetadata;
  exportOptions: ExportOptions;
}

/** API 응답 */
interface BusinessPlanGenerationResponse {
  success: boolean;
  data: GeneratedBusinessPlanData | null;
  error: {
    code: string;
    message: string;
    details?: Record<string, any>;
  } | null;
}
```

---

## 5. 에러 응답

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "필수 필드가 누락되었습니다.",
    "details": {
      "field": "businessPlanData.step1_problemRecognition.itemName",
      "reason": "필수 입력 항목입니다."
    }
  }
}
```

**에러 코드:**

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| `VALIDATION_ERROR` | 400 | 요청 데이터 검증 실패 |
| `UNAUTHORIZED` | 401 | 인증 실패 |
| `RATE_LIMIT_EXCEEDED` | 429 | 요청 제한 초과 |
| `GENERATION_FAILED` | 500 | AI 생성 실패 |
| `SERVICE_UNAVAILABLE` | 503 | 서비스 일시 중단 |

---

## 6. 프론트엔드 호출 예시

```typescript
import { useWizardStore } from '../stores/useWizardStore';

const generateBusinessPlan = async () => {
  const { getAllDataWithDefaults } = useWizardStore.getState();
  const wizardData = getAllDataWithDefaults();

  const requestBody: BusinessPlanGenerationRequest = {
    requestInfo: {
      templateType: 'pre-startup',
      generatedAt: new Date().toISOString(),
      userId: 'current-user-id',
      projectId: 'current-project-id',
    },
    businessPlanData: {
      step1_problemRecognition: {
        itemName: wizardData[1]['item-name'],
        itemSummary: wizardData[1]['item-summary'],
        problem: wizardData[1]['problem'],
        problemEvidence: wizardData[1]['problem-evidence'],
        targetCustomer: wizardData[1]['target-customer'],
      },
      step2_marketAnalysis: {
        marketSize: wizardData[2]['market-size'],
        marketTrend: wizardData[2]['market-trend'],
        competitors: wizardData[2]['competitors'],
        competitiveAdvantage: wizardData[2]['competitive-advantage'],
      },
      step3_solutionFeasibility: {
        solution: wizardData[3]['solution'],
        businessModel: wizardData[3]['business-model'],
        revenueStreams: wizardData[3]['revenue-streams'],
        techFeasibility: wizardData[3]['tech-feasibility'],
      },
      step4_commercializationStrategy: {
        goToMarket: wizardData[4]['go-to-market'],
        marketingStrategy: wizardData[4]['marketing-strategy'],
        growthStrategy: wizardData[4]['growth-strategy'],
        milestones: wizardData[4]['milestones'],
        partnership: wizardData[4]['partnership'],
      },
      step5_teamCapability: {
        teamComposition: wizardData[5]['team-composition'],
        teamExpertise: wizardData[5]['team-expertise'],
        teamTrackRecord: wizardData[5]['team-track-record'],
        hiringPlan: wizardData[5]['hiring-plan'],
        advisors: wizardData[5]['advisors'],
      },
      step6_financialPlan: {
        inputs: {
          initialCustomers: 100,
          pricePerCustomer: 35000,
          customerAcquisitionCost: 50000,
          monthlyFixedCosts: 15000000,
          variableCostRate: 0.1,
          monthlyChurnRate: 0.05,
        },
        calculatedMetrics: {
          monthlyRevenue: 3500000,
          yearlyRevenue: 42000000,
          ltv: 420000,
          ltvCacRatio: 8.4,
          breakEvenCustomers: 500,
          breakEvenMonths: 18,
          grossMargin: 0.9,
        },
      },
    },
    generationOptions: {
      tone: 'professional',
      targetLength: 'detailed',
      outputFormat: 'markdown',
      language: 'ko',
      sections: [
        'executive_summary',
        'problem_analysis',
        'market_analysis',
        'solution_overview',
        'business_model',
        'go_to_market',
        'team_introduction',
        'financial_projection',
        'risk_analysis',
        'conclusion',
      ],
    },
  };

  const response = await fetch('/api/v1/business-plan/generate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`,
    },
    body: JSON.stringify(requestBody),
  });

  return response.json();
};
```

---

## 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|----------|
| 1.0.0 | 2025-12-17 | AI Agent | 최초 작성 |
