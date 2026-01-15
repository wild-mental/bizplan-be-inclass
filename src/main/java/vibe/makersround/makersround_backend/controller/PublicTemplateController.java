package vibe.makersround.makersround_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vibe.makersround.makersround_backend.dto.ApiResponse;
import vibe.makersround.makersround_backend.dto.template.PMFQuestionDto;
import vibe.makersround.makersround_backend.dto.template.TemplateDto;
import vibe.makersround.makersround_backend.dto.template.WizardStepDto;
import vibe.makersround.makersround_backend.service.TemplateDataProvider;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Templates", description = "템플릿/마법사 단계/PMF 질문 공개 API")
public class PublicTemplateController {

    private final TemplateDataProvider templateDataProvider;

    @Operation(summary = "템플릿 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TemplateDto>>> getTemplates() {
        log.info("템플릿 목록 조회");
        return ResponseEntity.ok(ApiResponse.success(templateDataProvider.getTemplates()));
    }

    @Operation(summary = "마법사 단계/질문 조회")
    @GetMapping("/wizard-steps")
    public ResponseEntity<ApiResponse<List<WizardStepDto>>> getWizardSteps() {
        log.info("마법사 단계 조회");
        return ResponseEntity.ok(ApiResponse.success(templateDataProvider.getWizardSteps()));
    }

    @Operation(summary = "PMF 질문 조회")
    @GetMapping("/pmf-questions")
    public ResponseEntity<ApiResponse<List<PMFQuestionDto>>> getPmfQuestions() {
        log.info("PMF 질문 조회");
        return ResponseEntity.ok(ApiResponse.success(templateDataProvider.getPmfQuestions()));
    }
}
