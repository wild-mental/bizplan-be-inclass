package vibe.makersround.makersround_backend.dto.ab.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedExperimentDto {

    private String experimentId;

    private String experimentName;

    private String variantId;

    private String variantName;

    private Map<String, Object> content;
}
