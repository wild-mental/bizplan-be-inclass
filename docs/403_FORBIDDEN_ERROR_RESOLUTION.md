# 403 Forbidden 에러 해결 가이드

## 문제 상황
프론트엔드에서 백엔드 API 호출 시 다음과 같은 403 Forbidden 에러 발생:
```
POST https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/auth/signup 403 (Forbidden)
```

## 원인 분석

### 1. URL 경로 불일치 문제
- **호출 URL**: `https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/auth/signup`
- **기대 URL**: `https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/api/v1/auth/signup`
- 프론트엔드의 `baseURL`이 `/api/v1`을 포함하지 않아서 발생

### 2. CORS 설정 문제
- 백엔드의 `SecurityConfig`에서 `setAllowCredentials(true)`와 `setAllowedOrigins()`를 함께 사용
- 프론트엔드의 실제 origin이 CORS 허용 목록에 없으면 403 에러 발생

## 해결 방법

### 1. 프론트엔드 수정 (완료)
`apiClient.ts`와 `preRegistrationApi.ts`의 `getApiBaseUrl()` 함수를 수정하여:
- 환경 변수로 전체 URL이 설정된 경우 자동으로 `/api/v1` 경로 추가
- 예: `https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app` → `https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/api/v1`

### 2. 백엔드 CORS 설정 확인
백엔드 배포 환경에서 다음 환경 변수를 설정해야 합니다:

```bash
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,http://your-frontend-domain.com
```

또는 `application.properties`에서 기본값을 수정:

```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173,https://makersround.world,http://makersround.world,https://www.makersround.world,http://www.makersround.world,https://your-frontend-domain.com}
```

### 3. 프론트엔드 환경 변수 설정
프론트엔드 배포 환경에서 다음 환경 변수를 설정:

```bash
# 옵션 1: 전체 URL (자동으로 /api/v1 추가됨)
VITE_API_URL=https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app

# 옵션 2: 이미 /api/v1이 포함된 URL
VITE_API_BASE_URL=https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/api/v1
```

## 확인 사항

1. **프론트엔드 콘솔 로그 확인**
   - 개발 환경에서 API 호출 시 상세한 로그가 출력됨
   - `🔵 [API Request]` 로그에서 실제 호출 URL 확인
   - `🔴 [API Error]` 로그에서 에러 상세 정보 확인

2. **백엔드 로그 확인**
   - CORS 관련 에러 메시지 확인
   - Security 필터 체인 동작 확인

3. **네트워크 탭 확인**
   - 브라우저 개발자 도구의 Network 탭에서:
     - 실제 요청 URL 확인
     - 요청 헤더 확인 (Origin 헤더 포함)
     - 응답 헤더 확인 (Access-Control-Allow-Origin 헤더 확인)

## 추가 디버깅

### 백엔드 CORS 디버깅
`SecurityConfig.java`에 로깅 추가:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    List<String> origins = Stream.of(allowedOrigins.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    
    log.info("CORS Allowed Origins: {}", origins);
    configuration.setAllowedOrigins(origins);
    
    // ... 나머지 설정
}
```

### 프론트엔드 디버깅
브라우저 콘솔에서 확인:

```javascript
// 현재 설정된 API Base URL 확인
console.log('API Base URL:', import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || '/api/v1');
```

## 예상 결과

수정 후:
- ✅ API 호출 URL: `https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/api/v1/auth/signup`
- ✅ CORS 헤더가 올바르게 설정됨
- ✅ 201 Created 응답 수신

