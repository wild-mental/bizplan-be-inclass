package vibe.makersround.makersround_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import vibe.makersround.makersround_backend.exception.AuthenticationException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialOAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    public SocialUserInfo validateToken(String provider, String token) {
        // 개발용 모의 토큰 처리
        if (token.startsWith("mock_")) {
            log.info("모의 토큰 감지: provider={}, token={}", provider, token);
            return new SocialUserInfo(
                "social_id_" + System.currentTimeMillis(),
                "mock_user_" + System.currentTimeMillis() + "@" + provider + ".com",
                provider.toUpperCase() + " User"
            );
        }

        try {
            return switch (provider.toLowerCase()) {
                case "google" -> validateGoogleToken(token);
                case "kakao" -> validateKakaoToken(token);
                case "naver" -> validateNaverToken(token);
                default -> throw new AuthenticationException("지원하지 않는 소셜 공급자입니다: " + provider);
            };
        } catch (Exception e) {
            log.error("소셜 토큰 검증 실패: provider={}, error={}", provider, e.getMessage());
            throw new AuthenticationException("소셜 로그인 인증에 실패했습니다: " + e.getMessage());
        }
    }

    private SocialUserInfo validateGoogleToken(String token) {
        // Google UserInfo API 호출
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        
        Map<String, Object> body = response.getBody();
        if (body == null || body.get("sub") == null) {
            throw new AuthenticationException("Google 사용자 정보를 가져올 수 없습니다.");
        }
        
        return new SocialUserInfo(
            (String) body.get("sub"),
            (String) body.get("email"),
            (String) body.get("name")
        );
    }

    private SocialUserInfo validateKakaoToken(String token) {
        // Kakao UserInfo API 호출
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        
        Map<String, Object> body = response.getBody();
        if (body == null || body.get("id") == null) {
            throw new AuthenticationException("Kakao 사용자 정보를 가져올 수 없습니다.");
        }
        
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        
        return new SocialUserInfo(
            String.valueOf(body.get("id")),
            (String) kakaoAccount.get("email"),
            (String) profile.get("nickname")
        );
    }

    private SocialUserInfo validateNaverToken(String token) {
        // Naver UserInfo API 호출
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        
        Map<String, Object> body = response.getBody();
        if (body == null || !"00".equals(body.get("resultcode"))) {
            throw new AuthenticationException("Naver 사용자 정보를 가져올 수 없습니다.");
        }
        
        Map<String, Object> responseMap = (Map<String, Object>) body.get("response");
        
        return new SocialUserInfo(
            (String) responseMap.get("id"),
            (String) responseMap.get("email"),
            (String) responseMap.get("name")
        );
    }

    public record SocialUserInfo(String id, String email, String name) {}
}
