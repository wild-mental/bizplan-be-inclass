package vibe.makersround.makersround_backend.dto.ab.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentResponse {

    private String id;

    private String name;

    private String description;

    private String targetPage;

    private String targetElement;

    private String status;

    private Integer trafficPercentage;

    private String startDate;

    private String endDate;

    private String createdAt;

    private String updatedAt;

    private List<VariantDto> variants;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantDto {

        private String id;

        private String name;

        private Integer weight;

        private Boolean isControl;

        private Map<String, Object> content;

        private String createdAt;
    }
}
