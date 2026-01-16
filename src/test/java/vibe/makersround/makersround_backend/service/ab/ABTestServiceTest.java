package vibe.makersround.makersround_backend.service.ab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vibe.makersround.makersround_backend.dto.ab.request.ConversionRequest;
import vibe.makersround.makersround_backend.dto.ab.request.ExperimentCreateRequest;
import vibe.makersround.makersround_backend.dto.ab.response.ActiveExperimentsResponse;
import vibe.makersround.makersround_backend.dto.ab.response.ConversionResponse;
import vibe.makersround.makersround_backend.dto.ab.response.ExperimentResponse;
import vibe.makersround.makersround_backend.dto.ab.response.ExperimentStatsResponse;
import vibe.makersround.makersround_backend.entity.ab.ABAssignment;
import vibe.makersround.makersround_backend.entity.ab.ABConversion;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;
import vibe.makersround.makersround_backend.entity.ab.ABVariant;
import vibe.makersround.makersround_backend.repository.ab.ABAssignmentRepository;
import vibe.makersround.makersround_backend.repository.ab.ABConversionRepository;
import vibe.makersround.makersround_backend.repository.ab.ABExperimentRepository;
import vibe.makersround.makersround_backend.repository.ab.ABVariantRepository;
import vibe.makersround.makersround_backend.service.ABTestService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ABTestService 테스트")
class ABTestServiceTest {

    @Mock
    private ABExperimentRepository experimentRepository;

    @Mock
    private ABVariantRepository variantRepository;

    @Mock
    private ABAssignmentRepository assignmentRepository;

    @Mock
    private ABConversionRepository conversionRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ABTestService abTestService;

    private ABExperiment testExperiment;
    private ABVariant controlVariant;
    private ABVariant treatmentVariant;
    private String testVisitorId;

    @BeforeEach
    void setUp() {
        testVisitorId = UUID.randomUUID().toString();

        testExperiment = ABExperiment.builder()
                .id(UUID.randomUUID().toString())
                .name("test-experiment")
                .targetPage("landing")
                .targetElement("makers_section_order")
                .status("running")
                .trafficPercentage(100)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        controlVariant = ABVariant.builder()
                .id(UUID.randomUUID().toString())
                .experiment(testExperiment)
                .name("control")
                .weight(50)
                .isControl(true)
                .contentJson("{\"order\": [\"M\", \"A\", \"K\", \"E\", \"R\", \"S\"]}")
                .createdAt(LocalDateTime.now())
                .build();

        treatmentVariant = ABVariant.builder()
                .id(UUID.randomUUID().toString())
                .experiment(testExperiment)
                .name("treatment")
                .weight(50)
                .isControl(false)
                .contentJson("{\"order\": [\"S\", \"R\", \"E\", \"K\", \"A\", \"M\"]}")
                .createdAt(LocalDateTime.now())
                .build();

        testExperiment.setVariants(Arrays.asList(controlVariant, treatmentVariant));
    }

