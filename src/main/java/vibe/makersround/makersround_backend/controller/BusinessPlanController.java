package vibe.makersround.makersround_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.makersround.makersround_backend.dto.ApiResponse;
import vibe.makersround.makersround_backend.dto.businessplan.BusinessPlanGenerateRequest;
import vibe.makersround.makersround_backend.dto.businessplan.BusinessPlanGenerateResponse;
import vibe.makersround.makersround_backend.service.BusinessPlanGenerationService;

import java.util.List;
import java.util.UUID;

/**
 * 사업계획서 API Controller
 * 
 * POC-FUNC-001: AI 사업계획서 생성 API
 * 
 * 응답 포맷 (AI_GENERATION_BE_API_SUBMIT.md Section 4 스펙 준수):
 * {
 *   "success": true,
 *   "data": {
 *     "businessPlanId": "bp-2025-12-17-uuid-here",
 *     "projectId": "project-uuid-here",
 *     "generatedAt": "2025-12-17T12:35:00.000Z",
 *     "templateType": "pre-startup",
 *     "sections": [...],
 *     "metadata": {...},
 *     "exportOptions": {...}
 *   },
 *   "error": null
 * }
 * 
 * @see <a href="docs/AI_GENERATION_BE_API_SUBMIT.md">API 스펙 문서</a>
 */
@RestController
@RequestMapping("/api/v1/business-plan")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Business Plan", description = "사업계획서 API - AI 기반 사업계획서 생성")
public class BusinessPlanController {

    private final BusinessPlanGenerationService businessPlanGenerationService;

