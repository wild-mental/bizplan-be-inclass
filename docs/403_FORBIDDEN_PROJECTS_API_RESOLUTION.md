# 403 Forbidden 에러 해결 가이드 - Projects API

## 문제 상황
프론트엔드에서 프로젝트 생성/수정 API 호출 시 403 Forbidden 에러 발생:
```
POST https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/api/v1/projects 403 (Forbidden)
PUT https://port-0-makersround-be-mjr0mvfncbc90bc8.sel3.cloudtype.app/api/v1/projects/{id}/wizard 403 (Forbidden)
```

## 원인 분석

### 1. 인증 토큰 문제
- `/api/v1/projects/**` 엔드포인트는 인증이 필요함 (SecurityConfig에서 `.anyRequest().authenticated()`)
- 사용자가 로그인하지 않았거나 토큰이 만료된 경우 403 에러 발생
- `apiClient.ts`의 요청 인터셉터에서 토큰이 없으면 Authorization 헤더가 전송되지 않음

### 2. CORS 설정 문제
- 백엔드의 `SecurityConfig`에서 `setAllowCredentials(true)`와 함께 `setAllowedOrigins()` 사용
- 프론트엔드 origin (`https://www.makersround.world`)이 CORS 허용 목록에 없으면 403 에러 발생
- 배포 환경에서 `CORS_ALLOWED_ORIGINS` 환경 변수가 제대로 설정되지 않았을 가능성

## 해결 방법

### 1. 프론트엔드 수정 (완료)
`apiClient.ts`의 응답 인터셉터에 403 에러 처리 추가:
- 403 에러 발생 시 인증 상태 확인
- 인증되지 않은 경우 로그인 페이지로 리다이렉트
- 현재 경로를 저장하여 로그인 후 돌아올 수 있도록 함

### 2. 백엔드 CORS 설정 확인
백엔드 배포 환경에서 다음 환경 변수를 설정해야 합니다:

```bash
CORS_ALLOWED_ORIGINS=https://www.makersround.world,https://makersround.world,http://www.makersround.world,http://makersround.world
```

또는 `application.properties`에서 기본값 확인:
```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173,https://makersround.world,http://makersround.world,https://www.makersround.world,http://www.makersround.world}
```

### 3. 사용자 인증 확인
프로젝트 생성/수정 전에 인증 상태를 확인하는 로직 추가 권장:

```typescript
// 프로젝트 생성 전 인증 확인
const { isAuthenticated } = useAuthStore();
if (!isAuthenticated) {
  navigate('/signup');
  return;
}
```

## 확인 사항

1. **프론트엔드 콘솔 로그 확인**
   - 개발 환경에서 API 호출 시 상세한 로그가 출력됨
   - `🔴 [API Error]` 로그에서 403 에러 상세 정보 확인
   - `🔒 [403 Forbidden]` 로그에서 인증 상태 확인

2. **네트워크 탭 확인**
   - 브라우저 개발자 도구의 Network 탭에서:
     - 요청 헤더 확인 (Authorization 헤더 포함 여부)
     - 응답 헤더 확인 (Access-Control-Allow-Origin 헤더 확인)
     - OPTIONS 요청 (preflight) 성공 여부 확인

3. **백엔드 로그 확인**
   - CORS 관련 에러 메시지 확인
   - Security 필터 체인 동작 확인
   - JWT 토큰 검증 로그 확인

## 추가 디버깅

### 프론트엔드 디버깅
브라우저 콘솔에서 확인:

```javascript
// 인증 상태 확인
const authStore = useAuthStore.getState();
console.log('Auth state:', {
  isAuthenticated: authStore.isAuthenticated,
  hasToken: !!authStore.accessToken,
  token: authStore.accessToken ? 'Bearer ***' : null
});
```

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

## 예상 결과

수정 후:
- ✅ 인증되지 않은 사용자는 자동으로 로그인 페이지로 리다이렉트
- ✅ 인증된 사용자는 정상적으로 프로젝트 생성/수정 가능
- ✅ CORS 헤더가 올바르게 설정됨
- ✅ 201 Created 또는 200 OK 응답 수신
