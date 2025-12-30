# 데이터베이스 비교: SQLite vs MySQL

## 📋 현재 프로젝트 상태

| 항목 | 현재 설정 |
|------|----------|
| **운영 DB** | SQLite |
| **개발 DB** | SQLite |
| **테스트 DB** | SQLite (In-Memory 또는 파일) |
| **마이그레이션** | Flyway |
| **ORM** | Spring Data JPA / Hibernate |

---

## 🔍 SQLite vs MySQL 비교

### 1. 아키텍처

| 특성 | SQLite | MySQL |
|------|--------|-------|
| **유형** | 임베디드 (파일 기반) | 클라이언트-서버 |
| **프로세스** | 애플리케이션 내장 | 별도 서버 프로세스 |
| **설치** | 불필요 (라이브러리) | 서버 설치 필요 |
| **파일** | 단일 `.db` 파일 | 복잡한 파일 구조 |

### 2. 성능

| 특성 | SQLite | MySQL |
|------|--------|-------|
| **읽기 성능** | ⭐⭐⭐⭐⭐ 매우 빠름 | ⭐⭐⭐⭐ 빠름 |
| **쓰기 성능** | ⭐⭐⭐ 보통 (락 경합) | ⭐⭐⭐⭐⭐ 매우 빠름 |
| **동시 접속** | ⭐⭐ 제한적 | ⭐⭐⭐⭐⭐ 우수 |
| **대용량 처리** | ⭐⭐ 제한적 | ⭐⭐⭐⭐⭐ 우수 |

### 3. 기능

| 기능 | SQLite | MySQL |
|------|--------|-------|
| **트랜잭션** | ✅ ACID 지원 | ✅ ACID 지원 |
| **동시 쓰기** | ❌ 단일 쓰기 락 | ✅ 행 레벨 락 |
| **복제/클러스터** | ❌ 미지원 | ✅ 지원 |
| **저장 프로시저** | ❌ 미지원 | ✅ 지원 |
| **사용자 관리** | ❌ 파일 권한만 | ✅ 상세 권한 관리 |
| **JSON 지원** | ✅ 지원 | ✅ 지원 |
| **전문 검색** | ⚠️ 제한적 (FTS5) | ✅ 풀텍스트 인덱스 |

---

## ✅ SQLite 장점

### 1. 운영 단순화
```
🚀 서버 설치/관리 불필요
📦 단일 파일로 전체 DB 백업/복원
🔧 설정 파일 없음
💻 개발 환경 셋업 간소화
```

### 2. 배포 용이성
```
✅ Docker 이미지 크기 감소
✅ 의존성 최소화
✅ 오프라인 환경 지원
✅ 단일 바이너리 배포 가능
```

### 3. 리소스 효율
```
💾 메모리 사용량 최소
⚡ 빠른 시작 시간
🔋 낮은 CPU 사용률
📉 운영 비용 절감
```

### 4. 개발 편의성
```
🧪 테스트 환경 일관성 (H2 대체 가능)
📁 DB 파일 복사로 데이터 공유
🔄 개발-운영 DB 동일
```

---

## ❌ SQLite 단점

### 1. 동시성 제한
```
⚠️ 동시 쓰기 불가 (단일 쓰기 락)
⚠️ 다중 사용자 환경에서 성능 저하
⚠️ 웹 애플리케이션 병목 가능성
```

**영향 분석 (BizPlan 프로젝트):**
- 사업계획서 생성은 Gemini API 호출이 병목 (3-5초)
- DB 쓰기는 상대적으로 짧음
- 동시 사용자 10명 이하: 문제 없음
- 동시 사용자 50명 이상: 병목 가능

### 2. 확장성 제한
```
❌ 수평 확장 불가 (복제 없음)
❌ 샤딩 미지원
❌ 읽기 전용 복제본 없음
```

### 3. 기능 제한
```
❌ 저장 프로시저 없음
❌ 트리거 제한적
❌ 뷰 업데이트 제한
❌ ALTER TABLE 제한 (컬럼 삭제 등)
```

### 4. 운영 환경 제약
```
⚠️ 클라우드 관리형 서비스 없음
⚠️ 모니터링 도구 제한적
⚠️ 고가용성 구성 어려움
```

---

## 🎯 BizPlan 프로젝트 적합성 분석

### 현재 요구사항

| 요구사항 | SQLite | MySQL | 비고 |
|----------|:------:|:-----:|------|
| 사업계획서 CRUD | ✅ | ✅ | 단순 CRUD |
| JSON 데이터 저장 | ✅ | ✅ | 요청/응답 저장 |
| 동시 사용자 | ⚠️ | ✅ | MVP: 10명 이하 |
| 데이터 규모 | ✅ | ✅ | 수천 건 수준 |
| 클라우드 배포 | ⚠️ | ✅ | 영구 볼륨 필요 |
| 운영 단순화 | ✅ | ❌ | SQLite 유리 |