    /**
     * 사업계획서 생성 요청
     * 
     * AI_GENERATION_BE_API_SUBMIT.md 스펙에 따른 응답 반환
     * 
     * @param request FE에서 전송한 전체 데이터
     * @return 생성된 사업계획서 응답
     */
    @Operation(
            summary = "사업계획서 생성",
            description = """
                    프론트엔드에서 입력한 사업계획서 데이터를 기반으로 AI가 사업계획서를 생성합니다.
                    
                    **요청 데이터 구조:**
                    - : 프로젝트/사용자 메타 정보
                    - : 사업계획서 6단계 입력 데이터
                    - : 생성 옵션 (톤, 길이, 포맷, 언어, 섹션)
                    
                    **응답 데이터 (AI_GENERATION_BE_API_SUBMIT.md Section 4):**
                    - : 생성된 사업계획서 고유 ID
                    - : 프로젝트 ID
                    - : 생성 완료 시간 (ISO 8601)
                    - : 사용된 템플릿 유형
                    - : 생성된 사업계획서 섹션 목록
                    - : 생성 메타데이터 (토큰 수, 생성 시간 등)
                    - : 내보내기 옵션
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "사업계획서 생성 요청 데이터",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "사업계획서 생성 요청 예시",
                            value = """
                                    {
                                      "requestInfo": {
                                        "templateType": "pre-startup",
                                        "generatedAt": "2025-12-17T12:30:00.000Z",
                                        "userId": "user-uuid-here",
                                        "projectId": "project-uuid-here"
                                      },
                                      "businessPlanData": {
                                        "step1_problemRecognition": {
                                          "itemName": "AI 기반 맞춤형 학습 플랫폼 LearnAI",
                                          "itemSummary": "학생 개개인의 학습 패턴을 AI가 분석하여 맞춤형 학습 경로 제공",
                                          "problem": "획일화된 커리큘럼으로 인한 개인별 학습 효율 저하",
                                          "problemEvidence": "중고등학생 78%가 현재 학습 방식에 불만족",
                                          "targetCustomer": "중학생 자녀를 둔 35-45세 학부모"
                                        },
                                        "step2_marketAnalysis": {
                                          "marketSize": "TAM 25조원, SAM 5조원, SOM 100억원",
                                          "marketTrend": "온라인 교육 시장 연평균 22% 성장",
                                          "competitors": "메가스터디, 대교 스마트러닝",
                                          "competitiveAdvantage": "자체 AI 알고리즘, 한국 교육과정 100% 연계"
                                        },
                                        "step3_solutionFeasibility": {
                                          "solution": "AI 기반 맞춤형 학습 경로 제공 플랫폼",
                                          "businessModel": "B2C 구독형 SaaS",
                                          "revenueStreams": "구독 수익 75%, B2B 라이선스 15%",
                                          "techFeasibility": "AI 알고리즘 특허 출원 완료"
                                        },
                                        "step4_commercializationStrategy": {
                                          "goToMarket": "수도권 중학생 대상 집중 공략",
                                          "marketingStrategy": "디지털 마케팅, 바이럴 마케팅",
                                          "growthStrategy": "1년차 B2C, 2년차 B2B 확장",
                                          "milestones": "3개월: MVP 출시, 6개월: 유료 1,000명"
                                        },
                                        "step5_teamCapability": {
                                          "teamComposition": "CEO, CTO, CPO 3인 핵심 팀",
                                          "teamExpertise": "AI 논문 15편, 특허 3건",
                                          "teamTrackRecord": "전 스타트업 MAU 50만, Exit 경험"
                                        },
                                        "step6_financialPlan": {
                                          "inputs": {
                                            "initialCustomers": 100,
                                            "pricePerCustomer": 35000,
                                            "customerAcquisitionCost": 50000,
                                            "monthlyFixedCosts": 15000000,
                                            "variableCostRate": 0.1,
                                            "monthlyChurnRate": 0.05
                                          },
                                          "calculatedMetrics": {
                                            "monthlyRevenue": 3500000,
                                            "yearlyRevenue": 42000000,
                                            "ltv": 420000,
                                            "ltvCacRatio": 8.4,
                                            "breakEvenCustomers": 500,
                                            "breakEvenMonths": 18,
                                            "grossMargin": 0.9
                                          }
                                        }
                                      },
                                      "generationOptions": {
                                        "tone": "professional",
                                        "targetLength": "detailed",
                                        "outputFormat": "markdown",
                                        "language": "ko",
                                        "sections": ["executive_summary", "problem_analysis", "market_analysis", "solution_overview", "business_model", "go_to_market", "team_introduction", "financial_projection", "risk_analysis", "conclusion"]
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사업계획서 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "businessPlanId": "bp-2025-12-17-550e8400",
                                                "projectId": "project-uuid-here",
                                                "generatedAt": "2025-12-17T12:35:00.000Z",
                                                "templateType": "pre-startup",
                                                "sections": [
                                                  {
                                                    "id": "section-1",
                                                    "title": "1. 사업 개요",
                                                    "content": "### 1.1 사업 아이템\n\n**AI 기반 맞춤형 학습 플랫폼**...",
                                                    "order": 1
                                                  }
                                                ],
                                                "metadata": {
                                                  "totalSections": 6,
                                                  "wordCount": 3847,
                                                  "characterCount": 12450,
                                                  "generationTimeMs": 4500,
                                                  "modelUsed": "gemini-pro",
                                                  "promptTokens": 2500,
                                                  "completionTokens": 4200,
                                                  "totalTokens": 6700
                                                },
                                                "exportOptions": {
                                                  "availableFormats": ["pdf", "hwp", "docx", "markdown"],
                                                  "downloadUrls": {
                                                    "pdf": "/api/v1/business-plan/bp-2025-12-17-550e8400/export/pdf"
                                                  }
                                                }
                                              },
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "필수 필드 누락 또는 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "검증 실패 응답 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "error": {
                                                "code": "VALIDATION_ERROR",
                                                "message": "필수 필드가 누락되었습니다."
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "AI 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "생성 실패 응답 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "error": {
                                                "code": "GENERATION_FAILED",
                                                "message": "AI 사업계획서 생성에 실패했습니다."
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<BusinessPlanGenerateResponse>> generateBusinessPlan(
            @RequestBody BusinessPlanGenerateRequest request) {
        
        long startTime = System.currentTimeMillis();
        
        // ========================================
        // 1. 요청 데이터 추출 및 로깅
        // ========================================
        log.info("========================================");
        log.info("[POC-FUNC-001] 사업계획서 생성 요청 수신");
        log.info("========================================");
        
        // requestInfo 추출
        BusinessPlanGenerateRequest.RequestInfo requestInfo = request.getRequestInfo();
        String projectId = requestInfo != null && requestInfo.getProjectId() != null
                ? requestInfo.getProjectId()
                : "proj-" + UUID.randomUUID().toString().substring(0, 8);
        String userId = requestInfo != null && requestInfo.getUserId() != null
                ? requestInfo.getUserId()
                : "user-001";
        String templateType = requestInfo != null && requestInfo.getTemplateType() != null
                ? requestInfo.getTemplateType()
                : "pre-startup";
        
        log.info("📋 requestInfo:");
        log.info("   - projectId: {}", projectId);
        log.info("   - userId: {}", userId);
        log.info("   - templateType: {}", templateType);
        
        // businessPlanData 추출 및 요약 로깅
        BusinessPlanGenerateRequest.BusinessPlanData businessPlanData = request.getBusinessPlanData();
        String itemName = extractItemName(businessPlanData);
        logBusinessPlanDataSummary(businessPlanData);
        
        // generationOptions 추출 및 로깅
        BusinessPlanGenerateRequest.GenerationOptions generationOptions = request.getGenerationOptions();
        logGenerationOptions(generationOptions);
        
        // ========================================
        // 2. 사업계획서 생성 (Gemini 호출 - Spring AI)
        // ========================================
        BusinessPlanGenerateResponse response = businessPlanGenerationService.generateBusinessPlan(
                request,
                projectId,
                templateType,
                itemName,
                startTime
        );
        
        log.info("========================================");
        log.info("✅ 사업계획서 생성 완료");
        log.info("   - businessPlanId: {}", response.getBusinessPlanId());
        log.info("   - sections: {} 개", 
                response.getSections() != null ? response.getSections().size() : 0);
        log.info("   - generationTimeMs: {}", 
                response.getMetadata() != null ? response.getMetadata().getGenerationTimeMs() : "N/A");
        log.info("========================================");
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사업계획서 조회
     *
     * @param businessPlanId 사업계획서 ID
     * @return 사업계획서 응답
     */
    @Operation(
            summary = "사업계획서 조회",
            description = "생성된 사업계획서를 ID로 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사업계획서를 찾을 수 없음",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{businessPlanId}")
    public ResponseEntity<ApiResponse<BusinessPlanGenerateResponse>> getBusinessPlan(
            @Parameter(
                    description = "사업계획서 ID",
                    required = true,
                    example = "bp-2025-12-17-550e8400"
            )
            @PathVariable String businessPlanId) {
        
        log.info("[POC-FUNC-001] 사업계획서 조회 요청 - businessPlanId: {}", businessPlanId);
        
        BusinessPlanGenerateResponse response = businessPlanGenerationService.getBusinessPlan(businessPlanId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========== Private Helper Methods ==========
    
    /**
     * businessPlanData에서 아이템명 추출
     */
    private String extractItemName(BusinessPlanGenerateRequest.BusinessPlanData businessPlanData) {
        if (businessPlanData == null || businessPlanData.getStep1_problemRecognition() == null) {
            return "AI 기반 맞춤형 학습 플랫폼";
        }
        String itemName = businessPlanData.getStep1_problemRecognition().getItemName();
        return itemName != null ? itemName : "AI 기반 맞춤형 학습 플랫폼";
    }
    
    /**
     * businessPlanData 수신 현황 로깅
     */
    private void logBusinessPlanDataSummary(BusinessPlanGenerateRequest.BusinessPlanData businessPlanData) {
        BusinessPlanGenerateRequest.Step1ProblemRecognition step1 =
                businessPlanData != null ? businessPlanData.getStep1_problemRecognition() : null;
        String itemName = step1 != null && step1.getItemName() != null
                ? step1.getItemName()
                : "[아이템명 미입력]";
        
        log.info("📊 businessPlanData 수신 현황:");
        log.info("   - Step1 (문제 인식): {} itemName = {}",
                step1 != null ? "✅" : "❌", itemName);
        log.info("   - Step2 (시장 분석): {}",
                businessPlanData != null && businessPlanData.getStep2_marketAnalysis() != null ? "✅" : "❌");
        log.info("   - Step3 (실현 방안): {}",
                businessPlanData != null && businessPlanData.getStep3_solutionFeasibility() != null ? "✅" : "❌");
        log.info("   - Step4 (사업화 전략): {}",
                businessPlanData != null && businessPlanData.getStep4_commercializationStrategy() != null ? "✅" : "❌");
        log.info("   - Step5 (팀 역량): {}",
                businessPlanData != null && businessPlanData.getStep5_teamCapability() != null ? "✅" : "❌");
        log.info("   - Step6 (재무 계획): {}",
                businessPlanData != null && businessPlanData.getStep6_financialPlan() != null ? "✅" : "❌");
    }
    
    /**
     * generationOptions 로깅
     */
    private void logGenerationOptions(BusinessPlanGenerateRequest.GenerationOptions generationOptions) {
        String tone = generationOptions != null && generationOptions.getTone() != null
                ? generationOptions.getTone()
                : "professional";
        String targetLength = generationOptions != null && generationOptions.getTargetLength() != null
                ? generationOptions.getTargetLength()
                : "standard";
        String outputFormat = generationOptions != null && generationOptions.getOutputFormat() != null
                ? generationOptions.getOutputFormat()
                : "markdown";
        String language = generationOptions != null && generationOptions.getLanguage() != null
                ? generationOptions.getLanguage()
                : "ko";
        List<String> sections = generationOptions != null ? generationOptions.getSections() : null;
        
        log.info("⚙️ generationOptions:");
        log.info("   - tone: {}", tone);
        log.info("   - targetLength: {}", targetLength);
        log.info("   - outputFormat: {}", outputFormat);
        log.info("   - language: {}", language);
        log.info("   - sections: {} 개", sections != null ? sections.size() : 0);
    }
}
