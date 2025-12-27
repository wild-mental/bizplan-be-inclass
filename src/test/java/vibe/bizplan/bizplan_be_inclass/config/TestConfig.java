package vibe.bizplan.bizplan_be_inclass.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 통합 테스트용 설정 클래스
 * 
 * 메인 애플리케이션의 JacksonConfig와 충돌하지 않도록
 * @Primary를 사용하여 테스트 환경에서 우선순위를 부여합니다.
 */
@TestConfiguration
public class TestConfig {

    /**
     * 테스트용 ObjectMapper 빈
     * 
     * 메인 애플리케이션의 JacksonConfig.objectMapper()와 충돌을 방지하기 위해
     * @Primary를 사용하여 테스트 환경에서 이 빈이 우선 사용되도록 합니다.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
