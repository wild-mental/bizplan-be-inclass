package vibe.bizplan.bizplan_be_inclass.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson ObjectMapper 설정 클래스
 * 
 * BusinessPlanGenerationService에서 사용하는 ObjectMapper 빈을 제공합니다.
 * Spring Boot의 기본 JacksonAutoConfiguration이 작동하지 않는 경우를 대비하여
 * 명시적으로 빈을 정의합니다.
 */
@Configuration
public class JacksonConfig {

    /**
     * ObjectMapper 빈 정의
     * 
     * @return ObjectMapper 인스턴스
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 필요시 추가 설정 가능
        // mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }
}
