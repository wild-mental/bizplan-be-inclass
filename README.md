# BizPlan 백엔드 - 예비 창업자를 위한 AI 코파일럿

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.10+-blue.svg)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-Latest-009688.svg)](https://fastapi.tiangolo.com/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

## 📖 개요

**BizPlan Backend**는 복잡한 사업계획서 작성을 데이터 기반의 의사결정 여정으로 바꾸어주는 지능형 백엔드 시스템입니다. 이 프로젝트는 예비 창업자가 정부지원금, 대출 등 다양한 자금 조달 관문을 빠르게 통과하고 본질적인 성장에 집중할 수 있도록 돕는 AI 파트너입니다.

### 비전

경험이 부족한 창업자라도 30분 이내에 전문가 수준의 제출용 사업계획서를 작성할 수 있도록 하여 창업 실패율을 낮추는 것.

### 주요 기능

- **접수 마법사**: 정부/은행용 양식을 100% 템플릿 호환으로 단계별 가이드 제공
- **재무 자동 엔진**: 주요 변수 입력만으로 3년 P&L 및 현금흐름표 자동 산출
- **AI 초안 작성기**: 문맥 기반 글쓰기 지원(Easy/Expert 모드 제공, Google Gemini 기반)
- **PMF 진단**: 표준 프레임워크로 제품-시장 적합성 및 위험 분석
- **문서 내보내기**: 클릭 한 번으로 표준 제출용 HWP/PDF 완성

### 대상 사용자

- **예비 창업자**: 비즈니스 논리와 용어에 어려움을 겪는 비전문가
- **소상공인**: 은행 대출이 필요하지만 재무 모델링에 익숙지 않은 사용자
- **연쇄 창업가**: 본격 확장 전 PMF·단위 경제성 검증이 필요한 분

## 🎯 성공 지표

| 지표 | 목표 |
|------|------|
| **Time-to-Value (TTV)** | 첫 제출용 초안까지 30분 이내 |
| **초기 활성화율** | 첫 세션 내 40% 이상 초안 완성 |
| **통과율** | 제출안 중 65% 이상 승인 |
| **마법사 지연** | 단계 전환 800ms 이하(p95) |
| **문서 생성** | 전체 초안 생성 10초 이하(p95) |

## 🏗️ 아키텍처

### 시스템 구성

```
┌─────────────────┐      REST API       ┌──────────────────────┐
│                 │◄────────────────────►│                      │
│  프론트엔드(SPA)│                      │  Spring Boot Core    │
│  React + Vite   │                      │  (비즈니스 로직)     │
│                 │                      │                      │
└─────────────────┘                      └──────────┬───────────┘
                                                    │
                                         ┌──────────▼───────────┐
                                         │                      │
                                         │   MySQL 데이터베이스  │
                                         │   (InnoDB, utf8mb4)  │
                                         │                      │
                                         └──────────────────────┘
                                                    
┌─────────────────┐      REST API       ┌──────────────────────┐
│  Spring Boot    │◄────────────────────►│  Python FastAPI      │
│  Core           │                      │  AI/문서 엔진        │
│                 │                      │  (LangChain)         │
└─────────────────┘                      └──────────────────────┘
```

### 설계 원칙

- **마이크로서비스 준비**: 코어 API와 AI 엔진은 REST 통신
- **관심사 분리**: 
  - AI 초안(LLM 기반, 창의적)
  - 재무 엔진(룰 기반, 결정적 - 환각 없음)
- **보안 우선**:
  - 모든 전송 구간 TLS 1.2+
  - 저장 데이터 AES-256 암호화
  - 워크스페이스별 데이터 격리
- **API-우선 설계**: 모든 서비스는 문서화된 REST API 제공

## 🛠️ 기술 스택

### 백엔드 코어 (Spring Boot)

- **언어**: Java 21 (LTS)
- **프레임워크**: Spring Boot 4.0.0
- **빌드 도구**: Gradle (Kotlin DSL)
- **DB**: MySQL 8.x (InnoDB, utf8mb4)
- **ORM**: Spring Data JPA (Hibernate)
- **테스트**: JUnit 5, Mockito, AssertJ

### AI & 문서 엔진 (Python)

- **언어**: Python 3.10+
- **프레임워크**: FastAPI
- **AI 오케스트레이션**: LangChain
- **LLM 공급자**: Google Gemini (내부 게이트웨이 사용)
- **테스트**: Pytest

### 인프라 & 도구

- **컨테이너화**: Docker, Docker Compose
- **API 문서화**: Swagger/OpenAPI 3.0
- **버전관리**: Git (Git Flow/Feature Branch)

## 🚀 시작하기

### 사전 필요사항

- Java 21 이상
- Gradle 8.x
- Python 3.10+
- MySQL 8.x
- Docker & Docker Compose (선택사항)

### 설치 절차

1. **저장소 클론**

```bash
git clone <repository-url>
cd bizplan-be-inclass
```

2. **환경변수 설정**

```bash
# 템플릿 파일 복사
cp .env.example .env

# 실제 값으로 입력
vim .env  # 또는 원하는 에디터 사용
```

`.env` 파일에는 아래 예시와 같은 값들을 작성해야 합니다:

```bash
# ============ DB 설정 ============
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bizplan
DB_USERNAME=root
DB_PASSWORD=your_password           # ⚠️ 필수

# ============ Spring Boot 설정 ============
SPRING_PROFILES_ACTIVE=local        # local | dev | prod
SERVER_PORT=8080

# ============ AI 엔진 설정 ============
GEMINI_API_KEY=your_gemini_api_key  # ⚠️ 필수
AI_ENGINE_URL=http://localhost:8001

# ============ 보안 설정 ============
JWT_SECRET=your_jwt_secret_min_32_chars      # ⚠️ 필수 (32자 이상)
ENCRYPTION_KEY=your_aes_256_key_32_chars     # ⚠️ 필수 (정확히 32자)
```

> ⚠️ **보안 안내**: `.env` 파일은 절대 버전 관리(Git)에 커밋하지 마세요. `.gitignore`에 제외되어 있습니다.

3. **Spring Boot 애플리케이션 빌드**

```bash
./gradlew clean build
```

4. **애플리케이션 실행**

```bash
./gradlew bootRun
```

애플리케이션은 `http://localhost:8080`에서 기동됩니다.

### Docker Compose 이용

```bash
docker-compose up -d
```

## 📁 프로젝트 구조

```
bizplan-be-inclass/
├── .cursor/              # Cursor IDE 규칙 및 설정
│   └── rules/           # 개발 가이드 및 표준
├── docs/                # 프로젝트 문서
├── gradle/              # Gradle wrapper
├── logs/                # 애플리케이션 로그 (자동 생성)
│   ├── gemini-usage.log              # 최신 Gemini 로그
│   └── gemini-usage.YYYY-MM-DD.N.log # 일별 회전 로그
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── vibe/bizplan/bizplan_be_inclass/
│   │   │       ├── controller/      # REST API 컨트롤러
│   │   │       ├── service/         # 비즈니스 로직
│   │   │       ├── repository/      # 데이터 접근 레이어
│   │   │       ├── entity/          # JPA 엔티티
│   │   │       ├── dto/             # 데이터 전송 객체
│   │   │       ├── config/          # 스프링 설정
│   │   │       ├── exception/       # 예외처리
│   │   │       └── util/            # 유틸리티
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-{profile}.properties
│   │       └── logback-spring.xml   # 로깅 설정
│   └── test/            # 테스트 코드
├── tasks/               # 요구사항 및 작업 관리
│   ├── functional/      # 기능 요구사항
│   └── non-functional/  # 비기능 요구사항
├── build.gradle         # Gradle 빌드 설정
└── README.md
```

## 🎨 개발 가이드라인

### 레이어드 아키텍처

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← HTTP/REST 인터페이스
│  (DTO, 요청/응답 처리)              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          Service Layer              │  ← 비즈니스 로직
│   (도메인 로직, 트랜잭션)           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Repository Layer             │  ← 데이터 접근
│    (Spring Data JPA, 엔티티)        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          데이터베이스 (MySQL)        │
└─────────────────────────────────────┘
```

### 코딩 표준

#### Java/Spring Boot

- **이름 규칙**:
  - 클래스: `PascalCase` (예: `UserService`)
  - 메서드/변수: `camelCase` (예: `findUserById`)
  - 상수: `UPPER_SNAKE_CASE` (예: `MAX_RETRY_COUNT`)
- **Lombok**: 보일러플레잇 최소화를 위해 `@Data`, `@Builder`, `@RequiredArgsConstructor` 활용
- **의존성 주입**: 생성자 주입(`@RequiredArgsConstructor`) 권장
- **DTO 패턴**: 컨트롤러에서는 절대 `@Entity` 노출 금지. 항상 DTO로 변환
- **예외 처리**: 전역 `@RestControllerAdvice` 및 `ProblemDetail` 활용
- **유효성 검증**: DTO에 `jakarta.validation` 어노테이션(`@NotNull`, `@Email` 등) 사용

#### Python/FastAPI

- **스타일 가이드**: PEP 8 준수
- **타입 힌트**: 모든 함수 인자/반환값에 엄격 적용
- **Docstring**: 모든 공개 함수/클래스에 Google 스타일 docstring
- **포매터**: `black`, `isort` 사용
- **비동기**: 라우트 핸들러 및 IO 작업은 `async def` 사용
- **유효성 검증**: 모든 요청/응답 스키마에 Pydantic v2 모델 사용

### API 설계 기준

#### 자원 이름 규칙

- **복수 명사** 사용: `/api/v1/projects`
- **kebab-case** 사용: `/api/v1/financial-models`
- URL에 동사를 사용하지 않고 HTTP 메소드로 의미 구분

#### 표준 응답 Envelope

**성공 시**
```json
{
  "success": true,
  "data": {
    "projectId": "123",
    "name": "My Business Plan"
  },
  "error": null
}
```

**실패 시**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_INPUT",
    "message": "Email is required"
  }
}
```

#### HTTP 상태 코드

- `200 OK`: 성공
- `201 Created`: 생성됨(POST)
- `400 Bad Request`: 유효성 실패
- `401 Unauthorized`: 인증 토큰 없음/유효하지 않음
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 존재하지 않는 리소스
- `500 Internal Server Error`: 서버 내부오류

### 테스트 전략

#### 단위 테스트

```java
@Test
void findUserById_whenUserExists_shouldReturnUser() {
    // 준비
    User user = User.builder().id(1L).name("John").build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    
    // 실행
    User result = userService.findUserById(1L);
    
    // 검증
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John");
}
```

#### 통합 테스트

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void createUser_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John\",\"email\":\"john@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}
```

#### Gemini 연동 테스트

**테스트 커버리지**
- **단위 테스트**: 19건 (BusinessPlanGenerationServiceTest)
- **통합 테스트**: 2건 (BusinessPlanGenerationServiceIntegrationTest)
- **Repository 테스트**: 4건 (BusinessPlanGenerationRepositoryTest)
- **총**: 25건 (~95% 커버리지)

**테스트 실행**
```bash
# 모든 Gemini 관련 테스트 실행 
./gradlew test --tests "*BusinessPlanGeneration*"

# 단위 테스트만 실행(통합 제외)
./gradlew test --tests "*BusinessPlanGeneration*Test" --exclude-tag integration

# 통합 테스트 실행(GEMINI_API_KEY 필요)
export GEMINI_API_KEY="your-api-key"
./gradlew test --tests "*BusinessPlanGenerationServiceIntegrationTest*"
```

**테스트 리포트**: 자세한 결과 및 커버리지는 [GEMINI_TEST_REPORT.md](./docs/GEMINI_TEST_REPORT.md) 참고

## 📊 데이터베이스 확인 (H2 콘솔)

### H2 콘솔 접속

로컬 개발 환경(`local` 프로필 사용)에서는 H2 임시 메모리 데이터베이스에 저장된 내용을 직접 확인할 수 있습니다.

#### 1. 애플리케이션 실행

```bash
./gradlew bootRun
```
애플리케이션은 `http://localhost:8080`에서 실행됩니다.

#### 2. H2 콘솔 접속

브라우저에서 다음 주소 진입:
```
http://localhost:8080/h2-console
```

#### 3. 접속 정보 입력

| 항목      | 값                       |
|-----------|--------------------------|
| **JDBC URL**  | `jdbc:h2:mem:testdb`      |
| **사용자명**  | `sa`                      |
| **비밀번호**  | (공란)                    |

> ⚠️ **주의**: H2 인메모리 DB는 애플리케이션이 종료되면 데이터가 모두 사라집니다.

#### 4. 주요 데이터 조회 예시

##### 사업계획서 목록 조회

```sql
SELECT * FROM business_plans ORDER BY created_at DESC;
```

##### 특정 사업계획서 조회

```sql
SELECT * FROM business_plans WHERE business_plan_id = 'bp-2025-12-20-xxxxx';
SELECT * FROM business_plans WHERE project_id = 'your-project-uuid';
SELECT * FROM business_plans WHERE user_id = 'your-user-id';
SELECT * FROM business_plans WHERE template_type = 'pre-startup';
```

##### 요청 데이터 미리보기

```sql
SELECT 
    business_plan_id,
    template_type,
    SUBSTRING(request_data_json, 1, 200) AS request_data_preview,
    created_at
FROM business_plans
ORDER BY created_at DESC;
```

##### 응답 섹션 데이터 미리보기

```sql
SELECT 
    business_plan_id,
    SUBSTRING(response_sections_json, 1, 500) AS sections_preview,
    created_at
FROM business_plans
ORDER BY created_at DESC;
```

##### Gemini 메타데이터

```sql
SELECT 
    business_plan_id,
    JSON_EXTRACT(gemini_metadata_json, '$.promptTokens') AS prompt_tokens,
    JSON_EXTRACT(gemini_metadata_json, '$.completionTokens') AS completion_tokens,
    JSON_EXTRACT(gemini_metadata_json, '$.totalTokens') AS total_tokens,
    JSON_EXTRACT(gemini_metadata_json, '$.tokensPerSecond') AS tokens_per_second,
    JSON_EXTRACT(gemini_metadata_json, '$.durationMs') AS duration_ms,
    created_at
FROM business_plans
ORDER BY created_at DESC;
```

##### 통계 쿼리

```sql
-- 템플릿별 생성 건수
SELECT 
    template_type,
    COUNT(*) AS count,
    AVG(CAST(JSON_EXTRACT(gemini_metadata_json, '$.totalTokens') AS INT)) AS avg_tokens
FROM business_plans
GROUP BY template_type;

-- 일별 생성 건수
SELECT 
    DATE(created_at) AS date,
    COUNT(*) AS count
FROM business_plans
GROUP BY DATE(created_at)
ORDER BY date DESC;

-- 평균 토큰 사용량
SELECT 
    AVG(CAST(JSON_EXTRACT(gemini_metadata_json, '$.totalTokens') AS INT)) AS avg_total_tokens,
    AVG(CAST(JSON_EXTRACT(gemini_metadata_json, '$.durationMs') AS BIGINT)) AS avg_duration_ms,
    AVG(CAST(JSON_EXTRACT(gemini_metadata_json, '$.tokensPerSecond') AS DOUBLE)) AS avg_throughput
FROM business_plans;
```

#### 5. DB 스키마 확인

```sql
SHOW TABLES;
DESCRIBE business_plans;
SHOW INDEX FROM business_plans;
```

### 유의 사항

1. **데이터 지속성**: 인메모리 DB이므로 재기동시 데이터 소멸
2. **운영 환경**: 운영 환경(`prod` 프로필)에서는 MySQL만 사용, H2 콘솔 비활성화
3. **보안**: H2 콘솔은 로컬 개발환경에서만 활성화, 운영환경에서는 차단

### 참고 문서

- [H2 공식문서](https://www.h2database.com/html/main.html)
- [MYSQL_TO_H2_MIGRATION.md](./docs/MYSQL_TO_H2_MIGRATION.md)

---

## 📝 최근 업데이트

### 2025-12-20: 데이터베이스 저장 기능 구현

#### 주요 변경 사항

**사업계획서 생성 요청/응답 DB 저장**
- ✅ `BusinessPlan` 엔티티 생성 (요청, 응답, Gemini 메타데이터 저장)
- ✅ `BusinessPlanRepository` (Spring Data JPA)
- ✅ Flyway 마이그레이션(`V2__create_business_plans_table.sql`)
- ✅ `BusinessPlanGenerationService`에 DB저장 로직 추가
- ✅ DTO ↔ Entity 변환 메서드(Service 레이어)
- ✅ 3티어 구조 규칙 문서화 (`.cursor/rules/306-three-tier-architecture-rules.mdc`)

#### 저장 데이터

1. **요청 데이터** (`request_data_json`): BusinessPlanGenerateRequest 전체(JSON)
2. **응답 데이터** (`response_sections_json`): 생성된 섹션들(JSON)
3. **Gemini 메타데이터** (`gemini_metadata_json`): 토큰·시간 정보(JSON)

#### DB 스키마

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

#### 3-Tier 구조 준수

- **Controller**: HTTP 요청/응답 처리, DTO만 사용
- **Service**: 비즈니스 로직, DTO↔Entity 변환, 트랜잭션 관리
- **Repository**: 데이터 접근, Entity만 사용

더 자세한 내용은 [GEMINI_INTEGRATION_SUMMARY.md](./docs/GEMINI_INTEGRATION_SUMMARY.md) 참고

---

## 🔒 보안

### 환경변수 관리

계층별로 환경변수 안전하게 관리:

| 환경 | 방법 | 설명 |
|------|------|------|
| **로컬 개발** | `.env` 파일 | `.gitignore`로 제외, 각자 관리 |
| **CI/CD** | GitHub Secrets | 파이프라인에 환경변수 주입 |
| **스테이징/운영** | 서버 환경변수 | Docker Compose/클라우드 Secret |

#### 파일 구조 예시

```
bizplan-be-inclass/
├── .env.example                    # ✅ Git 포함(템플릿)
├── .env                            # ❌ Git 제외(실제값)
├── src/main/resources/
│   ├── application.properties      # ✅ 환경변수 참조
│   └── application-local.properties # ❌ Git 제외
└── ai-engine/
    ├── .env.example                # ✅ Git 포함
    └── .env                        # ❌ Git 제외
```

#### 필수 환경변수

| 변수명 | 설명 | 필수여부 |
|--------|------|:--------:|
| `DB_PASSWORD` | MySQL 비밀번호 | ✅ |
| `GEMINI_API_KEY` | Google Gemini API키 | ✅ |
| `JWT_SECRET` | JWT 서명키(32자 이상) | ✅ |
| `ENCRYPTION_KEY` | AES-256 암호키(32자) | ✅ |

### 데이터 보호

- **저장 데이터 암호화**: 민감정보(AI, 재무 등)는 AES-256로 암호화
- **전송 암호화**: 모든 API 통신 TLS 1.2+ 강제
- **데이터 격리**: 워크스페이스별 데이터 완전 분리
- **인증**: JWT 기반 인증
- **인가**: 역할 기반 접근 제어(RBAC)

### 모범 사례

- `.env`는 절대 커밋 금지
- `.env.example` 템플릿만 유지(실제 값 X)
- 민감데이터(비번, 토큰, 금융정보) 로그 남기지 말 것
- SQL 인젝션 방지: 파라미터 바인딩 필수
- 모든 입력 검증·정제
- API에 레이트 리미팅 적용
- 주기적 보안 점검 및 의존성 업데이트

## 📚 문서화

- **API 문서**: 실행시 `/swagger-ui.html`
- **프로젝트 규칙**: `.cursor/rules/` 폴더
- **과제 트래킹**: `tasks/` 폴더
- **아키텍처 결정**: `docs/` 폴더

### 주요 문서

#### 프로젝트 가이드
- [프로젝트 개요](.cursor/rules/001-project-overview.mdc)
- [기술 스택](.cursor/rules/002-tech-stack.mdc)
- [개발 가이드](.cursor/rules/003-development-guidelines.mdc)
- [에러 해결 프로세스](.cursor/rules/100-error-fixing-process.mdc)
- [Git 커밋 가이드](.cursor/rules/200-git-commit-push-pr.mdc)

#### Gemini 통합 문서
- [Gemini 통합 요약](./docs/GEMINI_INTEGRATION_SUMMARY.md)
- [Gemini 테스트 리포트](./docs/GEMINI_TEST_REPORT.md)
- [Gemini 통합 TODO](./docs/GEMINI_INTEGRATION_TODO.md)

## 🤝 기여 방법

### Git 워크플로우

**Git Flow**와 기능 브랜치 전략 사용:

```bash
# 기능 브랜치 생성
git checkout -b feature/ISSUE-123-add-user-authentication

# 변경 및 커밋
git add .
git commit -m "feat: add JWT authentication service"

# 푸시 및 PR 생성
git push origin feature/ISSUE-123-add-user-authentication
```

### 커밋 메시지 규약

**Conventional Commits** 형식 준수:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**타입 종류**
- `feat`: 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 스타일(포맷) 변경
- `refactor`: 리팩토링
- `test`: 테스트 코드
- `chore`: 기타 유지보수

**예시**
```
feat(auth): implement JWT authentication

- Add JwtTokenProvider for token generation
- Create AuthenticationFilter for request validation
- Add user authentication endpoints

Closes #123
```

## 📊 성능 요건

### 비기능 요구사항

- **마법사 지연**: 단계 전환 800ms 미만(p95)
- **문서 생성**: 초안생성 10초 미만(p95)
- **가용성**: 99.5% 이상 SLA
- **확장성**: 동시 사용자 1000명 이상 지원
- **DB 성능**: 쿼리 응답 100ms 미만(p95)

## 📈 모니터링 & 분석

### Gemini 사용 로그 구조

Gemini API 사용 내역은 콘솔과 파일(`logs/gemini-usage.log`)에 모두 기록되어 실시간 비용 추적과 분석이 쉽습니다.

#### 로그 파일 위치

- **현재 로그**: `logs/gemini-usage.log`
- **보관 로그**: `logs/gemini-usage.YYYY-MM-DD.N.log` (일일 회전, 최대 30일 100MB)

#### 로그 포맷

**파일 형식(CSV 유사)**
```
2025-12-18 14:30:19.500,[Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

**로그 필드 설명**

| 필드          | 설명                             | 예시                       |
|---------------|----------------------------------|----------------------------|
| `StartTime`   | Gemini API 호출 시작(ISO 8601)   | `2025-12-18T14:30:15.123Z` |
| `EndTime`     | Gemini 호출 종료(ISO 8601)       | `2025-12-18T14:30:19.500Z` |
| `Duration`    | 경과 시간(ms)                    | `4377ms`                   |
| `Input`       | 입력 토큰(프롬프트)              | `1234`                     |
| `Output`      | 출력 토큰(완성)                  | `5678`                     |
| `Total`       | 총 토큰 사용량                   | `6912`                     |
| `Throughput`  | 토큰 처리 속도(토큰/초)          | `1578.25 tokens/sec`       |

**레포지토리 로그 추가 필드(`businessPlanId`)**
```
2025-12-18 14:30:19.501,[Gemini Usage Log] businessPlanId=bp-2025-12-18-550e8400, StartTime: ..., ...
```

### 비용 추적 예시

#### 일일 사용량

```bash
# 오늘 토큰 총합
grep "Total:" logs/gemini-usage.log | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "Total tokens:", sum}'
```

#### 일일 비용 계산(예시)

```bash
# input 1천당 $0.000125, output 1천당 $0.0005 기준
grep "Total:" logs/gemini-usage.log | \
  awk -F'Input: ' '{print $2}' | \
  awk -F',' '{input+=$1; output+=$2} END {
    input_cost = (input / 1000) * 0.000125;
    output_cost = (output / 1000) * 0.0005;
    print "Estimated daily cost: $" (input_cost + output_cost)
  }'
