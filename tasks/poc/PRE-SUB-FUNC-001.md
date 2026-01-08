# Pre-Subscription Campaign Backend Implementation

> **프로모션 기간:** 2025년 12월 28일 ~ 2026년 정부지원사업 접수 시작일
> **혜택:** 2단계 얼리버드 할인 사전 등록
> **목표:** 잠재 수요 검증 및 얼리버드 회원 확보

---

## 🗓️ 프로모션 기간별 혜택

| 기간 | 할인율 | 설명 |
|------|--------|------|
| **Phase A: 연말연시 특별** | 🔥 **30% 할인** | 2025-12-28 ~ 2026-01-03 (7일간) |
| **Phase B: 공고 전 얼리버드** | ✨ **10% 할인** | 2026-01-04 ~ 2026 정부지원사업 접수 시작일 |

> 📢 **Phase B 종료일:** 예비창업패키지, 초기창업패키지 등 2026년 정부지원사업 접수 시작일 기준
> (통상 2월 중순~3월 초 예상, 공고 확정 시 업데이트)

---

## 📊 할인 요금 계산

### 🔥 Phase A: 연말연시 특별 30% 할인 (12/28 ~ 1/3)

| 요금제 | 정가 | 30% 할인가 | 절약 금액 |
|--------|------|-----------|----------|
| 플러스 | ₩399,000 | **₩279,300** | ₩119,700 |
| 프로 | ₩799,000 | **₩559,300** | ₩239,700 |
| 프리미엄 | ₩1,499,000 | **₩1,049,300** | ₩449,700 |

### ✨ Phase B: 공고 전 얼리버드 10% 할인 (1/4 ~ 접수 시작일)

| 요금제 | 정가 | 10% 할인가 | 절약 금액 |
|--------|------|-----------|----------|
| 플러스 | ₩399,000 | **₩359,100** | ₩39,900 |
| 프로 | ₩799,000 | **₩719,100** | ₩79,900 |
| 프리미엄 | ₩1,499,000 | **₩1,349,100** | ₩149,900 |

---

## 🛠️ 사전 준비: SQLite + Flyway 설정

> 📖 **참고:** [SQLITE_FLYWAY_GUIDE.md](/docs/SQLITE_FLYWAY_GUIDE.md)

### 의존성 추가 (build.gradle)

```gradle
dependencies {
    // Flyway
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-database-sqlite'
    
    // SQLite
    runtimeOnly 'org.xerial:sqlite-jdbc:3.45.1.0'
    
    // Hibernate SQLite Dialect
    implementation 'org.hibernate.orm:hibernate-community-dialects:6.4.4.Final'
}
```

### 애플리케이션 설정 (application.yml 또는 application.properties)

```yaml
spring:
  datasource:
    url: jdbc:sqlite:./data/makersround.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: validate  # Flyway가 스키마 관리, Hibernate는 검증만
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

> ⚠️ **SQLite 특징:**
> - ENUM 미지원 → TEXT + CHECK 제약조건 사용
> - COMMENT 미지원 → 주석으로 대체
> - ON UPDATE CURRENT_TIMESTAMP 미지원 → 애플리케이션에서 처리
> - INDEX는 CREATE TABLE 외부에서 별도 생성

---

## 🎯 Phase 1: 데이터베이스 설계 (필수)

### 1.1 사전 등록 테이블 스키마

**우선순위:** 🔴 Critical
**예상 소요:** 2-3시간

**Flyway 마이그레이션 파일:** `V3__create_pre_registrations_table.sql`

**AI 에이전트 프롬프트 (Cursor Composer):**
> "@entity PreRegistration 클래스를 보고 SQLite용 Flyway 마이그레이션 파일을 만들어줘. 
> 파일명은 `V3__create_pre_registrations_table.sql`로 해주고 위치는 `src/main/resources/db/migration`이야.
> SQLite는 ENUM을 지원하지 않으니 TEXT + CHECK 제약조건으로 대체해줘."

```sql
-- ============================================
-- V3: Create pre_registrations table (SQLite)
-- 사전 등록 프로모션 데이터 저장
-- ============================================

