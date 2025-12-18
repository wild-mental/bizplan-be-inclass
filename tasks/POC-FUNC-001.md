# POC-FUNC-001: 사업계획서 재료 데이터 수신 API PoC

## 📋 개요

**목표**: 프론트엔드에서 입력된 사업계획서 재료(Wizard 답변 데이터)를 백엔드에서 수신하고 검증 후 저장하는 API를 구현합니다.

**범위**: FE → BE 데이터 전송 및 저장 (AI 생성 기능 제외)  
**API 스펙**: [docs/AI_GENERATION_BE_API_SUBMIT.md](../docs/AI_GENERATION_BE_API_SUBMIT.md)  
**연관 문서**: [POC-FUNC-003.md](./POC-FUNC-003.md) (AI 생성 기능)

---

## 🎯 PoC 목표

### 사용자 시나리오
1. 사용자가 Wizard 6단계를 모두 작성 완료
2. "사업계획서 생성" 버튼 클릭
3. FE에서 BE로 전체 데이터 전송
4. BE에서 데이터 검증 후 저장
5. 저장 성공 응답 반환

### 검증할 핵심 사항
- ✅ FE에서 전송한 JSON 데이터 수신
- ✅ 요청 데이터 검증 (Bean Validation)
- ✅ 비즈니스 계획 데이터 저장
- ✅ 성공/실패 응답 반환

---

## 📐 아키텍처 플로우

```
[Frontend]
    │
    └─ POST /api/v1/business-plan/generate
       │
       ├─ Request Body (JSON)
       │   ├─ requestInfo (메타데이터)
       │   ├─ businessPlanData (6단계 입력 데이터)
       │   └─ generationOptions (생성 옵션)
       │
[Spring Boot Backend]
       │
       ├─ 1. Controller: 요청 수신
       │
       ├─ 2. Bean Validation: 데이터 검증
       │
       ├─ 3. Service: 비즈니스 로직 처리
       │   ├─ Project 조회/생성
       │   ├─ BusinessPlanSubmission 저장
       │   └─ 트랜잭션 관리
       │
       └─ 4. Response: 저장 결과 응답
           │
           └─ { success: true, data: { submissionId, projectId, ... } }
```

---

## 🔧 구현 작업 계획

### Phase 1: 요청 DTO 구현

#### 1.1 메인 요청 DTO
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/dto/businessplan/BusinessPlanGenerateRequest.java`

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessPlanGenerateRequest {
    
    @NotNull(message = "requestInfo는 필수입니다")
    @Valid
    private RequestInfo requestInfo;
    
    @NotNull(message = "businessPlanData는 필수입니다")
    @Valid
    private BusinessPlanData businessPlanData;
    
    @NotNull(message = "generationOptions는 필수입니다")
    @Valid
    private GenerationOptions generationOptions;
}
```

**예상 소요**: 15분

---

#### 1.2 RequestInfo DTO
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/dto/businessplan/RequestInfo.java`

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestInfo {
    
    @NotBlank(message = "templateType은 필수입니다")
    @Pattern(regexp = "^(pre-startup|early-startup|bank-loan)$")
    private String templateType;
    
    @NotBlank(message = "generatedAt은 필수입니다")
    private String generatedAt;
    
    @NotBlank(message = "userId는 필수입니다")
    private String userId;
    
    @NotBlank(message = "projectId는 필수입니다")
    private String projectId;
}
```

**예상 소요**: 10분

---

#### 1.3 BusinessPlanData DTO (6단계 전체)
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/dto/businessplan/BusinessPlanData.java`

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessPlanData {
    
    @NotNull @Valid
    private Step1ProblemRecognition step1_problemRecognition;
    
    @NotNull @Valid
    private Step2MarketAnalysis step2_marketAnalysis;
    
    @NotNull @Valid
    private Step3SolutionFeasibility step3_solutionFeasibility;
    
    @NotNull @Valid
    private Step4CommercializationStrategy step4_commercializationStrategy;
    
    @NotNull @Valid
    private Step5TeamCapability step5_teamCapability;
    
    @NotNull @Valid
    private Step6FinancialPlan step6_financialPlan;
}
```

**예상 소요**: 10분

---

#### 1.4 Step1 ~ Step6 DTO
**파일들**: `src/main/java/vibe/bizplan/bizplan_be_inclass/dto/businessplan/steps/`

