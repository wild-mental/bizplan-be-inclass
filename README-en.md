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
                                         │   SQLite Database    │
                                         │   (Local/Prod/Test)  │
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
- **Database**: SQLite (used for local/production/test environments)
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
- SQLite 3.x (included by default)
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
├── logs/                # Application logs (auto-generated)
│   ├── gemini-usage.log              # Current Gemini usage log
│   └── gemini-usage.YYYY-MM-DD.N.log # Rolled logs (daily rotation)
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
│   │       ├── application-{profile}.properties
│   │       └── logback-spring.xml   # Logging configuration
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
│          Database (SQLite)          │
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

#### Gemini Integration Tests

**Test Coverage**:
- **Unit Tests**: 19 tests (BusinessPlanGenerationServiceTest)
- **Integration Tests**: 2 tests (BusinessPlanGenerationServiceIntegrationTest)
- **Repository Tests**: 4 tests (BusinessPlanGenerationRepositoryTest)
- **Total**: 25 tests with ~95% code coverage

**Running Tests**:
```bash
# Run all Gemini-related tests
./gradlew test --tests "*BusinessPlanGeneration*"

# Run unit tests only (exclude integration tests)
./gradlew test --tests "*BusinessPlanGeneration*Test" --exclude-tag integration

# Run integration tests (requires GEMINI_API_KEY)
export GEMINI_API_KEY="your-api-key"
./gradlew test --tests "*BusinessPlanGenerationServiceIntegrationTest*"
```

**Test Report**: See [GEMINI_TEST_REPORT.md](./docs/GEMINI_TEST_REPORT.md) for detailed test results and coverage analysis.

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
| `GEMINI_API_KEY` | Google Gemini API Key | ✅ |
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

#### Project Guidelines
- [Project Overview](.cursor/rules/001-project-overview.mdc)
- [Tech Stack](.cursor/rules/002-tech-stack.mdc)
- [Development Guidelines](.cursor/rules/003-development-guidelines.mdc)
- [Error Fixing Process](.cursor/rules/100-error-fixing-process.mdc)
- [Git Commit Guidelines](.cursor/rules/200-git-commit-push-pr.mdc)

#### Gemini Integration Documentation
- [Gemini Integration Summary](./docs/GEMINI_INTEGRATION_SUMMARY.md) - Complete integration guide, architecture, and logging structure
- [Gemini Test Report](./docs/GEMINI_TEST_REPORT.md) - Test coverage, results, and quality metrics
- [Gemini Integration TODO](./docs/GEMINI_INTEGRATION_TODO.md) - Future improvements and enhancement suggestions

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

## 📈 Monitoring & Analytics

### Gemini Usage Log Schema

The application logs all Gemini API calls to both console and file (`logs/gemini-usage.log`) for comprehensive monitoring and cost tracking.

#### Log File Location

- **Current log**: `logs/gemini-usage.log`
- **Rolled logs**: `logs/gemini-usage.YYYY-MM-DD.N.log` (daily rotation, max 30 days, 100MB total)

#### Log Format

**File Format (CSV-like)**:
```
2025-12-18 14:30:19.500,[Gemini Usage Log] StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

**Field Descriptions**:

| Field | Description | Example |
|-------|-------------|---------|
| `StartTime` | Gemini API call start time (ISO 8601) | `2025-12-18T14:30:15.123Z` |
| `EndTime` | Gemini API call end time (ISO 8601) | `2025-12-18T14:30:19.500Z` |
| `Duration` | Elapsed time in milliseconds | `4377ms` |
| `Input` | Input tokens (prompt tokens) | `1234` |
| `Output` | Output tokens (completion tokens) | `5678` |
| `Total` | Total tokens consumed | `6912` |
| `Throughput` | Token processing rate (tokens/sec) | `1578.25 tokens/sec` |

**Repository Log Format** (includes `businessPlanId`):
```
2025-12-18 14:30:19.501,[Gemini Usage Log] businessPlanId=bp-2025-12-18-550e8400, StartTime: 2025-12-18T14:30:15.123Z, EndTime: 2025-12-18T14:30:19.500Z, Duration: 4377ms, Input: 1234, Output: 5678, Total: 6912, Throughput: 1578.25 tokens/sec
```

### Cost Tracking

#### Daily Cost Analysis

**Total Daily Token Usage**:
```bash
# Extract total tokens from log file
grep "Total:" logs/gemini-usage.log | \
  awk -F'Total: ' '{print $2}' | \
  awk -F',' '{sum+=$1} END {print "Total tokens:", sum}'
