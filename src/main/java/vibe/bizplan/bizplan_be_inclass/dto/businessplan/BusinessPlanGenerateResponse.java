package vibe.bizplan.bizplan_be_inclass.dto.businessplan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 사업계획서 생성 응답 DTO
 * 
 * AI_GENERATION_BE_API_SUBMIT.md Section 4 스펙 준수:
 * - businessPlanId: 생성된 사업계획서 고유 ID
 * - projectId: 프로젝트 ID
 * - generatedAt: 생성 완료 시간
 * - templateType: 사용된 템플릿 유형
 * - sections: 생성된 사업계획서 섹션 목록
 * - metadata: 생성 메타데이터
 * - exportOptions: 내보내기 옵션
 * 
 * @see <a href="docs/AI_GENERATION_BE_API_SUBMIT.md">API 스펙 문서</a>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사업계획서 생성 응답")
public class BusinessPlanGenerateResponse {
    
    @Schema(description = "생성된 사업계획서 고유 ID", example = "bp-2025-12-17-550e8400")
    private String businessPlanId;
    
    @Schema(description = "프로젝트 ID", example = "project-uuid-here")
    private String projectId;
    
    @Schema(description = "생성 완료 시간 (ISO 8601)", example = "2025-12-17T12:35:00.000Z")
    private String generatedAt;
    
    @Schema(description = "사용된 템플릿 유형", example = "pre-startup", 
            allowableValues = {"pre-startup", "early-startup", "bank-loan"})
    private String templateType;
    
    @Schema(description = "생성된 사업계획서 섹션 목록")
    private List<BusinessPlanSection> sections;
    
    @Schema(description = "생성 메타데이터")
    private GenerationMetadata metadata;
    
    @Schema(description = "내보내기 옵션")
    private ExportOptions exportOptions;
    
    /**
     * 사업계획서 섹션
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사업계획서 섹션")
    public static class BusinessPlanSection {
        
        @Schema(description = "섹션 고유 ID", example = "section-1")
        private String id;
        
        @Schema(description = "섹션 제목", example = "1. 사업 개요")
        private String title;
        
        @Schema(description = "섹션 내용 (마크다운 형식)", 
                example = "### 1.1 사업 아이템\\n\\n**AI 기반 맞춤형 학습 플랫폼**\\n\\n...")
        private String content;
        
        @Schema(description = "섹션 순서 (1부터 시작)", example = "1")
        private int order;
    }
    
    /**
     * 생성 메타데이터
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "생성 메타데이터")
    public static class GenerationMetadata {
        
        @Schema(description = "총 섹션 수", example = "6")
        private int totalSections;
        
        @Schema(description = "총 단어 수", example = "3847")
        private int wordCount;
        
        @Schema(description = "총 글자 수", example = "12450")
        private int characterCount;
        
        @Schema(description = "생성 소요 시간 (밀리초)", example = "4500")
        private long generationTimeMs;
        
        @Schema(description = "사용된 AI 모델", example = "gemini-pro")
        private String modelUsed;
        
        @Schema(description = "입력 토큰 수", example = "2500")
        private int promptTokens;
        
        @Schema(description = "출력 토큰 수", example = "4200")
        private int completionTokens;
        
        @Schema(description = "총 토큰 수", example = "6700")
        private int totalTokens;
    }
    
    /**
     * 내보내기 옵션
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "내보내기 옵션")
    public static class ExportOptions {
        
        @Schema(description = "사용 가능한 내보내기 형식", 
                example = "[\"pdf\", \"hwp\", \"docx\", \"markdown\"]")
        private List<String> availableFormats;
        
        @Schema(description = "형식별 다운로드 URL")
        private Map<String, String> downloadUrls;
    }
}