| 파일명 | 필수 필드 |
|--------|----------|
| `Step1ProblemRecognition.java` | itemName, itemSummary, problem, problemEvidence, targetCustomer |
| `Step2MarketAnalysis.java` | marketSize, marketTrend, competitors, competitiveAdvantage |
| `Step3SolutionFeasibility.java` | solution, businessModel, revenueStreams, techFeasibility |
| `Step4CommercializationStrategy.java` | goToMarket, marketingStrategy, growthStrategy, milestones, partnership(optional) |
| `Step5TeamCapability.java` | teamComposition, teamExpertise, teamTrackRecord, hiringPlan(optional), advisors(optional) |
| `Step6FinancialPlan.java` | inputs(FinancialInputs), calculatedMetrics(CalculatedMetrics) |

**예상 소요**: 1시간

---

#### 1.5 GenerationOptions DTO
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/dto/businessplan/GenerationOptions.java`

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationOptions {
    
    @NotBlank
    @Pattern(regexp = "^(professional|casual|formal)$")
    private String tone;
    
    @NotBlank
    @Pattern(regexp = "^(brief|standard|detailed)$")
    private String targetLength;
    
    @NotBlank
    @Pattern(regexp = "^(markdown|html|plain)$")
    private String outputFormat;
    
    @NotBlank
    @Pattern(regexp = "^(ko|en)$")
    private String language;
    
    @NotEmpty
    private List<String> sections;
}
```

**예상 소요**: 15분

---

### Phase 2: 엔티티 구현