```

**Daily Cost Estimation** (assuming Gemini pricing):
```bash
# Calculate cost (example: $0.000125 per 1K input tokens, $0.0005 per 1K output tokens)
grep "Total:" logs/gemini-usage.log | \
  awk -F'Input: ' '{print $2}' | \
  awk -F',' '{input+=$1; output+=$2} END {
    input_cost = (input / 1000) * 0.000125;
    output_cost = (output / 1000) * 0.0005;
    print "Estimated daily cost: $" (input_cost + output_cost)
  }'
```

**Daily Request Count**:
```bash
# Count requests per day
grep -c "\[Gemini Usage Log\]" logs/gemini-usage.log
```

#### Weekly Cost Analysis

**Weekly Token Usage**:
```bash
# Aggregate tokens for the last 7 days
find logs/ -name "gemini-usage.*.log" -mtime -7 -exec grep "Total:" {} \; | \
  awk -F'Total: ' '{print $2}' | \
  awk -F',' '{sum+=$1} END {print "Weekly total tokens:", sum}'
```

**Weekly Average Daily Cost**:
```bash
# Calculate average daily cost over the week
for i in {0..6}; do
  date=$(date -v-${i}d +%Y-%m-%d 2>/dev/null || date -d "${i} days ago" +%Y-%m-%d)
  tokens=$(grep "Total:" logs/gemini-usage.${date}.*.log 2>/dev/null | \
    awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print sum}')
  echo "${date}: ${tokens:-0} tokens"
done | awk '{sum+=$2; count++} END {print "Average daily tokens:", sum/count}'
```

#### Monthly Cost Analysis

**Monthly Token Usage**:
```bash
# Aggregate tokens for the current month
find logs/ -name "gemini-usage.*.log" -newermt "$(date +%Y-%m-01)" -exec grep "Total:" {} \; | \
  awk -F'Total: ' '{print $2}' | \
  awk -F',' '{sum+=$1} END {print "Monthly total tokens:", sum}'
```

**Monthly Cost Breakdown**:
```bash
# Monthly cost breakdown by week
echo "Week 1:"
find logs/ -name "gemini-usage.*.log" -newermt "$(date +%Y-%m-01)" -not -newermt "$(date +%Y-%m-08)" -exec grep "Total:" {} \; | \
  awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print sum " tokens"}'

echo "Week 2:"
find logs/ -name "gemini-usage.*.log" -newermt "$(date +%Y-%m-08)" -not -newermt "$(date +%Y-%m-15)" -exec grep "Total:" {} \; | \
  awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print sum " tokens"}'

echo "Week 3:"
find logs/ -name "gemini-usage.*.log" -newermt "$(date +%Y-%m-15)" -not -newermt "$(date +%Y-%m-22)" -exec grep "Total:" {} \; | \
  awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print sum " tokens"}'

echo "Week 4:"
find logs/ -name "gemini-usage.*.log" -newermt "$(date +%Y-%m-22)" -exec grep "Total:" {} \; | \
  awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print sum " tokens"}'
```

### Performance Monitoring

#### Response Time Analysis

**Average Response Time**:
```bash
# Calculate average Gemini API response time
grep "Duration:" logs/gemini-usage.log | \
  awk -F'Duration: ' '{print $2}' | \
  awk -F'ms' '{sum+=$1; count++} END {print "Average duration:", sum/count, "ms"}'
