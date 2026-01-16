package vibe.makersround.makersround_backend.dto.ab.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionResponse {

    private String conversionId;

    private String assignmentId;

    private String eventType;

    private String convertedAt;
}