#### 2.1 BusinessPlanSubmission 엔티티
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/entity/BusinessPlanSubmission.java`

```java
@Entity
@Table(name = "business_plan_submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BusinessPlanSubmission {
    
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;
    
    @Column(name = "template_type", nullable = false, length = 20)
    private String templateType;
    
    @Column(name = "business_plan_data", columnDefinition = "TEXT", nullable = false)
    private String businessPlanDataJson;  // JSON 문자열로 저장
    
    @Column(name = "generation_options", columnDefinition = "TEXT", nullable = false)
    private String generationOptionsJson;  // JSON 문자열로 저장
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubmissionStatus status;
    
    @Column(name = "submitted_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime submittedAt;
    
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = SubmissionStatus.SUBMITTED;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

**예상 소요**: 30분

---

#### 2.2 SubmissionStatus Enum
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/entity/SubmissionStatus.java`

```java
public enum SubmissionStatus {
    SUBMITTED,      // 제출됨 (처리 대기)
    PROCESSING,     // AI 생성 처리 중
    COMPLETED,      // 완료
    FAILED          // 실패
}
```

**예상 소요**: 5분

---

#### 2.3 Repository 인터페이스
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/repository/BusinessPlanSubmissionRepository.java`

```java
public interface BusinessPlanSubmissionRepository 
        extends JpaRepository<BusinessPlanSubmission, UUID> {
    
    List<BusinessPlanSubmission> findByProjectIdOrderBySubmittedAtDesc(UUID projectId);
    
    Optional<BusinessPlanSubmission> findByIdAndUserId(UUID id, String userId);
}
```

**예상 소요**: 10분

---

### Phase 3: 응답 DTO 구현

#### 3.1 성공 응답 DTO (AI_GENERATION_BE_API_SUBMIT.md Section 4 준수)
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/dto/businessplan/BusinessPlanGenerateResponse.java`

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessPlanGenerateResponse {
    
    /** 생성된 사업계획서 고유 ID */
    private String businessPlanId;
    
    /** 프로젝트 ID */
    private String projectId;
    
    /** 생성 완료 시간 (ISO 8601) */
    private String generatedAt;
    
    /** 사용된 템플릿 유형 */
    private String templateType;
    
    /** 생성된 사업계획서 섹션 목록 */
    private List<BusinessPlanSection> sections;
    
    /** 생성 메타데이터 */
    private GenerationMetadata metadata;
    
    /** 내보내기 옵션 */
    private ExportOptions exportOptions;
}

/** 사업계획서 섹션 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessPlanSection {
    private String id;
    private String title;
    private String content;
    private int order;
}

/** 생성 메타데이터 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationMetadata {
    private int totalSections;
    private int wordCount;
    private int characterCount;
    private long generationTimeMs;
    private String modelUsed;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}

/** 내보내기 옵션 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportOptions {
    private List<String> availableFormats;
    private Map<String, String> downloadUrls;
}
```

**예상 소요**: 30분

---

#### 3.2 공통 API 응답 래퍼
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/dto/ApiResponse.java`

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private ErrorInfo error;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorInfo {
        private String code;
        private String message;
        private Map<String, Object> details;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .error(null)
            .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .data(null)
            .error(ErrorInfo.builder()
                .code(code)
                .message(message)
                .build())
            .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message, Map<String, Object> details) {
        return ApiResponse.<T>builder()
            .success(false)
            .data(null)
            .error(ErrorInfo.builder()
                .code(code)
                .message(message)
                .details(details)
                .build())
            .build();
    }
}
```

**예상 소요**: 20분

---

### Phase 4: Service 구현

#### 4.1 BusinessPlanSubmissionService
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/service/BusinessPlanSubmissionService.java`

```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BusinessPlanSubmissionService {
    
    private final BusinessPlanSubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 사업계획서 데이터 제출 및 저장
     */
    public BusinessPlanSubmission submitBusinessPlan(BusinessPlanGenerateRequest request) {
        // 1. Project 조회 또는 생성
        UUID projectId = UUID.fromString(request.getRequestInfo().getProjectId());
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));
        
        // 2. JSON 직렬화
        String businessPlanDataJson = serializeToJson(request.getBusinessPlanData());
        String generationOptionsJson = serializeToJson(request.getGenerationOptions());
        
        // 3. Submission 엔티티 생성 및 저장
        BusinessPlanSubmission submission = BusinessPlanSubmission.builder()
            .project(project)
            .userId(request.getRequestInfo().getUserId())
            .templateType(request.getRequestInfo().getTemplateType())
            .businessPlanDataJson(businessPlanDataJson)
            .generationOptionsJson(generationOptionsJson)
            .build();
        
        return submissionRepository.save(submission);
    }
    
    /**
     * Submission 조회
     */
    @Transactional(readOnly = true)
    public BusinessPlanSubmission getSubmission(UUID submissionId) {
        return submissionRepository.findById(submissionId)
            .orElseThrow(() -> new ResourceNotFoundException("제출 데이터를 찾을 수 없습니다: " + submissionId));
    }
    
    /**
     * 상태 업데이트
     */
    public BusinessPlanSubmission updateStatus(UUID submissionId, SubmissionStatus status) {
        BusinessPlanSubmission submission = getSubmission(submissionId);
        // 상태 업데이트 로직 (별도 메서드 필요)
        return submissionRepository.save(submission);
    }
    
    private String serializeToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 실패", e);
            throw new RuntimeException("데이터 직렬화에 실패했습니다.", e);
        }
    }
}
```

**예상 소요**: 1시간

---

### Phase 5: Controller 구현

#### 5.1 BusinessPlanController (AI_GENERATION_BE_API_SUBMIT.md 스펙 준수)
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/controller/BusinessPlanController.java`

```java
@RestController
@RequestMapping("/api/v1/business-plan")
@RequiredArgsConstructor
@Slf4j
public class BusinessPlanController {
    
    private final BusinessPlanGenerationService generationService;
    
    /**
     * 사업계획서 생성 요청
     * 
     * POST /api/v1/business-plan/generate
     * 
     * 응답 스펙: AI_GENERATION_BE_API_SUBMIT.md Section 4 참조
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<BusinessPlanGenerateResponse>> generateBusinessPlan(
            @Valid @RequestBody BusinessPlanGenerateRequest request) {
        
        log.info("사업계획서 생성 요청 수신 - projectId: {}, userId: {}", 
            request.getRequestInfo().getProjectId(),
            request.getRequestInfo().getUserId());
        
        // 사업계획서 생성 (POC: Mock 데이터 반환)
        BusinessPlanGenerateResponse response = generationService.generateBusinessPlan(request);
        
        log.info("사업계획서 생성 완료 - businessPlanId: {}", response.getBusinessPlanId());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 사업계획서 조회
     * 
     * GET /api/v1/business-plan/{businessPlanId}
     */
    @GetMapping("/{businessPlanId}")
    public ResponseEntity<ApiResponse<BusinessPlanGenerateResponse>> getBusinessPlan(
            @PathVariable String businessPlanId) {
        
        BusinessPlanGenerateResponse response = generationService.getBusinessPlan(businessPlanId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
```

