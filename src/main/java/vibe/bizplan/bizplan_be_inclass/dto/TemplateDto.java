package vibe.bizplan.bizplan_be_inclass.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 템플릿 응답 DTO
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Getter
@AllArgsConstructor
@Schema(description = "사업계획서 템플릿 정보")
public class TemplateDto {
    
    /**
     * 템플릿 코드
     */
    @Schema(description = "템플릿 고유 코드", example = "KSTARTUP_2025")
    private final String code;
    
    /**
     * 템플릿 이름
     */
    @Schema(description = "템플릿 표시 이름", example = "K-스타트업 2025")
    private final String name;
    
    /**
     * 템플릿 설명
     */
    @Schema(description = "템플릿 상세 설명", example = "2025년 K-스타트업 사업계획서 양식")
    private final String description;
}

