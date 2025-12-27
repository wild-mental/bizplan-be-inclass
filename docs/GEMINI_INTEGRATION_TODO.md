# Gemini 통합 개선 제안 TO-DO

**작성 일자**: 2025-12-18  
**목적**: 코드 품질 개선 및 기능 확장성을 높이기 위한 제안사항 정리

---

## 📋 목차

1. [코드 품질 개선](#코드-품질-개선)
2. [기능 확장](#기능-확장)
3. [성능 최적화](#성능-최적화)
4. [에러 핸들링 강화](#에러-핸들링-강화)
5. [테스트 코드 작성](#테스트-코드-작성)
6. [모니터링 및 알림](#모니터링-및-알림)
7. [보안 강화](#보안-강화)

---

## 코드 품질 개선

### 🔴 High Priority

#### 1. 린터 에러 수정
**파일**: `BusinessPlanGenerationService.java`

- [x] **사용하지 않는 변수 제거** ✅ (2025-12-20 확인)
  - `modelStart`, `modelEnd` 변수는 이미 제거됨
  - 현재 `geminiStartTime`, `geminiEndTime`으로 시간 측정 로직 구현됨 (81-88줄)

- [x] **문자열 리터럴 상수화** ✅ (2025-12-20 완료)
  - `/api/v1/business-plan/` 문자열 4회 중복 → `API_BASE_PATH` 상수로 추출 완료 (47줄)
  - `"section-1"` → `DEFAULT_SECTION_ID` 상수로 추출 완료 (50줄)
  - `"AI 보강 사업계획서"` → `DEFAULT_SECTION_TITLE` 상수로 추출 완료 (51줄)
  - 모든 하드코딩된 문자열을 상수로 대체 완료

- [x] **시스템 프롬프트 상수화** ✅ (2025-12-20 완료)
  - `buildSystemPrompt()` 메서드의 반환값을 `SYSTEM_PROMPT` 클래스 상수로 이동 완료 (53-66줄)
  - 프롬프트 수정 시 상수만 수정하면 되도록 개선 완료
  - 향후 외부 리소스 파일로 이동 가능한 구조로 확장 가능

**구현 완료**:
```java
// API 경로 상수
private static final String API_BASE_PATH = "/api/v1/business-plan/";

// 섹션 기본값 상수
private static final String DEFAULT_SECTION_ID = "section-1";
private static final String DEFAULT_SECTION_TITLE = "AI 보강 사업계획서";

// 시스템 프롬프트 상수
private static final String SYSTEM_PROMPT = """...""";
```

#### 2. Null Safety 강화
**파일**: `BusinessPlanGenerationService.java`

**현재 상태** (2025-12-20 확인):
- null 체크는 구현되어 있으나 Optional 미사용 (99-101줄)
- `generation.getOutput()` null 체크는 구현됨 (91-93줄)
- 빈 문자열 기본값 반환은 구현됨 (93줄)

- [ ] **Optional 활용**
  - `usage.getPromptTokens()` 등 null 체크를 Optional로 개선 (현재는 삼항 연산자 사용)
  - `generation.getOutput()` null 체크는 있으나 Optional로 리팩토링 필요

- [x] **방어적 프로그래밍** ✅ (부분 완료)
  - Gemini 응답이 null이거나 빈 문자열일 경우 기본값 반환 구현됨 (91-93줄)
  - 추가 개선: Optional 활용으로 더 명시적인 null 처리 가능

**예시**:
```java
String generatedContent = Optional.ofNullable(chatResponse.getResult())
    .map(Generation::getOutput)
    .map(AssistantMessage::getContent)
    .orElse("");
```

---

### 🟡 Medium Priority

#### 3. 코드 가독성 개선
**파일**: `BusinessPlanGenerationService.java`

- [ ] **메서드 분리**
  - `generateBusinessPlan()` 메서드가 너무 길어짐 (약 80줄)
  - 프롬프트 구성, Gemini 호출, 응답 매핑을 별도 private 메서드로 분리

- [ ] **매직 넘버 제거**
  - `UUID.randomUUID().toString().substring(0, 8)` → 상수로 추출
  - 토큰 기본값 `0` → 명시적 상수

#### 4. 문서화 개선
**전체 파일**

- [ ] **JavaDoc 보강**
  - 모든 public 메서드에 상세 JavaDoc 추가
  - 예외 처리, null 반환 가능성 명시

- [ ] **인라인 주석 추가**
  - 복잡한 로직에 대한 설명 주석 추가
  - 비즈니스 의사결정 근거 문서화

---

## 기능 확장

### 🔴 High Priority

#### 5. 섹션 자동 파싱 개선 ✅ (2025-12-20 완료)
**파일**: `BusinessPlanGenerationService.mapToSections()`

**구현 완료**:
- [x] **마크다운 파서 구현** ✅
  - `##` (H2) 기준으로 섹션 자동 분할 구현 완료
  - 각 섹션에 고유 ID 부여 (`section-1`, `section-2`, ...) 구현 완료
  - 섹션 제목 추출 (`## 1. 사업 개요` → `title: "1. 사업 개요"`) 구현 완료
  - H2가 없는 경우 기본 섹션 반환 로직 구현 완료

**구현 내용**:
- `mapToSections()` 메서드: 마크다운 H2 기준 분할 로직 구현 (229-276줄)
- `extractTitle()` 메서드: H2 헤더에서 제목 추출 로직 구현 (278-300줄)
- `createDefaultSection()` 메서드: 기본 섹션 생성 헬퍼 메서드 구현 (302-320줄)
- 정규표현식 `(?=^## )`를 사용하여 H2 헤더 기준으로 분할
- Java Stream API를 활용한 함수형 프로그래밍 스타일 적용

**테스트**:
- 기존 테스트 업데이트 완료
- H2 헤더가 있는 경우 섹션 제목이 올바르게 추출되는지 검증

#### 6. 프롬프트 최적화
**파일**: `BusinessPlanGenerationService.buildSystemPrompt()`, `buildUserPrompt()`

- [ ] **템플릿별 프롬프트 분기**
  - `templateType`에 따라 다른 시스템 프롬프트 적용
  - 예: `pre-startup` vs `bank-loan` vs `ir-pitch`

- [ ] **generationOptions 반영**
  - `tone` (professional/casual), `targetLength` (brief/detailed) 등 옵션을 프롬프트에 반영
  - 현재는 하드코딩된 "professional" 톤만 사용

**예시**:
```java
private String buildSystemPrompt(String templateType, String tone) {
    String basePrompt = "...";
    
    if ("bank-loan".equals(templateType)) {
        basePrompt += "\n- 은행 대출 심사 기준에 맞춰 재무 건전성 강조";
    } else if ("ir-pitch".equals(templateType)) {
        basePrompt += "\n- 투자자 관점에서 성장성과 수익성 강조";
    }
    
    if ("casual".equals(tone)) {
        basePrompt += "\n- 친근하고 접근하기 쉬운 문체 사용";
    }
    
    return basePrompt;
}
```

#### 7. 사용량 DB 저장
**파일**: `BusinessPlanGenerationRepository`

**현재 상태**:
- 로그 기반 추적만 수행

**개선 방안**:
- [ ] **JPA 엔티티 생성**
  ```java
  @Entity
  @Table(name = "business_plan_generation_log")
  public class BusinessPlanGenerationLog {
      @Id
      private String businessPlanId;
      private String projectId;
      private int promptTokens;
      private int completionTokens;
      private int totalTokens;
      private String modelUsed;
      private LocalDateTime generatedAt;
      // ...
  }
  ```

- [ ] **Repository 메서드 구현**
  - `saveUsage()` 메서드에 실제 DB 저장 로직 추가
  - 사용량 통계 조회 메서드 추가 (`findByProjectId`, `findByDateRange`)

- [ ] **비용 분석 기능**
  - 프로젝트별/기간별 토큰 사용량 집계
  - 예산 초과 알림 기능

---

### 🟡 Medium Priority

#### 8. 재시도 로직
**파일**: `BusinessPlanGenerationService`

- [ ] **Spring Retry 통합**
  - Gemini API 호출 실패 시 자동 재시도 (최대 3회)
  - 지수 백오프(exponential backoff) 적용

**예시**:
```java
@Retryable(
    value = {GeminiApiException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
public BusinessPlanGenerateResponse generateBusinessPlan(...) {
    // ...
}
```

#### 9. 캐싱 전략
**파일**: `BusinessPlanGenerationService`

- [ ] **동일 요청 캐싱**
  - 동일한 `BusinessPlanGenerateRequest`에 대해 캐시된 결과 반환
  - `@Cacheable` 어노테이션 활용
  - TTL 설정 (예: 1시간)

**주의사항**:
- 사용자가 수정한 초안은 매번 새로 생성해야 하므로, 캐시 키 설계 시 주의 필요

---

## 성능 최적화

### 🟡 Medium Priority

#### 10. 비동기 처리
**파일**: `BusinessPlanController`, `BusinessPlanGenerationService`

**현재 상태**:
- 동기 처리로 인해 Gemini 응답 대기 중 스레드 블로킹

**개선 방안**:
- [ ] **비동기 엔드포인트 제공**
  - `POST /api/v1/business-plan/generate-async` 엔드포인트 추가
  - 즉시 `businessPlanId` 반환, 생성 완료 후 웹훅 또는 폴링으로 결과 전달

- [ ] **@Async 활용**
  ```java
  @Async
  public CompletableFuture<BusinessPlanGenerateResponse> generateBusinessPlanAsync(...) {
      // ...
  }
  ```

#### 11. 스트리밍 응답
**파일**: `BusinessPlanGenerationService`

- [ ] **Server-Sent Events (SSE) 지원**
  - Gemini 스트리밍 응답을 실시간으로 FE에 전달
  - 사용자가 생성 과정을 실시간으로 확인 가능

---

## 에러 핸들링 강화

### 🔴 High Priority

#### 12. 예외 처리 개선 ✅ (2025-12-20 완료)
**파일**: `BusinessPlanGenerationService`, `GlobalExceptionHandler`

**구현 완료**:
- [x] **커스텀 예외 생성** ✅
  - `GeminiGenerationException` 생성 완료
  - HTTP 상태 코드, 에러 코드, businessPlanId, retryCount 포함
  - 다양한 생성자 제공 (인증 실패, 한도 초과, 서버 오류 등)

- [x] **예외 상황별 처리** ✅
  - `GlobalExceptionHandler`에 `GeminiGenerationException` 처리 추가
  - API 키 누락/만료 → `401 Unauthorized` 처리 구현
  - 토큰 한도 초과 → `429 Too Many Requests` 처리 구현
  - 서버 오류 → `500 Internal Server Error` 처리 구현
  - `handleHttpException()` 메서드로 HTTP 상태 코드별 예외 변환

- [x] **에러 메시지 제공** ✅
  - 사용자에게 명확한 에러 메시지 제공
  - 로깅을 통한 디버깅 정보 기록

**구현 내용**:
- `GeminiGenerationException.java`: 커스텀 예외 클래스 생성
- `GlobalExceptionHandler.java`: GeminiGenerationException 처리 핸들러 추가
- `BusinessPlanGenerationService.java`: 
  - try-catch로 Gemini API 호출 예외 처리
  - `handleHttpException()` 메서드로 HTTP 상태 코드별 예외 변환
  - 명확한 에러 메시지 및 로깅

**Fallback 전략** (옵션):
- 현재는 명확한 에러 메시지 제공으로 충분
- 향후 필요 시 Mock 데이터 반환 기능 추가 가능

#### 13. 입력 검증 강화
**파일**: `BusinessPlanGenerationService`

- [ ] **프롬프트 길이 제한**
  - 최대 토큰 수 제한 (예: 32K tokens)
  - 초과 시 요청 거부 또는 요약 처리

- [ ] **비즈니스 로직 검증**
  - 필수 필드 누락 검증
  - 재무 수치 일관성 검증 (예: LTV/CAC 비율)

---

## 테스트 코드 작성

### ✅ 완료 (2025-12-19)

#### 14. 단위 테스트 ✅
**파일**: `BusinessPlanGenerationServiceTest.java`

- [x] **프롬프트 구성 테스트**
  - 시스템 프롬프트가 올바르게 구성되는지 검증
  - 유저 프롬프트에 요청 데이터가 포함되는지 검증

- [x] **섹션 매핑 테스트**
  - 마크다운 파싱 로직 테스트
  - 빈 응답 처리 테스트

- [x] **메타데이터 구성 테스트**
  - 토큰 사용량이 올바르게 추출되는지 검증
  - wordCount/characterCount 계산 정확성 검증

- [x] **예외 처리 테스트**
  - Usage/Metadata/Generation null 처리
  - 토큰 필드 null 처리
  - JsonProcessingException fallback 처리
  - durationMs 0 처리

**구현 완료**:
- 총 19개 단위 테스트 작성 완료
- 코드 커버리지 ~95%
- 모든 주요 시나리오 및 예외 케이스 커버

#### 15. 통합 테스트 ✅
**파일**: `BusinessPlanGenerationServiceIntegrationTest.java`

- [x] **실제 Gemini API 호출 테스트**
  - 실제 API 호출하여 사업계획서 생성 검증
  - 실제 토큰 사용량 기록 검증
  - 파일 로그 기록 검증

- [x] **프롬프트 길이별 토큰 사용량 비교 테스트**
  - 짧은/긴 프롬프트에 따른 토큰 사용량 차이 검증

**구현 완료**:
- 총 2개 통합 테스트 작성 완료
- 실제 API 호출 및 파일 I/O 검증 완료
- 테스트 보고서 문서화 완료

**테스트 보고서**: [GEMINI_TEST_REPORT.md](./GEMINI_TEST_REPORT.md)

#### 16. Repository 테스트 ✅
**파일**: `BusinessPlanGenerationRepositoryTest.java`

- [x] **사용량 로깅 테스트**
  - 정상적인 사용량 정보 로깅 검증
  - 0 토큰 처리 검증
  - 큰 토큰 수 처리 검증
  - 소수점 처리량 포맷팅 검증

**구현 완료**:
- 총 4개 Repository 테스트 작성 완료
- 로깅 기능 100% 커버

---

### 🟡 Medium Priority

#### 16. 성능 테스트
**파일**: `BusinessPlanGenerationServicePerformanceTest.java`

- [ ] **응답 시간 측정**
  - p95 < 10s 요구사항 준수 여부 확인
  - 부하 테스트 (동시 요청 100건)

- [ ] **토큰 사용량 모니터링**
  - 평균 토큰 사용량 측정
  - 비용 예측 모델 구축

---

## 모니터링 및 알림

### 🟡 Medium Priority

#### 17. 메트릭 수집
**파일**: `BusinessPlanGenerationService`

- [ ] **Micrometer 통합**
  - `generation.duration` (히스토그램)
  - `generation.tokens.total` (카운터)
  - `generation.success/failure` (카운터)

- [ ] **Prometheus/Grafana 대시보드**
  - 실시간 생성 요청 수
  - 평균 응답 시간
  - 토큰 사용량 추이

#### 18. 알림 시스템
**파일**: `BusinessPlanGenerationService`, `BusinessPlanGenerationRepository`

- [ ] **비용 임계값 알림**
  - 일일/월간 토큰 사용량이 임계값 초과 시 알림
  - Slack/Email 알림 연동

- [ ] **에러 알림**
  - Gemini API 호출 실패 시 즉시 알림
  - 에러율이 일정 수준 초과 시 알림

---

## 보안 강화

### 🟡 Medium Priority

#### 19. API 키 관리
**파일**: `application.properties`

- [ ] **Vault 통합**
  - 환경변수 대신 HashiCorp Vault 등 시크릿 관리 도구 사용
  - 프로덕션 환경에서 API 키 로테이션 자동화

#### 20. 입력 검증 및 Sanitization
**파일**: `BusinessPlanGenerationService`

- [ ] **프롬프트 인젝션 방지**
  - 사용자 입력에 포함된 시스템 프롬프트 조작 시도 차단
  - 입력 데이터 검증 및 이스케이프 처리

- [ ] **Rate Limiting**
  - 사용자별/프로젝트별 요청 제한
  - Spring Security 또는 Redis 기반 Rate Limiter 적용

---

## 우선순위 요약

### 즉시 처리 필요 (이번 스프린트) - High Priority
1. ✅ 린터 에러 수정 (2025-12-20 완료)
   - ✅ 사용하지 않는 변수 제거 완료
   - ✅ 문자열 리터럴 상수화 완료 (`API_BASE_PATH`, `DEFAULT_SECTION_ID`, `DEFAULT_SECTION_TITLE`)
   - ✅ 시스템 프롬프트 상수화 완료 (`SYSTEM_PROMPT`)
2. ✅ 섹션 자동 파싱 개선 (2025-12-20 완료)
   - ✅ 마크다운 H2 기준 분할 구현 완료
   - ✅ 섹션 제목 추출 구현 완료
   - ✅ 각 섹션에 고유 ID 부여 구현 완료
3. ✅ 예외 처리 개선 (2025-12-20 완료)
   - ✅ GeminiGenerationException 커스텀 예외 생성
   - ✅ 예외 상황별 처리 (401, 429, 500 등)
   - ✅ GlobalExceptionHandler에 예외 처리 추가
4. ✅ 단위 테스트 작성 (2025-12-19 완료)
5. ✅ 통합 테스트 작성 (2025-12-19 완료)
6. ✅ Repository 테스트 작성 (2025-12-19 완료)
7. ✅ 테스트 보고서 문서화 (2025-12-19 완료)

**남은 High Priority 작업**:
- Optional 활용 (null 체크를 Optional로 리팩토링)
- 프롬프트 최적화 (템플릿별 분기, generationOptions 반영)
- ✅ 사용량 DB 저장 (2025-12-20 완료) - JPA 엔티티, Repository 메서드, 요청/응답/메타데이터 저장
- 입력 검증 강화 (프롬프트 길이 제한, 비즈니스 로직 검증)

### 단기 개선 (다음 스프린트)
1. 프롬프트 최적화 (템플릿별 분기, 옵션 반영)
2. ✅ 사용량 DB 저장 (2025-12-20 완료)
3. 엔드포인트 통합 테스트 (MockMvc 기반)

### 중장기 개선 (향후 계획)
8. 비동기 처리
9. 캐싱 전략
10. 모니터링 및 알림
11. 성능 테스트

---

## 참고 문서

- [Spring AI Best Practices](https://docs.spring.io/spring-ai/reference/)
- [Google Gemini API Documentation](https://ai.google.dev/docs)
- [Spring Retry Documentation](https://docs.spring.io/spring-retry/docs/current/reference/html/)
- [Micrometer Documentation](https://micrometer.io/docs)
- [GEMINI_TEST_REPORT.md](./GEMINI_TEST_REPORT.md) - 테스트 보고서
- [GEMINI_INTEGRATION_SUMMARY.md](./GEMINI_INTEGRATION_SUMMARY.md) - 통합 작업 요약

---

## 최근 업데이트

### 2025-12-20: 예외 처리 개선 완료

#### 완료된 항목
- ✅ 예외 처리 개선
  - `GeminiGenerationException` 커스텀 예외 생성
  - HTTP 상태 코드별 예외 처리 (401, 429, 500 등)
  - `GlobalExceptionHandler`에 예외 처리 핸들러 추가
  - `handleHttpException()` 메서드로 예외 변환
  - 명확한 에러 메시지 및 로깅 구현
  - 모든 테스트 통과 확인

### 2025-12-20: 섹션 자동 파싱 개선 완료

#### 완료된 항목
- ✅ 섹션 자동 파싱 개선
  - 마크다운 H2(##) 기준으로 섹션 자동 분할 구현
  - 각 섹션에 고유 ID 부여 (`section-1`, `section-2`, ...)
  - 섹션 제목 추출 (`## 1. 사업 개요` → `title: "1. 사업 개요"`)
  - H2가 없는 경우 기본 섹션 반환 로직 구현
  - `extractTitle()`, `createDefaultSection()` 헬퍼 메서드 추가
  - 기존 테스트 업데이트 및 모든 테스트 통과 확인

### 2025-12-20: 린터 에러 수정 완료

#### 완료된 항목
- ✅ 문자열 리터럴 상수화
  - `/api/v1/business-plan/` → `API_BASE_PATH` 상수로 추출 (4회 중복 제거)
  - `"section-1"` → `DEFAULT_SECTION_ID` 상수로 추출
  - `"AI 보강 사업계획서"` → `DEFAULT_SECTION_TITLE` 상수로 추출
- ✅ 시스템 프롬프트 상수화
  - `buildSystemPrompt()` 메서드의 반환값을 `SYSTEM_PROMPT` 클래스 상수로 이동
  - 프롬프트 수정 시 코드 재컴파일 없이 상수만 수정하면 되도록 개선

### 2025-12-20: High Priority 항목 구현 상태 검토

#### 완료된 항목
- ✅ 사용하지 않는 변수 제거 (`modelStart`, `modelEnd`는 이미 제거됨)
- ✅ 방어적 프로그래밍 (null 체크 및 기본값 반환 구현됨)
- ✅ 전역 예외 처리 (`GlobalExceptionHandler` 구현됨)
- ✅ 커스텀 예외 (`InvalidTemplateException` 존재)

#### 부분 완료된 항목
- ⚠️ Null Safety 강화: null 체크는 있으나 Optional 미사용

#### 완료된 항목 (이전에 미완료로 표시되었으나 실제로는 완료됨)
- ✅ 문자열 리터럴 상수화 (2025-12-20 완료)
  - `API_BASE_PATH`, `DEFAULT_SECTION_ID`, `DEFAULT_SECTION_TITLE` 상수로 추출 완료
- ✅ 시스템 프롬프트 상수화 (2025-12-20 완료)
  - `SYSTEM_PROMPT` 상수로 추출 완료
- ✅ 섹션 자동 파싱 개선 (2025-12-20 완료)
  - 마크다운 H2(##) 기준 분할 구현 완료
  - `extractTitle()` 메서드 구현 완료
- ✅ 예외 처리 개선 (2025-12-20 완료)
  - `GeminiGenerationException` 커스텀 예외 생성 완료
  - Gemini 특화 예외 처리 (401, 429, 500 등) 구현 완료
  - `handleHttpException()` 메서드 구현 완료

#### 미완료 항목 (High Priority)
- ❌ Optional 활용 (null 체크는 있으나 Optional로 리팩토링 필요)
- ❌ 프롬프트 최적화 (템플릿별 분기, generationOptions 반영)
- ✅ 사용량 DB 저장 (2025-12-20 완료)
  - ✅ BusinessPlan 엔티티 생성
  - ✅ BusinessPlanRepository 인터페이스 생성
  - ✅ Flyway 마이그레이션 파일 생성 (V2__create_business_plans_table.sql)
  - ✅ Service Layer에 DB 저장 로직 추가
  - ✅ DTO ↔ Entity 매핑 구현
  - ✅ 3-tier 구조 규칙 문서 작성 (306-three-tier-architecture-rules.mdc)
- ❌ Fallback 전략 (옵션 - 현재는 명확한 에러 메시지 제공으로 충분)
- ❌ 입력 검증 강화 (프롬프트 길이 제한, 비즈니스 로직 검증)

### 2025-12-19: 테스트 코드 작성 완료

#### 완료된 항목
- ✅ 단위 테스트 작성 완료 (19개 테스트)
- ✅ 통합 테스트 작성 완료 (2개 테스트)
- ✅ Repository 테스트 작성 완료 (4개 테스트)
- ✅ 테스트 보고서 문서화 완료
- ✅ JacksonConfig 추가 (ObjectMapper 빈 명시적 정의)
- ✅ 예외 처리 테스트 보강 (8개 예외 케이스)
- ✅ 파일 로깅 테스트 추가

### 테스트 커버리지
- Service 레이어: ~95%
- Repository 레이어: 100%
- 통합 테스트: 실제 API 호출 및 파일 I/O 검증 완료

---

### 2025-12-20: 데이터베이스 저장 기능 구현 완료

#### 완료된 항목
- ✅ 데이터베이스 저장 기능 구현
  - `BusinessPlan` 엔티티 생성 (요청/응답/Gemini 메타데이터 저장)
  - `BusinessPlanRepository` 인터페이스 생성 (Spring Data JPA)
  - Flyway 마이그레이션 파일 생성 (`V2__create_business_plans_table.sql`)
  - `BusinessPlanGenerationService`에 DB 저장 로직 추가
  - DTO ↔ Entity 매핑 메서드 구현 (Service Layer)
  - 3-tier 구조 규칙 문서 작성 (`.cursor/rules/306-three-tier-architecture-rules.mdc`)
  - Repository 테스트 작성 및 검증
  - Service 테스트에 DB 저장 호출 검증 추가

#### 저장되는 데이터
- 요청 데이터: `BusinessPlanGenerateRequest` 전체 (JSON)
- 응답 데이터: 생성된 섹션들 (JSON)
- Gemini 메타데이터: 토큰 사용량, 시간 정보 등 (JSON)

#### 3-Tier 구조 준수
- Controller: HTTP 요청/응답 처리, DTO만 사용
- Service: 비즈니스 로직, DTO ↔ Entity 변환, 트랜잭션 관리
- Repository: 데이터 접근, Entity만 사용

---

**작성자**: AI Assistant  
**최종 수정일**: 2025-12-20
