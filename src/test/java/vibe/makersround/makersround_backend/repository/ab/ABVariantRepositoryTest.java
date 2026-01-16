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
@DisplayName("ABVariantRepository 테스트")
class ABVariantRepositoryTest {

    @Autowired
    private ABVariantRepository variantRepository;

    @Autowired
    private ABExperimentRepository experimentRepository;

    private ABExperiment testExperiment;

    @BeforeEach
    void setUp() {
        testExperiment = ABExperiment.builder()
                .name("variant-test-experiment-" + System.currentTimeMillis())
                .description("Test experiment for variant testing")
                .targetPage("landing")
                .targetElement("makers_section_order")
                .status("running")
                .trafficPercentage(100)
                .build();
        testExperiment = experimentRepository.save(testExperiment);
    }

    @Test
    @DisplayName("변형 저장 및 조회")
    void save_and_findById() {
        ABVariant variant = ABVariant.builder()
                .experiment(testExperiment)
                .name("control")
                .weight(50)
                .isControl(true)
                .contentJson("{\"order\": [\"M\", \"A\", \"K\", \"E\", \"R\", \"S\"]}")
                .build();

        ABVariant saved = variantRepository.save(variant);

        Optional<ABVariant> found = variantRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("control");
        assertThat(found.get().getWeight()).isEqualTo(50);
        assertThat(found.get().getIsControl()).isTrue();
    }

    @Test
    @DisplayName("실험 ID로 변형 목록 조회")
    void findByExperimentId() {
        ABVariant control = ABVariant.builder()
                .experiment(testExperiment)
                .name("control")
                .weight(50)
                .isControl(true)
                .contentJson("{}")
                .build();
        variantRepository.save(control);

        ABVariant treatment = ABVariant.builder()
                .experiment(testExperiment)
                .name("treatment")
                .weight(50)
                .isControl(false)
                .contentJson("{}")
                .build();
        variantRepository.save(treatment);

        List<ABVariant> variants = variantRepository.findByExperimentId(testExperiment.getId());

        assertThat(variants).hasSize(2);
        assertThat(variants).extracting(ABVariant::getName)
                .containsExactlyInAnyOrder("control", "treatment");
    }

    @Test
    @DisplayName("컨트롤 변형 조회")
    void findByExperimentIdAndIsControlTrue() {
        ABVariant control = ABVariant.builder()
                .experiment(testExperiment)
                .name("control")
                .weight(50)
                .isControl(true)
                .contentJson("{}")
                .build();
        variantRepository.save(control);

        ABVariant treatment = ABVariant.builder()
                .experiment(testExperiment)
                .name("treatment")
                .weight(50)
                .isControl(false)
                .contentJson("{}")
                .build();
        variantRepository.save(treatment);

        Optional<ABVariant> controlVariant = variantRepository
                .findByExperimentIdAndIsControlTrue(testExperiment.getId());

        assertThat(controlVariant).isPresent();
        assertThat(controlVariant.get().getName()).isEqualTo("control");
        assertThat(controlVariant.get().getIsControl()).isTrue();
    }

    @Test
    @DisplayName("변형 정보 수정")
    void update_variant() {
        ABVariant variant = ABVariant.builder()
                .experiment(testExperiment)
                .name("original")
                .weight(50)
                .isControl(false)
                .contentJson("{}")
                .build();
        ABVariant saved = variantRepository.save(variant);

        saved.setName("modified");
        saved.setWeight(75);
        saved.setContentJson("{\"modified\": true}");
        variantRepository.save(saved);

        ABVariant updated = variantRepository.findById(saved.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo("modified");
        assertThat(updated.getWeight()).isEqualTo(75);
        assertThat(updated.getContentJson()).contains("modified");
    }

    @Test
    @DisplayName("변형 삭제")
    void delete_variant() {
        ABVariant variant = ABVariant.builder()
                .experiment(testExperiment)
                .name("to-delete")
                .weight(50)
                .isControl(false)
                .contentJson("{}")
                .build();
        ABVariant saved = variantRepository.save(variant);
        String variantId = saved.getId();

        variantRepository.delete(saved);

        Optional<ABVariant> deleted = variantRepository.findById(variantId);
        assertThat(deleted).isEmpty();
    }
}