**예상 소요**: 45분

---

### Phase 6: 예외 처리

#### 6.1 GlobalExceptionHandler 업데이트
**파일**: `src/main/java/vibe/bizplan/bizplan_be_inclass/exception/GlobalExceptionHandler.java`

```java
// 기존 핸들러에 추가

/**
 * Bean Validation 예외 처리
 */
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException ex) {
    
    Map<String, Object> details = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
        details.put(error.getField(), error.getDefaultMessage());
    });
    
    log.warn("요청 데이터 검증 실패: {}", details);
    
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("VALIDATION_ERROR", "필수 필드가 누락되었거나 형식이 올바르지 않습니다.", details));
}
```

**예상 소요**: 20분

---

### Phase 7: 데이터베이스 마이그레이션

#### 7.1 테이블 생성 SQL
**파일**: `src/main/resources/db/migration/V2__create_business_plan_submissions_table.sql`

```sql
-- 사업계획서 제출 데이터 테이블
CREATE TABLE business_plan_submissions (
    id CHAR(36) NOT NULL PRIMARY KEY COMMENT '제출 ID (UUID)',
    project_id CHAR(36) NOT NULL COMMENT '프로젝트 ID',
    user_id VARCHAR(36) NOT NULL COMMENT '사용자 ID',
    template_type VARCHAR(20) NOT NULL COMMENT '템플릿 유형 (pre-startup, early-startup, bank-loan)',
    business_plan_data TEXT NOT NULL COMMENT '사업계획서 입력 데이터 (JSON)',
    generation_options TEXT NOT NULL COMMENT '생성 옵션 (JSON)',
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' COMMENT '상태 (SUBMITTED, PROCESSING, COMPLETED, FAILED)',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '제출 시간',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    
    CONSTRAINT fk_submissions_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    INDEX idx_submissions_project_id (project_id),
    INDEX idx_submissions_user_id (user_id),
    INDEX idx_submissions_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='사업계획서 제출 데이터';
```

**예상 소요**: 15분

---

## 📊 작업 일정 요약

| Phase | 작업 | 예상 소요 시간 |
|-------|------|--------------|
| Phase 1 | 요청 DTO 구현 | 1시간 50분 |
| Phase 2 | 엔티티 구현 | 45분 |
| Phase 3 | 응답 DTO 구현 | 35분 |
| Phase 4 | Service 구현 | 1시간 |
| Phase 5 | Controller 구현 | 45분 |
| Phase 6 | 예외 처리 | 20분 |
| Phase 7 | DB 마이그레이션 | 15분 |
| **총계** | | **약 5시간 30분** |

---

## ✅ 구현 결과물 목표

### 1. API 엔드포인트

#### 1.1 사업계획서 생성 요청
- **엔드포인트**: `POST /api/v1/business-plan/generate`
- **Content-Type**: `application/json`
- **요청 본문**: [docs/AI_GENERATION_BE_API_SUBMIT.md](../docs/AI_GENERATION_BE_API_SUBMIT.md) 참조
- **응답** (AI_GENERATION_BE_API_SUBMIT.md Section 4 준수):
  ```json
  {
    "success": true,
    "data": {
      "businessPlanId": "bp-2025-12-17-uuid-here",
      "projectId": "project-uuid-here",
      "generatedAt": "2025-12-17T12:35:00.000Z",
      "templateType": "pre-startup",
      "sections": [
        {
          "id": "section-1",
          "title": "1. 사업 개요",
          "content": "### 1.1 사업 아이템\n\n**AI 기반 맞춤형 학습 플랫폼 \"LearnAI\"**\n\n...",
          "order": 1
        },
        {
          "id": "section-2",
          "title": "2. 시장 분석",
          "content": "### 2.1 시장 규모\n\n...",
          "order": 2
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
          "pdf": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/pdf",
          "hwp": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/hwp",
          "docx": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/docx",
          "markdown": "/api/v1/business-plan/bp-2025-12-17-uuid-here/export/markdown"
        }
      }
    },
    "error": null
  }
  ```

#### 1.2 사업계획서 조회
- **엔드포인트**: `GET /api/v1/business-plan/{businessPlanId}`
- **응답**: 위와 동일한 형식

