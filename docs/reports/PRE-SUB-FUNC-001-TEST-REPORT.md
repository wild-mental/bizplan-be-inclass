# 사전 등록 기능 테스트 수행 보고서

> **문서 ID:** PRE-SUB-FUNC-001-TEST-REPORT  
> **작성일:** 2025-12-28  
> **테스트 실행일:** 2025-12-28 06:32:23 UTC  
> **테스트 환경:** macOS, OpenJDK 21, Spring Boot 4.0.0, SQLite Database

---

## 📊 테스트 결과 요약

| 구분 | 테스트 수 | 성공 | 실패 | 스킵 | 소요시간 |
|------|----------|------|------|------|----------|
| **Controller** | 10 | ✅ 10 | 0 | 0 | 1.771s |
| **Service** | 10 | ✅ 10 | 0 | 0 | 0.238s |
| **Repository** | 8 | ✅ 8 | 0 | 0 | 0.773s |
| **합계** | **28** | ✅ **28** | **0** | **0** | **2.782s** |

### 🎯 테스트 커버리지: **100% 성공**

---

## 🔬 Controller 테스트 상세 (`PreRegistrationControllerTest`)

| # | 테스트 케이스 | 결과 | 소요시간 |
|---|--------------|------|----------|
| 1 | GET /api/v1/pre-registrations/check-email - 이메일 미존재 | ✅ PASSED | 1.502s |
| 2 | GET /api/v1/promotions/current - 성공 | ✅ PASSED | 0.025s |
| 3 | GET /api/v1/pre-registrations/{id} - 실패 (없는 ID) | ✅ PASSED | 0.020s |
| 4 | POST /api/v1/pre-registrations - 성공 | ✅ PASSED | 0.097s |
| 5 | POST /api/v1/pre-registrations - 유효성 검사 실패 (잘못된 이메일) | ✅ PASSED | 0.038s |
| 6 | POST /api/v1/pre-registrations - 유효성 검사 실패 (빈 이름) | ✅ PASSED | 0.020s |
| 7 | POST /api/v1/pre-registrations - 이메일 중복 | ✅ PASSED | 0.020s |
| 8 | GET /api/v1/pre-registrations/check-email - 이메일 존재 | ✅ PASSED | 0.013s |
| 9 | GET /api/v1/pre-registrations/{id} - 성공 | ✅ PASSED | 0.014s |
| 10 | POST /api/v1/pre-registrations - 프로모션 종료 | ✅ PASSED | 0.016s |

### 테스트 시나리오 설명

- **정상 케이스:** 사전 등록, 이메일 체크, ID 조회, 프로모션 정보 조회
- **에러 케이스:** 이메일 중복 (409), 잘못된 입력 (400), 리소스 없음 (404), 프로모션 종료 (410)

---

## 🔬 Service 테스트 상세 (`PreRegistrationServiceTest`)

| # | 테스트 케이스 | 결과 | 소요시간 |
|---|--------------|------|----------|
| 1 | 활성화된 프로모션이 없을 때 예외 발생 | ✅ PASSED | 0.216s |
| 2 | 이메일 중복 체크 - 존재하지 않는 경우 | ✅ PASSED | 0.001s |
| 3 | 프로모션 종료 시 예외 발생 | ✅ PASSED | 0.005s |
| 4 | 할인 코드로 조회 - 성공 | ✅ PASSED | 0.003s |
| 5 | 프로모션 정보 조회 | ✅ PASSED | 0.003s |
| 6 | ID로 등록 정보 조회 - 실패 | ✅ PASSED | 0.002s |
| 7 | 사전 등록 성공 | ✅ PASSED | 0.003s |
| 8 | 이메일 중복 시 예외 발생 | ✅ PASSED | 0.002s |
| 9 | 이메일 중복 체크 - 존재하는 경우 | ✅ PASSED | < 0.001s |
| 10 | ID로 등록 정보 조회 - 성공 | ✅ PASSED | < 0.001s |

### 테스트 시나리오 설명

- **비즈니스 로직 검증:** 할인 계산, 코드 생성, 상태 관리
- **예외 처리 검증:** DuplicateEmailException, PromotionEndedException, ResourceNotFoundException

---

## 🔬 Repository 테스트 상세 (`PreRegistrationRepositoryTest`)

