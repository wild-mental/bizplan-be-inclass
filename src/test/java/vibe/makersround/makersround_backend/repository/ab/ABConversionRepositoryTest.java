package vibe.makersround.makersround_backend.repository.ab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_backend.entity.ab.ABAssignment;
import vibe.makersround.makersround_backend.entity.ab.ABConversion;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;
import vibe.makersround.makersround_backend.entity.ab.ABVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ABConversionRepository 테스트")
class ABConversionRepositoryTest {

    @Autowired
    private ABConversionRepository conversionRepository;

    @Autowired
    private ABAssignmentRepository assignmentRepository;

    @Autowired
    private ABExperimentRepository experimentRepository;

    @Autowired
    private ABVariantRepository variantRepository;

    private ABExperiment testExperiment;
    private ABVariant testVariant;
    private ABAssignment testAssignment;

    @BeforeEach
    void setUp() {
        testExperiment = ABExperiment.builder()
                .name("conversion-test-experiment-" + System.currentTimeMillis())
                .targetPage("landing")
                .targetElement("hero_cta")
                .status("running")
                .build();
        testExperiment = experimentRepository.save(testExperiment);

        testVariant = ABVariant.builder()
                .experiment(testExperiment)
                .name("control")
                .weight(100)
                .isControl(true)
                .contentJson("{}")
                .build();
        testVariant = variantRepository.save(testVariant);

        testAssignment = ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(UUID.randomUUID().toString())
                .build();
        testAssignment = assignmentRepository.save(testAssignment);
    }

    @Test
    @DisplayName("전환 저장 및 조회")
    void save_and_findById() {
        ABConversion conversion = ABConversion.builder()
                .assignment(testAssignment)
                .eventType("cta_click")
                .eventDataJson("{\"button\": \"start_free\"}")
                .build();

        ABConversion saved = conversionRepository.save(conversion);

        Optional<ABConversion> found = conversionRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEventType()).isEqualTo("cta_click");
        assertThat(found.get().getConvertedAt()).isNotNull();
    }

    @Test
    @DisplayName("할당 ID로 전환 목록 조회")
    void findByAssignmentId() {
        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("page_view")
                .build());

        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("cta_click")
                .build());

        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("signup")
                .build());

        List<ABConversion> conversions = conversionRepository.findByAssignmentId(testAssignment.getId());

        assertThat(conversions).hasSize(3);
        assertThat(conversions).extracting(ABConversion::getEventType)
                .containsExactlyInAnyOrder("page_view", "cta_click", "signup");
    }

    @Test
    @DisplayName("변형 ID별 전환 수 집계")
    void countByVariantId() {
        ABAssignment assignment1 = ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(UUID.randomUUID().toString())
                .build();
        assignment1 = assignmentRepository.save(assignment1);

        ABAssignment assignment2 = ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(UUID.randomUUID().toString())
                .build();
        assignment2 = assignmentRepository.save(assignment2);

        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("signup")
                .build());

        conversionRepository.save(ABConversion.builder()
                .assignment(assignment1)
                .eventType("signup")
                .build());

        conversionRepository.save(ABConversion.builder()
                .assignment(assignment2)
                .eventType("cta_click")
                .build());

        long count = conversionRepository.countByVariantId(testVariant.getId());

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("실험 ID별 전환 수 집계")
    void countByExperimentId() {
        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("signup")
                .build());

        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("purchase")
                .build());

        long count = conversionRepository.countByExperimentId(testExperiment.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("이벤트 타입별 전환 조회")
    void findByAssignmentIdAndEventType() {
        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("cta_click")
                .build());

        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("signup")
                .build());

        conversionRepository.save(ABConversion.builder()
                .assignment(testAssignment)
                .eventType("cta_click")
                .build());

        List<ABConversion> ctaClicks = conversionRepository
                .findByAssignmentIdAndEventType(testAssignment.getId(), "cta_click");

        assertThat(ctaClicks).hasSize(2);
        assertThat(ctaClicks).allMatch(c -> c.getEventType().equals("cta_click"));
    }

    @Test
    @DisplayName("전환 데이터 JSON 저장 및 조회")
    void conversion_with_event_data() {
        String eventData = "{\"revenue\": 29900, \"plan\": \"pro\", \"currency\": \"KRW\"}";

        ABConversion conversion = ABConversion.builder()
                .assignment(testAssignment)
                .eventType("purchase")
                .eventDataJson(eventData)
                .build();

        ABConversion saved = conversionRepository.save(conversion);

        ABConversion found = conversionRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getEventDataJson()).contains("29900");
        assertThat(found.getEventDataJson()).contains("pro");
    }
}