---

### 2. 검증 규칙

| 필드 | 검증 규칙 |
|------|----------|
| `requestInfo.templateType` | 필수, `pre-startup`, `early-startup`, `bank-loan` 중 하나 |
| `requestInfo.projectId` | 필수, 유효한 UUID |
| `requestInfo.userId` | 필수, 문자열 |
| `businessPlanData.step1_*` | 모든 필수 필드 입력 필요 |
| `generationOptions.tone` | 필수, `professional`, `casual`, `formal` 중 하나 |
| `generationOptions.sections` | 필수, 최소 1개 이상 |

---

### 3. 에러 응답 (AI_GENERATION_BE_API_SUBMIT.md Section 5 준수)

#### 3.1 검증 실패 (400)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "필수 필드가 누락되었습니다.",
    "details": {
      "field": "businessPlanData.step1_problemRecognition.itemName",
      "reason": "필수 입력 항목입니다."
    }
  }
}
```

#### 3.2 인증 실패 (401)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "인증이 필요합니다."
  }
}
```

#### 3.3 요청 제한 초과 (429)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "요청 제한을 초과했습니다. 잠시 후 다시 시도해주세요."
  }
}
```

#### 3.4 AI 생성 실패 (500)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "GENERATION_FAILED",
    "message": "AI 사업계획서 생성에 실패했습니다."
  }
}
```

#### 3.5 서비스 일시 중단 (503)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "SERVICE_UNAVAILABLE",
    "message": "서비스가 일시적으로 이용 불가능합니다."
  }
}
```

---

## 🧪 테스트 시나리오

### 시나리오 1: 정상 요청
```bash
curl -X POST http://localhost:8080/api/v1/business-plan/generate \
  -H "Content-Type: application/json" \
  -d '{
    "requestInfo": {
      "templateType": "pre-startup",
      "generatedAt": "2025-12-17T12:30:00.000Z",
      "userId": "test-user-001",
      "projectId": "existing-project-uuid"
    },
    "businessPlanData": {
      "step1_problemRecognition": {
        "itemName": "AI 학습 플랫폼",
        "itemSummary": "맞춤형 AI 학습 서비스",
        "problem": "획일화된 교육의 한계",
        "problemEvidence": "통계 자료 기반 근거",
        "targetCustomer": "중학생 학부모"
      },
      ...
    },
    "generationOptions": {
      "tone": "professional",
      "targetLength": "detailed",
      "outputFormat": "markdown",
      "language": "ko",
      "sections": ["executive_summary", "problem_analysis"]
    }
  }'
```

### 시나리오 2: 필수 필드 누락
- `itemName` 누락 시 400 에러 응답 확인

### 시나리오 3: 존재하지 않는 projectId
- 404 에러 응답 확인

---

## 🔗 POC-FUNC-003 연동

이 PoC가 완료되면 `POC-FUNC-003`에서 다음과 같이 연동됩니다:

1. `BusinessPlanSubmission` 엔티티에서 저장된 데이터 조회
2. `businessPlanDataJson`을 파싱하여 Gemini API 프롬프트 구성
3. AI 생성 완료 후 `status`를 `COMPLETED`로 업데이트

---

## 🚀 실행 계획

### Step 1: 환경 준비
- [ ] 데이터베이스 연결 확인
- [ ] 기존 Project 테이블 확인

### Step 2: Phase 1-3 구현
- [ ] 요청 DTO 구현
- [ ] 엔티티 구현
- [ ] 응답 DTO 구현

### Step 3: Phase 4-5 구현
- [ ] Service 구현
- [ ] Controller 구현

### Step 4: Phase 6-7 구현
- [ ] 예외 처리 업데이트
- [ ] DB 마이그레이션

### Step 5: 테스트 및 검증
- [ ] 단위 테스트
- [ ] 통합 테스트 (curl)
- [ ] 에러 케이스 테스트

---

## 📚 관련 문서

- [API 요청 스펙](../docs/AI_GENERATION_BE_API_SUBMIT.md)
- [POC-FUNC-003: AI 생성 기능](./POC-FUNC-003.md)
- [API 명세서](../docs/API_SPECIFICATION.md)

---

**작성일**: 2025-12-17  
**작성자**: AI Assistant  
**상태**: 계획 완료, 구현 대기