```

**Response Time Percentiles**:
```bash
# Calculate p50, p95, p99 response times
grep "Duration:" logs/gemini-usage.log | \
  awk -F'Duration: ' '{print $2}' | \
  awk -F'ms' '{print $1}' | \
  sort -n | \
  awk '{
    arr[NR] = $1
  }
  END {
    p50 = arr[int(NR*0.50)]
    p95 = arr[int(NR*0.95)]
    p99 = arr[int(NR*0.99)]
    print "p50:", p50 "ms"
    print "p95:", p95 "ms"
    print "p99:", p99 "ms"
  }'
```

#### Throughput Analysis

**Average Throughput**:
```bash
# Calculate average token processing rate
grep "Throughput:" logs/gemini-usage.log | \
  awk -F'Throughput: ' '{print $2}' | \
  awk '{sum+=$1; count++} END {print "Average throughput:", sum/count, "tokens/sec"}'
```

**Throughput Distribution**:
```bash
# Throughput distribution (min, max, median)
grep "Throughput:" logs/gemini-usage.log | \
  awk -F'Throughput: ' '{print $2}' | \
  awk '{print $1}' | \
  sort -n | \
  awk '{
    arr[NR] = $1
  }
  END {
    min = arr[1]
    max = arr[NR]
    median = arr[int(NR/2)]
    print "Min:", min " tokens/sec"
    print "Median:", median " tokens/sec"
    print "Max:", max " tokens/sec"
  }'
```

#### Hourly Usage Patterns

**Requests per Hour**:
```bash
# Count requests per hour
cut -d',' -f1 logs/gemini-usage.log | \
  cut -d' ' -f2 | \
  cut -d':' -f1 | \
  sort | \
  uniq -c | \
  awk '{print $2 ":00 - " $1 " requests"}'
```

**Peak Usage Hours**:
```bash
# Identify peak usage hours
cut -d',' -f1 logs/gemini-usage.log | \
  cut -d' ' -f2 | \
  cut -d':' -f1 | \
  sort | \
  uniq -c | \
  sort -rn | \
  head -5 | \
  awk '{print "Hour " $2 ":00 - " $1 " requests"}'
```

### Advanced Analytics Script

Create a comprehensive analytics script (`scripts/analyze-gemini-usage.sh`):

```bash
#!/bin/bash
# analyze-gemini-usage.sh - Comprehensive Gemini usage analytics

LOG_DIR="logs"
LOG_FILE="${LOG_DIR}/gemini-usage.log"

echo "=== Gemini Usage Analytics ==="
echo ""

# Daily summary
echo "📊 Daily Summary (Today)"
TODAY=$(date +%Y-%m-%d)
TODAY_TOKENS=$(grep "${TODAY}" "${LOG_FILE}" 2>/dev/null | \
  grep "Total:" | \
  awk -F'Total: ' '{print $2}' | \
  awk -F',' '{sum+=$1} END {print sum}')
TODAY_REQUESTS=$(grep "${TODAY}" "${LOG_FILE}" 2>/dev/null | grep -c "\[Gemini Usage Log\]")
echo "  Requests: ${TODAY_REQUESTS:-0}"
echo "  Total Tokens: ${TODAY_TOKENS:-0}"
echo ""

# Weekly summary
echo "📈 Weekly Summary (Last 7 days)"
WEEKLY_TOKENS=$(find "${LOG_DIR}" -name "gemini-usage.*.log" -mtime -7 -exec grep "Total:" {} \; 2>/dev/null | \
  awk -F'Total: ' '{print $2}' | \
  awk -F',' '{sum+=$1} END {print sum}')
WEEKLY_REQUESTS=$(find "${LOG_DIR}" -name "gemini-usage.*.log" -mtime -7 -exec grep -c "\[Gemini Usage Log\]" {} \; 2>/dev/null | \
  awk '{sum+=$1} END {print sum}')
echo "  Requests: ${WEEKLY_REQUESTS:-0}"
echo "  Total Tokens: ${WEEKLY_TOKENS:-0}"
echo "  Average Daily: $((WEEKLY_TOKENS / 7)) tokens"
echo ""

# Performance metrics
echo "⚡ Performance Metrics"
AVG_DURATION=$(grep "Duration:" "${LOG_FILE}" 2>/dev/null | \
  awk -F'Duration: ' '{print $2}' | \
  awk -F'ms' '{sum+=$1; count++} END {if(count>0) print sum/count; else print 0}')
