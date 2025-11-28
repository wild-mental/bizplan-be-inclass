package vibe.bizplan.bizplan_be_inclass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 템플릿 응답 DTO
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Getter
@AllArgsConstructor
public class TemplateDto {
    
    /**
     * 템플릿 코드
     */
    private final String code;
    
    /**
     * 템플릿 이름
     */
    private final String name;
    
    /**
     * 템플릿 설명
     */
    private final String description;
}

