# POC-FUNC-003: AI 사업계획서 생성 기능 PoC

## 📋 개요

**목표**: POC-FUNC-001에서 수신한 사업계획서 재료 데이터를 기반으로 Google Gemini API를 호출하여 AI 사업계획서를 생성합니다.

**간소화 전략**: 기존 계획(별도 Python FastAPI AI 엔진 구축) 대신, Spring Boot 백엔드에서 Google Gemini API를 직접 호출하는 방식으로 간소화하여 구현 복잡도를 낮추고 개발 속도를 향상시킵니다.

**선행 작업**: [POC-FUNC-001](./POC-FUNC-001.md) (사업계획서 재료 데이터 수신 API)  
**기간**: 단기 PoC (MVP 핵심 기능 검증)  
**범위**: AI 생성 기능에 집중 (데이터 수신은 POC-FUNC-001에서 처리)  
**참조**: REQ-FUNC-003-AI-001, REQ-FUNC-003-BE-001

---

## 🎯 PoC 목표

### 사용자 시나리오
1. (POC-FUNC-001) 사용자가 Wizard 데이터 입력 후 제출
2. (POC-FUNC-003) 저장된 데이터 기반으로 AI 사업계획서 생성 요청
3. AI가 사업계획서를 생성하고 결과를 확인

### 검증할 핵심 사항
- ✅ BusinessPlanSubmission 데이터 조회
- ✅ Gemini API 직접 호출 (Spring Boot → Google Gemini API)
- ✅ AI 응답 파싱 및 섹션별 텍스트 추출
- ✅ 생성된 사업계획서 저장 및 응답

---

## 📐 아키텍처 플로우

```
[POC-FUNC-001: 데이터 수신]
    │
    └─ POST /api/v1/business-plan/generate (데이터 제출)
         │
         └─ BusinessPlanSubmission 저장 (status: SUBMITTED)
              │
              └─ submissionId 반환

[POC-FUNC-003: AI 생성] ← 본 문서
    │
    └─ POST /api/v1/business-plan/submissions/{submissionId}/generate (AI 생성 요청)
         │
[Spring Boot Backend]
         │
         ├─ 1. BusinessPlanSubmission 조회 (businessPlanDataJson 포함)
         │
         ├─ 2. 프롬프트 구성 (businessPlanData → 프롬프트 변환)
         │
         ├─ 3. Gemini API 직접 호출 (WebClient)
         │       │
         │       └─ POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
         │          {
         │            "contents": [{
         │              "parts": [{
         │                "text": "프롬프트 텍스트 (6단계 입력 데이터 기반)"
         │              }]
         │            }]
         │          }
         │
         ├─ 4. 응답 파싱 및 섹션별 텍스트 추출
         │
         ├─ 5. BusinessPlanDocument 저장
         │
         ├─ 6. Submission 상태 업데이트 (status: COMPLETED)
         │
         └─ 7. 생성된 문서 응답
```

---

## 🔧 구현 작업 계획

> **📌 선행 조건**: [POC-FUNC-001](./POC-FUNC-001.md)에서 다음이 구현되어 있어야 합니다:
> - `BusinessPlanSubmission` 엔티티 및 Repository
> - `BusinessPlanSubmissionService` (데이터 저장)
> - `POST /api/v1/business-plan/generate` API (데이터 수신)

---

### Phase 1: 데이터 모델 확장