### 사용 시나리오별 권장

| 시나리오 | 권장 DB | 이유 |
|----------|---------|------|
| **로컬 개발** | SQLite | 설치 없이 바로 시작 |
| **MVP/PoC** | SQLite | 빠른 검증, 운영 단순화 |
| **소규모 서비스** (<50 DAU) | SQLite | 충분한 성능 |
| **중규모 서비스** (50-500 DAU) | MySQL | 동시성 필요 |
| **대규모 서비스** (>500 DAU) | MySQL | 확장성 필수 |

---

## 💡 권장사항

### Option A: SQLite 적용 (MVP/초기 단계)

**적합한 경우:**
- MVP 빠른 검증이 목표
- 동시 사용자 50명 이하 예상
- 운영 인력/비용 최소화 필요
- 단일 서버 배포

```
장점: 운영 단순화, 비용 절감, 빠른 배포
단점: 확장 시 마이그레이션 필요
```

### Option B: MySQL 유지 (성장 대비)

**적합한 경우:**
- 빠른 사용자 증가 예상
- 다중 서버 배포 계획
- 클라우드 관리형 DB 사용 (RDS 등)
- 장기 운영 안정성 중시

```
장점: 확장성, 안정성, 도구 지원
단점: 운영 복잡도, 비용
```

### Option C: SQLite 통일 (현재 적용) ⭐

**개발/테스트/운영:** SQLite (모두 동일)

```java
// application.properties
spring.profiles.active=${SPRING_PROFILES_ACTIVE:local}

// 모든 환경에서 SQLite 사용
spring.datasource.url=jdbc:sqlite:./data/bizplan.db
spring.datasource.driver-class-name=org.sqlite.JDBC
```

```
장점: 환경 간 일관성, 운영 단순화, 배포 용이성
단점: 동시성 제한 (소규모 서비스에는 충분)
```

---

## 🔧 SQLite 적용 시 기술적 고려사항

### 1. 의존성 변경

```gradle
// build.gradle
dependencies {
    // MySQL 제거
    // runtimeOnly 'com.mysql:mysql-connector-j'
    
    // SQLite 추가
    runtimeOnly 'org.xerial:sqlite-jdbc:3.45.1.0'
    
    // Hibernate SQLite Dialect
    implementation 'org.hibernate.orm:hibernate-community-dialects:6.4.4.Final'
}
```

### 2. Flyway 호환성

```gradle
// SQLite용 Flyway
implementation 'org.flywaydb:flyway-core'
// flyway-mysql 제거
```

**주의:** 일부 MySQL 전용 SQL 문법 수정 필요
- `AUTO_INCREMENT` → `AUTOINCREMENT`
- `TINYINT(1)` → `INTEGER`
- `DATETIME` → `TEXT` (ISO 8601 형식)

### 3. JPA 설정

```properties
# application-sqlite.properties
spring.datasource.url=jdbc:sqlite:./data/bizplan.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
```

### 4. 파일 위치

```
bizplan-be-inclass/
└── data/
    └── bizplan.db    # SQLite 데이터베이스 파일
```

> ⚠️ Docker 배포 시 볼륨 마운트 필요: `-v ./data:/app/data`

---

## 📊 성능 벤치마크 (예상치)

| 작업 | SQLite | MySQL | 비고 |
|------|--------|-------|------|
| 단일 INSERT | 0.5ms | 1ms | SQLite 빠름 |
| 단일 SELECT | 0.1ms | 0.5ms | SQLite 빠름 |
| 동시 INSERT (10건) | 50ms | 10ms | MySQL 빠름 |
| 동시 INSERT (100건) | 500ms+ | 50ms | MySQL 10배 빠름 |
| JSON 쿼리 | 1ms | 1ms | 유사 |

---

## 🎬 결론

### MVP/PoC 단계
> **SQLite 권장** - 빠른 검증, 운영 단순화

### 상용 서비스 단계
> **MySQL 권장** - 확장성, 안정성, 도구 지원

### BizPlan 프로젝트
> **현재: SQLite 사용 중**
> - SQLite 기반 구현 완료
> - Flyway 마이그레이션 작성됨 (SQLite용)
> - 로컬/운영/테스트 환경 모두 SQLite 사용
> - 운영 단순화 및 빠른 배포에 최적화

---

**작성일**: 2025-12-27  
**관련 문서**: [ENV_SETUP_GUIDE.md](./ENV_SETUP_GUIDE.md)

