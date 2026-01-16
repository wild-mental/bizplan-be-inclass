package vibe.makersround.makersround_backend.dto.ab.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ExperimentUpdateRequest {

    private String name;

    private String description;

    private String targetPage;

    private String targetElement;

    @Min(value = 0, message = "트래픽 비율은 0 이상이어야 합니다")
    @Max(value = 100, message = "트래픽 비율은 100 이하여야 합니다")
    private Integer trafficPercentage;

    @Valid
    private List<VariantUpdateRequest> variants;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantUpdateRequest {

        private String id;

        private String name;

        @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
        @Max(value = 100, message = "가중치는 100 이하여야 합니다")
        private Integer weight;

        private Boolean isControl;

        private Map<String, Object> content;
    }
}
