package vibe.makersround.makersround_backend.dto.ab.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveExperimentsRequest {

    @NotBlank(message = "페이지 정보는 필수입니다")
    private String page;

    @NotBlank(message = "방문자 ID는 필수입니다")
    private String visitorId;

    private String userId;
}
