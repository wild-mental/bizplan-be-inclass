# 환경변수 설정 가이드

## 📁 파일 위치

```
bizplan-be-inclass/
├── .env                    # ⭐ 프로젝트 루트 (Spring Boot + AI Engine 공용)
├── .env.example            # 템플릿 파일 (Git 추적됨)
└── ai-engine/
    └── .env                # (선택) AI Engine 전용 설정 시
```

> ⚠️ `.env` 파일은 `.gitignore`에 등록되어 Git에 커밋되지 않습니다.

---

## 🔧 필수 환경변수

### 1. 데이터베이스 (MySQL)

| 변수명 | 설명 | 기본값 | 예시 |
|--------|------|--------|------|
| `DB_HOST` | DB 호스트 | `localhost` | `db.example.com` |
| `DB_PORT` | DB 포트 | `3306` | `3306` |
| `DB_NAME` | DB 이름 | `bizplan` | `bizplan_prod` |
| `DB_USERNAME` | DB 사용자 | `root` | `bizplan_user` |
| `DB_PASSWORD` | DB 비밀번호 | (빈값) | `SecureP@ss123!` |

### 2. Spring Boot 서버

| 변수명 | 설명 | 기본값 | 예시 |
|--------|------|--------|------|
| `SPRING_PROFILES_ACTIVE` | 활성 프로파일 | `local` | `dev`, `prod` |
| `SERVER_PORT` | 서버 포트 | `8080` | `8080` |
| `JPA_SHOW_SQL` | SQL 로깅 | `false` | `true` (개발용) |

### 3. Google Gemini API ⭐

| 변수명 | 설명 | 기본값 | 예시 |
|--------|------|--------|------|
| `GEMINI_API_KEY` | Gemini API 키 | (필수) | `AIzaSy...` |

> 🔑 API 키 발급: https://makersuite.google.com/app/apikey

### 4. AI Engine (FastAPI)

| 변수명 | 설명 | 기본값 | 예시 |
|--------|------|--------|------|
| `AI_ENGINE_URL` | AI 엔진 URL | `http://localhost:8001` | `http://ai:8001` |

### 5. 보안 설정

| 변수명 | 설명 | 기본값 | 요구사항 |
|--------|------|--------|----------|
| `JWT_SECRET` | JWT 서명 키 | (필수) | 최소 32자 |
| `ENCRYPTION_KEY` | AES-256 암호화 키 | (필수) | 정확히 32자 |

---

## 📝 .env 파일 예시

```bash
# ============ Database ============
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bizplan
DB_USERNAME=root
DB_PASSWORD=your_secure_password

# ============ Spring Boot ============
SPRING_PROFILES_ACTIVE=local
SERVER_PORT=8080

# ============ AI / Gemini ============
GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
AI_ENGINE_URL=http://localhost:8001

# ============ Security ============
JWT_SECRET=your-jwt-secret-minimum-32-characters-long
ENCRYPTION_KEY=your-aes256-key-exactly-32chars
```

---

## 🚀 환경별 설정

### Local (개발)
```bash
SPRING_PROFILES_ACTIVE=local
# H2 인메모리 DB 사용 (MySQL 불필요)
# application-local.properties 자동 적용
```

### Dev (개발 서버)
```bash
SPRING_PROFILES_ACTIVE=dev
DB_HOST=dev-db.internal
DB_NAME=bizplan_dev
```

### Prod (운영)
```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=prod-db.internal
DB_NAME=bizplan_prod
# 보안 키는 반드시 운영용으로 변경!
```

---

## ⚡ 빠른 설정

```bash
# 1. 템플릿 복사
cp .env.example .env

# 2. 편집
vi .env  # 또는 원하는 편집기

# 3. 실행
./gradlew bootRun
```

---

## 🔐 보안 주의사항

1. **`.env` 파일은 절대 Git에 커밋하지 않음**
2. **운영 환경에서는 환경변수를 직접 주입** (Docker, K8s secrets 등)
3. **API 키, 비밀번호는 주기적으로 교체**
4. **JWT_SECRET, ENCRYPTION_KEY는 운영용으로 별도 생성**

```bash
# 안전한 키 생성 예시
openssl rand -base64 32  # JWT_SECRET
openssl rand -hex 16     # ENCRYPTION_KEY (32자)
```

---

**작성일**: 2025-12-27

