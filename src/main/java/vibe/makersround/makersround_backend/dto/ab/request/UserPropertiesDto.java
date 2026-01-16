package vibe.makersround.makersround_backend.dto.ab.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPropertiesDto {

    private String jtbd;
    private String source;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmContent;
    private String referer;
    private String deviceType;
    private String browser;
    private String os;
}
