package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vibe.bizplan.bizplan_be_inclass.dto.financial.*;
import vibe.bizplan.bizplan_be_inclass.entity.FinancialData;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.FinancialDataRepository;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FinancialService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FinancialService 테스트")
class FinancialServiceTest {

    @Mock
    private FinancialDataRepository financialDataRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private FinancialService financialService;
    
    private ObjectMapper objectMapper;

    private UUID projectId;
    private Project testProject;
    private FinancialSimulateRequest request;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // FinancialService에 실제 ObjectMapper 주입
        org.springframework.test.util.ReflectionTestUtils.setField(financialService, "objectMapper", objectMapper);
        projectId = UUID.randomUUID();
        testProject = Project.builder()
                .id(projectId)
                .name("테스트 프로젝트")
                .templateCode("pre-startup")
                .status(Project.ProjectStatus.draft)
                .currentStep(1)
                .build();

        FinancialSimulateRequest.FinancialInputs inputs = FinancialSimulateRequest.FinancialInputs.builder()
                .customers(FinancialSimulateRequest.CustomerInfo.builder()
                        .year1(100)
                        .year2(200)
                        .year3(300)
                        .build())
                .pricing(FinancialSimulateRequest.PricingInfo.builder()
                        .pricePerCustomer(10000)
                        .build())
                .costs(FinancialSimulateRequest.CostInfo.builder()
                        .fixedCosts(Map.of("rent", 1000000L, "salary", 5000000L))
                        .variableCosts(Map.of("material", 2000L))
                        .marketing(Map.of("year1", 2000000L, "year2", 3000000L, "year3", 4000000L))
                        .build())
                .build();

        request = FinancialSimulateRequest.builder()
                .inputs(inputs)
                .build();
    }

    @Test
    @DisplayName("재무 시뮬레이션 성공")
    void simulate_success() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(financialDataRepository.findByProject(any())).thenReturn(Optional.empty());
        when(financialDataRepository.save(any(FinancialData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        FinancialSimulateResponse response = financialService.simulate(projectId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getYearlyProjections()).hasSize(3);
        assertThat(response.getMonthlyProjections()).hasSize(24);
        assertThat(response.getSummary().getBreakEvenMonth()).isGreaterThan(0);
        assertThat(response.getCharts()).isNotNull();
        verify(financialDataRepository).save(any(FinancialData.class));
    }

    @Test
    @DisplayName("재무 시뮬레이션 실패 - 프로젝트 없음")
    void simulate_projectNotFound() {
        // given
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> financialService.simulate(projectId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로젝트를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("재무 시뮬레이션 - 손익분기점 계산")
    void simulate_breakEvenCalculation() {
        // given
        FinancialSimulateRequest.FinancialInputs inputs = FinancialSimulateRequest.FinancialInputs.builder()
                .customers(FinancialSimulateRequest.CustomerInfo.builder()
                        .year1(500)
                        .year2(1000)
                        .year3(1500)
                        .build())
                .pricing(FinancialSimulateRequest.PricingInfo.builder()
                        .pricePerCustomer(50000)
                        .build())
                .costs(FinancialSimulateRequest.CostInfo.builder()
                        .fixedCosts(Map.of("rent", 2000000L, "salary", 10000000L))
                        .variableCosts(Map.of("material", 10000L))
                        .marketing(Map.of("year1", 5000000L))
                        .build())
                .build();

        FinancialSimulateRequest request = FinancialSimulateRequest.builder()
                .inputs(inputs)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(financialDataRepository.findByProject(any())).thenReturn(Optional.empty());
        when(financialDataRepository.save(any(FinancialData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        FinancialSimulateResponse response = financialService.simulate(projectId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSummary().getBreakEvenMonth()).isBetween(1, 24);
    }
}

