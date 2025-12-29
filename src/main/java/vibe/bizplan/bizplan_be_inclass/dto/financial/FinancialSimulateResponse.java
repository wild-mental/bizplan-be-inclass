package vibe.bizplan.bizplan_be_inclass.dto.financial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 재무 시뮬레이션 응답 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 8.1 - 재무 시뮬레이션 계산
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "재무 시뮬레이션 응답")
public class FinancialSimulateResponse {

    @Schema(description = "요약 정보")
    private Summary summary;

    @Schema(description = "연도별 예측")
    private List<YearlyProjection> yearlyProjections;

    @Schema(description = "월별 예측")
    private List<MonthlyProjection> monthlyProjections;

    @Schema(description = "차트 데이터")
    private ChartData charts;

    @Schema(description = "리스크 분석")
    private List<Risk> risks;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Integer breakEvenMonth;
        private Long cumulativeProfitYear3;
        private Double irr;
        private String paybackPeriod;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YearlyProjection {
        private Integer year;
        private Long revenue;
        private Long costs;
        private Long grossProfit;
        private Long operatingProfit;
        private Long netProfit;
        private Integer customers;
        private Integer arpu;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyProjection {
        private Integer month;
        private Long revenue;
        private Long costs;
        private Long profit;
        private Long cumulative;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChartData {
        private List<DataPoint> revenueGrowth;
        private List<DataPoint> profitMargin;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataPoint {
        private String label;
        private Object value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Risk {
        private String type;
        private String severity;  // low, medium, high
        private String message;
        private String suggestion;
    }
}

