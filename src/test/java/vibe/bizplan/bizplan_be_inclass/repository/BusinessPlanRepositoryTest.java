package vibe.bizplan.bizplan_be_inclass.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.entity.BusinessPlan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BusinessPlanRepository 테스트
 * 
 * Rule 306: Repository Layer 테스트는 실제 DB 사용
 * Rule 303: 실제 DB (H2) 사용하여 Query Methods 검증
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("BusinessPlanRepository 테스트")
class BusinessPlanRepositoryTest {

    @Autowired
    private BusinessPlanRepository repository;

    private BusinessPlan testBusinessPlan;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        testBusinessPlan = BusinessPlan.builder()
                .businessPlanId("bp-2025-12-20-test123")
                .projectId(UUID.randomUUID())
                .userId("user-123")
                .templateType("pre-startup")
                .requestDataJson("{\"requestInfo\":{\"templateType\":\"pre-startup\"}}")
                .responseSectionsJson("[{\"id\":\"section-1\",\"title\":\"테스트 섹션\"}]")
                .geminiMetadataJson("{\"promptTokens\":100,\"completionTokens\":200}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("save - BusinessPlan을 저장할 수 있다")
    void save_validBusinessPlan_savesSuccessfully() {
        // when
        BusinessPlan saved = repository.save(testBusinessPlan);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBusinessPlanId()).isEqualTo("bp-2025-12-20-test123");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findByBusinessPlanId - businessPlanId로 조회할 수 있다")
    void findByBusinessPlanId_validId_returnsBusinessPlan() {
        // given
        BusinessPlan saved = repository.save(testBusinessPlan);

        // when
        Optional<BusinessPlan> found = repository.findByBusinessPlanId("bp-2025-12-20-test123");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getBusinessPlanId()).isEqualTo("bp-2025-12-20-test123");
    }

    @Test
    @DisplayName("findByProjectId - projectId로 조회할 수 있다")
    void findByProjectId_validProjectId_returnsBusinessPlans() {
        // given
        UUID projectId = UUID.randomUUID();
        BusinessPlan plan1 = BusinessPlan.builder()
                .businessPlanId("bp-2025-12-20-plan1")
                .projectId(projectId)
                .templateType("pre-startup")
                .requestDataJson("{}")
                .responseSectionsJson("[]")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        BusinessPlan plan2 = BusinessPlan.builder()
                .businessPlanId("bp-2025-12-20-plan2")
                .projectId(projectId)
                .templateType("pre-startup")
                .requestDataJson("{}")
                .responseSectionsJson("[]")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        repository.save(plan1);
        repository.save(plan2);

        // when
        List<BusinessPlan> found = repository.findByProjectId(projectId);

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(BusinessPlan::getBusinessPlanId)
                .containsExactlyInAnyOrder("bp-2025-12-20-plan2", "bp-2025-12-20-plan1"); // 내림차순
    }

    @Test
    @DisplayName("findByUserId - userId로 조회할 수 있다")
    void findByUserId_validUserId_returnsBusinessPlans() {
        // given
        String userId = "user-456";
        BusinessPlan plan = BusinessPlan.builder()
                .businessPlanId("bp-2025-12-20-user-plan")
                .userId(userId)
                .templateType("pre-startup")
                .requestDataJson("{}")
                .responseSectionsJson("[]")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        repository.save(plan);

        // when
        List<BusinessPlan> found = repository.findByUserId(userId);

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("findByTemplateType - templateType으로 조회할 수 있다")
    void findByTemplateType_validTemplateType_returnsBusinessPlans() {
        // given
        BusinessPlan plan = BusinessPlan.builder()
                .businessPlanId("bp-2025-12-20-template-plan")
                .templateType("bank-loan")
                .requestDataJson("{}")
                .responseSectionsJson("[]")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        repository.save(plan);

        // when
        List<BusinessPlan> found = repository.findByTemplateType("bank-loan");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTemplateType()).isEqualTo("bank-loan");
    }
}