```

#### 일별 요청 수

```bash
grep -c "\[Gemini Usage Log\]" logs/gemini-usage.log
```

#### 주간 사용량

```bash
find logs/ -name "gemini-usage.*.log" -mtime -7 -exec grep "Total:" {} \; | \
  awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "주간 전체 토큰:", sum}'
```

#### 주간 평균 일별 사용량

```bash
for i in {0..6}; do
  date=$(date -v-${i}d +%Y-%m-%d 2>/dev/null || date -d "${i} days ago" +%Y-%m-%d)
  tokens=$(grep "Total:" logs/gemini-usage.${date}.*.log 2>/dev/null | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print sum}')
  echo "${date}: ${tokens:-0} tokens"
done | awk '{sum+=$2; count++} END {print "Average daily tokens:", sum/count}'
```

#### 월간 토큰 집계

```bash
find logs/ -name "gemini-usage.*.log" -newermt "$(date +%Y-%m-01)" -exec grep "Total:" {} \; | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "월간 전체 토큰:", sum}'
```

### 성능 모니터링

#### 응답 시간 분석

```bash
grep "Duration:" logs/gemini-usage.log | awk -F'Duration: ' '{print $2}' | awk -F'ms' '{sum+=$1; count++} END {print "평균:", sum/count, "ms"}'
```

#### 응답 시간 퍼센타일

```bash
grep "Duration:" logs/gemini-usage.log | awk -F'Duration: ' '{print $2}' | awk -F'ms' '{print $1}' | sort -n | awk '{
    arr[NR] = $1
  } END {
    p50 = arr[int(NR*0.50)]
    p95 = arr[int(NR*0.95)]
    p99 = arr[int(NR*0.99)]
    print "p50:", p50 "ms"
    print "p95:", p95 "ms"
    print "p99:", p99 "ms"
  }'
