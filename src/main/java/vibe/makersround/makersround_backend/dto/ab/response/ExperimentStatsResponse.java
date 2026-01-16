package vibe.makersround.makersround_backend.dto.ab.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentStatsResponse {

    private String experimentId;

    private String experimentName;

    private String status;

    private long totalAssignments;

    private List<VariantStatsDto> variants;

    private Double statisticalSignificance;

    private String winner;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantStatsDto {

        private String variantId;

        private String name;

        private Boolean isControl;

        private long assignments;

        private long conversions;

        private Double conversionRate;

        private String uplift;
    }
}
