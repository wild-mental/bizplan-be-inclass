package vibe.makersround.makersround_backend.dto.ab.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunnelAnalysisResponse {

    private String experimentId;
    private String experimentName;
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalVisitors;
    private List<FunnelStep> steps;
    private Map<String, VariantFunnel> byVariant;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FunnelStep {
        private String name;
        private long count;
        private double rate;
        private double conversionRate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantFunnel {
        private String variantId;
        private String variantName;
        private long totalVisitors;
        private List<FunnelStep> steps;
    }
}