CREATE TABLE IF NOT EXISTS pre_registrations (
    -- UUID PK
    id TEXT NOT NULL PRIMARY KEY,
    
    -- 사용자 정보
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL,
    
    -- 요금제 정보 (ENUM 대신 TEXT + CHECK)
    selected_plan TEXT NOT NULL CHECK (selected_plan IN ('plus', 'pro', 'premium')),
    business_category TEXT,
    
    -- 동의 항목 (SQLite BOOLEAN은 0/1로 저장)
    marketing_consent INTEGER NOT NULL DEFAULT 0,
    
    -- 프로모션 정보
    promotion_phase TEXT NOT NULL CHECK (promotion_phase IN ('A', 'B')),
    
    -- 할인 정보
    discount_code TEXT NOT NULL UNIQUE,
    discount_rate INTEGER NOT NULL,
    original_price INTEGER NOT NULL,
    discounted_price INTEGER NOT NULL,
    
    -- 만료일 (ISO 8601 형식 TEXT)
    expires_at TEXT NOT NULL,
    
    -- 상태 관리 (ENUM 대신 TEXT + CHECK)
    status TEXT NOT NULL DEFAULT 'CONFIRMED' 
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CONVERTED')),
    
    -- 감사 컬럼 (ISO 8601 형식 TEXT)
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 인덱스 생성 (SQLite는 CREATE TABLE 외부에서)
CREATE INDEX IF NOT EXISTS idx_pre_registrations_email ON pre_registrations(email);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_status ON pre_registrations(status);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_selected_plan ON pre_registrations(selected_plan);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_discount_code ON pre_registrations(discount_code);
CREATE INDEX IF NOT EXISTS idx_pre_registrations_created_at ON pre_registrations(created_at);
```

### 1.2 프로모션 설정 테이블

**우선순위:** 🟡 High
**예상 소요:** 1-2시간

**Flyway 마이그레이션 파일:** `V4__create_promotions_table.sql`

**AI 에이전트 프롬프트:**
> "프로모션 설정을 위한 promotions 테이블을 SQLite용으로 만들어줘.
> `V4__create_promotions_table.sql` 파일로 생성하고, 초기 데이터도 INSERT해줘."

```sql
-- ============================================
-- V4: Create promotions table (SQLite)
-- 프로모션 설정 관리 (동적 종료일 업데이트용)
-- ============================================

CREATE TABLE IF NOT EXISTS promotions (
    -- UUID PK
    id TEXT NOT NULL PRIMARY KEY,
    
    -- 프로모션 정보
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    description TEXT,
    
    -- Phase A 설정 (ISO 8601 형식)
    phase_a_start TEXT NOT NULL,
    phase_a_end TEXT NOT NULL,
    phase_a_discount_rate INTEGER NOT NULL DEFAULT 30,
    
    -- Phase B 설정
    phase_b_start TEXT NOT NULL,
    phase_b_end TEXT,
    phase_b_discount_rate INTEGER NOT NULL DEFAULT 10,
    
    -- 상태 (SQLite BOOLEAN = INTEGER)
    is_active INTEGER NOT NULL DEFAULT 1,
    
    -- 감사 컬럼
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_promotions_code ON promotions(code);
CREATE INDEX IF NOT EXISTS idx_promotions_is_active ON promotions(is_active);

-- 초기 프로모션 데이터 삽입
-- Note: SQLite는 UUID() 함수가 없으므로 하드코딩된 UUID 사용
INSERT INTO promotions (
    id, code, name, description,
    phase_a_start, phase_a_end, phase_a_discount_rate,
    phase_b_start, phase_b_end, phase_b_discount_rate,
    is_active
) VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'pre-registration-2026-h1',
    '2026 상반기 사전 등록',
    '연말연시 30% + 공고 전 10% 할인 프로모션',
    '2025-12-28T00:00:00',
    '2026-01-03T23:59:59',
    30,
    '2026-01-04T00:00:00',
    '2026-03-01T23:59:59',
    10,
    1
);
```

### 1.3 SQLite 스키마 변경 시 주의사항

> ⚠️ **SQLite ALTER TABLE 제한:** 컬럼 삭제/변경이 직접 지원되지 않습니다.

**컬럼 변경이 필요한 경우 AI에게 요청:**
> "현재 `pre_registrations` 테이블에서 `business_category` 컬럼을 삭제하고 싶어.
> SQLite는 컬럼 삭제가 직접 안 되니까, **새 임시 테이블 생성 → 데이터 복사 → 기존 테이블 삭제 → 이름 변경** 방식으로 `V5__remove_business_category.sql` 파일을 작성해줘."

```sql
-- V5: SQLite 컬럼 삭제 예시 (우회 방식)
-- 1. 새 테이블 생성 (삭제할 컬럼 제외)
CREATE TABLE pre_registrations_new (
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    -- business_category 제외
    ...
);

-- 2. 데이터 복사
INSERT INTO pre_registrations_new SELECT id, name, email, ... FROM pre_registrations;

-- 3. 기존 테이블 삭제
DROP TABLE pre_registrations;

-- 4. 이름 변경
ALTER TABLE pre_registrations_new RENAME TO pre_registrations;
```

---

## 🎯 Phase 2: Entity 및 Repository 구현 (필수)

### 2.1 PreRegistration Entity

**우선순위:** 🔴 Critical
**예상 소요:** 2-3시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/entity/PreRegistration.java`

> 💡 **SQLite 호환:** `columnDefinition = "TEXT"`로 UUID 저장, Boolean은 Integer 매핑

```java
package vibe.makersround.makersround_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사전 등록 엔티티
 * 
 * Rule 303: snake_case naming, UUID PK, Audit columns
 * Rule 306: Entity는 Repository Layer에서만 사용
 * 
 * Note: SQLite 호환을 위해 TEXT 타입 사용
 */
@Entity
@Table(name = "pre_registrations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PreRegistration {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")  // SQLite: TEXT로 UUID 저장
    private UUID id;

    // 사용자 정보
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // 요금제 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_plan", nullable = false, length = 10)
    private PlanType selectedPlan;

    @Column(name = "business_category", length = 50)
    private String businessCategory;

    // 동의 항목
    @Column(name = "marketing_consent", nullable = false)
    private Boolean marketingConsent;

    // 프로모션 정보
    @Column(name = "promotion_phase", nullable = false, length = 5)
    private String promotionPhase;

    // 할인 정보
    @Column(name = "discount_code", nullable = false, unique = true, length = 50)
    private String discountCode;

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discounted_price", nullable = false)
    private Integer discountedPrice;

    // 만료일
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.CONFIRMED;

    // 감사 컬럼
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상태 변경 메서드
     */
    public void updateStatus(RegistrationStatus newStatus) {
        this.status = newStatus;
    }

    // Enum 정의
    public enum PlanType {
        plus, pro, premium
    }

    public enum RegistrationStatus {
        PENDING, CONFIRMED, CANCELLED, CONVERTED
    }
}
```

### 2.2 Promotion Entity

**우선순위:** 🟡 High
**예상 소요:** 1-2시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/entity/Promotion.java`

> 💡 **SQLite 호환:** UUID는 TEXT, Boolean은 Integer (0/1)로 자동 매핑

```java
package vibe.makersround.makersround_be_inclass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프로모션 설정 엔티티
 * 
 * Note: SQLite 호환 - TEXT 타입 사용
 */
@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "TEXT")  // SQLite: TEXT
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Phase A
    @Column(name = "phase_a_start", nullable = false)
    private LocalDateTime phaseAStart;

    @Column(name = "phase_a_end", nullable = false)
    private LocalDateTime phaseAEnd;

    @Column(name = "phase_a_discount_rate", nullable = false)
    private Integer phaseADiscountRate;

    // Phase B
    @Column(name = "phase_b_start", nullable = false)
    private LocalDateTime phaseBStart;

    @Column(name = "phase_b_end")
    private LocalDateTime phaseBEnd;

    @Column(name = "phase_b_discount_rate", nullable = false)
    private Integer phaseBDiscountRate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // 감사 컬럼
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 현재 시점의 할인율 반환
     */
    public Integer getCurrentDiscountRate() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(phaseAEnd) && now.isAfter(phaseAStart)) {
            return phaseADiscountRate;
        } else if (now.isAfter(phaseBStart) && (phaseBEnd == null || now.isBefore(phaseBEnd))) {
            return phaseBDiscountRate;
        }
        return 0; // 프로모션 종료
    }

    /**
     * 현재 Phase 반환 ("A", "B", "ENDED")
     */
    public String getCurrentPhase() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(phaseAStart)) return "NOT_STARTED";
        if (now.isBefore(phaseAEnd)) return "A";
        if (phaseBEnd == null || now.isBefore(phaseBEnd)) return "B";
        return "ENDED";
    }
}
```

### 2.3 Repository 인터페이스

**우선순위:** 🔴 Critical
**예상 소요:** 1시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/repository/PreRegistrationRepository.java`

