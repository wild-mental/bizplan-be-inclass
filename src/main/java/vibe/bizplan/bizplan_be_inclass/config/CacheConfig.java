package vibe.bizplan.bizplan_be_inclass.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 캐시 설정
 * Caffeine 캐시를 사용하여 로그 분석 결과를 캐싱
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caffeine 캐시 매니저
     * - 분석 API의 결과를 5분간 캐싱
     * - 최대 100개의 캐시 항목 유지
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 캐시 이름 설정
        cacheManager.setCacheNames(Arrays.asList(
            "landingHourlyUsage",
            "writingDemoHourlyUsage",
            "evaluationDemoHourlyUsage",
            "usageSummary",
            "dailyUsage"
        ));
        
        // Caffeine 설정
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)  // 5분 TTL
            .maximumSize(100)                        // 최대 100개 항목
            .recordStats());                         // 통계 기록
        
        return cacheManager;
    }
}
