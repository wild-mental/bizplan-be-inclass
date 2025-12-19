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

- [ ] **사용하지 않는 변수 제거**
  - `modelStart`, `modelEnd` 변수 제거 (81-83줄)
  - 현재 사용되지 않으므로 제거 또는 실제 모델 호출 시간 측정 로직 추가

- [ ] **문자열 리터럴 상수화**
  - `/api/v1/business-plan/` 문자열 4회 중복 → 상수로 추출
  - `"section-1"`, `"AI 보강 사업계획서"` 등 하드코딩된 값 상수화

- [ ] **시스템 프롬프트 상수화**
  - `buildSystemPrompt()` 메서드의 반환값을 클래스 상수 또는 외부 리소스 파일로 이동
  - 프롬프트 수정 시 코드 재컴파일 없이 변경 가능하도록 개선

**예시**:
```java
private static final String API_BASE_PATH = "/api/v1/business-plan/";
private static final String DEFAULT_SECTION_ID = "section-1";
private static final String DEFAULT_SECTION_TITLE = "AI 보강 사업계획서";
```

#### 2. Null Safety 강화
**파일**: `BusinessPlanGenerationService.java`

- [ ] **Optional 활용**
  - `usage.getPromptTokens()` 등 null 체크를 Optional로 개선
  - `generation.getOutput()` null 체크 강화

- [ ] **방어적 프로그래밍**
  - Gemini 응답이 null이거나 빈 문자열일 경우 기본값 반환 또는 예외 처리

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

#### 5. 섹션 자동 파싱 개선
**파일**: `BusinessPlanGenerationService.mapToSections()`

**현재 상태**:
- 전체 마크다운을 단일 섹션으로 반환
- FE가 섹션 단위 렌더링에 제약

**개선 방안**:
- [ ] **마크다운 파서 구현**
  - `##` (H2) 기준으로 섹션 자동 분할
  - 각 섹션에 고유 ID 부여 (`section-1`, `section-2`, ...)
  - 섹션 제목 추출 (`## 1. 사업 개요` → `title: "1. 사업 개요"`)

**예시 구현**:
```java
private List<BusinessPlanSection> mapToSections(String generatedContent) {
    if (generatedContent == null || generatedContent.isBlank()) {
        return List.of(createDefaultSection());
    }
    
    // 마크다운 H2(##) 기준으로 분할
    String[] parts = generatedContent.split("(?=^## )", Pattern.MULTILINE);
    
    return IntStream.range(0, parts.length)
        .mapToObj(i -> {
            String part = parts[i].trim();
            if (part.isEmpty()) return null;
            
            String title = extractTitle(part); // "## 1. 사업 개요" → "1. 사업 개요"
            String content = part.replaceFirst("^## .+\\n", "");
            
            return BusinessPlanSection.builder()
                .id("section-" + (i + 1))
                .title(title != null ? title : "AI 보강 사업계획서")
                .content(content)
                .order(i + 1)
                .build();
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}
```

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

#### 12. 예외 처리 개선
**파일**: `BusinessPlanGenerationService`

**현재 상태**:
- 예외 처리 미흡 (null 체크만 존재)

**개선 방안**:
- [ ] **커스텀 예외 생성**
  ```java
  public class GeminiGenerationException extends RuntimeException {
      private final String businessPlanId;
      private final int retryCount;
      // ...
  }
  ```

- [ ] **예외 상황별 처리**
  - API 키 누락/만료 → `401 Unauthorized`
  - 토큰 한도 초과 → `429 Too Many Requests`
  - 모델 응답 실패 → `500 Internal Server Error` + 재시도

- [ ] **Fallback 전략**
  - Gemini 호출 실패 시 Mock 데이터 반환 (옵션)
  - 사용자에게 명확한 에러 메시지 제공

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

### 즉시 처리 필요 (이번 스프린트)
1. ✅ 린터 에러 수정 (사용하지 않는 변수, 상수화)
2. ✅ 섹션 자동 파싱 개선
3. ✅ 예외 처리 개선
4. ✅ 단위 테스트 작성
5. ✅ 통합 테스트 작성 (2025-12-19 완료)
6. ✅ Repository 테스트 작성 (2025-12-19 완료)
7. ✅ 테스트 보고서 문서화 (2025-12-19 완료)

### 단기 개선 (다음 스프린트)
5. 프롬프트 최적화 (템플릿별 분기, 옵션 반영)
6. 사용량 DB 저장
7. 엔드포인트 통합 테스트 (MockMvc 기반)

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

## 최근 업데이트 (2025-12-19)

### 완료된 항목
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

**작성자**: AI Assistant  
**최종 수정일**: 2025-12-19