```

#### 처리 속도(Throughput)

```bash
grep "Throughput:" logs/gemini-usage.log | awk -F'Throughput: ' '{print $2}' | awk '{sum+=$1; count++} END {print "평균 처리속도:", sum/count, "tokens/sec"}'
```

#### 시간대별 요청 분석

```bash
cut -d',' -f1 logs/gemini-usage.log | cut -d' ' -f2 | cut -d':' -f1 | sort | uniq -c | awk '{print $2 ":00 - " $1 " requests"}'
```

### 고급 분석 스크립트

`scripts/analyze-gemini-usage.sh` 스크립트로 종합 분석:
```bash
chmod +x scripts/analyze-gemini-usage.sh
./scripts/analyze-gemini-usage.sh
```
> 자세한 분석 예시는 위 스크립트와 [Monitoring & Analytics](#-monitoring--analytics) 참고

### 로그 파일 관리 정책

- **일별 + 크기별(10MB)** 회전
- **30일** 최대 보관
- **총 100MB** 초과시 자동삭제 (가장 오래된 것부터)
- Git에서는 logs 폴더 제외

### 외부 모니터링 툴 연동

- **Grafana**: LogQL 쿼리로 시각화
- **ELK Stack**: Logstash로 파싱
- **Prometheus**: Exporter 추가로 연계
- **커스텀 대시보드**: pandas 등 사용해서 분석

자세한 내용은 다음 문서 참고:
- [GEMINI_INTEGRATION_SUMMARY.md](./docs/GEMINI_INTEGRATION_SUMMARY.md)
- [GEMINI_TEST_REPORT.md](./docs/GEMINI_TEST_REPORT.md)
- [GEMINI_INTEGRATION_TODO.md](./docs/GEMINI_INTEGRATION_TODO.md)

## 🔧 운영 및 유지보수

### 로그 관리

- **로그 위치**: `logs/gemini-usage.log`
- **정책**: 일기준 + 크기별 회전, 30일 보관, 100MB 한도
- **형식**: CSV 유사, shell 분석 용이
- **빠른 조회**
```bash
# 오늘 토큰 합계
grep "$(date +%Y-%m-%d)" logs/gemini-usage.log | grep "Total:" | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "Total tokens:", sum}'

