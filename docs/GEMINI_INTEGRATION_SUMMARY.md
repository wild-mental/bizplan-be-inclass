# Gemini API 통합 작업 요약

**작업 일자**: 2025-12-18  
**작업 범위**: Spring AI를 통한 Google Gemini API 통합 및 사업계획서 생성 기능 구현

---

## 📋 목차

1. [배경 및 목표](#배경-및-목표)
2. [구현 내용](#구현-내용)
3. [아키텍처 변경사항](#아키텍처-변경사항)
4. [주요 기능](#주요-기능)
5. [설정 변경사항](#설정-변경사항)
6. [API 인터페이스 유지](#api-인터페이스-유지)
7. [토큰 사용량 추적](#토큰-사용량-추적)
8. [로깅 구조](#로깅-구조)
9. [데이터베이스 저장](#데이터베이스-저장)
10. [테스트 코드](#테스트-코드)
11. [추가 구현 사항](#추가-구현-사항)

---

## 배경 및 목표

### 배경
- `/api/v1/business-plan/generate` 엔드포인트가 기존에 Mock 데이터를 반환하던 상태였음
- 실제 AI 엔진을 통한 사업계획서 보강 기능이 필요했음
- Spring Boot 백엔드에서 Google Gemini API를 직접 호출하여 사용자 초안을 보강하는 기능 구현 필요

### 목표
- **인터페이스 유지**: 기존 FE-BE 간 요청/응답 포맷을 절대적으로 유지
- **Gemini 통합**: Spring AI를 통해 Google Gemini API 호출
- **토큰 추적**: Usage 메타데이터를 통한 비용 추적 및 로깅
- **3-Tier 구조**: Controller → Service → Repository 레이어 분리

---

## 구현 내용

### 1. 의존성 추가 (`build.gradle`)

```gradle
// Spring AI (Google Gemini)
implementation platform('org.springframework.ai:spring-ai-bom:2.0.0-M1')
implementation 'org.springframework.ai:spring-ai-starter-model-google-genai'
```

- **Spring AI BOM**: 버전 관리 일관성 보장
- **Google GenAI Starter**: Spring Boot Auto-Configuration을 통한 자동 빈 주입

### 2. 설정 파일 업데이트 (`application.properties`)

```properties
# Spring AI - Google Gemini Configuration
spring.ai.google.genai.api-key=${GEMINI_API_KEY:}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash-lite
spring.ai.google.genai.chat.options.temperature=0.8
spring.ai.google.genai.chat.options.top-p=0.9
```

- **모델 선택**: `gemini-2.5-flash-lite` (문서 보강 속도와 비용 효율성 고려)
- **Temperature**: 0.8 (창의성 조절, 문서 보강에 적합)
- **API Key**: 환경변수 `GEMINI_API_KEY`로 주입 (보안)

### 3. 신규 클래스 생성

#### 3.1 `BusinessPlanGenerationService` (Service Layer)
- **위치**: `src/main/java/vibe/makersround/makersround_be_inclass/service/BusinessPlanGenerationService.java`
- **역할**:
  - Spring AI `ChatModel`을 통한 Gemini API 호출
  - 시스템/유저 프롬프트 구성
  - 생성 결과를 `BusinessPlanGenerateResponse` DTO로 매핑
  - 토큰 사용량 추출 및 메타데이터 구성

**주요 메서드**:
- `generateBusinessPlan()`: 메인 생성 로직
- `buildSystemPrompt()`: 전문 편집자 역할 부여
- `buildUserPrompt()`: 사용자 초안 데이터를 프롬프트로 변환
- `mapToSections()`: 생성된 마크다운을 섹션 리스트로 매핑
- `buildGenerationMetadata()`: 토큰/시간/텍스트 길이 메타데이터 구성

#### 3.2 `BusinessPlanGenerationRepository` (Repository Layer)
- **위치**: `src/main/java/vibe/makersround/makersround_be_inclass/repository/BusinessPlanGenerationRepository.java`
- **역할**:
  - Gemini 토큰 사용량을 로그로 기록
  - 향후 JPA 기반 DB 저장 확장 대비

**주요 메서드**:
- `saveUsage()`: 토큰 사용량 로깅 (`[Gemini Usage Log]` 포맷)

### 4. 컨트롤러 수정 (`BusinessPlanController`)

**변경 전**:
- Mock 데이터 생성 로직 (하드코딩된 섹션/메타데이터)

**변경 후**:
- `BusinessPlanGenerationService` 호출로 위임
- 기존 요청/응답 인터페이스 100% 유지

**주요 변경사항**:
```java
// Before
List<BusinessPlanSection> sections = createMockSections(itemName);
GenerationMetadata metadata = GenerationMetadata.builder()...build();

// After
BusinessPlanGenerateResponse response = businessPlanGenerationService.generateBusinessPlan(
    request, projectId, templateType, itemName, startTime
);
```

---

## 아키텍처 변경사항

### 기존 구조 (Mock)
```
Controller → Mock 데이터 생성 → Response 반환
```

### 변경 후 구조 (3-Tier)
```
Controller → Service → Repository
              ↓
         Spring AI ChatModel
              ↓
         Google Gemini API
```

### 레이어별 책임

| 레이어 | 클래스 | 책임 |
|--------|--------|------|
| **Controller** | `BusinessPlanController` | HTTP 요청/응답 처리, 요청 데이터 추출/검증 |
| **Service** | `BusinessPlanGenerationService` | 비즈니스 로직, Gemini 호출, DTO 매핑 |
| **Repository** | `BusinessPlanGenerationRepository` | 사용량 로깅 |
| **Repository** | `BusinessPlanRepository` | 사업계획서 데이터 DB 저장 (JPA) |

---

## 주요 기능

### 1. 프롬프트 구성

#### 시스템 프롬프트
- **역할**: 전문적인 사업계획서 편집자/컨설턴트
- **요구사항**:
  - 정부 지원사업, 은행 대출, 투자 유치용 문서 전문성
  - 사용자 초안 존중 + 논리적 구조 재구성
  - 재무 수치 보존, 표현만 명확화
  - 한국어, 전문적 톤, 마크다운 형식

#### 유저 프롬프트
- **입력**: `BusinessPlanGenerateRequest` 전체를 JSON으로 직렬화
- **구조**: 6단계 구조 유지 (문제 인식 → 시장 분석 → 실현 가능성 → 사업화 전략 → 팀 역량 → 재무 계획)
- **출력 요구**: 마크다운 섹션 제목(`## 1. 사업 개요`) 및 하위 소제목

### 2. 토큰 사용량 추적

#### Usage 메타데이터 추출
```java
ResponseMetadata metadata = chatResponse.getMetadata();
Usage usage = metadata != null ? metadata.getUsage() : null;

int promptTokens = usage != null && usage.getPromptTokens() != null ? usage.getPromptTokens() : 0;
int completionTokens = usage != null && usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0;
int totalTokens = usage != null && usage.getTotalTokens() != null ? usage.getTotalTokens() : promptTokens + completionTokens;
```

#### 로깅 포맷
```
[Gemini Usage Log] Input: {promptTokens}, Output: {completionTokens}, Total: {totalTokens}
```

#### 응답 DTO 매핑
- `BusinessPlanGenerateResponse.GenerationMetadata`에 토큰 정보 포함:
  - `promptTokens`: 입력 토큰 수
  - `completionTokens`: 출력 토큰 수
  - `totalTokens`: 총 토큰 수
  - `modelUsed`: 사용된 모델명 (`gemini-2.5-flash-lite`)

### 3. 섹션 매핑 (MVP 전략)

**현재 구현**:
- Gemini가 생성한 전체 마크다운을 단일 섹션으로 감싸서 반환
- FE 스펙(섹션 배열 구조) 유지

**향후 개선**:
- 마크다운 파싱을 통한 자동 섹션 분할 (`##` 기준)
- FE가 섹션 단위로 렌더링하기 용이하도록 개선

---

## 설정 변경사항

### 환경변수 필수 설정

```bash
export GEMINI_API_KEY="your-api-key-here"
```

### 애플리케이션 설정 (`application.properties`)

```properties
# Spring AI - Google Gemini Configuration
spring.ai.google.genai.api-key=${GEMINI_API_KEY:}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash-lite
spring.ai.google.genai.chat.options.temperature=0.8
spring.ai.google.genai.chat.options.top-p=0.9
```

---

## API 인터페이스 유지

### 요청 포맷 (변경 없음)
```json
POST /api/v1/business-plan/generate
{
  "requestInfo": { ... },
  "businessPlanData": { ... },
  "generationOptions": { ... }
}
```

### 응답 포맷 (변경 없음)
```json
{
  "success": true,
  "data": {
    "businessPlanId": "bp-2025-12-18-xxxxxxxx",
    "projectId": "...",
    "generatedAt": "2025-12-18T...",
    "templateType": "pre-startup",
    "sections": [ ... ],
    "metadata": {
      "promptTokens": <실제 값>,
      "completionTokens": <실제 값>,
      "totalTokens": <실제 값>,
      "modelUsed": "gemini-2.5-flash-lite",
      ...
    },
    "exportOptions": { ... }
  },
  "error": null
}
```

**변경점**:
- `metadata.promptTokens/completionTokens/totalTokens`: Mock 값 → 실제 Usage 값
- `metadata.modelUsed`: Mock 값 → 실제 모델명
- `sections[].content`: Mock 텍스트 → Gemini 생성 마크다운

---

## 토큰 사용량 추적

### 로그 출력 예시 (콘솔)
```
[Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

### 로그 출력 예시 (파일 - logs/gemini-usage.log)
```
2025-12-18 14:30:19.500,[Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

### 응답 메타데이터 예시
```json
{
  "metadata": {
    "promptTokens": 1234,
    "completionTokens": 5678,
    "totalTokens": 6912,
    "modelUsed": "gemini-2.5-flash-lite",
    "generationTimeMs": 4500,
    "wordCount": 3847,
    "characterCount": 12450
  }
}
```

### 비용 추적 및 성능 모니터링 활용

#### 콘솔 로그 활용
- 서버 로그에서 일괄 추출하여 비용 분석
- 실시간 모니터링 및 디버깅

#### 파일 로그 활용 (`logs/gemini-usage.log`)
- CSV 형식으로 저장되어 분석 도구(Excel, Python pandas 등)로 쉽게 처리 가능
- 일별/월간 사용량 집계
- 성능 메트릭 분석 (평균 처리량, 응답 시간 분포)
- 프로젝트별/사용자별 사용량 집계

#### 응답 메타데이터 활용
- FE에서 표시하여 사용자에게 투명성 제공
- API 응답을 통한 실시간 사용량 확인

#### 데이터베이스 저장 (2025-12-20 완료)
- ✅ `BusinessPlan` 엔티티 생성
- ✅ `BusinessPlanRepository` 인터페이스 생성
- ✅ 요청 데이터, 응답 데이터, Gemini 메타데이터 모두 DB 저장
- ✅ 3-tier 구조 준수 (Service Layer에서 DTO → Entity 변환)

#### 향후 확장 가능성
- 사용량 통계/알림 기능 확장 가능
- 대시보드 구축 (Grafana 등)
- 예산 초과 알림 기능

---

## 로깅 구조

사업계획서 생성 프로세스는 3-Tier 아키텍처의 각 레이어에서 상세한 로그를 남깁니다. 각 파일별로 어떤 로그가 출력되는지, 실제 예제와 함께 설명합니다.

### 로깅 흐름도

```
HTTP Request
    ↓
[Controller] 요청 수신 및 데이터 추출 로깅
    ↓
[Service] Gemini 호출 및 Usage 로깅
    ↓
[Repository] 사용량 저장 로깅
    ↓
[Controller] 생성 완료 로깅
    ↓
HTTP Response
```

---

### 1. Controller Layer 로깅

**파일**: `BusinessPlanController.java`  
**로거**: `@Slf4j` (Lombok)  
**로그 레벨**: `INFO`

#### 1.1 요청 수신 로그

**출력 위치**: `generateBusinessPlan()` 메서드 시작 부분

**로그 예제**:
```
2025-12-18 14:30:15.123  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
2025-12-18 14:30:15.123  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : [POC-FUNC-001] 사업계획서 생성 요청 수신
2025-12-18 14:30:15.123  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
```

**코드 위치**:
```java
log.info("========================================");
log.info("[POC-FUNC-001] 사업계획서 생성 요청 수신");
log.info("========================================");
```

#### 1.2 RequestInfo 추출 로그

**출력 위치**: `requestInfo` 추출 후

**로그 예제**:
```
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : 📋 requestInfo:
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - projectId: proj-abc12345
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - userId: user-uuid-here
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - templateType: pre-startup
```

**코드 위치**:
```java
log.info("📋 requestInfo:");
log.info("   - projectId: {}", projectId);
log.info("   - userId: {}", userId);
log.info("   - templateType: {}", templateType);
```

#### 1.3 BusinessPlanData 요약 로그

**출력 위치**: `logBusinessPlanDataSummary()` 메서드 호출

**로그 예제**:
```
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : 📊 businessPlanData 수신 현황:
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step1 (문제 인식): ✅ itemName = AI 기반 맞춤형 학습 플랫폼 LearnAI
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step2 (시장 분석): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step3 (실현 방안): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step4 (사업화 전략): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step5 (팀 역량): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step6 (재무 계획): ✅
```

**코드 위치**: `logBusinessPlanDataSummary()` private 메서드

#### 1.4 GenerationOptions 로그

**출력 위치**: `logGenerationOptions()` 메서드 호출

**로그 예제**:
```
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ⚙️ generationOptions:
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - tone: professional
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - targetLength: detailed
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - outputFormat: markdown
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - language: ko
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - sections: 10 개
```

**코드 위치**: `logGenerationOptions()` private 메서드

#### 1.5 생성 완료 로그

**출력 위치**: `generateBusinessPlan()` 메서드 종료 전

**로그 예제**:
```
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ✅ 사업계획서 생성 완료
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - businessPlanId: bp-2025-12-18-550e8400
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - sections: 1 개
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - generationTimeMs: 4500
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
```

**코드 위치**:
```java
log.info("========================================");
log.info("✅ 사업계획서 생성 완료");
log.info("   - businessPlanId: {}", response.getBusinessPlanId());
log.info("   - sections: {} 개", 
        response.getSections() != null ? response.getSections().size() : 0);
log.info("   - generationTimeMs: {}", 
        response.getMetadata() != null ? response.getMetadata().getGenerationTimeMs() : "N/A");
log.info("========================================");
```

#### 1.6 조회 요청 로그

**출력 위치**: `getBusinessPlan()` 메서드

**로그 예제**:
```
2025-12-18 14:35:20.456  INFO  --- [http-nio-8080-exec-2] c.v.b.controller.BusinessPlanController : [POC-FUNC-001] 사업계획서 조회 요청 - businessPlanId: bp-2025-12-18-550e8400
```

**코드 위치**:
```java
log.info("[POC-FUNC-001] 사업계획서 조회 요청 - businessPlanId: {}", businessPlanId);
```

---

### 2. Service Layer 로깅

**파일**: `BusinessPlanGenerationService.java`  
**로거**: `@Slf4j` (Lombok)  
**로그 레벨**: `INFO`, `WARN`

#### 2.1 Gemini Usage 로그 (비용 추적 및 성능 모니터링)

**출력 위치**: `generateBusinessPlan()` 메서드 내 Usage 추출 후

**로그 예제 (콘솔 출력)**:
```
2025-12-18 14:30:19.500  INFO  --- [http-nio-8080-exec-1] c.v.b.s.BusinessPlanGenerationService : [Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

**로그 예제 (파일 출력 - logs/gemini-usage.log)**:
```
2025-12-18 14:30:19.500,[Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

**코드 위치**:
```java
long geminiStartTime = System.currentTimeMillis();
String geminiStartTimeIso = Instant.now().toString();

ChatResponse chatResponse = chatModel.call(prompt);

long geminiEndTime = System.currentTimeMillis();
String geminiEndTimeIso = Instant.now().toString();
long geminiDurationMs = geminiEndTime - geminiStartTime;

// 토큰 처리량 계산 (tokens/sec)
double tokensPerSecond = geminiDurationMs > 0 
        ? totalTokens / (geminiDurationMs / 1000.0) 
        : 0.0;

log.info("[Gemini Usage Log] StartTime: {}, EndTime: {}, Duration: {}ms, Input: {}, Output: {}, Total: {}, Throughput: {} tokens/sec",
        geminiStartTimeIso, geminiEndTimeIso, geminiDurationMs, 
        promptTokens, completionTokens, totalTokens, String.format("%.2f", tokensPerSecond));
```

**로그 필드 설명**:
- `StartTime`: Gemini API 호출 시작 시간 (ISO 8601 형식)
- `EndTime`: Gemini API 호출 종료 시간 (ISO 8601 형식)
- `Duration`: 소요 시간 (밀리초)
- `Input`: 입력 토큰 수 (prompt tokens)
- `Output`: 출력 토큰 수 (completion tokens)
- `Total`: 총 토큰 수
- `Throughput`: 토큰 처리량 (tokens/sec) - 성능 지표

**용도**:
- 비용 추적 및 분석
- 성능 모니터링 (처리량, 응답 시간)
- 서버 로그 및 파일 로그에서 일괄 추출하여 일일/월간 사용량 집계 가능
- 예산 초과 알림 기능 구현 시 활용
- 성능 병목 지점 분석

#### 2.2 직렬화 실패 경고 로그

**출력 위치**: `safeSerialize()` 메서드 내 예외 처리

**로그 예제** (정상 케이스에서는 출력되지 않음):
```
2025-12-18 14:30:15.200  WARN  --- [http-nio-8080-exec-1] c.v.b.s.BusinessPlanGenerationService : BusinessPlanGenerateRequest 직렬화에 실패했습니다. 간단한 문자열로 대체합니다.
com.fasterxml.jackson.core.JsonProcessingException: ...
    at vibe.makersround.makersround_be_inclass.service.BusinessPlanGenerationService.safeSerialize(BusinessPlanGenerationService.java:193)
    ...
```

**코드 위치**:
```java
catch (JsonProcessingException e) {
    log.warn("BusinessPlanGenerateRequest 직렬화에 실패했습니다. 간단한 문자열로 대체합니다.", e);
    return "Failed to serialize request. Use high-level fields only.";
}
```

**용도**:
- 프롬프트 구성 실패 시 디버깅 정보 제공
- Fallback 메커니즘 동작 확인

---

### 3. Repository Layer 로깅

**파일**: `BusinessPlanGenerationRepository.java`  
**로거**: `@Slf4j` (Lombok)  
**로그 레벨**: `INFO`

#### 3.1 사용량 저장 로그

**출력 위치**: `saveUsage()` 메서드

**로그 예제 (콘솔 출력)**:
```
2025-12-18 14:30:19.501  INFO  --- [http-nio-8080-exec-1] c.v.b.r.BusinessPlanGenerationRepository : [Gemini Usage Log] businessPlanId=bp-2025-12-18-550e8400, StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

**로그 예제 (파일 출력 - logs/gemini-usage.log)**:
```
2025-12-18 14:30:19.501,[Gemini Usage Log] businessPlanId=bp-2025-12-18-550e8400, StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

**코드 위치**:
```java
log.info("[Gemini Usage Log] businessPlanId={}, StartTime: {}, EndTime: {}, Duration: {}ms, Input: {}, Output: {}, Total: {}, Throughput: {} tokens/sec",
        businessPlanId, startTimeIso, endTimeIso, durationMs,
        promptTokens, completionTokens, totalTokens, String.format("%.2f", tokensPerSecond));
```

**용도**:
- 비즈니스 계획서별 토큰 사용량 추적
- 개별 문서의 성능 메트릭 추적
- 향후 DB 저장 시 이 로그를 기반으로 엔티티 생성 가능
- 프로젝트별/사용자별 사용량 집계 시 활용

**차이점**:
- Service Layer의 Usage 로그: 전체 요청에 대한 토큰 사용량 및 성능 메트릭 기록
- Repository Layer의 Usage 로그: `businessPlanId`와 함께 기록하여 개별 문서 추적 가능

---

### 전체 로그 흐름 예제

한 번의 `/api/v1/business-plan/generate` 요청에 대한 전체 로그 출력 예제:

```
2025-12-18 14:30:15.123  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
2025-12-18 14:30:15.123  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : [POC-FUNC-001] 사업계획서 생성 요청 수신
2025-12-18 14:30:15.123  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : 📋 requestInfo:
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - projectId: proj-abc12345
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - userId: user-uuid-here
2025-12-18 14:30:15.125  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - templateType: pre-startup
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : 📊 businessPlanData 수신 현황:
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step1 (문제 인식): ✅ itemName = AI 기반 맞춤형 학습 플랫폼 LearnAI
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step2 (시장 분석): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step3 (실현 방안): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step4 (사업화 전략): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step5 (팀 역량): ✅
2025-12-18 14:30:15.127  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - Step6 (재무 계획): ✅
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ⚙️ generationOptions:
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - tone: professional
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - targetLength: detailed
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - outputFormat: markdown
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - language: ko
2025-12-18 14:30:15.128  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - sections: 10 개
2025-12-18 14:30:19.500  INFO  --- [http-nio-8080-exec-1] c.v.b.s.BusinessPlanGenerationService : [Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
2025-12-18 14:30:19.501  INFO  --- [http-nio-8080-exec-1] c.v.b.r.BusinessPlanGenerationRepository : [Gemini Usage Log] businessPlanId=bp-2025-12-18-550e8400, StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
2025-12-18 14:30:15.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ✅ 사업계획서 생성 완료
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - businessPlanId: bp-2025-12-18-550e8400
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - sections: 1 개
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController :    - generationTimeMs: 4500
2025-12-18 14:30:19.623  INFO  --- [http-nio-8080-exec-1] c.v.b.controller.BusinessPlanController : ========================================
```

**시간 흐름 분석**:
- `14:30:15.123` ~ `14:30:15.128`: 요청 수신 및 데이터 추출 (약 5ms)
- `14:30:15.128` ~ `14:30:19.500`: Gemini API 호출 및 응답 대기 (약 4.4초)
- `14:30:19.500` ~ `14:30:19.623`: 응답 매핑 및 완료 처리 (약 123ms)

---

### 로그 활용 가이드

#### 1. 비용 추적
```bash
# 일일 토큰 사용량 집계 (콘솔 로그)
grep "\[Gemini Usage Log\]" application.log | grep "Total:" | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "Total tokens:", sum}'

# 일일 토큰 사용량 집계 (파일 로그 - 권장)
grep "Total:" logs/gemini-usage.log | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "Total tokens:", sum}'

# 프로젝트별 사용량 추적 (파일 로그)
grep "businessPlanId=bp-" logs/gemini-usage.log | awk -F'Total: ' '{print $2}' | awk -F',' '{print $1}'
```

#### 2. 성능 모니터링
```bash
# 평균 생성 시간 계산 (Controller 로그)
grep "generationTimeMs:" application.log | awk -F': ' '{sum+=$2; count++} END {print "Average:", sum/count, "ms"}'

# Gemini API 평균 응답 시간 (파일 로그)
grep "Duration:" logs/gemini-usage.log | awk -F'Duration: ' '{print $2}' | awk -F'ms' '{sum+=$1; count++} END {print "Average duration:", sum/count, "ms"}'

# 평균 처리량 (tokens/sec) 계산
grep "Throughput:" logs/gemini-usage.log | awk -F'Throughput: ' '{print $2}' | awk '{sum+=$1; count++} END {print "Average throughput:", sum/count, "tokens/sec"}'

# 최대/최소 처리량
grep "Throughput:" logs/gemini-usage.log | awk -F'Throughput: ' '{print $2}' | awk '{print $1}' | sort -n | awk 'NR==1{min=$1} END{max=$1} {print "Min:", min, "tokens/sec, Max:", max, "tokens/sec"}'
```

#### 3. 에러 디버깅
```bash
# 직렬화 실패 케이스 확인
grep "직렬화에 실패했습니다" application.log

# 요청 데이터 누락 확인
grep "Step.*❌" application.log
```

#### 4. 사용량 통계
```bash
# 시간대별 요청 수
grep "\[POC-FUNC-001\] 사업계획서 생성 요청 수신" application.log | cut -d' ' -f1-2 | uniq -c
```

---

### 로그 레벨 설정

**현재 설정**: 모든 로그가 `INFO` 레벨로 출력

**프로덕션 환경 권장 설정** (`application.properties`):
```properties
# 로깅 레벨 설정
logging.level.vibe.makersround.makersround_be_inclass.controller=INFO
logging.level.vibe.makersround.makersround_be_inclass.service=INFO
logging.level.vibe.makersround.makersround_be_inclass.repository=INFO

# Spring AI 내부 로그는 WARN으로 제한 (너무 상세함)
logging.level.org.springframework.ai=WARN
```

**개발 환경 설정**:
```properties
# 디버깅을 위해 DEBUG 레벨 활성화 가능
logging.level.vibe.makersround.makersround_be_inclass.service=DEBUG
```

### 파일 로깅 설정

**설정 파일**: `src/main/resources/logback-spring.xml`

**주요 기능**:
- Gemini Usage Log만 별도 파일로 기록 (`logs/gemini-usage.log`)
- 콘솔 출력은 그대로 유지 (additivity=true)
- 일별 롤링 및 크기 기반 롤링 (10MB 초과 시, 최대 30일 보관, 총 100MB 제한)
- CSV 형식으로 저장하여 분석 용이

**파일 위치**:
- 현재 날짜: `logs/gemini-usage.log`
- 과거 날짜: `logs/gemini-usage.2025-12-18.0.log`, `logs/gemini-usage.2025-12-18.1.log` 등

**로그 포맷 (파일)**:
```
2025-12-18 14:30:19.500,[Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

**활용 예시**:
```bash
# 일일 토큰 사용량 집계
grep "Total:" logs/gemini-usage.log | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "Total tokens:", sum}'

# 평균 처리량 계산
grep "Throughput:" logs/gemini-usage.log | awk -F'Throughput: ' '{print $2}' | awk '{sum+=$1; count++} END {print "Average throughput:", sum/count, "tokens/sec"}'

# 시간대별 요청 수
cut -d',' -f1 logs/gemini-usage.log | cut -d' ' -f2 | cut -d':' -f1 | sort | uniq -c
```

---

## 테스트 방법

### 1. 환경변수 설정
```bash
export GEMINI_API_KEY="your-api-key"
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. API 호출
```bash
curl -X POST http://localhost:8080/api/v1/business-plan/generate \
  -H "Content-Type: application/json" \
  -d @request.json
```

### 4. 로그 확인
- 서버 로그에서 `[Gemini Usage Log]` 메시지 확인
- 응답 JSON의 `metadata` 필드에서 토큰 사용량 확인

---

## 테스트 코드

### 테스트 커버리지

**작성 완료일**: 2025-12-19  
**테스트 파일**:
- `BusinessPlanGenerationServiceTest.java` (단위 테스트, 19개)
- `BusinessPlanGenerationServiceIntegrationTest.java` (통합 테스트, 2개)
- `BusinessPlanGenerationRepositoryTest.java` (Repository 테스트, 4개)

**테스트 결과**:
- 총 테스트 수: 25개
- 성공률: 96% (조건부 포함 시 100%)
- 코드 커버리지: ~95% (Service 레이어)

**주요 테스트 케이스**:
- ✅ 정상 플로우 테스트 (6개)
- ✅ 예외 처리 테스트 (8개)
- ✅ 기능 검증 테스트 (3개)
- ✅ 실제 Gemini API 호출 테스트 (2개)
- ✅ Repository 로깅 테스트 (4개)

**테스트 실행 방법**:
```bash
# 전체 테스트 실행
./gradlew test --tests "*BusinessPlanGeneration*"

# 단위 테스트만 실행
./gradlew test --tests "*BusinessPlanGeneration*Test" --exclude-tag integration

# 통합 테스트만 실행 (GEMINI_API_KEY 필요)
export GEMINI_API_KEY="your-api-key"
./gradlew test --tests "*BusinessPlanGenerationServiceIntegrationTest*"
```

**상세 테스트 보고서**: [GEMINI_TEST_REPORT.md](./GEMINI_TEST_REPORT.md)

---

## 추가 구현 사항

### JacksonConfig 추가 (2025-12-19)

**파일**: `src/main/java/vibe/makersround/makersround_be_inclass/config/JacksonConfig.java`

**목적**: `BusinessPlanGenerationService`에서 사용하는 `ObjectMapper` 빈을 명시적으로 제공

**구현 내용**:
- `@Configuration` 클래스로 `ObjectMapper` 빈 정의
- `@Primary` 어노테이션으로 우선순위 부여
- Spring Boot의 기본 JacksonAutoConfiguration이 작동하지 않는 경우를 대비

**코드**:
```java
@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

---

## 데이터베이스 저장

### 개요

2025-12-20에 비즈니스 플랜 생성 요청 시 제출된 요청 데이터, 생성된 사업계획서 문서 응답, 그리고 Gemini 요청 메타데이터를 모두 데이터베이스에 저장하는 기능이 구현되었습니다.

### 구현 내용

#### 1. 엔티티 및 Repository 생성

**BusinessPlan 엔티티** (`entity/BusinessPlan.java`):
- 요청 데이터 전체 (JSON)
- 응답 섹션 데이터 (JSON)
- Gemini 메타데이터 (토큰 사용량, 시간 정보 등 JSON)
- 프로젝트 ID, 사용자 ID, 템플릿 유형 등 메타 정보

**BusinessPlanRepository** (`repository/BusinessPlanRepository.java`):
- Spring Data JPA 인터페이스
- `findByBusinessPlanId()`, `findByProjectId()`, `findByUserId()`, `findByTemplateType()` 메서드 제공

#### 2. 데이터베이스 스키마

**Flyway 마이그레이션** (`V2__create_business_plans_table.sql`):
```sql
CREATE TABLE business_plans (
    id CHAR(36) NOT NULL PRIMARY KEY,                    -- UUID
    business_plan_id VARCHAR(50) NOT NULL UNIQUE,        -- bp-2025-12-20-xxx
    project_id CHAR(36),                                 -- FK to projects (nullable)
    user_id VARCHAR(36),
    template_type VARCHAR(20) NOT NULL,
    request_data_json TEXT NOT NULL,                     -- 요청 전체 JSON
    response_sections_json TEXT NOT NULL,                -- 응답 섹션들 JSON
    gemini_metadata_json TEXT,                           -- Gemini 메타데이터 JSON
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
)
```

#### 3. Service Layer 통합

**BusinessPlanGenerationService**에 `saveBusinessPlanToDatabase()` 메서드 추가:
- DTO → Entity 변환 (Rule 306: 3-tier 구조 준수)
- JSON 직렬화 (요청, 응답, 메타데이터)
- 트랜잭션 관리 (`@Transactional`)
- 예외 처리 (JSON 직렬화 실패, UUID 파싱 실패 등)

#### 4. 저장되는 데이터 상세

**요청 데이터** (`request_data_json`):
- `BusinessPlanGenerateRequest` 전체를 JSON으로 직렬화
- 6단계 사업계획서 입력 데이터 포함
- 생성 옵션 (톤, 길이, 포맷 등) 포함

**응답 데이터** (`response_sections_json`):
- 생성된 `BusinessPlanSection` 리스트를 JSON으로 직렬화
- 각 섹션의 ID, 제목, 내용, 순서 포함

**Gemini 메타데이터** (`gemini_metadata_json`):
```json
{
  "startTimeIso": "2025-12-20T14:30:15.123Z",
  "endTimeIso": "2025-12-20T14:30:19.500Z",
  "durationMs": 4377,
  "promptTokens": 1234,
  "completionTokens": 5678,
  "totalTokens": 6912,
  "tokensPerSecond": 1578.25,
  "modelUsed": "gemini-2.5-flash-lite",
  "generationTimeMs": 4500,
  "wordCount": 3847,
  "characterCount": 12450,
  "totalSections": 6
}
```

### 3-Tier 구조 준수

**Rule 306: Three-Tier Architecture Rules** 준수:
- ✅ Controller: HTTP 요청/응답 처리, DTO만 사용
- ✅ Service: 비즈니스 로직, DTO ↔ Entity 변환, 트랜잭션 관리
- ✅ Repository: 데이터 접근, Entity만 사용

**데이터 흐름**:
```
HTTP Request (JSON)
    ↓
Controller: Request DTO 생성
    ↓
Service: DTO → Entity 변환 + 비즈니스 로직
    ↓
Repository: Entity 저장
    ↓
Database: INSERT
```

### 테스트

**BusinessPlanRepositoryTest**:
- `save_validBusinessPlan_savesSuccessfully()`: 저장 기능 검증
- `findByBusinessPlanId()`: ID로 조회 검증
- `findByProjectId()`: 프로젝트별 조회 검증
- `findByUserId()`: 사용자별 조회 검증
- `findByTemplateType()`: 템플릿별 조회 검증

**BusinessPlanGenerationServiceTest**:
- DB 저장 호출 검증 (`verify(businessPlanRepository, times(1)).save(any())`)

---

## 참고 문서

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Google Gemini API](https://ai.google.dev/)
- [AI_GENERATION_BE_API_SUBMIT.md](./AI_GENERATION_BE_API_SUBMIT.md) - API 스펙 문서
- [GEMINI_TEST_REPORT.md](./GEMINI_TEST_REPORT.md) - 테스트 보고서
- [GEMINI_INTEGRATION_TODO.md](./GEMINI_INTEGRATION_TODO.md) - 개선 제안 TO-DO

---

**작성자**: AI Assistant  
**최종 수정일**: 2025-12-20
