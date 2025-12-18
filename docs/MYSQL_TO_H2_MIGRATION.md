# MySQL → H2 데이터베이스 전환 작업 요약

## 📋 개요

로컬 개발 환경에서 MySQL 대신 H2 인메모리 데이터베이스를 사용하도록 변경하여, 개발자들이 별도의 MySQL 설치 없이도 프로젝트를 실행할 수 있도록 개선했습니다.

**작업 일시**: 2025-12-17  
**영향 범위**: 로컬 개발 환경 (`local` 프로파일)  
**프로덕션 영향**: 없음 (프로덕션은 여전히 MySQL 사용)

---

## 🎯 목표

1. **개발 환경 간소화**: MySQL 설치 및 설정 없이 프로젝트 실행 가능
2. **빠른 개발 사이클**: 인메모리 DB로 즉시 테스트 가능
3. **데이터베이스 독립성**: 로컬 개발과 프로덕션 환경 분리

---

## 📝 변경 사항 상세

### 1. 의존성 추가 (`build.gradle`)

#### 1.1 H2 데이터베이스 의존성 추가
```gradle
// JPA & Database
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.mysql:mysql-connector-j'
runtimeOnly 'com.h2database:h2'  // H2 for local development ← 추가
```

**변경 이유**: H2 인메모리 데이터베이스를 사용하기 위한 의존성 추가

#### 1.2 Spring Boot DevTools 추가
```gradle
// DevTools (for H2 console in local development)
developmentOnly 'org.springframework.boot:spring-boot-devtools'  ← 추가
```

**변경 이유**: H2 콘솔 활성화 및 개발 편의성 향상

---

### 2. 설정 파일 변경 (`application-local.properties`)

#### 2.1 데이터소스 설정 변경
```properties
# ============================================
# H2 In-Memory Database Configuration
# ============================================
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

**변경 전 (MySQL)**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bizplan?...
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=...
```

**변경 이유**: H2 인메모리 데이터베이스로 전환

#### 2.2 H2 콘솔 활성화
```properties
# H2 Console (for development)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=false
spring.h2.console.settings.trace=false
```

**변경 이유**: 개발 중 데이터베이스 상태 확인을 위한 웹 콘솔 제공

#### 2.3 JPA/Hibernate 설정 변경
```properties
# JPA / Hibernate Configuration for H2
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**변경 전 (MySQL)**:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

**변경 이유**: 
- H2 Dialect 사용
- `create-drop` 모드로 애플리케이션 시작 시 테이블 자동 생성, 종료 시 삭제

#### 2.4 Flyway 비활성화
```properties
# Flyway disabled for H2 (using Hibernate ddl-auto instead)
spring.flyway.enabled=false
```

**변경 이유**: Hibernate의 `ddl-auto=create-drop`을 사용하므로 Flyway 마이그레이션 불필요

---

### 3. 엔티티 수정 (`Project.java`)

#### 3.1 인덱스 정의 제거
**변경 전**:
```java
@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_projects_status", columnList = "status"),
    @Index(name = "idx_projects_created_at", columnList = "created_at")
})
```

**변경 후**:
```java
@Entity
@Table(name = "projects")
```

**변경 이유**: H2에서 `@Table`의 `indexes` 속성이 테이블 생성 전에 인덱스를 생성하려고 시도하여 오류 발생. H2 인메모리 DB에서는 인덱스가 필수적이지 않으므로 제거.

**해결한 문제**: 
```
Table "PROJECTS" not found; SQL statement:
    create index idx_projects_status on projects (status)
```

#### 3.2 TIMESTAMP 컬럼 정의 추가
**변경 전**:
```java
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(name = "updated_at", nullable = false)
private LocalDateTime updatedAt;
```

**변경 후**:
```java
@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
private LocalDateTime createdAt;

@Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
private LocalDateTime updatedAt;
```

**변경 이유**: H2는 MySQL의 `DATETIME` 타입을 인식하지 못하므로 `TIMESTAMP`로 명시적 지정

**해결한 문제**:
```
Unknown data type: "DATETIME"; SQL statement
```

---

### 4. H2 콘솔 설정 클래스 추가 (`H2ConsoleConfig.java`)

**신규 파일 생성**: `src/main/java/vibe/bizplan/bizplan_be_inclass/config/H2ConsoleConfig.java`

```java
@Configuration
@Profile("local")
public class H2ConsoleConfig {
    @Bean
    public ServletRegistrationBean<?> h2ConsoleServletRegistration() {
        // H2 2.x JakartaWebServlet 또는 H2 1.x WebServlet 자동 감지
        // /h2-console/* 경로로 서블릿 등록
    }
}
```

**변경 이유**: Spring Boot 4.0에서는 H2 콘솔 자동 설정이 제거되어 수동으로 서블릿 등록 필요

**해결한 문제**: H2 콘솔 접속 시 404 Not Found 오류

---

### 5. 예외 처리기 수정 (`GlobalExceptionHandler.java`)

**변경 전**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
```