```java
package vibe.makersround.makersround_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.makersround.makersround_be_inclass.entity.PreRegistration;
import vibe.makersround.makersround_be_inclass.entity.PreRegistration.PlanType;
import vibe.makersround.makersround_be_inclass.entity.PreRegistration.RegistrationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PreRegistrationRepository extends JpaRepository<PreRegistration, UUID> {

    // 이메일로 조회 (중복 체크용)
    Optional<PreRegistration> findByEmail(String email);
    
    // 이메일 존재 여부
    boolean existsByEmail(String email);

    // 할인 코드로 조회
    Optional<PreRegistration> findByDiscountCode(String discountCode);

    // 상태별 조회
    List<PreRegistration> findByStatus(RegistrationStatus status);

    // 요금제별 조회
    List<PreRegistration> findBySelectedPlan(PlanType planType);

    // 기간별 등록 수 조회 (createdAt 기준)
    @Query("SELECT COUNT(p) FROM PreRegistration p WHERE p.createdAt BETWEEN :start AND :end")
    Long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 요금제별 통계
    @Query("SELECT p.selectedPlan, COUNT(p) FROM PreRegistration p GROUP BY p.selectedPlan")
    List<Object[]> countByPlanType();

    // 마케팅 동의율 (필드명: marketingConsent)
    @Query("SELECT COUNT(p) FROM PreRegistration p WHERE p.marketingConsent = true")
    Long countMarketingAgreed();

    // 검색 (이름 또는 이메일)
    @Query("SELECT p FROM PreRegistration p WHERE p.name LIKE %:keyword% OR p.email LIKE %:keyword%")
    List<PreRegistration> searchByKeyword(@Param("keyword") String keyword);
}
```

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/repository/PromotionRepository.java`

```java
package vibe.makersround.makersround_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.makersround.makersround_be_inclass.entity.Promotion;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    Optional<Promotion> findByCode(String code);
    
    Optional<Promotion> findByIsActiveTrue();
}
```

---

## 🎯 Phase 3: DTO 및 Request/Response 설계 (필수)

### 3.1 Request DTOs

**우선순위:** 🔴 Critical
**예상 소요:** 2시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/dto/preregistration/PreRegistrationRequest.java`

```java
package vibe.makersround.makersround_be_inclass.dto.preregistration;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 사전 등록 요청 DTO
 * 
 * PRE-SUB-FUNC-002 명세서 준수
 * Rule 304: Request DTO validation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegistrationRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글 또는 영문만 입력 가능합니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX입니다")
    private String phone;

    @NotNull(message = "요금제 선택은 필수입니다")
    private PlanType plan;

    @Size(max = 50, message = "사업 분야는 50자 이내여야 합니다")
    private String businessCategory;

    @Builder.Default
    private Boolean marketingConsent = false;

    /**
     * 요금제 유형
     */
    public enum PlanType {
        plus, pro, premium
    }
}
```

### 3.2 Response DTOs

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/dto/preregistration/PreRegistrationResponse.java`

```java
package vibe.makersround.makersround_be_inclass.dto.preregistration;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 사전 등록 응답 DTO
 * 
 * PRE-SUB-FUNC-002 명세서 준수
 * Rule 304: Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegistrationResponse {

    /**
     * 등록 ID (UUID)
     */
    private String registrationId;
    
    /**
     * 선택한 요금제
     */
    private String plan;
    
    /**
     * 적용된 프로모션 Phase ("A" or "B")
     */
    private String promotionPhase;
    
    /**
     * 적용된 할인율 (%)
     */
    private Integer discountRate;
    
    /**
     * 발급된 할인 코드
     */
    private String discountCode;
    
    /**
     * 정가
     */
    private Integer originalPrice;
    
    /**
     * 할인가
     */
    private Integer discountedPrice;
    
    /**
     * 절약 금액
     */
    private Integer savings;
    
    /**
     * 할인 코드 만료일
     */
    private LocalDateTime expiresAt;
    
    /**
     * 등록 일시
     */
    private LocalDateTime createdAt;
}
```

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/dto/preregistration/PromotionInfoResponse.java`

