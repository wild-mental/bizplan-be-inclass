# 프로젝트 실행 에러 해결 리포트

**날짜**: 2025-12-29  
**프로젝트**: makersround-backend  
**문제**: Spring Boot 애플리케이션 실행 시 데이터베이스 연결 오류

---

## 🔴 발견된 문제들

### 1. MySQL 연결 실패 (초기 에러) - 해결됨
> ⚠️ **역사적 기록**: 이 문제는 이미 해결되었으며, 현재 프로젝트는 모든 환경에서 SQLite를 사용합니다.

**에러 메시지** (과거):
```
Communications link failure
Connection refused
```

**원인** (과거):
- `application.properties`가 MySQL을 기본 데이터소스로 사용하도록 설정되어 있음
- 로컬 개발 환경에서 MySQL 서버가 실행되지 않음

**해결 방법** (완료):
- 프로젝트를 SQLite로 전환하여 모든 환경에서 SQLite 사용
- `application.properties`에 SQLite 설정 적용

---

### 2. Flyway 마이그레이션 실행 순서 문제
**에러 메시지**:
```
Schema-validation: missing table [business_plans]
Unable to build Hibernate SessionFactory
```

**원인**:
- Hibernate가 Flyway 마이그레이션 실행 전에 스키마를 검증하려고 시도
- `spring.jpa.hibernate.ddl-auto=validate`로 설정되어 있어서 테이블이 없으면 검증 실패
- Flyway가 마이그레이션을 실행하기 전에 Hibernate 초기화가 진행됨

**해결 방법**:
- `spring.jpa.hibernate.ddl-auto=none`으로 변경하여 Hibernate가 스키마 생성을 시도하지 않도록 설정
- Flyway가 모든 스키마 관리를 담당하도록 변경

---

## ✅ 적용된 해결 방법

### 1. `application-local.properties` 파일 생성
**위치**: `src/main/resources/application-local.properties`

**주요 설정**:
```properties
# SQLite 데이터베이스 설정
spring.datasource.url=jdbc:sqlite:./data/makersround.db
spring.datasource.driver-class-name=org.sqlite.JDBC

# SQLite용 Hibernate Dialect
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.properties.hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect

# Flyway가 스키마를 관리하므로 Hibernate는 스키마 생성을 하지 않음
spring.jpa.hibernate.ddl-auto=none

# Flyway 마이그레이션 설정
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration/sqlite

# SQLite 연결 풀 설정 (단일 쓰기 락 때문에 pool size 1)
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.minimum-idle=1
```

### 2. 데이터 디렉토리 생성
```bash
mkdir -p data
```

---

## 📊 해결 결과

### ✅ 성공적으로 해결된 항목
1. ✅ MySQL 연결 오류 해결 → SQLite로 전환 (완료)
2. ✅ Flyway 마이그레이션 실행 순서 문제 해결
3. ✅ 애플리케이션 정상 시작 확인

### 애플리케이션 시작 로그
```
2025-12-29 12:28:18.365  INFO --- [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
2025-12-29 12:28:18.370  INFO --- [  restartedMain] v.b.b.BizplanBeInclassApplication        : Started BizplanBeInclassApplication in 3.361 seconds
```

---

## 🔧 추가 권장 사항

### 1. 환경 변수 설정
로컬 개발 시 다음 환경 변수를 설정하는 것을 권장합니다:

```bash
# .env 파일 또는 환경 변수로 설정
SPRING_PROFILES_ACTIVE=local
GEMINI_API_KEY=your_api_key_here  # AI 기능 사용 시 필요
JWT_SECRET=local-dev-jwt-secret-key-min-32-chars-required
ENCRYPTION_KEY=local-dev-encryption-key-32-chars
```

### 2. Flyway 마이그레이션 확인
데이터베이스가 제대로 생성되었는지 확인:
```bash
sqlite3 data/makersround.db ".tables"
```

### 3. 프로파일별 설정 분리
- `local`: SQLite 사용 (개발 환경)
- `dev`: SQLite 사용 (개발 서버)
- `prod`: SQLite 사용 (운영 환경)

---

## 📝 참고 사항

1. **SQLite 제한사항**:
   - 단일 쓰기 락: 동시 쓰기 작업이 제한적
   - 연결 풀 크기를 1로 설정하여 락 경합 방지

2. **Flyway 마이그레이션**:
   - SQLite용 마이그레이션 파일은 `src/main/resources/db/migration/sqlite/` 디렉토리에 위치
   - SQLite용 마이그레이션 파일은 `src/main/resources/db/migration/sqlite/` 디렉토리에 위치

3. **데이터베이스 파일**:
   - SQLite 데이터베이스 파일: `./data/makersround.db`
   - `.gitignore`에 포함되어 버전 관리에서 제외됨

---

## 🎯 결론

프로젝트가 성공적으로 실행되도록 모든 문제를 해결했습니다. 현재 프로젝트는 모든 환경(로컬/운영/테스트)에서 SQLite를 사용하며, 별도의 데이터베이스 서버 설치 없이 개발할 수 있습니다.

**실행 명령어**:
```bash
./gradlew bootRun
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

