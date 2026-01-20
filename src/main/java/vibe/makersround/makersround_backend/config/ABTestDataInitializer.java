package vibe.makersround.makersround_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;
import vibe.makersround.makersround_backend.entity.ab.ABVariant;
import vibe.makersround.makersround_backend.repository.ab.ABExperimentRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * A/B 테스트 초기 데이터 자동 삽입 컴포넌트
 * 
 * Flyway 마이그레이션이 완료된 후 자동으로 실행되어
 * 기본 A/B 테스트 실험 데이터를 데이터베이스에 삽입합니다.
 * 
 * 실행 순서:
 * 1. FlywayConfig (@PostConstruct) - 마이그레이션 실행
 * 2. ABTestDataInitializer (@EventListener ApplicationReadyEvent) - 초기 데이터 삽입
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ABTestDataInitializer {

    private final ABExperimentRepository experimentRepository;
    private final ObjectMapper objectMapper;

    private static final String EXPERIMENT_NAME = "makers-section-layout";
    private static final String EXPERIMENT_ID = "exp_makers_section_priority_001";
    private static final String CONTROL_VARIANT_ID = "var_001_a";
    private static final String VARIANT_B_ID = "var_001_b";

    /**
     * 애플리케이션이 완전히 시작된 후 실행
     * Flyway 마이그레이션이 완료된 후에 실행되도록 보장됨
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeABTestData() {
        try {
            log.info("A/B 테스트 초기 데이터 삽입 시작...");

            // 중복 체크: 이미 실험이 존재하면 스킵
            if (experimentRepository.existsByName(EXPERIMENT_NAME)) {
                log.info("A/B 테스트 실험 '{}'이(가) 이미 존재합니다. 초기 데이터 삽입을 건너뜁니다.", EXPERIMENT_NAME);
                return;
            }

            // 실험(Experiment) 생성
            ABExperiment experiment = createExperiment();
            experiment = experimentRepository.save(experiment);

            // Control 변형 생성
            ABVariant controlVariant = createControlVariant(experiment);
            experiment.addVariant(controlVariant);

            // Variant B 생성
            ABVariant variantB = createVariantB(experiment);
            experiment.addVariant(variantB);

            // 실험 저장 (cascade로 변형도 함께 저장됨)
            experimentRepository.save(experiment);

            log.info("A/B 테스트 초기 데이터 삽입 완료: 실험 '{}' (ID: {})", EXPERIMENT_NAME, EXPERIMENT_ID);
            log.info("  - Control 변형: {} (weight: 50)", controlVariant.getName());
            log.info("  - Variant 변형: {} (weight: 50)", variantB.getName());

        } catch (Exception e) {
            log.error("A/B 테스트 초기 데이터 삽입 중 오류 발생", e);
            // 초기화 실패해도 애플리케이션은 계속 실행되도록 예외를 다시 던지지 않음
        }
    }

    /**
     * 실험(Experiment) 엔티티 생성
     */
    private ABExperiment createExperiment() {
        ABExperiment experiment = ABExperiment.builder()
                .id(EXPERIMENT_ID)
                .name(EXPERIMENT_NAME)
                .description("AI 심사위원단 섹션 상위 배치 및 상세 콘텐츠 기본 노출 실험")
                .targetPage("/")
                .targetElement("landing_page_section_order")
                .status("running")
                .trafficPercentage(100)
                .startDate(LocalDateTime.now())
                .build();

        // @PrePersist가 실행되지 않도록 수동 설정
        experiment.setCreatedAt(LocalDateTime.now());
        experiment.setUpdatedAt(LocalDateTime.now());

        return experiment;
    }

    /**
     * Control 변형 생성 (기존 디자인 유지)
     */
    private ABVariant createControlVariant(ABExperiment experiment) {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("sectionOrder", "hero,testimonials,makers,business_category,pricing,steps,makers_world");
        contentMap.put("isMakersDetailOpen", false);
        contentMap.put("description", "기존 순서 유지, AI 심사위원 상세 접힘");

        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(contentMap);
        } catch (Exception e) {
            log.error("Control 변형 contentJson 생성 실패", e);
            contentJson = "{\"sectionOrder\":\"hero,testimonials,makers,business_category,pricing,steps,makers_world\",\"isMakersDetailOpen\":false}";
        }

        ABVariant variant = ABVariant.builder()
                .id(CONTROL_VARIANT_ID)
                .experiment(experiment)
                .name("control")
                .weight(50)
                .contentJson(contentJson)
                .isControl(true)
                .build();

        // @PrePersist가 실행되지 않도록 수동 설정
        variant.setCreatedAt(LocalDateTime.now());

        return variant;
    }

    /**
     * Variant B 생성 (개선안 적용)
     */
    private ABVariant createVariantB(ABExperiment experiment) {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("sectionOrder", "hero,makers,testimonials,business_category,pricing,steps,makers_world");
        contentMap.put("isMakersDetailOpen", true);
        contentMap.put("description", "M.A.K.E.R.S 섹션 상위 배치, AI 심사위원 상세 펼침");

        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(contentMap);
        } catch (Exception e) {
            log.error("Variant B contentJson 생성 실패", e);
            contentJson = "{\"sectionOrder\":\"hero,makers,testimonials,business_category,pricing,steps,makers_world\",\"isMakersDetailOpen\":true}";
        }

        ABVariant variant = ABVariant.builder()
                .id(VARIANT_B_ID)
                .experiment(experiment)
                .name("variant")
                .weight(50)
                .contentJson(contentJson)
                .isControl(false)
                .build();

        // @PrePersist가 실행되지 않도록 수동 설정
        variant.setCreatedAt(LocalDateTime.now());

        return variant;
    }
}