```java
package vibe.makersround.makersround_be_inclass.dto.preregistration;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 현재 프로모션 정보 응답 DTO
 * 
 * PRE-SUB-FUNC-002 명세서 준수
 * Rule 304: Response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionInfoResponse {

    /**
     * 프로모션 활성 상태
     */
    private Boolean isActive;
    
    /**
     * 현재 Phase ("A", "B", "ENDED", "NOT_STARTED")
     */
    private String currentPhase;
    
    /**
     * Phase 상세 정보 목록
     */
    private List<PhaseInfo> phases;
    
    /**
     * 카운트다운 정보
     */
    private CountdownInfo countdown;
    
    /**
     * 요금제별 가격 정보
     */
    private Map<String, PriceInfo> pricing;

    /**
     * Phase 상세 정보 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhaseInfo {
        private String phase;
        private String name;
        private Integer discountRate;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Boolean isCurrentPhase;
    }

    /**
     * 카운트다운 정보 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CountdownInfo {
        private LocalDateTime targetDate;
        private Long remainingDays;
        private Long remainingHours;
        private Long remainingMinutes;
        private Long remainingSeconds;
    }

    /**
     * 가격 정보 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PriceInfo {
        private Integer original;
        private Integer discounted;
        private Integer savings;
    }
}
```

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/dto/preregistration/EmailCheckResponse.java`

```java
package vibe.makersround.makersround_be_inclass.dto.preregistration;

import lombok.*;

