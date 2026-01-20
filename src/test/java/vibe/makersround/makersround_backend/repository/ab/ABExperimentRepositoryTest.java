package vibe.makersround.makersround_backend.repository.ab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;
import vibe.makersround.makersround_backend.entity.ab.ABVariant;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ABExperimentRepository 테스트")
class ABExperimentRepositoryTest {

    @Autowired
    private ABExperimentRepository experimentRepository;

    @Autowired
    private ABVariantRepository variantRepository;

    private ABExperiment testExperiment;

    @BeforeEach
    void setUp() {
        testExperiment = ABExperiment.builder()
                .name("test-experiment-" + System.currentTimeMillis())
                .description("Test experiment for unit testing")
                .targetPage("landing")
                .targetElement("makers_section_order")
                .status("draft")
                .trafficPercentage(100)
                .build();
    }

    @Test
    @DisplayName("실험 저장 및 조회")
    void save_and_findById() {
        ABExperiment saved = experimentRepository.save(testExperiment);

        Optional<ABExperiment> found = experimentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(testExperiment.getName());
        assertThat(found.get().getTargetPage()).isEqualTo("landing");
        assertThat(found.get().getStatus()).isEqualTo("draft");
    }

    @Test
    @DisplayName("이름으로 실험 조회")
    void findByName() {
        experimentRepository.save(testExperiment);

        Optional<ABExperiment> found = experimentRepository.findByName(testExperiment.getName());

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Test experiment for unit testing");
    }

    @Test
    @DisplayName("상태별 실험 조회")
    void findByStatus() {
        testExperiment.setStatus("running");
        experimentRepository.save(testExperiment);

        ABExperiment draftExperiment = ABExperiment.builder()
                .name("draft-experiment-" + System.currentTimeMillis())
                .targetPage("landing")
                .status("draft")
                .build();
        experimentRepository.save(draftExperiment);

        List<ABExperiment> runningExperiments = experimentRepository.findByStatus("running");
        List<ABExperiment> draftExperiments = experimentRepository.findByStatus("draft");

        assertThat(runningExperiments).hasSize(1);
        assertThat(runningExperiments.get(0).getName()).isEqualTo(testExperiment.getName());
        assertThat(draftExperiments).hasSize(1);
    }

    @Test
    @DisplayName("타겟 페이지별 실험 조회")
    void findByTargetPage() {
        experimentRepository.save(testExperiment);

        ABExperiment pricingExperiment = ABExperiment.builder()
                .name("pricing-experiment-" + System.currentTimeMillis())
                .targetPage("pricing")
                .status("draft")
                .build();
        experimentRepository.save(pricingExperiment);

        List<ABExperiment> landingExperiments = experimentRepository.findByTargetPage("landing");
        List<ABExperiment> pricingExperiments = experimentRepository.findByTargetPage("pricing");

        assertThat(landingExperiments).hasSize(1);
        assertThat(pricingExperiments).hasSize(1);
    }

    @Test
    @DisplayName("활성 실험 및 변형 함께 조회")
    void findActiveExperimentsWithVariants() {
        testExperiment.setStatus("running");
        
        ABVariant controlVariant = ABVariant.builder()
                .name("control")
                .weight(50)
                .isControl(true)
                .contentJson("{\"order\": [\"M\", \"A\", \"K\", \"E\", \"R\", \"S\"]}")
                .build();
        testExperiment.addVariant(controlVariant);

        ABVariant treatmentVariant = ABVariant.builder()
                .name("treatment")
                .weight(50)
                .isControl(false)
                .contentJson("{\"order\": [\"S\", \"R\", \"E\", \"K\", \"A\", \"M\"]}")
                .build();
        testExperiment.addVariant(treatmentVariant);
        
        experimentRepository.save(testExperiment);

        List<ABExperiment> experiments = experimentRepository
                .findActiveExperimentsWithVariants("running", "landing");

        assertThat(experiments).hasSize(1);
        assertThat(experiments.get(0).getVariants()).hasSize(2);
    }

    @Test
    @DisplayName("실험명 존재 여부 확인")
    void existsByName() {
        experimentRepository.save(testExperiment);

        boolean exists = experimentRepository.existsByName(testExperiment.getName());
        boolean notExists = experimentRepository.existsByName("non-existent-experiment");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("실험 시작 및 중지")
    void start_and_stop_experiment() {
        ABExperiment saved = experimentRepository.save(testExperiment);

        saved.start();
        experimentRepository.save(saved);

        ABExperiment running = experimentRepository.findById(saved.getId()).orElseThrow();
        assertThat(running.getStatus()).isEqualTo("running");
        assertThat(running.getStartDate()).isNotNull();

        running.stop();
        experimentRepository.save(running);

        ABExperiment paused = experimentRepository.findById(saved.getId()).orElseThrow();
        assertThat(paused.getStatus()).isEqualTo("paused");
        assertThat(paused.getEndDate()).isNotNull();
    }
}
