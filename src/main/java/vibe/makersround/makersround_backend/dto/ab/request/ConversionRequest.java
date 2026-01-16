package vibe.makersround.makersround_backend.dto.ab.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionRequest {

    @NotBlank(message = "실험 ID는 필수입니다")
    private String experimentId;

    @NotBlank(message = "변형 ID는 필수입니다")
    private String variantId;

    @NotBlank(message = "방문자 ID는 필수입니다")
    private String visitorId;

    @NotBlank(message = "이벤트 타입은 필수입니다")
    private String eventType;

    private Map<String, Object> eventData;
}
