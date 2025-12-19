package vibe.bizplan.bizplan_be_inclass.dto.businessplan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사업계획서 생성 요청 DTO
 *
 * 프론트엔드에서 전달하는 JSON 구조를 타입으로 정의합니다.
 * - requestInfo: 프로젝트/사용자 메타 정보
 * - businessPlanData: 6단계 사업계획서 입력 데이터
 * - generationOptions: 생성 옵션 (톤, 길이, 포맷, 언어, 섹션)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사업계획서 생성 요청")
public class BusinessPlanGenerateRequest {

    @Valid
    @NotNull(message = "requestInfo는 필수입니다.")
    @Schema(description = "프로젝트 및 사용자 메타 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private RequestInfo requestInfo;

    @Valid
    @NotNull(message = "businessPlanData는 필수입니다.")
    @Schema(description = "6단계 사업계획서 입력 데이터", requiredMode = Schema.RequiredMode.REQUIRED)
    private BusinessPlanData businessPlanData;

    @Valid
    @NotNull(message = "generationOptions는 필수입니다.")
    @Schema(description = "생성 옵션 (톤, 길이, 포맷, 언어, 섹션)", requiredMode = Schema.RequiredMode.REQUIRED)
    private GenerationOptions generationOptions;

    /**
     * 프로젝트/사용자 메타 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "프로젝트 및 사용자 메타 정보")
    public static class RequestInfo {

        @Schema(description = "템플릿 유형", example = "pre-startup",
                allowableValues = {"pre-startup", "early-startup", "bank-loan"})
        private String templateType;

        @Schema(description = "요청 시각 (프론트 기준, ISO 8601)", example = "2025-12-17T12:30:00.000Z")
        private String generatedAt;

        @Schema(description = "사용자 ID", example = "user-uuid-here")
        private String userId;

        @Schema(description = "프로젝트 ID", example = "project-uuid-here")
        private String projectId;
    }

    /**
     * 6단계 사업계획서 입력 데이터
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "6단계 사업계획서 입력 데이터")
    public static class BusinessPlanData {

        @Schema(description = "1단계 - 문제 인식 및 아이템 정의")
        private Step1ProblemRecognition step1_problemRecognition;

        @Schema(description = "2단계 - 시장 분석")
        private Step2MarketAnalysis step2_marketAnalysis;

        @Schema(description = "3단계 - 실현 가능성 및 비즈니스 모델")
        private Step3SolutionFeasibility step3_solutionFeasibility;

        @Schema(description = "4단계 - 사업화 전략")
        private Step4CommercializationStrategy step4_commercializationStrategy;

        @Schema(description = "5단계 - 팀 역량")
        private Step5TeamCapability step5_teamCapability;

        @Schema(description = "6단계 - 재무 계획")
        private Step6FinancialPlan step6_financialPlan;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "1단계 - 문제 인식 및 아이템 정의")
    public static class Step1ProblemRecognition {

        @Schema(description = "아이템 명", example = "AI 기반 맞춤형 학습 플랫폼 LearnAI")
        private String itemName;

        @Schema(description = "아이템 개요", example = "학생 개개인의 학습 패턴을 AI가 분석하여 맞춤형 학습 경로 제공")
        private String itemSummary;

        @Schema(description = "해결하고자 하는 문제", example = "획일화된 커리큘럼으로 인한 개인별 학습 효율 저하")
        private String problem;

        @Schema(description = "문제의 근거/증거", example = "중고등학생 78%가 현재 학습 방식에 불만족")
        private String problemEvidence;

        @Schema(description = "타겟 고객", example = "중학생 자녀를 둔 35-45세 학부모")
        private String targetCustomer;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "2단계 - 시장 분석")
    public static class Step2MarketAnalysis {

        @Schema(description = "시장 규모", example = "TAM 25조원, SAM 5조원, SOM 100억원")
        private String marketSize;

        @Schema(description = "시장 트렌드", example = "온라인 교육 시장 연평균 22% 성장")
        private String marketTrend;

        @Schema(description = "주요 경쟁사", example = "메가스터디, 대교 스마트러닝")
        private String competitors;

        @Schema(description = "경쟁 우위", example = "자체 AI 알고리즘, 한국 교육과정 100% 연계")
        private String competitiveAdvantage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "3단계 - 실현 가능성 및 비즈니스 모델")
    public static class Step3SolutionFeasibility {

        @Schema(description = "솔루션 개요", example = "AI 기반 맞춤형 학습 경로 제공 플랫폼")
        private String solution;

        @Schema(description = "비즈니스 모델", example = "B2C 구독형 SaaS")
        private String businessModel;

        @Schema(description = "수익 구조", example = "구독 수익 75%, B2B 라이선스 15%")
        private String revenueStreams;

        @Schema(description = "기술 실현 가능성", example = "AI 알고리즘 특허 출원 완료")
        private String techFeasibility;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "4단계 - 사업화 전략")
    public static class Step4CommercializationStrategy {

        @Schema(description = "Go-To-Market 전략", example = "수도권 중학생 대상 집중 공략")
        private String goToMarket;

        @Schema(description = "마케팅 전략", example = "디지털 마케팅, 바이럴 마케팅")
        private String marketingStrategy;

        @Schema(description = "성장 전략", example = "1년차 B2C, 2년차 B2B 확장")
        private String growthStrategy;

        @Schema(description = "주요 마일스톤", example = "3개월: MVP 출시, 6개월: 유료 1,000명")
        private String milestones;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "5단계 - 팀 역량")
    public static class Step5TeamCapability {

        @Schema(description = "팀 구성", example = "CEO, CTO, CPO 3인 핵심 팀")
        private String teamComposition;

        @Schema(description = "팀 전문성", example = "AI 논문 15편, 특허 3건")
        private String teamExpertise;

        @Schema(description = "팀 트랙 레코드", example = "전 스타트업 MAU 50만, Exit 경험")
        private String teamTrackRecord;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "6단계 - 재무 계획")
    public static class Step6FinancialPlan {

        @Schema(description = "재무 입력 값")
        private FinancialInputs inputs;

        @Schema(description = "계산된 재무 지표")
        private CalculatedMetrics calculatedMetrics;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "재무 입력 값")
    public static class FinancialInputs {

        @Schema(description = "초기 고객 수", example = "100")
        private Integer initialCustomers;

        @Schema(description = "고객당 가격", example = "35000")
        private Integer pricePerCustomer;

        @Schema(description = "고객 획득 비용(CAC)", example = "50000")
        private Integer customerAcquisitionCost;

        @Schema(description = "월 고정비", example = "15000000")
        private Integer monthlyFixedCosts;

        @Schema(description = "변동비율", example = "0.1")
        private Double variableCostRate;

        @Schema(description = "월 이탈률", example = "0.05")
        private Double monthlyChurnRate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "계산된 재무 지표")
    public static class CalculatedMetrics {

        @Schema(description = "월 매출", example = "3500000")
        private Integer monthlyRevenue;

        @Schema(description = "연 매출", example = "42000000")
        private Integer yearlyRevenue;

        @Schema(description = "고객 생애 가치(LTV)", example = "420000")
        private Integer ltv;

        @Schema(description = "LTV/CAC 비율", example = "8.4")
        private Double ltvCacRatio;

        @Schema(description = "손익분기점 고객 수", example = "500")
        private Integer breakEvenCustomers;

        @Schema(description = "손익분기점 도달 개월 수", example = "18")
        private Integer breakEvenMonths;

        @Schema(description = "매출 총이익률", example = "0.9")
        private Double grossMargin;
    }

    /**
     * 생성 옵션
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "생성 옵션")
    public static class GenerationOptions {

        @Schema(description = "톤", example = "professional")
        private String tone;

        @Schema(description = "목표 길이", example = "detailed")
        private String targetLength;

        @Schema(description = "출력 포맷", example = "markdown")
        private String outputFormat;

        @Schema(description = "언어 코드", example = "ko")
        private String language;

        @Schema(description = "생성할 섹션 목록",
                example = "[\"executive_summary\", \"problem_analysis\", \"market_analysis\", \"solution_overview\", \"business_model\", \"go_to_market\", \"team_introduction\", \"financial_projection\", \"risk_analysis\", \"conclusion\"]")
        private List<String> sections;
    }
}

