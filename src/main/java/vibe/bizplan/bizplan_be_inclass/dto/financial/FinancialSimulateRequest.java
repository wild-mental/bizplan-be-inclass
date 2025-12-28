package vibe.bizplan.bizplan_be_inclass.dto.financial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

/**
 * 재무 시뮬레이션 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 8.1 - 재무 시뮬레이션 계산
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "재무 시뮬레이션 요청")
public class FinancialSimulateRequest {

    @NotNull(message = "입력 데이터는 필수입니다")
    @Schema(description = "시뮬레이션 입력 데이터")
    private FinancialInputs inputs;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FinancialInputs {
        @Schema(description = "고객 수 정보")
        private CustomerInfo customers;
        
        @Schema(description = "가격 정보")
        private PricingInfo pricing;
        
        @Schema(description = "비용 정보")
        private CostInfo costs;
        
        @Schema(description = "자금 조달 정보")
        private FundingInfo funding;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerInfo {
        private Integer year1;
        private Integer year2;
        private Integer year3;
        private Integer growthRate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingInfo {
        private Integer pricePerCustomer;
        private String subscriptionModel;  // monthly, yearly, one-time
        private Integer churnRate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CostInfo {
        private Map<String, Long> fixedCosts;
        private Map<String, Long> variableCosts;
        private Map<String, Long> marketing;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FundingInfo {
        private Long governmentGrant;
        private String investmentRound;
        private Long investmentAmount;
    }
}