#### 1.1 BusinessPlanDocument 엔티티 생성
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/entity/BusinessPlanDocument.java`

**엔티티 구조**:
```java
@Entity
@Table(name = "business_plan_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BusinessPlanDocument {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private BusinessPlanSubmission submission;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(name = "sections", columnDefinition = "TEXT", nullable = false)
    private String sectionsJson;  // 섹션별 텍스트 (JSON)
    
    @Column(name = "template_type", nullable = false, length = 20)
    private String templateType;
    
    @Column(name = "word_count")
    private Integer wordCount;
    
    @Column(name = "generation_time_ms")
    private Long generationTimeMs;
    
    @Column(name = "model_used", length = 50)
    private String modelUsed;
    
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
```

**작업 내용**:
- BusinessPlanDocument 엔티티 생성
- BusinessPlanSubmission, Project와의 관계 설정
- Repository 인터페이스 생성

**예상 소요**: 45분

---

### Phase 2: Gemini API 클라이언트 구현

#### 2.1 Gemini API 클라이언트
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/client/GeminiApiClient.java`

**기능**:
- WebClient를 사용한 HTTP 클라이언트
- Google Gemini API 직접 호출 (`POST /v1beta/models/gemini-2.0-flash:generateContent`)
- 프롬프트 템플릿 구성 (6단계 businessPlanData 기반)
- 타임아웃 설정 (60초)
- 에러 처리

**요청 형식**:
```java
public GeminiGenerateResponse generateContent(String prompt);
```

**예상 소요**: 1시간

---

#### 2.2 프롬프트 빌더
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/service/PromptBuilder.java`

**기능**:
- BusinessPlanData → 프롬프트 변환
- 6단계 입력 데이터를 구조화된 프롬프트로 변환
- 생성 옵션(tone, targetLength 등) 반영

```java
public String buildBusinessPlanPrompt(
    BusinessPlanData data,
    GenerationOptions options
);
```

**프롬프트 구성**:
- Role: 전문 사업계획서 컨설턴트
- Context: 6단계 입력 데이터 (문제 인식, 시장 분석, 실현 방안 등)
- Task: 요청된 섹션별 사업계획서 작성
- Format: 섹션별 마크다운 형식

**예상 소요**: 1시간

---

#### 2.3 Gemini API 요청/응답 DTO
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/dto/gemini/GeminiGenerateRequest.java`  
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/dto/gemini/GeminiGenerateResponse.java`

**작업 내용**:
- Gemini API 요청/응답 DTO 생성
- JSON 응답 파싱 및 텍스트 추출
- 에러 응답 처리

**예상 소요**: 30분

---

### Phase 3: 사업계획서 생성 서비스

#### 3.1 BusinessPlanGenerationService
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/service/BusinessPlanGenerationService.java`

**기능**:
- BusinessPlanSubmission 조회 및 데이터 파싱
- PromptBuilder로 프롬프트 구성
- GeminiApiClient 호출
- 응답 파싱 및 섹션별 텍스트 추출
- BusinessPlanDocument 저장
- Submission 상태 업데이트 (COMPLETED/FAILED)
- 트랜잭션 관리

```java
public BusinessPlanDocument generateBusinessPlan(UUID submissionId);
```

**예상 소요**: 1.5시간

---

#### 3.2 응답 파서
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/service/ResponseParser.java`

**기능**:
- Gemini API 응답 텍스트를 섹션별로 파싱
- 마크다운 헤더 기반 섹션 분리
- 메타데이터 추출 (단어 수, 글자 수 등)

```java
public List<BusinessPlanSection> parseResponse(String responseText);
```

**예상 소요**: 45분

---

### Phase 4: API 엔드포인트

#### 4.1 AI 생성 API 엔드포인트
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/controller/BusinessPlanController.java`

**엔드포인트**: `POST /api/v1/business-plan/submissions/{submissionId}/generate`

**기능**:
- Submission ID 검증
- BusinessPlanGenerationService 호출
- 생성된 문서 응답

**응답 형식** (AI_GENERATION_BE_API_SUBMIT.md 스펙 준수):
```json
{
  "success": true,
  "data": {
    "businessPlanId": "bp-uuid-here",
    "projectId": "project-uuid",
    "generatedAt": "2025-12-17T12:35:00.000Z",
    "templateType": "pre-startup",
    "sections": [
      {
        "id": "section-1",
        "title": "1. 사업 개요",
        "content": "마크다운 콘텐츠...",
        "order": 1
      }
    ],
    "metadata": {
      "totalSections": 6,
      "wordCount": 3847,
      "generationTimeMs": 4500,
      "modelUsed": "gemini-2.0-flash"
    }
  },
  "error": null
}
```

**예상 소요**: 1시간

---

#### 4.2 응답 DTO
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/dto/businessplan/BusinessPlanGenerationResponse.java`

```java
@Getter
@Builder
public class BusinessPlanGenerationResponse {
    private String businessPlanId;
    private String projectId;
    private LocalDateTime generatedAt;
    private String templateType;
    private List<BusinessPlanSection> sections;
    private GenerationMetadata metadata;
}

@Getter
@Builder
public class BusinessPlanSection {
    private String id;
    private String title;
    private String content;
    private int order;
}

@Getter
@Builder
public class GenerationMetadata {
    private int totalSections;
    private int wordCount;
    private long generationTimeMs;
    private String modelUsed;
}
```

**예상 소요**: 30분

---

### Phase 5: 설정 및 통합

#### 5.1 Gemini API 설정
**파일**: `src/main/resources/application-local.properties`

```properties
# Google Gemini API 설정
gemini.api.key=${GEMINI_API_KEY:}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta
gemini.api.model=gemini-2.0-flash
gemini.api.timeout=60000
```

**예상 소요**: 10분

---

#### 5.2 WebClient 설정
**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/config/WebClientConfig.java`

**기능**:
- WebClient 빈 등록
- 타임아웃 설정 (연결: 10초, 읽기: 60초)
- 에러 핸들러 설정
- 로깅 설정

**예상 소요**: 30분

---

#### 5.3 의존성 추가
**파일**: `build.gradle`

```gradle
// WebClient (Spring WebFlux)
implementation 'org.springframework.boot:spring-boot-starter-webflux'
```

**예상 소요**: 5분

---

### Phase 6: 데이터베이스 마이그레이션

#### 6.1 테이블 생성 SQL
**파일**: `src/main/resources/db/migration/V3__create_business_plan_documents_table.sql`

```sql
CREATE TABLE business_plan_documents (
    id CHAR(36) NOT NULL PRIMARY KEY COMMENT '문서 ID (UUID)',
    submission_id CHAR(36) NOT NULL UNIQUE COMMENT '제출 ID',
    project_id CHAR(36) NOT NULL COMMENT '프로젝트 ID',
    sections TEXT NOT NULL COMMENT '섹션별 내용 (JSON)',
    template_type VARCHAR(20) NOT NULL COMMENT '템플릿 유형',
    word_count INT COMMENT '총 단어 수',
    generation_time_ms BIGINT COMMENT '생성 소요 시간 (ms)',
    model_used VARCHAR(50) COMMENT '사용된 AI 모델',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    
    CONSTRAINT fk_documents_submission FOREIGN KEY (submission_id) 
        REFERENCES business_plan_submissions(id) ON DELETE CASCADE,
    CONSTRAINT fk_documents_project FOREIGN KEY (project_id) 
        REFERENCES projects(id) ON DELETE CASCADE,
    INDEX idx_documents_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='AI 생성 사업계획서 문서';
```

**예상 소요**: 15분

---

## 📊 작업 일정 요약

| Phase | 작업 | 예상 소요 시간 |
|-------|------|--------------|
| Phase 1 | 데이터 모델 (Document 엔티티) | 45분 |
| Phase 2 | Gemini API 클라이언트 | 2시간 30분 |
| Phase 3 | 사업계획서 생성 서비스 | 2시간 15분 |
| Phase 4 | API 엔드포인트 | 1시간 30분 |
| Phase 5 | 설정 및 통합 | 45분 |
| Phase 6 | DB 마이그레이션 | 15분 |
| **총계** | | **약 8시간** |

> **참고**: POC-FUNC-001 (데이터 수신 API) 구현 시간 약 5.5시간 추가

---

## ✅ 구현 결과물 목표

### 1. API 엔드포인트

#### 1.1 AI 사업계획서 생성 (본 PoC)
- **엔드포인트**: `POST /api/v1/business-plan/submissions/{submissionId}/generate`
- **선행 조건**: POC-FUNC-001에서 `submissionId` 확보
- **요청**: 없음 (submissionId만 필요)
- **응답**: [docs/AI_GENERATION_BE_API_SUBMIT.md](../docs/AI_GENERATION_BE_API_SUBMIT.md) 섹션 4 참조

---

### 2. 데이터베이스 스키마

#### 2.1 business_plan_documents 테이블
```sql
CREATE TABLE business_plan_documents (
    id CHAR(36) NOT NULL PRIMARY KEY,
    submission_id CHAR(36) NOT NULL UNIQUE,
    project_id CHAR(36) NOT NULL,
    sections TEXT NOT NULL COMMENT '섹션별 내용 (JSON)',
    template_type VARCHAR(20) NOT NULL,
    word_count INT,
    generation_time_ms BIGINT,
    model_used VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (submission_id) REFERENCES business_plan_submissions(id),
    FOREIGN KEY (project_id) REFERENCES projects(id)
);
```

---

### 3. 통합 테스트 시나리오

#### 시나리오 1: 전체 플로우 검증 (POC-FUNC-001 + POC-FUNC-003)

**Step 1**: 사업계획서 데이터 제출 (POC-FUNC-001)
```bash
POST /api/v1/business-plan/generate
Content-Type: application/json

{
  "requestInfo": {
    "templateType": "pre-startup",
    "generatedAt": "2025-12-17T12:30:00.000Z",
    "userId": "test-user-001",
    "projectId": "existing-project-uuid"
  },
  "businessPlanData": { ... },  # 6단계 데이터
  "generationOptions": {
    "tone": "professional",
    "targetLength": "detailed",
    "outputFormat": "markdown",
    "language": "ko",
    "sections": ["executive_summary", "problem_analysis", ...]
  }
}
```
→ `submissionId` 반환

**Step 2**: AI 사업계획서 생성 (POC-FUNC-003)
```bash
POST /api/v1/business-plan/submissions/{submissionId}/generate
```
→ 생성된 사업계획서 반환

#### 검증 포인트
- ✅ Submission 데이터가 정상 조회됨
- ✅ Gemini API가 호출되고 응답을 받음
- ✅ 응답이 섹션별로 파싱됨
- ✅ 생성된 문서가 DB에 저장됨
- ✅ Submission 상태가 COMPLETED로 업데이트됨
- ✅ API 스펙에 맞는 응답이 반환됨

---

## 🔍 간소화 사항 (PoC 범위)

### 제외된 기능
- ❌ 비동기 처리 (동기 호출로 단순화)
- ❌ 재시도 로직 (기본 에러 처리만)
- ❌ 사용자 인증/인가
- ❌ 문서 버전 관리
- ❌ 섹션별 수정/업데이트
- ❌ 스트리밍 응답 (일괄 응답만)
- ❌ 내보내기 기능 (PDF, HWP 등)

### 포함된 기능
- ✅ Submission 데이터 조회
- ✅ 프롬프트 구성 및 Gemini API 호출
- ✅ AI 응답 파싱 (섹션별 분리)
- ✅ 생성된 문서 저장 및 조회
- ✅ Submission 상태 관리

---

## 🚀 실행 계획

### 선행 작업: POC-FUNC-001 완료 확인
- [ ] BusinessPlanSubmission 엔티티 구현 완료
- [ ] `POST /api/v1/business-plan/generate` API 동작 확인
- [ ] 테스트용 submissionId 확보

### Step 1: 환경 준비
- [ ] Google Gemini API Key 발급
- [ ] 환경변수 설정 (`GEMINI_API_KEY`)
- [ ] Gemini API 직접 호출 테스트 (curl)

### Step 2: Phase 1 구현
- [ ] BusinessPlanDocument 엔티티 생성
- [ ] Repository 생성

### Step 3: Phase 2 구현
- [ ] GeminiApiClient 구현
- [ ] PromptBuilder 구현
- [ ] Gemini 요청/응답 DTO

### Step 4: Phase 3 구현
- [ ] BusinessPlanGenerationService 구현
- [ ] ResponseParser 구현

### Step 5: Phase 4 구현
- [ ] 생성 API 엔드포인트
- [ ] 응답 DTO

### Step 6: Phase 5-6 구현
- [ ] 설정 파일 업데이트
- [ ] WebClient 설정
- [ ] DB 마이그레이션

### Step 7: 테스트 및 검증
- [ ] 단위 테스트 (GeminiApiClient, PromptBuilder)
- [ ] 통합 테스트 (전체 플로우)
- [ ] 에러 케이스 테스트

---

## 📝 참고 사항

### Google Gemini API 인터페이스
- **엔드포인트**: `POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent`
- **인증**: API Key를 쿼리 파라미터로 전달 (`?key=YOUR_API_KEY`)
- **요청 형식**:
  ```json
  {
    "contents": [{
      "parts": [{
        "text": "프롬프트 텍스트..."
      }]
    }],
    "generationConfig": {
      "temperature": 0.7,
      "maxOutputTokens": 8192
    }
  }
  ```
- **응답 형식**:
  ```json
  {
    "candidates": [{
      "content": {
        "parts": [{
          "text": "생성된 사업계획서 텍스트..."
        }]
      },
      "finishReason": "STOP"
    }],
    "usageMetadata": {
      "promptTokenCount": 2500,
      "candidatesTokenCount": 4200,
      "totalTokenCount": 6700
    }
  }
  ```

### 프롬프트 전략
```
당신은 전문 사업계획서 컨설턴트입니다.

## 입력 데이터
[6단계 businessPlanData 구조화]

## 생성 지시
다음 섹션들을 마크다운 형식으로 작성해주세요:
- executive_summary: 사업 개요
- problem_analysis: 문제 분석
- market_analysis: 시장 분석
- solution_overview: 솔루션 개요
...

## 출력 형식
각 섹션은 "## [섹션제목]" 헤더로 구분해주세요.
```

### 의존성 추가 필요
```gradle
// WebClient (Spring WebFlux)
implementation 'org.springframework.boot:spring-boot-starter-webflux'
```

### 설정 파일
```properties
# application-local.properties
gemini.api.key=${GEMINI_API_KEY:}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta
gemini.api.model=gemini-2.0-flash
gemini.api.timeout=60000
gemini.api.temperature=0.7
gemini.api.max-output-tokens=8192
```

### 환경변수 설정
```bash
export GEMINI_API_KEY=your_gemini_api_key_here
```

### curl 테스트 예시
```bash
curl -X POST "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${GEMINI_API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
    "contents": [{
      "parts": [{
        "text": "안녕하세요. 테스트 메시지입니다."
      }]
    }]
  }'
```

---

## 🎯 성공 기준

### 기능적 요구사항
- ✅ Submission 데이터 조회 가능
- ✅ 프롬프트 구성 정상 동작
- ✅ Gemini API 호출 성공 및 응답 수신
- ✅ 응답 텍스트 섹션별 파싱 성공
- ✅ 생성된 문서 DB 저장 가능
- ✅ Submission 상태 업데이트 정상 동작

### 비기능적 요구사항
- ✅ API 응답 시간 < 60초 (AI 생성 포함)
- ✅ 에러 발생 시 명확한 에러 메시지
- ✅ 데이터 일관성 유지 (트랜잭션)
- ✅ Gemini API 타임아웃 처리

---

## 📚 관련 문서

- [POC-FUNC-001: 데이터 수신 API](./POC-FUNC-001.md) ← **선행 작업**
- [API 요청/응답 스펙](../docs/AI_GENERATION_BE_API_SUBMIT.md)
- [REQ-FUNC-003-AI-001](./functional/REQ-FUNC-003-AI-001.md)
- [REQ-FUNC-003-BE-001](./functional/REQ-FUNC-003-BE-001.md)
- [API 명세서](../docs/API_SPECIFICATION.md)

---

**작성일**: 2025-12-17  
**작성자**: AI Assistant  
**상태**: 계획 완료, POC-FUNC-001 완료 후 구현 예정