| # | 테스트 케이스 | 결과 | 소요시간 |
|---|--------------|------|----------|
| 1 | 할인 코드로 조회 | ✅ PASSED | 0.680s |
| 2 | 키워드로 검색 (이름 또는 이메일) | ✅ PASSED | 0.020s |
| 3 | 이메일 존재 여부 확인 | ✅ PASSED | 0.013s |
| 4 | 사전 등록 저장 및 조회 | ✅ PASSED | 0.009s |
| 5 | 요금제별 조회 | ✅ PASSED | 0.009s |
| 6 | 마케팅 동의 수 조회 | ✅ PASSED | 0.016s |
| 7 | 이메일로 조회 | ✅ PASSED | 0.007s |
| 8 | 상태별 조회 | ✅ PASSED | 0.011s |

### 테스트 시나리오 설명

- **CRUD 검증:** 저장, 조회, 검색
- **쿼리 메서드 검증:** 이메일, 할인 코드, 상태, 요금제별 조회
- **집계 쿼리 검증:** 마케팅 동의 수 카운트

---

## 🧪 테스트 환경

### 기술 스택

```
- Java: OpenJDK 21
- Spring Boot: 4.0.0
- Test Framework: JUnit 5, Mockito
- Database: SQLite (In-Memory 또는 파일)
- Build Tool: Gradle 9.2.1
```

### 테스트 설정

```properties
# src/test/resources/application.properties
spring.datasource.url=jdbc:sqlite::memory:
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.flyway.enabled=false
spring.sql.init.mode=never
```

---

## 📋 API 통합 테스트 결과

### 테스트 시나리오 (수동 검증)

애플리케이션 실행 후 실제 API 호출 테스트:

#### 1. 프로모션 정보 조회

```bash
GET /api/v1/promotions/current
```

**응답:** ✅ 200 OK
```json
{
  "success": true,
  "data": {
    "isActive": true,
    "currentPhase": "A",
    "discountRate": 30,
    "prices": {
      "pro": { "original": 799000, "discounted": 559300, "saved": 239700 }
    }
  }
}
```

#### 2. 사전 등록 신청

```bash
POST /api/v1/pre-registrations
Content-Type: application/json
```

**요청:**
```json
{
  "name": "홍길동",
  "email": "hong@example.com",
  "phone": "010-1234-5678",
  "selectedPlan": "pro",
  "agreeTerms": true
}
```

**응답:** ✅ 201 Created
```json
{
  "success": true,
  "data": {
    "id": "84fe4a3b-0788-47d4-b239-612d3ba6fd6f",
    "discountCode": "MR2026-D19DSO",
    "discountRate": 30,
    "originalPrice": 799000,
    "discountedPrice": 559300,
    "status": "CONFIRMED"
  }
}
```

#### 3. 이메일 중복 체크

```bash
GET /api/v1/pre-registrations/check-email?email=hong@example.com
```

**응답:** ✅ 200 OK
```json
{
  "success": true,
  "data": { "exists": true, "discountCode": "MR2026-D19DSO" }
}
```

#### 4. 중복 등록 시도

```bash
POST /api/v1/pre-registrations (동일 이메일)
```

**응답:** ✅ 409 Conflict
```json
{
  "success": false,
  "error": { "code": "DUPLICATE_EMAIL", "message": "이미 등록된 이메일입니다" }
}
```

---

## ✅ 결론

### 테스트 결과 판정: **PASS**

- 모든 28개 단위 테스트 통과 (100%)
- 모든 API 통합 테스트 정상 동작 확인
- 예외 처리 시나리오 검증 완료
- 할인 계산 로직 검증 완료

### 검증된 기능

| 기능 | 상태 |
|------|------|
| 사전 등록 신청 | ✅ 정상 |
| 이메일 중복 체크 | ✅ 정상 |
| 등록 정보 조회 (ID) | ✅ 정상 |
| 할인 코드 조회 | ✅ 정상 |
| 프로모션 정보 조회 | ✅ 정상 |
| 할인율 계산 (30%/10%) | ✅ 정상 |
| 할인 코드 생성 (MR2026-XXXXXX) | ✅ 정상 |
| 이메일 중복 예외 (409) | ✅ 정상 |
| 프로모션 종료 예외 (410) | ✅ 정상 |
| 리소스 없음 예외 (404) | ✅ 정상 |
| 유효성 검사 (400) | ✅ 정상 |

---

*Report Generated: 2025-12-28*

