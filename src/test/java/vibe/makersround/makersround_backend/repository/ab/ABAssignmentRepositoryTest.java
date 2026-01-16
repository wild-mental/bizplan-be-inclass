package vibe.makersround.makersround_backend.repository.ab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_backend.entity.ab.ABAssignment;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;
import vibe.makersround.makersround_backend.entity.ab.ABVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ABAssignmentRepository 테스트")
class ABAssignmentRepositoryTest {

    @Autowired
    private ABAssignmentRepository assignmentRepository;

    @Autowired
    private ABExperimentRepository experimentRepository;

    @Autowired
    private ABVariantRepository variantRepository;

    private ABExperiment testExperiment;
    private ABVariant testVariant;
    private String testVisitorId;

    @BeforeEach
    void setUp() {
        testVisitorId = UUID.randomUUID().toString();

        testExperiment = ABExperiment.builder()
                .name("assignment-test-experiment-" + System.currentTimeMillis())
                .targetPage("landing")
                .targetElement("makers_section_order")
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
    }

    @Test
    @DisplayName("할당 저장 및 조회")
    void save_and_findById() {
        ABAssignment assignment = ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(testVisitorId)
                .build();

        ABAssignment saved = assignmentRepository.save(assignment);

        Optional<ABAssignment> found = assignmentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getVisitorId()).isEqualTo(testVisitorId);
        assertThat(found.get().getVariant().getId()).isEqualTo(testVariant.getId());
    }

    @Test
    @DisplayName("방문자 ID와 실험 ID로 할당 조회")
    void findByVisitorIdAndExperimentId() {
        ABAssignment assignment = ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(testVisitorId)
                .build();
        assignmentRepository.save(assignment);

        Optional<ABAssignment> found = assignmentRepository
                .findByVisitorIdAndExperimentId(testVisitorId, testExperiment.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getVariant().getName()).isEqualTo("control");
    }

    @Test
    @DisplayName("방문자 ID로 모든 할당 조회")
    void findByVisitorId() {
        ABExperiment anotherExperiment = ABExperiment.builder()
                .name("another-experiment-" + System.currentTimeMillis())
                .targetPage("pricing")
                .status("running")
                .build();
        anotherExperiment = experimentRepository.save(anotherExperiment);

        ABVariant anotherVariant = ABVariant.builder()
                .experiment(anotherExperiment)
                .name("treatment")
                .weight(100)
                .isControl(false)
                .contentJson("{}")
                .build();
        anotherVariant = variantRepository.save(anotherVariant);

        assignmentRepository.save(ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(testVisitorId)
                .build());

        assignmentRepository.save(ABAssignment.builder()
                .experiment(anotherExperiment)
                .variant(anotherVariant)
                .visitorId(testVisitorId)
                .build());

        List<ABAssignment> assignments = assignmentRepository.findByVisitorId(testVisitorId);

        assertThat(assignments).hasSize(2);
    }

    @Test
    @DisplayName("실험 ID별 할당 수 집계")
    void countByExperimentId() {
        String visitor1 = UUID.randomUUID().toString();
        String visitor2 = UUID.randomUUID().toString();

        assignmentRepository.save(ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(visitor1)
                .build());

        assignmentRepository.save(ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(visitor2)
                .build());

        long count = assignmentRepository.countByExperimentId(testExperiment.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("변형 ID별 할당 수 집계")
    void countByVariantId() {
        String visitor1 = UUID.randomUUID().toString();
        String visitor2 = UUID.randomUUID().toString();
        String visitor3 = UUID.randomUUID().toString();

        for (String visitor : List.of(visitor1, visitor2, visitor3)) {
            assignmentRepository.save(ABAssignment.builder()
                    .experiment(testExperiment)
                    .variant(testVariant)
                    .visitorId(visitor)
                    .build());
        }

        long count = assignmentRepository.countByVariantId(testVariant.getId());

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("동일 방문자-실험 조합 중복 방지")
    void duplicate_assignment_prevention() {
        ABAssignment first = ABAssignment.builder()
                .experiment(testExperiment)
                .variant(testVariant)
                .visitorId(testVisitorId)
                .build();
        assignmentRepository.save(first);

        Optional<ABAssignment> existing = assignmentRepository
                .findByVisitorIdAndExperimentId(testVisitorId, testExperiment.getId());

        assertThat(existing).isPresent();
        assertThat(existing.get().getId()).isEqualTo(first.getId());
    }
}