# 평균 응답 시간
grep "Duration:" logs/gemini-usage.log | awk -F'Duration: ' '{print $2}' | awk -F'ms' '{sum+=$1; count++} END {print "Average:", sum/count, "ms"}'
```
- 자세한 분석 및 비용 추적은 위 섹션/스크립트 참고

### 헬스 체크

```bash
# 서비스 상태
curl http://localhost:8080/actuator/health
open http://localhost:8080/swagger-ui.html

# DB 확인
mysql -u root -p -e "SELECT 1" bizplan

# Gemini API 헬스 및 오류
tail -n 10 logs/gemini-usage.log
grep -i "error\|exception\|failed" logs/gemini-usage.log
```

### 백업 및 복구

- **로그**: 30일간 자동 보관, 영구 보관 필요시 아래 스크립트로 백업
```bash
find logs/ -name "gemini-usage.*.log" -mtime +30 -exec gzip {} \;
```
- **DB**: MySQL 정기 백업 권장, Flyway 마이그레이션으로 스키마 관리
- **설정**: `.env` 파일은 Git 제외, `.env.example`은 템플릿 관리

## 🐛 문제 해결

### 자주 발생하는 에러

**"Java version mismatch" 빌드 오류**
```bash
java -version  # Java 21인지 확인
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**DB 연결 오류**
```bash
mysql -u root -p
# .env 내 인증정보 재확인
CREATE DATABASE IF NOT EXISTS bizplan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**포트 충돌(8080 사용중)**
```bash
lsof -i :8080
kill -9 <PID>
```

**Gemini API 오류**
```bash
echo $GEMINI_API_KEY
grep -i "error\|exception" logs/gemini-usage.log | tail -20
curl -H "Content-Type: application/json" \
  -d '{"contents":[{"parts":[{"text":"test"}]}]}' \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=${GEMINI_API_KEY}"
```

**ObjectMapper bean 오류**
```bash
# JacksonConfig로 해결됨
grep -r "JacksonConfig" src/main/java/
```

## 📞 지원 문의

- **GitHub Issues**: [이슈 등록](../../issues)
- **문서**: `docs/` 폴더 참고
- **개발가이드**: `.cursor/rules/` 폴더 참고

## 📄 라이선스

Proprietary - All rights reserved.

## 🙏 감사의 글

- 훌륭한 프레임워크를 제공한 Spring Boot팀
- 고성능 Python 웹프레임워크 FastAPI팀
- Gemini LLM 기술의 Google
- 모든 프로젝트 기여자분들

---

**예비 창업자를 위해 ❤️ 으로 만듭니다**