AVG_THROUGHPUT=$(grep "Throughput:" "${LOG_FILE}" 2>/dev/null | \
  awk -F'Throughput: ' '{print $2}' | \
  awk '{sum+=$1; count++} END {if(count>0) print sum/count; else print 0}')
echo "  Average Duration: ${AVG_DURATION}ms"
echo "  Average Throughput: ${AVG_THROUGHPUT} tokens/sec"
echo ""

# Cost estimation (example pricing)
echo "💰 Cost Estimation (Example Pricing)"
INPUT_TOKENS=$(grep "Input:" "${LOG_FILE}" 2>/dev/null | \
  awk -F'Input: ' '{print $2}' | \
  awk -F',' '{sum+=$1} END {print sum}')
OUTPUT_TOKENS=$(grep "Output:" "${LOG_FILE}" 2>/dev/null | \
  awk -F'Output: ' '{print $2}' | \
  awk -F',' '{sum+=$1} END {print sum}')
INPUT_COST=$(echo "scale=4; (${INPUT_TOKENS:-0} / 1000) * 0.000125" | bc)
OUTPUT_COST=$(echo "scale=4; (${OUTPUT_TOKENS:-0} / 1000) * 0.0005" | bc)
TOTAL_COST=$(echo "scale=4; ${INPUT_COST} + ${OUTPUT_COST}" | bc)
echo "  Input Tokens: ${INPUT_TOKENS:-0} ($${INPUT_COST})"
echo "  Output Tokens: ${OUTPUT_TOKENS:-0} ($${OUTPUT_COST})"
echo "  Total Estimated Cost: $${TOTAL_COST}"
echo ""
```

**Usage**:
```bash
chmod +x scripts/analyze-gemini-usage.sh
./scripts/analyze-gemini-usage.sh
```

### Log File Management

#### Log Retention Policy

**Current Policy** (configured in `logback-spring.xml`):
- **Rolling Strategy**: Daily + Size-based (10MB per file)
- **Retention Period**: Maximum 30 days
- **Total Size Limit**: 100MB (oldest files deleted when exceeded)
- **File Pattern**: `logs/gemini-usage.YYYY-MM-DD.N.log`

**Log File Locations**:
- **Current log**: `logs/gemini-usage.log`
- **Rolled logs**: `logs/gemini-usage.2025-12-19.0.log`, `logs/gemini-usage.2025-12-19.1.log`, etc.
- **Test logs**: `logs/gemini-usage-test.log` (test environment only)

**Check Log File Size**:
```bash
du -h logs/gemini-usage.log
```

**View Recent Logs**:
```bash
tail -n 100 logs/gemini-usage.log
```

**Search by Date Range**:
```bash
# Logs from specific date
grep "2025-12-18" logs/gemini-usage.log