/**
 * 이메일 중복 체크 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCheckResponse {
    
    private Boolean exists;
    private String discountCode;  // 이미 등록된 경우 할인 코드 반환
}
```

---

## 🎯 Phase 4: Service Layer 구현 (필수)

### 4.1 PreRegistrationService

**우선순위:** 🔴 Critical
**예상 소요:** 4-5시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/service/PreRegistrationService.java`

```java
package vibe.makersround.makersround_be_inclass.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_be_inclass.dto.preregistration.*;
import vibe.makersround.makersround_be_inclass.entity.PreRegistration;
import vibe.makersround.makersround_be_inclass.entity.PreRegistration.PlanType;
import vibe.makersround.makersround_be_inclass.entity.Promotion;
import vibe.makersround.makersround_be_inclass.exception.DuplicateEmailException;
import vibe.makersround.makersround_be_inclass.exception.PromotionEndedException;
import vibe.makersround.makersround_be_inclass.exception.ResourceNotFoundException;
import vibe.makersround.makersround_be_inclass.repository.PreRegistrationRepository;
import vibe.makersround.makersround_be_inclass.repository.PromotionRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PreRegistrationService {

    private final PreRegistrationRepository preRegistrationRepository;
    private final PromotionRepository promotionRepository;

    // 요금제별 정가
    private static final Map<PlanType, Integer> ORIGINAL_PRICES = Map.of(
        PlanType.plus, 399000,
        PlanType.pro, 799000,
        PlanType.premium, 1499000
    );

    /**
     * 사전 등록 신청
     * PRE-SUB-FUNC-002: POST /api/v1/pre-registrations
     */
    @Transactional
    public PreRegistrationResponse register(PreRegistrationRequest request) {
        log.info("사전 등록 요청: email={}, plan={}", request.getEmail(), request.getPlan());

        // 1. 이메일 중복 체크
        if (preRegistrationRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 등록된 이메일입니다: " + request.getEmail());
        }

        // 2. 프로모션 활성 여부 확인
        Promotion promotion = getActivePromotion();
        String currentPhase = promotion.getCurrentPhase();
        if ("ENDED".equals(currentPhase) || "NOT_STARTED".equals(currentPhase)) {
            throw new PromotionEndedException("프로모션이 종료되었거나 아직 시작되지 않았습니다.");
        }

        // 3. 할인율 및 가격 계산
        Integer discountRate = promotion.getCurrentDiscountRate();
        PlanType planType = request.getPlan();
        Integer originalPrice = ORIGINAL_PRICES.get(planType);
        Integer discountedPrice = calculateDiscountedPrice(originalPrice, discountRate);

        // 4. 할인 코드 생성 (MR2026-{PLAN}-{PHASE}{RANDOM} 형식)
        String discountCode = generateDiscountCode(planType, currentPhase);

        // 5. 만료일 계산 (현재 Phase 종료일)
        LocalDateTime expiresAt = "A".equals(currentPhase) 
            ? promotion.getPhaseAEnd() 
            : promotion.getPhaseBEnd();

        // 6. Entity 생성 및 저장
        PreRegistration entity = PreRegistration.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .selectedPlan(convertToPlanType(planType))
                .businessCategory(request.getBusinessCategory())
                .marketingConsent(request.getMarketingConsent() != null ? request.getMarketingConsent() : false)
                .promotionPhase(currentPhase)
                .discountCode(discountCode)
                .discountRate(discountRate)
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .expiresAt(expiresAt)
                .build();

        PreRegistration saved = preRegistrationRepository.save(entity);
        log.info("사전 등록 완료: id={}, discountCode={}", saved.getId(), discountCode);

        // 7. 응답 생성
        return mapToResponse(saved);
    }

    /**
     * 이메일 중복 체크
     */
    public EmailCheckResponse checkEmail(String email) {
        Optional<PreRegistration> existing = preRegistrationRepository.findByEmail(email);
        
        if (existing.isPresent()) {
            return EmailCheckResponse.builder()
                    .exists(true)
                    .discountCode(existing.get().getDiscountCode())
                    .build();
        }
        
        return EmailCheckResponse.builder()
                .exists(false)
                .build();
    }

    /**
     * 등록 정보 조회 (ID)
     */
    public PreRegistrationResponse getById(String id) {
        PreRegistration entity = preRegistrationRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("등록 정보를 찾을 수 없습니다: " + id));
        return mapToResponse(entity);
    }

    /**
     * 할인 코드로 조회
     */
    public PreRegistrationResponse getByDiscountCode(String discountCode) {
        PreRegistration entity = preRegistrationRepository.findByDiscountCode(discountCode)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 할인 코드입니다: " + discountCode));
        return mapToResponse(entity);
    }

    /**
     * 현재 프로모션 정보 조회
     * PRE-SUB-FUNC-002: GET /api/v1/promotions/current
     */
    public PromotionInfoResponse getPromotionInfo() {
        Promotion promotion = getActivePromotion();
        String currentPhase = promotion.getCurrentPhase();
        Integer discountRate = promotion.getCurrentDiscountRate();

        // Phase 목록 생성
        List<PromotionInfoResponse.PhaseInfo> phases = buildPhaseList(promotion, currentPhase);

        // 카운트다운 계산
        PromotionInfoResponse.CountdownInfo countdown = buildCountdown(promotion, currentPhase);

        // 요금제별 가격 정보 계산
        Map<String, PromotionInfoResponse.PriceInfo> pricing = new HashMap<>();
        for (Map.Entry<PlanType, Integer> entry : ORIGINAL_PRICES.entrySet()) {
            Integer original = entry.getValue();
            Integer discounted = calculateDiscountedPrice(original, discountRate);
            pricing.put(entry.getKey().name(), PromotionInfoResponse.PriceInfo.builder()
                    .original(original)
                    .discounted(discounted)
                    .savings(original - discounted)
                    .build());
        }

        return PromotionInfoResponse.builder()
                .isActive(!"ENDED".equals(currentPhase) && !"NOT_STARTED".equals(currentPhase))
                .currentPhase(currentPhase)
                .phases(phases)
                .countdown(countdown)
                .pricing(pricing)
                .build();
    }

    // ========== Private Methods ==========

    private Promotion getActivePromotion() {
        return promotionRepository.findByIsActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("활성화된 프로모션이 없습니다."));
    }

    private List<PromotionInfoResponse.PhaseInfo> buildPhaseList(Promotion promotion, String currentPhase) {
        List<PromotionInfoResponse.PhaseInfo> phases = new ArrayList<>();
        
        phases.add(PromotionInfoResponse.PhaseInfo.builder()
                .phase("A")
                .name("연말연시 특별 할인")
                .discountRate(promotion.getPhaseADiscountRate())
                .startDate(promotion.getPhaseAStart())
                .endDate(promotion.getPhaseAEnd())
                .isCurrentPhase("A".equals(currentPhase))
                .build());
        
        phases.add(PromotionInfoResponse.PhaseInfo.builder()
                .phase("B")
                .name("얼리버드 할인")
                .discountRate(promotion.getPhaseBDiscountRate())
                .startDate(promotion.getPhaseBStart())
                .endDate(promotion.getPhaseBEnd())
                .isCurrentPhase("B".equals(currentPhase))
                .build());
        
        return phases;
    }

    private PromotionInfoResponse.CountdownInfo buildCountdown(Promotion promotion, String currentPhase) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDate = "A".equals(currentPhase) ? promotion.getPhaseAEnd() : promotion.getPhaseBEnd();
        
        if (targetDate == null) return null;
        
        Duration duration = Duration.between(now, targetDate);
        long totalSeconds = Math.max(0, duration.getSeconds());
        
        return PromotionInfoResponse.CountdownInfo.builder()
                .targetDate(targetDate)
                .remainingDays(totalSeconds / (24 * 3600))
                .remainingHours((totalSeconds % (24 * 3600)) / 3600)
                .remainingMinutes((totalSeconds % 3600) / 60)
                .remainingSeconds(totalSeconds % 60)
                .build();
    }

    private Integer calculateDiscountedPrice(Integer originalPrice, Integer discountRate) {
        double discount = originalPrice * (discountRate / 100.0);
        return (int) Math.round(originalPrice - discount);
    }

    private String generateDiscountCode(PlanType planType, String phase) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder("MR2026-");
        code.append(planType.name().toUpperCase()).append("-");
        code.append(phase);
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // 중복 체크
        String generatedCode = code.toString();
        if (preRegistrationRepository.findByDiscountCode(generatedCode).isPresent()) {
            return generateDiscountCode(planType, phase); // 재귀 호출로 재생성
        }
        return generatedCode;
    }

    private PreRegistration.PlanType convertToPlanType(PlanType planType) {
        return PreRegistration.PlanType.valueOf(planType.name());
    }

    private PreRegistrationResponse mapToResponse(PreRegistration entity) {
        return PreRegistrationResponse.builder()
                .registrationId(entity.getId().toString())
                .plan(entity.getSelectedPlan().name())
                .promotionPhase(entity.getPromotionPhase())
                .discountRate(entity.getDiscountRate())
                .discountCode(entity.getDiscountCode())
                .originalPrice(entity.getOriginalPrice())
                .discountedPrice(entity.getDiscountedPrice())
                .savings(entity.getOriginalPrice() - entity.getDiscountedPrice())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
```

---

## 🎯 Phase 5: Controller 구현 (필수)

### 5.1 PreRegistrationController

**우선순위:** 🔴 Critical
**예상 소요:** 2-3시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/controller/PreRegistrationController.java`

```java
package vibe.makersround.makersround_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.makersround.makersround_be_inclass.dto.ApiResponse as ApiResp;
import vibe.makersround.makersround_be_inclass.dto.preregistration.*;
import vibe.makersround.makersround_be_inclass.service.PreRegistrationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pre-Registration", description = "사전 등록 프로모션 API")
public class PreRegistrationController {

    private final PreRegistrationService preRegistrationService;

    /**
     * 사전 등록 신청
     */
    @PostMapping("/pre-registrations")
    @Operation(summary = "사전 등록 신청", description = "사전 등록을 신청하고 할인 코드를 발급받습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "입력값 오류"),
        @ApiResponse(responseCode = "409", description = "이메일 중복"),
        @ApiResponse(responseCode = "410", description = "프로모션 종료")
    })
    public ResponseEntity<ApiResp<PreRegistrationResponse>> register(
            @Valid @RequestBody PreRegistrationRequest request) {
        
        log.info("POST /api/v1/pre-registrations - email: {}", request.getEmail());
        PreRegistrationResponse response = preRegistrationService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResp.success(response, "사전 등록이 완료되었습니다."));
    }

    /**
     * 이메일 중복 체크
     */
    @GetMapping("/pre-registrations/check-email")
    @Operation(summary = "이메일 중복 체크", description = "이메일 등록 여부를 확인합니다.")
    public ResponseEntity<ApiResp<EmailCheckResponse>> checkEmail(
            @Parameter(description = "확인할 이메일 주소") @RequestParam String email) {
        
        log.info("GET /api/v1/pre-registrations/check-email - email: {}", email);
        EmailCheckResponse response = preRegistrationService.checkEmail(email);
        
        return ResponseEntity.ok(ApiResp.success(response));
    }

    /**
     * 등록 정보 조회
     */
    @GetMapping("/pre-registrations/{id}")
    @Operation(summary = "등록 정보 조회", description = "등록 ID로 사전 등록 정보를 조회합니다.")
    public ResponseEntity<ApiResp<PreRegistrationResponse>> getById(
            @Parameter(description = "등록 ID") @PathVariable String id) {
        
        log.info("GET /api/v1/pre-registrations/{}", id);
        PreRegistrationResponse response = preRegistrationService.getById(id);
        
        return ResponseEntity.ok(ApiResp.success(response));
    }

    /**
     * 할인 코드로 조회
     */
    @GetMapping("/pre-registrations/code/{discountCode}")
    @Operation(summary = "할인 코드로 조회", description = "할인 코드로 사전 등록 정보를 조회합니다.")
    public ResponseEntity<ApiResp<PreRegistrationResponse>> getByDiscountCode(
            @Parameter(description = "할인 코드") @PathVariable String discountCode) {
        
        log.info("GET /api/v1/pre-registrations/code/{}", discountCode);
        PreRegistrationResponse response = preRegistrationService.getByDiscountCode(discountCode);
        
        return ResponseEntity.ok(ApiResp.success(response));
    }

    /**
     * 현재 프로모션 정보 조회
     */
    @GetMapping("/promotions/current")
    @Operation(summary = "현재 프로모션 정보", description = "현재 활성화된 프로모션 정보와 할인가를 조회합니다.")
    public ResponseEntity<ApiResp<PromotionInfoResponse>> getPromotionInfo() {
        
        log.info("GET /api/v1/promotions/current");
        PromotionInfoResponse response = preRegistrationService.getPromotionInfo();
        
        return ResponseEntity.ok(ApiResp.success(response));
    }
}
```

---

## 🎯 Phase 6: 예외 처리 (필수)

### 6.1 커스텀 예외 클래스

**우선순위:** 🔴 Critical
**예상 소요:** 1시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/exception/DuplicateEmailException.java`

```java
package vibe.makersround.makersround_be_inclass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
```

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/exception/PromotionEndedException.java`

```java
package vibe.makersround.makersround_be_inclass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class PromotionEndedException extends RuntimeException {
    public PromotionEndedException(String message) {
        super(message);
    }
}
```

### 6.2 GlobalExceptionHandler 업데이트

`GlobalExceptionHandler.java`에 추가:

```java
@ExceptionHandler(DuplicateEmailException.class)
public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException ex) {
    log.warn("Duplicate email: {}", ex.getMessage());
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("DUPLICATE_EMAIL", ex.getMessage()));
}

@ExceptionHandler(PromotionEndedException.class)
public ResponseEntity<ApiResponse<Void>> handlePromotionEnded(PromotionEndedException ex) {
    log.warn("Promotion ended: {}", ex.getMessage());
    return ResponseEntity
            .status(HttpStatus.GONE)
            .body(ApiResponse.error("PROMOTION_ENDED", ex.getMessage()));
}
```

---

## 🎯 Phase 7: 테스트 코드 (필수)

### 7.1 Repository 테스트

**우선순위:** 🔴 Critical
**예상 소요:** 2-3시간

**파일:** `src/test/java/vibe/makersround/makersround_be_inclass/repository/PreRegistrationRepositoryTest.java`

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("PreRegistrationRepository 테스트")
class PreRegistrationRepositoryTest {

    @Autowired
    private PreRegistrationRepository repository;

    @Test
    @DisplayName("사전 등록 저장 및 조회")
    void save_and_findById() {
        // given
        PreRegistration entity = createTestEntity("test@example.com");
        
        // when
        PreRegistration saved = repository.save(entity);
        Optional<PreRegistration> found = repository.findById(saved.getId());
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("이메일 중복 체크")
    void existsByEmail() {
        // given
        repository.save(createTestEntity("duplicate@example.com"));
        
        // when & then
        assertThat(repository.existsByEmail("duplicate@example.com")).isTrue();
        assertThat(repository.existsByEmail("new@example.com")).isFalse();
    }

    @Test
    @DisplayName("할인 코드로 조회")
    void findByDiscountCode() {
        // given
        PreRegistration entity = createTestEntity("code@example.com");
        entity = repository.save(entity);
        
        // when
        Optional<PreRegistration> found = repository.findByDiscountCode(entity.getDiscountCode());
        
        // then
        assertThat(found).isPresent();
    }

    private PreRegistration createTestEntity(String email) {
        return PreRegistration.builder()
                .name("테스트")
                .email(email)
                .phone("010-1234-5678")
                .selectedPlan(PreRegistration.PlanType.pro)
                .agreeTerms(true)
                .agreeMarketing(false)
                .discountCode("MR2026-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .discountRate(30)
                .originalPrice(799000)
                .discountedPrice(559300)
                .build();
    }
}
```

### 7.2 Service 테스트

**파일:** `src/test/java/vibe/makersround/makersround_be_inclass/service/PreRegistrationServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("PreRegistrationService 테스트")
class PreRegistrationServiceTest {

    @Mock
    private PreRegistrationRepository preRegistrationRepository;
    
    @Mock
    private PromotionRepository promotionRepository;
    
    @InjectMocks
    private PreRegistrationService service;

    @Test
    @DisplayName("사전 등록 성공")
    void register_success() {
        // given
        PreRegistrationRequest request = createTestRequest();
        Promotion promotion = createActivePromotion();
        
        when(preRegistrationRepository.existsByEmail(anyString())).thenReturn(false);
        when(promotionRepository.findByIsActiveTrue()).thenReturn(Optional.of(promotion));
        when(preRegistrationRepository.findByDiscountCode(anyString())).thenReturn(Optional.empty());
        when(preRegistrationRepository.save(any())).thenAnswer(invocation -> {
            PreRegistration entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
            return entity;
        });
        
        // when
        PreRegistrationResponse response = service.register(request);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountCode()).startsWith("MR2026-");
        assertThat(response.getDiscountRate()).isEqualTo(30);
    }

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void register_duplicateEmail_throwsException() {
        // given
        PreRegistrationRequest request = createTestRequest();
        when(preRegistrationRepository.existsByEmail(anyString())).thenReturn(true);
        
        // when & then
        assertThrows(DuplicateEmailException.class, () -> service.register(request));
    }

    @Test
    @DisplayName("프로모션 종료 시 예외 발생")
    void register_promotionEnded_throwsException() {
        // given
        PreRegistrationRequest request = createTestRequest();
        Promotion endedPromotion = createEndedPromotion();
        
        when(preRegistrationRepository.existsByEmail(anyString())).thenReturn(false);
        when(promotionRepository.findByIsActiveTrue()).thenReturn(Optional.of(endedPromotion));
        
        // when & then
        assertThrows(PromotionEndedException.class, () -> service.register(request));
    }
}
```

### 7.3 Controller 테스트 (MockMvc)

**파일:** `src/test/java/vibe/makersround/makersround_be_inclass/controller/PreRegistrationControllerTest.java`

```java
@WebMvcTest(PreRegistrationController.class)
@DisplayName("PreRegistrationController 테스트")
class PreRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PreRegistrationService service;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/pre-registrations - 성공")
    void register_success() throws Exception {
        // given
        PreRegistrationRequest request = createTestRequest();
        PreRegistrationResponse response = createTestResponse();
        when(service.register(any())).thenReturn(response);
        
        // when & then
        mockMvc.perform(post("/api/v1/pre-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.discountCode").value(response.getDiscountCode()));
    }

    @Test
    @DisplayName("POST /api/v1/pre-registrations - 유효성 검사 실패")
    void register_validationFail() throws Exception {
        // given
        PreRegistrationRequest request = PreRegistrationRequest.builder()
                .name("")  // 빈 이름
                .email("invalid-email")  // 잘못된 이메일
                .build();
        
        // when & then
        mockMvc.perform(post("/api/v1/pre-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/promotions/current - 성공")
    void getPromotionInfo_success() throws Exception {
        // given
        PromotionInfoResponse response = createPromotionInfoResponse();
        when(service.getPromotionInfo()).thenReturn(response);
        
        // when & then
        mockMvc.perform(get("/api/v1/promotions/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.currentPhase").value("A"));
    }
}
```

---

## 🎯 Phase 8: 관리자 API (선택)

### 8.1 AdminPreRegistrationController

**우선순위:** 🟢 Medium
**예상 소요:** 3-4시간

**파일:** `src/main/java/vibe/makersround/makersround_be_inclass/controller/AdminPreRegistrationController.java`

```java
@RestController
@RequestMapping("/api/v1/admin/pre-registrations")
@RequiredArgsConstructor
@Tag(name = "Admin - Pre-Registration", description = "사전 등록 관리 API")
public class AdminPreRegistrationController {

    private final AdminPreRegistrationService adminService;

    @GetMapping
    @Operation(summary = "사전 등록 목록 조회")
    public ResponseEntity<ApiResponse<Page<PreRegistrationResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String plan,
            @RequestParam(required = false) String keyword) {
        // 구현
    }

    @GetMapping("/statistics")
    @Operation(summary = "사전 등록 통계")
    public ResponseEntity<ApiResponse<PreRegistrationStatistics>> getStatistics() {
        // 총 등록 수, 오늘 등록 수, 요금제별 분포, 마케팅 동의율
    }

    @GetMapping("/export")
    @Operation(summary = "CSV 내보내기")
    public ResponseEntity<byte[]> exportToCsv() {
        // CSV 파일 생성 및 반환
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "상태 변경")
    public ResponseEntity<ApiResponse<PreRegistrationResponse>> updateStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateRequest request) {
        // 상태 변경 (pending → confirmed 등)
    }
}
```

---

## 📋 구현 체크리스트

### Phase 0: SQLite + Flyway 설정 (필수) ✅
- [x] build.gradle에 SQLite 및 Flyway 의존성 추가
- [x] application.properties에 SQLite 데이터소스 설정
- [x] Hibernate SQLite Dialect 설정
- [x] data/ 디렉토리 생성 (DB 파일 저장 위치)

### Phase 1: 데이터베이스 (필수) ✅
- [x] V3__create_pre_registrations_table.sql 마이그레이션 (SQLite 문법)
- [x] V4__create_promotions_table.sql 마이그레이션 (SQLite 문법)
- [x] 초기 프로모션 데이터 삽입 (하드코딩 UUID)

### Phase 2: Entity & Repository (필수) ✅
- [x] PreRegistration 엔티티
- [x] Promotion 엔티티
- [x] PreRegistrationRepository
- [x] PromotionRepository

### Phase 3: DTO (필수) ✅
- [x] PreRegistrationRequest (유효성 검사 포함)
- [x] PreRegistrationResponse
- [x] PromotionInfoResponse (phases, countdown 포함)
- [x] EmailCheckResponse

### Phase 4: Service (필수) ✅
- [x] PreRegistrationService
- [x] 할인 코드 생성 로직 (MR2026-{PLAN}-{PHASE}{RANDOM} 형식)
- [x] 가격 계산 로직

### Phase 5: Controller (필수) ✅
- [x] POST /api/v1/pre-registrations
- [x] GET /api/v1/pre-registrations/check-email
- [x] GET /api/v1/pre-registrations/{id}
- [x] GET /api/v1/pre-registrations/code/{discountCode}
- [x] GET /api/v1/promotions/current
- [x] Swagger 문서화

### Phase 6: 예외 처리 (필수) ✅
- [x] DuplicateEmailException
- [x] PromotionEndedException
- [x] ResourceNotFoundException
- [x] GlobalExceptionHandler 업데이트

### Phase 7: 테스트 (필수) ✅
- [x] Repository 테스트 (8개)
- [x] Service 테스트 (10개)
- [x] Controller 테스트 (10개)

### Phase 8: 관리자 기능 (선택)
- [ ] AdminPreRegistrationController
- [ ] 통계 API
- [ ] CSV 내보내기
- [ ] 상태 변경 API

---

## 📝 API 엔드포인트 요약

### 구현 완료 ✅

| Method | Endpoint | 설명 | 상태 |
|--------|----------|------|------|
| POST | `/api/v1/pre-registrations` | 사전 등록 신청 | ✅ |
| GET | `/api/v1/pre-registrations/check-email?email=` | 이메일 중복 체크 | ✅ |
| GET | `/api/v1/pre-registrations/{id}` | 등록 정보 조회 | ✅ |
| GET | `/api/v1/pre-registrations/code/{discountCode}` | 할인 코드로 조회 | ✅ |
| GET | `/api/v1/promotions/current` | 현재 프로모션 정보 | ✅ |

### 관리자 기능 (미구현)

| Method | Endpoint | 설명 | 상태 |
|--------|----------|------|------|
| GET | `/api/v1/admin/pre-registrations` | 관리자: 목록 조회 | ⏳ |
| GET | `/api/v1/admin/pre-registrations/statistics` | 관리자: 통계 | ⏳ |
| GET | `/api/v1/admin/pre-registrations/export` | 관리자: CSV 내보내기 | ⏳ |
| PATCH | `/api/v1/admin/pre-registrations/{id}/status` | 관리자: 상태 변경 | ⏳ |

---

## 🔗 참고 문서

- [SQLITE_FLYWAY_GUIDE.md](/docs/SQLITE_FLYWAY_GUIDE.md) - SQLite + Flyway 설정 가이드 ⭐
- [306-three-tier-architecture-rules.mdc](/.cursor/rules/306-three-tier-architecture-rules.mdc)
- [304-api-rest-design-rules.mdc](/.cursor/rules/304-api-rest-design-rules.mdc)
- [305-api-swagger-testing-rules.mdc](/.cursor/rules/305-api-swagger-testing-rules.mdc)

---

## 🤖 AI 프롬프트 치트시트

> Cursor Composer (`Ctrl + I`)에서 사용

| 작업 | AI 프롬프트 예시 |
|------|-----------------|
| **테이블 생성** | "PreRegistration 엔티티를 보고 SQLite용 Flyway 마이그레이션 파일을 만들어줘. ENUM은 TEXT + CHECK로 대체해줘." |
| **컬럼 추가** | "pre_registrations 테이블에 `referral_code` 컬럼을 추가하는 새 버전 마이그레이션 파일을 만들어줘." |
| **컬럼 삭제** | "SQLite에서 컬럼 삭제는 직접 안 되니까 임시 테이블 방식으로 `business_category` 컬럼을 삭제하는 마이그레이션을 작성해줘." |
| **에러 수정** | "[에러 로그] 이 에러가 났어. SQLite 문법에 맞게 마이그레이션 파일을 수정해줘." |
| **데이터 확인** | "@makersround.db 파일에서 등록된 사용자 중 pro 요금제를 선택한 사람을 조회해줘." |

---

*Created: 2025-12-26*
*Last Updated: 2025-12-28 (구현 완료, PRE-SUB-FUNC-002 명세서와 동기화)*
