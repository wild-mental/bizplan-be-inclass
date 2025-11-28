package vibe.bizplan.bizplan_be_inclass.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TemplateService 단위 테스트
 * 
 * Rule 301: methodName_scenario_expectedBehavior naming
 */
class TemplateServiceTest {

    private TemplateService templateService;

    @BeforeEach
    void setUp() {
        templateService = new TemplateService();
    }

    @Test
    @DisplayName("getAllTemplates - 모든 템플릿 목록을 반환한다")
    void getAllTemplates_returnsAllTemplates() {
        // when
        var templates = templateService.getAllTemplates();

        // then
        assertThat(templates).isNotEmpty();
        assertThat(templates).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("isValidTemplate - 유효한 템플릿 코드이면 true를 반환한다")
    void isValidTemplate_validCode_returnsTrue() {
        // when
        boolean result = templateService.isValidTemplate("KSTARTUP_2025");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValidTemplate - 유효하지 않은 템플릿 코드이면 false를 반환한다")
    void isValidTemplate_invalidCode_returnsFalse() {
        // when
        boolean result = templateService.isValidTemplate("INVALID_CODE");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidTemplate - null이면 false를 반환한다")
    void isValidTemplate_nullCode_returnsFalse() {
        // when
        boolean result = templateService.isValidTemplate(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getTemplate - 유효한 코드로 템플릿을 조회한다")
    void getTemplate_validCode_returnsTemplate() {
        // when
        Optional<TemplateService.Template> result = templateService.getTemplate("KSTARTUP_2025");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("KSTARTUP_2025");
        assertThat(result.get().getName()).isEqualTo("예비창업패키지");
    }

    @Test
    @DisplayName("getTemplate - 유효하지 않은 코드로 조회하면 empty를 반환한다")
    void getTemplate_invalidCode_returnsEmpty() {
        // when
        Optional<TemplateService.Template> result = templateService.getTemplate("INVALID_CODE");

        // then
        assertThat(result).isEmpty();
    }
}