# Logs from date range
grep -E "2025-12-(1[8-9]|2[0-5])" logs/gemini-usage.log
```

**Archive Old Logs**:
```bash
# Archive logs older than 30 days
find logs/ -name "gemini-usage.*.log" -mtime +30 -exec gzip {} \;
```

**Log Management Best Practices**:
- Logs are automatically rotated when file size exceeds 10MB or daily
- Old logs are automatically deleted when total size exceeds 100MB
- Logs directory is excluded from Git (`.gitignore`)
- For production, consider integrating with centralized logging (ELK, Grafana Loki)

### Integration with Monitoring Tools

The log format is designed to be easily parsed by monitoring tools:

- **Grafana**: Use LogQL queries to visualize metrics
- **ELK Stack**: Parse CSV format with Logstash
- **Prometheus**: Export metrics via log exporter
- **Custom Dashboards**: Parse CSV format with Python pandas or similar tools

For more detailed information, see:
- [GEMINI_INTEGRATION_SUMMARY.md](./docs/GEMINI_INTEGRATION_SUMMARY.md) - Complete integration documentation
- [GEMINI_TEST_REPORT.md](./docs/GEMINI_TEST_REPORT.md) - Test coverage and results
- [GEMINI_INTEGRATION_TODO.md](./docs/GEMINI_INTEGRATION_TODO.md) - Future improvements and enhancements

## 🔧 Maintenance & Operations

### Log Management

#### Gemini Usage Logs

**Log File Location**: `logs/gemini-usage.log`

**Retention Policy**:
- Daily rotation + size-based (10MB per file)
- Maximum 30 days retention
- Total size limit: 100MB
- Automatic cleanup of oldest files when limit exceeded

**Log Format**: CSV format for easy analysis
```
2025-12-19 19:11:20.171,[Gemini Usage Log] businessPlanId=bp-2025-12-19-d7cfda31, StartTime: 2025-12-19T12:11:02.346440Z, EndTime: 2025-12-19T12:11:20.170608Z, Duration: 17824ms, Input: 1370, Output: 2465, Total: 3835, Throughput: 215.16 tokens/sec
```

**Quick Analysis Commands**:
```bash
# Today's total token usage
grep "$(date +%Y-%m-%d)" logs/gemini-usage.log | grep "Total:" | awk -F'Total: ' '{print $2}' | awk -F',' '{sum+=$1} END {print "Total tokens:", sum}'

# Average response time
grep "Duration:" logs/gemini-usage.log | awk -F'Duration: ' '{print $2}' | awk -F'ms' '{sum+=$1; count++} END {print "Average:", sum/count, "ms"}'

# Request count by hour
cut -d',' -f1 logs/gemini-usage.log | cut -d' ' -f2 | cut -d':' -f1 | sort | uniq -c
```

**For detailed analysis scripts and cost tracking**, see [Monitoring & Analytics](#-monitoring--analytics) section above.

### Health Checks

**Application Health**:
```bash
# Check if application is running
curl http://localhost:8080/actuator/health

# Check Swagger UI
open http://localhost:8080/swagger-ui.html
```

**Database Health**:
```bash
# Check database connection
sqlite3 ./data/bizplan.db "SELECT 1"
```

**Gemini API Health**:
```bash
# Check recent Gemini API calls
tail -n 10 logs/gemini-usage.log

# Check for errors
grep -i "error\|exception\|failed" logs/gemini-usage.log
```

### Performance Monitoring

**Key Metrics to Monitor**:
- **Response Time**: Average Gemini API response time (target: < 10s p95)
- **Throughput**: Token processing rate (tokens/sec)
- **Error Rate**: Failed API calls / Total calls
- **Cost**: Daily/weekly/monthly token usage and estimated costs

**Monitoring Dashboard** (Future):
- Integrate with Prometheus/Grafana for real-time metrics
- Set up alerts for error rate spikes or cost thresholds
- See [GEMINI_INTEGRATION_TODO.md](./docs/GEMINI_INTEGRATION_TODO.md) for monitoring improvements

### Backup & Recovery

**Log Files**:
- Logs are automatically rotated and retained for 30 days
- For longer retention, archive logs before deletion:
```bash
# Archive logs older than 30 days
find logs/ -name "gemini-usage.*.log" -mtime +30 -exec gzip {} \;
```

**Database**:
- Regular SQLite database file backups recommended (./data/bizplan.db)
- Use Flyway migrations for schema versioning

**Configuration**:
- Environment variables stored in `.env` (not in Git)
- Keep `.env.example` updated as template

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
# Verify SQLite database file exists
ls -la ./data/bizplan.db
sqlite3 ./data/bizplan.db ".tables"

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

**Gemini API errors**
```bash
# Check if GEMINI_API_KEY is set
echo $GEMINI_API_KEY

# Check recent API errors
grep -i "error\|exception" logs/gemini-usage.log | tail -20

# Verify API key is valid
curl -H "Content-Type: application/json" \
  -d '{"contents":[{"parts":[{"text":"test"}]}]}' \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=${GEMINI_API_KEY}"
```

**ObjectMapper bean not found**
```bash
# This should be resolved by JacksonConfig
# Verify configuration
grep -r "JacksonConfig" src/main/java/
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