    @Test
    @DisplayName("활성 실험 조회 - 새 방문자에게 변형 할당")
    void getActiveExperiments_assignsVariantToNewVisitor() {
        when(experimentRepository.findActiveExperimentsWithVariants("running", "landing"))
                .thenReturn(Collections.singletonList(testExperiment));
        when(assignmentRepository.findByVisitorIdAndExperimentId(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(assignmentRepository.save(any(ABAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ActiveExperimentsResponse response = abTestService.getActiveExperiments("landing", testVisitorId);

        assertThat(response).isNotNull();
        assertThat(response.getVisitorId()).isEqualTo(testVisitorId);
        assertThat(response.getExperiments()).hasSize(1);
        verify(assignmentRepository).save(any(ABAssignment.class));
    }

    @Test
    @DisplayName("활성 실험 조회 - 기존 방문자는 기존 할당 반환")
    void getActiveExperiments_returnsExistingAssignment() {
        ABAssignment existingAssignment = ABAssignment.builder()
                .id(UUID.randomUUID().toString())
                .experiment(testExperiment)
                .variant(controlVariant)
                .visitorId(testVisitorId)
                .build();

        when(experimentRepository.findActiveExperimentsWithVariants("running", "landing"))
                .thenReturn(Collections.singletonList(testExperiment));
        when(assignmentRepository.findByVisitorIdAndExperimentId(testVisitorId, testExperiment.getId()))
                .thenReturn(Optional.of(existingAssignment));

        ActiveExperimentsResponse response = abTestService.getActiveExperiments("landing", testVisitorId);

        assertThat(response.getExperiments()).hasSize(1);
        assertThat(response.getExperiments().get(0).getVariantId()).isEqualTo(controlVariant.getId());
        verify(assignmentRepository, never()).save(any(ABAssignment.class));
    }

    @Test
    @DisplayName("전환 기록 성공")
    void recordConversion_success() {
        ABAssignment assignment = ABAssignment.builder()
                .id(UUID.randomUUID().toString())
                .experiment(testExperiment)
                .variant(controlVariant)
                .visitorId(testVisitorId)
                .build();

        ConversionRequest request = ConversionRequest.builder()
                .experimentId(testExperiment.getId())
                .visitorId(testVisitorId)
                .eventType("cta_click")
                .eventData(Map.of("button", "start_free"))
                .build();

        when(assignmentRepository.findByVisitorIdAndExperimentId(testVisitorId, testExperiment.getId()))
                .thenReturn(Optional.of(assignment));
        when(conversionRepository.save(any(ABConversion.class)))
                .thenAnswer(invocation -> {
                    ABConversion conv = invocation.getArgument(0);
                    conv.setId(UUID.randomUUID().toString());
                    conv.setConvertedAt(LocalDateTime.now());
                    return conv;
                });

        ConversionResponse response = abTestService.recordConversion(request);

        assertThat(response).isNotNull();
        assertThat(response.getAssignmentId()).isEqualTo(assignment.getId());
        assertThat(response.getEventType()).isEqualTo("cta_click");
    }

    @Test
    @DisplayName("전환 기록 실패 - 할당 없음")
    void recordConversion_failsWithoutAssignment() {
        ConversionRequest request = ConversionRequest.builder()
                .experimentId(testExperiment.getId())
                .visitorId(testVisitorId)
                .eventType("cta_click")
                .build();

        when(assignmentRepository.findByVisitorIdAndExperimentId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> abTestService.recordConversion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Assignment not found");
    }

    @Test
    @DisplayName("실험 생성 성공")
    void createExperiment_success() {
        ExperimentCreateRequest request = ExperimentCreateRequest.builder()
                .name("new-experiment")
                .description("Test description")
                .targetPage("landing")
                .targetElement("hero_cta")
                .trafficPercentage(100)
                .variants(Arrays.asList(
                        ExperimentCreateRequest.VariantCreateRequest.builder()
                                .name("control")
                                .weight(50)
                                .isControl(true)
                                .content(Map.of("text", "Get Started"))
                                .build(),
                        ExperimentCreateRequest.VariantCreateRequest.builder()
                                .name("treatment")
                                .weight(50)
                                .isControl(false)
                                .content(Map.of("text", "Start Free Trial"))
                                .build()
                ))
                .build();

        when(experimentRepository.existsByName("new-experiment")).thenReturn(false);
        when(experimentRepository.save(any(ABExperiment.class)))
                .thenAnswer(invocation -> {
                    ABExperiment exp = invocation.getArgument(0);
                    exp.setId(UUID.randomUUID().toString());
                    exp.setCreatedAt(LocalDateTime.now());
                    exp.setUpdatedAt(LocalDateTime.now());
                    return exp;
                });
        when(variantRepository.save(any(ABVariant.class)))
                .thenAnswer(invocation -> {
                    ABVariant var = invocation.getArgument(0);
                    var.setId(UUID.randomUUID().toString());
                    var.setCreatedAt(LocalDateTime.now());
                    return var;
                });
        when(experimentRepository.findByIdWithVariants(anyString()))
                .thenReturn(Optional.of(testExperiment));
        when(variantRepository.findByExperimentId(anyString()))
                .thenReturn(Arrays.asList(controlVariant, treatmentVariant));

        ExperimentResponse response = abTestService.createExperiment(request);

        assertThat(response).isNotNull();
        verify(experimentRepository).save(any(ABExperiment.class));
        verify(variantRepository, times(2)).save(any(ABVariant.class));
    }

    @Test
    @DisplayName("실험 생성 실패 - 중복 이름")
    void createExperiment_failsWithDuplicateName() {
        ExperimentCreateRequest request = ExperimentCreateRequest.builder()
                .name("existing-experiment")
                .build();

        when(experimentRepository.existsByName("existing-experiment")).thenReturn(true);

        assertThatThrownBy(() -> abTestService.createExperiment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("실험 시작")
    void startExperiment_success() {
        when(experimentRepository.findById(testExperiment.getId()))
                .thenReturn(Optional.of(testExperiment));
        when(experimentRepository.save(any(ABExperiment.class)))
                .thenReturn(testExperiment);
        when(experimentRepository.findByIdWithVariants(anyString()))
                .thenReturn(Optional.of(testExperiment));
        when(variantRepository.findByExperimentId(anyString()))
                .thenReturn(Arrays.asList(controlVariant, treatmentVariant));

        ExperimentResponse response = abTestService.startExperiment(testExperiment.getId());

        assertThat(response).isNotNull();
        verify(experimentRepository).save(any(ABExperiment.class));
    }

    @Test
    @DisplayName("실험 통계 조회")
    void getExperimentStats_success() {
        when(experimentRepository.findByIdWithVariants(testExperiment.getId()))
                .thenReturn(Optional.of(testExperiment));
        when(assignmentRepository.countByExperimentId(testExperiment.getId()))
                .thenReturn(100L);
        when(assignmentRepository.countByVariantId(controlVariant.getId()))
                .thenReturn(50L);
        when(assignmentRepository.countByVariantId(treatmentVariant.getId()))
                .thenReturn(50L);
        when(conversionRepository.countByVariantId(controlVariant.getId()))
                .thenReturn(5L);
        when(conversionRepository.countByVariantId(treatmentVariant.getId()))
                .thenReturn(10L);

        ExperimentStatsResponse response = abTestService.getExperimentStats(testExperiment.getId());

        assertThat(response).isNotNull();
        assertThat(response.getTotalAssignments()).isEqualTo(100L);
        assertThat(response.getVariants()).hasSize(2);
    }

    @Test
    @DisplayName("가중치 기반 변형 선택")
    void selectVariantByWeight_distributesCorrectly() {
        Map<String, Integer> selectionCounts = new HashMap<>();
        selectionCounts.put(controlVariant.getId(), 0);
        selectionCounts.put(treatmentVariant.getId(), 0);

        when(assignmentRepository.findByVisitorIdAndExperimentId(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(assignmentRepository.save(any(ABAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        for (int i = 0; i < 1000; i++) {
            ABVariant selected = abTestService.getOrAssignVariant(testExperiment, UUID.randomUUID().toString());
            if (selected != null) {
                selectionCounts.merge(selected.getId(), 1, Integer::sum);
            }
        }

        int controlCount = selectionCounts.get(controlVariant.getId());
        int treatmentCount = selectionCounts.get(treatmentVariant.getId());

        assertThat(controlCount).isBetween(400, 600);
        assertThat(treatmentCount).isBetween(400, 600);
    }
}
