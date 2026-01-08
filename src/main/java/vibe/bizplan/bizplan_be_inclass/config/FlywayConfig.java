package vibe.bizplan.bizplan_be_inclass.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Flyway 마이그레이션 명시적 실행 설정
 * 
 * Spring Boot 4.0.0의 Flyway 자동 설정이 SQLite에서 동작하지 않아
 * 명시적으로 Flyway를 실행하도록 구성합니다.
 * 
 * Flyway 11.17.0으로 업그레이드했지만 자동 설정이 여전히 동작하지 않아
 * 이 커스텀 코드를 사용합니다.
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    private final DataSource dataSource;
    
    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String locations;
    
    @Value("${spring.flyway.baseline-on-migrate:false}")
    private boolean baselineOnMigrate;
    
    @Value("${spring.flyway.baseline-version:0}")
    private int baselineVersion;

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 애플리케이션 시작 시 Flyway 마이그레이션 실행
     * 
     * @PostConstruct를 사용하여 Spring 컨텍스트 초기화 후 실행되도록 보장합니다.
     * 이렇게 하면 DataSource가 완전히 초기화된 후에 Flyway가 실행됩니다.
     */
    @PostConstruct
    public void migrate() {
        try {
            log.info("Flyway 마이그레이션 시작...");
            log.info("마이그레이션 파일 위치: {}", locations);
            
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations(locations)
                    .baselineOnMigrate(baselineOnMigrate)
                    .baselineVersion(MigrationVersion.fromVersion(String.valueOf(baselineVersion)))
                    .load();
            
            MigrateResult result = flyway.migrate();
            
            if (result.migrationsExecuted > 0) {
                log.info("Flyway 마이그레이션 완료: {}개의 마이그레이션 적용됨", result.migrationsExecuted);
            } else {
                log.info("Flyway 마이그레이션 완료: 적용할 마이그레이션이 없습니다 (이미 최신 상태)");
            }
        } catch (Exception e) {
            log.error("Flyway 마이그레이션 실행 중 오류 발생", e);
            throw new RuntimeException("Flyway 마이그레이션 실패", e);
        }
    }
}
