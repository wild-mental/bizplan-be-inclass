# BizPlan Backend API 명세서

**버전**: 1.0.0  
**기본 URL**: `http://localhost:8080`  
**API Prefix**: `/api/v1`  
**작성일**: 2025-12-17

---

## 📋 목차

1. [공통 사항](#공통-사항)
2. [API 엔드포인트](#api-엔드포인트)
3. [에러 응답](#에러-응답)
4. [예제](#예제)

---

## 공통 사항

### 기본 정보

- **Content-Type**: `application/json`
- **인코딩**: UTF-8
- **인증**: 현재 미구현 (향후 JWT 기반 인증 예정)

### 응답 형식

모든 API 응답은 표준 래퍼 형식을 따릅니다:

```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

**성공 응답**:
- `success`: `true`
- `data`: 응답 데이터 (타입에 따라 다름)
- `error`: `null`

**에러 응답**:
- `success`: `false`
- `data`: `null`
- `error`: 에러 정보 객체

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

---

## API 엔드포인트

### 1. 템플릿 목록 조회

지원되는 사업계획서 템플릿 목록을 조회합니다.

**엔드포인트**: `GET /api/v1/projects/templates`

**요청**
- **헤더**: 없음
- **경로 파라미터**: 없음
- **쿼리 파라미터**: 없음
- **요청 본문**: 없음

**응답**

**성공 (200 OK)**
```json
{
  "success": true,
  "data": [
    {
      "code": "KSTARTUP_2025",
      "name": "예비창업패키지",
      "description": "중소벤처기업부 예비창업패키지 양식"
    },
    {
      "code": "BANK_LOAN_2025",
      "name": "은행 대출용 사업계획서",
      "description": "시중은행 창업대출 심사용 양식"
    },
    {
      "code": "IR_PITCH_2025",
      "name": "투자유치용 IR 자료",
      "description": "시드/시리즈 A 투자유치용 양식"
    }
  ],
  "error": null
}
```

**응답 필드 설명**

| 필드 | 타입 | 설명 |
|------|------|------|
| `data[].code` | `string` | 템플릿 고유 코드 |
| `data[].name` | `string` | 템플릿 이름 |
| `data[].description` | `string` | 템플릿 설명 |

**지원되는 템플릿 코드**

| 코드 | 이름 | 설명 |
|------|------|------|
| `KSTARTUP_2025` | 예비창업패키지 | 중소벤처기업부 예비창업패키지 양식 |
| `BANK_LOAN_2025` | 은행 대출용 사업계획서 | 시중은행 창업대출 심사용 양식 |
| `IR_PITCH_2025` | 투자유치용 IR 자료 | 시드/시리즈 A 투자유치용 양식 |

**에러 응답**

이 엔드포인트는 항상 성공합니다. (에러 응답 없음)

---

### 2. 프로젝트 생성

새로운 사업계획서 프로젝트를 생성합니다.

**엔드포인트**: `POST /api/v1/projects`

**요청**

**헤더**
```
Content-Type: application/json
```

**경로 파라미터**: 없음

**쿼리 파라미터**: 없음

**요청 본문**
```json
{
  "templateCode": "KSTARTUP_2025"
}
```

**요청 필드 설명**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `templateCode` | `string` | ✅ | 사용할 템플릿 코드 (NotBlank) |

**응답**

**성공 (201 Created)**
```json
{
  "success": true,
  "data": {
    "projectId": "550e8400-e29b-41d4-a716-446655440000",
    "templateCode": "KSTARTUP_2025",
    "status": "draft",
    "createdAt": "2025-12-17T11:00:00"
  },
  "error": null
}
```

**응답 필드 설명**

| 필드 | 타입 | 설명 |
|------|------|------|
| `data.projectId` | `string` | 프로젝트 고유 식별자 (UUID) |
| `data.templateCode` | `string` | 사용된 템플릿 코드 |
| `data.status` | `string` | 프로젝트 상태 (기본값: "draft") |
| `data.createdAt` | `string` | 프로젝트 생성 시각 (ISO 8601 형식) |

**에러 응답**

**400 Bad Request - 유효성 검사 실패**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_INPUT",
    "message": "templateCode는 필수 항목입니다."
  }
}
```

**400 Bad Request - 유효하지 않은 템플릿 코드**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_TEMPLATE",
    "message": "지원하지 않는 템플릿 코드입니다: INVALID_CODE"
  }
}
```

**500 Internal Server Error - 서버 오류**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INTERNAL_ERROR",
    "message": "서버 내부 오류가 발생했습니다."
  }
}
```

---

## 에러 응답

### 에러 코드 목록

| 코드 | HTTP 상태 | 설명 |
|------|----------|------|
| `INVALID_INPUT` | 400 | 요청 데이터 유효성 검사 실패 |
| `INVALID_TEMPLATE` | 400 | 유효하지 않은 템플릿 코드 |
| `INVALID_ARGUMENT` | 400 | 잘못된 인자 |
| `INTERNAL_ERROR` | 500 | 서버 내부 오류 |

### 에러 응답 형식

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

---

## 예제

### cURL 예제

#### 1. 템플릿 목록 조회
```bash
curl -X GET http://localhost:8080/api/v1/projects/templates \
  -H "Content-Type: application/json"
```

#### 2. 프로젝트 생성
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "templateCode": "KSTARTUP_2025"
  }'
```

### JavaScript (Fetch API) 예제

#### 1. 템플릿 목록 조회
```javascript
fetch('http://localhost:8080/api/v1/projects/templates')
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      console.log('템플릿 목록:', data.data);
    } else {
      console.error('에러:', data.error);
    }
  });
```

#### 2. 프로젝트 생성
```javascript
fetch('http://localhost:8080/api/v1/projects', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    templateCode: 'KSTARTUP_2025'
  })
})
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      console.log('프로젝트 생성됨:', data.data);
    } else {
      console.error('에러:', data.error);
    }
  });
```

### Python 예제

#### 1. 템플릿 목록 조회
```python
import requests

response = requests.get('http://localhost:8080/api/v1/projects/templates')
data = response.json()

if data['success']:
    print('템플릿 목록:', data['data'])
else:
    print('에러:', data['error'])
```

#### 2. 프로젝트 생성
```python
import requests

response = requests.post(
    'http://localhost:8080/api/v1/projects',
    json={'templateCode': 'KSTARTUP_2025'},
    headers={'Content-Type': 'application/json'}
)
data = response.json()

if data['success']:
    print('프로젝트 생성됨:', data['data'])
else:
    print('에러:', data['error'])
```

---

## 추가 정보

### 프로젝트 상태

현재 지원되는 프로젝트 상태:
- `draft`: 초안 (기본값)

### 날짜/시간 형식

모든 날짜/시간 필드는 ISO 8601 형식을 사용합니다:
- 형식: `YYYY-MM-DDTHH:mm:ss`
- 예시: `2025-12-17T11:00:00`
- 타임존: 서버 로컬 타임존

### UUID 형식

프로젝트 ID는 UUID v4 형식을 사용합니다:
- 형식: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
- 예시: `550e8400-e29b-41d4-a716-446655440000`

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| 1.0.0 | 2025-12-17 | 초기 API 명세서 작성 |

---

## 참고

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [REST API Design Guidelines](.cursor/rules/304-api-rest-design-rules.mdc)
- [프로젝트 README](../README.md)

