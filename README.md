# BizPlan Backend - AI Co-Pilot for First-time Founders

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.10+-blue.svg)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-Latest-009688.svg)](https://fastapi.tiangolo.com/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

## 📖 Overview

**BizPlan Backend** is an intelligent backend system that transforms the complex business planning process into a data-driven decision-making journey. This project acts as an intelligent partner to help first-time founders quickly pass funding gates (government grants, loans) and focus on sustainable growth.

### Vision

Reduce business failure rates by helping founders with minimal expertise create professional, submission-ready business plans in under 30 minutes.

### Key Features

- **Submission Wizard**: Step-by-step guide for government/bank forms with 100% template compatibility
- **Financial Auto-Engine**: Generates 3-year P&L and cash flow projections from key variables
- **AI Drafting**: Context-aware writing assistance with Easy/Expert modes (powered by Google Gemini)
- **PMF Diagnostic**: Analyzes product-market fit and risks using standard frameworks
- **Document Export**: One-click HWP/PDF export fully compliant with submission standards

### Target Audience

- **First-time Founders**: Non-technical entrepreneurs struggling with business logic and jargon
- **Small Business Owners**: Seeking bank loans but lacking financial modeling expertise
- **Serial Entrepreneurs**: Need to verify PMF and unit economics before scaling

## 🎯 Success Metrics

| Metric | Target |
|--------|--------|
| **Time-to-Value (TTV)** | < 30 mins to first submit-ready draft |
| **Activation Rate** | > 40% users complete draft in first session |
| **Pass Rate** | > 65% acceptance for submitted proposals |
| **Wizard Latency** | < 800ms step transitions (p95) |
| **Doc Generation** | < 10s full draft creation (p95) |

## 🏗️ Architecture

### System Design

```
┌─────────────────┐      REST API       ┌──────────────────────┐
│                 │◄────────────────────►│                      │
│  Frontend (SPA) │                      │  Spring Boot Core    │
│  React + Vite   │                      │  (Business Logic)    │
│                 │                      │                      │
└─────────────────┘                      └──────────┬───────────┘
                                                    │
                                         ┌──────────▼───────────┐
                                         │                      │
                                         │   MySQL Database     │
                                         │   (InnoDB, utf8mb4)  │
                                         │                      │
                                         └──────────────────────┘
                                                    
┌─────────────────┐      REST API       ┌──────────────────────┐
│  Spring Boot    │◄────────────────────►│  Python FastAPI      │
│  Core           │                      │  AI/Doc Engine       │
│                 │                      │  (LangChain)         │
└─────────────────┘                      └──────────────────────┘
```

### Architecture Principles

- **Micro-Service Ready**: Core API and AI Engine communicate via REST
- **Separation of Concerns**: 
  - AI Drafting (LLM-based, creative)
  - Financial Engine (Rule-based, deterministic - NO hallucinations)
- **Security First**:
  - TLS 1.2+ for all transit
  - AES-256 for sensitive data at rest
  - Workspace-level data isolation
- **API-First Design**: All services expose well-documented REST APIs

## 🛠️ Tech Stack

### Backend Core (Spring Boot)

- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 4.0.0
- **Build Tool**: Gradle (Kotlin DSL)
- **Database**: MySQL 8.x (InnoDB, utf8mb4)
- **ORM**: Spring Data JPA (Hibernate)
- **Testing**: JUnit 5, Mockito, AssertJ

### AI & Document Engine (Python)

- **Language**: Python 3.10+
- **Framework**: FastAPI
- **AI Orchestration**: LangChain
- **LLM Provider**: Google Gemini (via Internal Gateway)
- **Testing**: Pytest

### Infrastructure & Tools

- **Containerization**: Docker, Docker Compose
- **API Documentation**: Swagger/OpenAPI 3.0
- **Version Control**: Git (Git Flow / Feature Branch Workflow)

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 8.x
- Python 3.10+
- MySQL 8.x
- Docker & Docker Compose (optional)

### Installation

1. **Clone the repository**

```bash
git clone <repository-url>
cd bizplan-be-inclass
```

2. **Set up environment variables**

```bash
# Copy the template file
cp .env.example .env

# Edit with your actual values
vim .env  # or use your preferred editor
```

The `.env` file should contain:

```bash
# ============ Database Configuration ============
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bizplan
DB_USERNAME=root
DB_PASSWORD=your_password           # ⚠️ Required

# ============ Spring Boot Configuration ============
SPRING_PROFILES_ACTIVE=local        # local | dev | prod
SERVER_PORT=8080

# ============ AI Engine Configuration ============
GEMINI_API_KEY=your_gemini_api_key  # ⚠️ Required
AI_ENGINE_URL=http://localhost:8001

# ============ Security Configuration ============
JWT_SECRET=your_jwt_secret_min_32_chars      # ⚠️ Required (min 32 chars)
ENCRYPTION_KEY=your_aes_256_key_32_chars     # ⚠️ Required (exactly 32 chars)
```

> ⚠️ **Security Notice**: Never commit `.env` files to version control. The `.gitignore` is configured to exclude these files.

3. **Build the Spring Boot application**

```bash
./gradlew clean build
```

4. **Run the application**

```bash
./gradlew bootRun
```

The application will start at `http://localhost:8080`

### Using Docker Compose

```bash
docker-compose up -d
```

## 📁 Project Structure

```
bizplan-be-inclass/
├── .cursor/              # Cursor IDE rules and configurations
│   └── rules/           # Development guidelines and standards
├── docs/                # Project documentation
├── gradle/              # Gradle wrapper
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── vibe/bizplan/bizplan_be_inclass/
│   │   │       ├── controller/      # REST API Controllers
│   │   │       ├── service/         # Business Logic Layer
│   │   │       ├── repository/      # Data Access Layer
│   │   │       ├── entity/          # JPA Entities
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── config/          # Spring Configuration
│   │   │       ├── exception/       # Exception Handling
│   │   │       └── util/            # Utility Classes
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-{profile}.properties
│   └── test/            # Test files
├── tasks/               # Task tracking and requirements
│   ├── functional/      # Functional requirements
│   └── non-functional/  # Non-functional requirements
├── build.gradle         # Gradle build configuration
└── README.md
```

## 🎨 Development Guidelines

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← HTTP/REST Interface
│  (DTOs, Request/Response Handling)  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          Service Layer              │  ← Business Logic
│   (Domain Logic, Transactions)      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Repository Layer             │  ← Data Access
│    (Spring Data JPA, Entities)      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          Database (MySQL)           │
└─────────────────────────────────────┘
```

### Coding Standards

#### Java/Spring Boot

- **Naming Conventions**:
  - Classes: `PascalCase` (e.g., `UserService`)
  - Methods/Variables: `camelCase` (e.g., `findUserById`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
- **Lombok**: Use `@Data`, `@Builder`, `@RequiredArgsConstructor` to reduce boilerplate
- **Dependency Injection**: Use Constructor Injection (via `@RequiredArgsConstructor`)
- **DTO Pattern**: NEVER expose `@Entity` in Controllers. Always map to/from DTOs
- **Exception Handling**: Use global `@RestControllerAdvice` with `ProblemDetail`
- **Validation**: Use `jakarta.validation` annotations (`@NotNull`, `@Email`) in DTOs

#### Python/FastAPI

- **Style Guide**: Follow PEP 8
- **Type Hinting**: Strict type hints required for all function arguments and return values
- **Docstrings**: Use Google-style docstrings for all public functions/classes
- **Formatter**: Use `black` and `isort`
- **Async**: Use `async def` for route handlers and I/O bound operations
- **Validation**: Use Pydantic v2 models for all request/response schemas

### API Design Standards

#### Resource Naming

- Use **Plural Nouns**: `/api/v1/projects` (not `/api/v1/project`)
- Use **Kebab-case**: `/api/v1/financial-models`
- Avoid verbs in URLs (use HTTP methods instead)

#### Standard Response Envelope

**Success Response:**
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

**Error Response:**
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

#### HTTP Status Codes

- `200 OK`: Success
- `201 Created`: Resource created (POST)
- `400 Bad Request`: Validation error
- `401 Unauthorized`: Missing/Invalid token
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource does not exist
- `500 Internal Server Error`: Unexpected server error

### Testing Strategy

#### Unit Tests

```java
@Test
void findUserById_whenUserExists_shouldReturnUser() {
    // Arrange
    User user = User.builder().id(1L).name("John").build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    
    // Act
    User result = userService.findUserById(1L);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John");
}
```

#### Integration Tests

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

## 🔒 Security

### Environment Variable Management

환경변수는 계층별로 안전하게 관리됩니다:

| 환경 | 방법 | 설명 |
|------|------|------|
| **로컬 개발** | `.env` 파일 | `.gitignore`에서 제외, 개발자 로컬 관리 |
| **CI/CD** | GitHub Secrets | 파이프라인에서 환경변수로 주입 |
| **스테이징/프로덕션** | 서버 환경변수 | Docker Compose 또는 클라우드 시크릿 |

#### 파일 구조

```
bizplan-be-inclass/
├── .env.example                    # ✅ Git 포함 (템플릿)
├── .env                            # ❌ Git 제외 (실제 값)
├── src/main/resources/
│   ├── application.properties      # ✅ 환경변수 참조
│   └── application-local.properties # ❌ Git 제외 (로컬 설정)
└── ai-engine/
    ├── .env.example                # ✅ Git 포함 (템플릿)
    └── .env                        # ❌ Git 제외 (실제 값)
```

#### 필수 환경변수

| Variable | Description | Required |
|----------|-------------|:--------:|
| `DB_PASSWORD` | MySQL 데이터베이스 비밀번호 | ✅ |
| `GEMINI_API_KEY` | Google Gemini API 키 | ✅ |
| `JWT_SECRET` | JWT 토큰 서명 키 (32자 이상) | ✅ |
| `ENCRYPTION_KEY` | AES-256 암호화 키 (32자) | ✅ |

### Data Protection

- **Encryption at Rest**: AES-256 for sensitive fields (business ideas, financial data)
- **Encryption in Transit**: TLS 1.2+ mandatory for all API communications
- **Data Isolation**: Strict workspace-level separation of user data
- **Authentication**: JWT-based authentication
- **Authorization**: Role-based access control (RBAC)

### Best Practices

- Never commit `.env` files to version control
- Use `.env.example` as a template (no real values)
- Never log sensitive data (passwords, tokens, financial details)
- Use parameterized queries to prevent SQL injection
- Validate and sanitize all user inputs
- Implement rate limiting on API endpoints
- Regular security audits and dependency updates

## 📚 Documentation

- **API Documentation**: Available at `/swagger-ui.html` when running
- **Project Rules**: See `.cursor/rules/` directory
- **Task Tracking**: See `tasks/` directory
- **Architecture Decisions**: See `docs/` directory

### Key Documents

- [Project Overview](.cursor/rules/001-project-overview.mdc)
- [Tech Stack](.cursor/rules/002-tech-stack.mdc)
- [Development Guidelines](.cursor/rules/003-development-guidelines.mdc)
- [Error Fixing Process](.cursor/rules/100-error-fixing-process.mdc)
- [Git Commit Guidelines](.cursor/rules/200-git-commit-push-pr.mdc)

## 🤝 Contributing

### Git Workflow

We follow **Git Flow** with feature branches:

```bash
# Create a feature branch
git checkout -b feature/ISSUE-123-add-user-authentication

# Make changes and commit
git add .
git commit -m "feat: add JWT authentication service"

# Push and create Pull Request
git push origin feature/ISSUE-123-add-user-authentication
```

### Commit Message Convention

Follow **Conventional Commits**:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation only
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance tasks

**Example:**
```
feat(auth): implement JWT authentication

- Add JwtTokenProvider for token generation
- Create AuthenticationFilter for request validation
- Add user authentication endpoints

Closes #123
```

## 📊 Performance Requirements

### Non-Functional Requirements

- **Wizard Latency**: Step transitions < 800ms (p95)
- **Document Generation**: Full draft creation < 10s (p95)
- **Availability**: 99.5% uptime SLA
- **Scalability**: Support 1000+ concurrent users
- **Database Performance**: Query response < 100ms (p95)

## 🐛 Troubleshooting

### Common Issues

**Build fails with "Java version mismatch"**
```bash
# Check Java version
java -version  # Should be 21

# Set JAVA_HOME if needed
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**Database connection error**
```bash
# Verify MySQL is running
mysql -u root -p

# Check credentials in .env file
# Ensure database exists
CREATE DATABASE IF NOT EXISTS bizplan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Port already in use**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

## 📞 Support

For issues, questions, or contributions:

- **GitHub Issues**: [Create an issue](../../issues)
- **Documentation**: See `docs/` directory
- **Development Guidelines**: See `.cursor/rules/` directory

## 📄 License

Proprietary - All rights reserved.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- FastAPI team for the high-performance Python framework
- Google for Gemini LLM capabilities
- All contributors to this project

---

**Built with ❤️ for First-time Founders**