**변경 후**:
```java
@RestControllerAdvice(basePackages = "vibe.bizplan.bizplan_be_inclass.controller")
public class GlobalExceptionHandler {
```

**변경 이유**: `@RestControllerAdvice`가 모든 요청(including H2 콘솔)을 가로채서 H2 콘솔 접속 시 500 오류 발생. 컨트롤러 패키지만 처리하도록 제한.

**해결한 문제**: H2 콘솔 접속 시 "서버 내부 오류가 발생했습니다" 오류

---

## 🔧 해결한 문제들

### 문제 1: 인덱스 생성 순서 오류
**증상**: `Table "PROJECTS" not found` 오류  
**원인**: Hibernate가 테이블 생성 전에 인덱스를 생성하려고 시도  
**해결**: `@Table`의 `indexes` 속성 제거

### 문제 2: DATETIME 타입 인식 불가
**증상**: `Unknown data type: "DATETIME"` 오류  
**원인**: H2는 MySQL의 DATETIME 타입을 지원하지 않음  
**해결**: `columnDefinition = "TIMESTAMP"` 명시적 지정

### 문제 3: H2 콘솔 404 오류
**증상**: `/h2-console` 접속 시 404 Not Found  
**원인**: Spring Boot 4.0에서 H2 콘솔 자동 설정 제거  
**해결**: `H2ConsoleConfig`에서 서블릿 수동 등록

### 문제 4: H2 콘솔 500 오류
**증상**: H2 콘솔 접속 시 "서버 내부 오류"  
**원인**: `GlobalExceptionHandler`가 H2 콘솔 요청도 가로챔  
**해결**: `@RestControllerAdvice`에 `basePackages` 지정

---

## 📊 변경 파일 요약

| 파일 경로 | 변경 유형 | 주요 변경 내용 |
|----------|---------|--------------|
| `build.gradle` | 수정 | H2 의존성, DevTools 추가 |
| `application-local.properties` | 수정 | H2 데이터소스 설정, 콘솔 활성화 |
| `Project.java` | 수정 | 인덱스 제거, TIMESTAMP 컬럼 정의 |
| `H2ConsoleConfig.java` | 신규 | H2 콘솔 서블릿 등록 |
| `GlobalExceptionHandler.java` | 수정 | basePackages 제한 추가 |

---

## ✅ 검증 결과

### 애플리케이션 실행
- ✅ Spring Boot 애플리케이션 정상 시작
- ✅ H2 인메모리 데이터베이스 연결 성공
- ✅ 테이블 자동 생성 (`create-drop` 모드)

### API 테스트
- ✅ `GET /api/v1/projects/templates` 정상 응답
- ✅ `POST /api/v1/projects` 정상 작동

### H2 콘솔
- ✅ `http://localhost:8080/h2-console` 접속 가능
- ✅ 데이터베이스 연결 및 쿼리 실행 가능

---

## 🚀 사용 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. H2 콘솔 접속
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **사용자명**: `sa`
- **비밀번호**: (비어있음)

### 3. 프로파일 확인
현재 활성 프로파일은 `local`이며, `application-local.properties`의 설정이 적용됩니다.

---

## ⚠️ 주의사항

1. **데이터 지속성**: H2 인메모리 데이터베이스는 애플리케이션 종료 시 모든 데이터가 삭제됩니다.
2. **프로덕션 환경**: 프로덕션 환경(`prod` 프로파일)에서는 여전히 MySQL을 사용합니다.
3. **Flyway 마이그레이션**: 로컬 환경에서는 비활성화되어 있지만, 프로덕션에서는 활성화됩니다.

---

## 📚 참고 자료

- [H2 Database Documentation](https://www.h2database.com/html/main.html)
- [Spring Boot H2 Console](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.h2-web-console)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)

---

## 🔄 향후 개선 사항

1. **테스트 데이터 초기화**: 애플리케이션 시작 시 샘플 데이터 자동 생성
2. **H2 파일 모드**: 필요시 파일 기반 H2 데이터베이스로 전환 가능
3. **프로파일별 설정 문서화**: 각 프로파일별 데이터베이스 설정 가이드 작성

---

**작성일**: 2025-12-17  
**작성자**: AI Assistant  
**검토 상태**: 완료

