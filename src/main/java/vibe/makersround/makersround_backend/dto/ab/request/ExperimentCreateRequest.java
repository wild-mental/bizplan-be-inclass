package vibe.makersround.makersround_backend.dto.ab.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class ExperimentCreateRequest {

    @NotBlank(message = "실험명은 필수입니다")
    private String name;

    private String description;

    private String targetPage;

    private String targetElement;

    @Min(value = 0, message = "트래픽 비율은 0 이상이어야 합니다")
    @Max(value = 100, message = "트래픽 비율은 100 이하여야 합니다")
    @Builder.Default
    private Integer trafficPercentage = 100;

    @NotEmpty(message = "최소 하나 이상의 변형이 필요합니다")
    @Valid
    private List<VariantCreateRequest> variants;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantCreateRequest {

        @NotBlank(message = "변형 이름은 필수입니다")
        private String name;

        @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
        @Max(value = 100, message = "가중치는 100 이하여야 합니다")
        @Builder.Default
        private Integer weight = 50;

        @Builder.Default
        private Boolean isControl = false;

        private Map<String, Object> content;
    }
}
