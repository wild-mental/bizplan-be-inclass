package vibe.bizplan.bizplan_be_inclass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vibe.bizplan.bizplan_be_inclass.dto.financial.*;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.service.FinancialService;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FinancialController 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FinancialController 테스트")
class FinancialControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private FinancialService financialService;

    @InjectMocks
    private FinancialController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/projects/{projectId}/financial/simulate - 성공")
    void simulate_success() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        FinancialSimulateRequest request = FinancialSimulateRequest.builder()
                .inputs(FinancialSimulateRequest.FinancialInputs.builder()
                        .customers(FinancialSimulateRequest.CustomerInfo.builder()
                                .year1(100)
                                .year2(200)
                                .year3(300)
                                .build())
                        .pricing(FinancialSimulateRequest.PricingInfo.builder()
                                .pricePerCustomer(10000)
                                .build())
                        .costs(FinancialSimulateRequest.CostInfo.builder()
                                .fixedCosts(Map.of("rent", 1000000L))
                                .variableCosts(Map.of("material", 2000L))
                                .build())
                        .build())
                .build();

        FinancialSimulateResponse response = FinancialSimulateResponse.builder()
                .summary(FinancialSimulateResponse.Summary.builder()
                        .breakEvenMonth(12)
                        .cumulativeProfitYear3(10000000L)
                        .irr(35.2)
                        .paybackPeriod("12개월")
                        .build())
                .yearlyProjections(Collections.emptyList())
                .monthlyProjections(Collections.emptyList())
                .charts(FinancialSimulateResponse.ChartData.builder()
                        .revenueGrowth(Collections.emptyList())
                        .profitMargin(Collections.emptyList())
                        .build())
                .risks(Collections.emptyList())
                .build();

        given(financialService.simulate(eq(projectId), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/projects/{projectId}/financial/simulate", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.summary.breakEvenMonth").value(12));
    }

    @Test
    @DisplayName("POST /api/v1/projects/{projectId}/financial/simulate - 프로젝트 없음")
    void simulate_projectNotFound() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        FinancialSimulateRequest request = FinancialSimulateRequest.builder()
                .inputs(FinancialSimulateRequest.FinancialInputs.builder()
                        .customers(FinancialSimulateRequest.CustomerInfo.builder()
                                .year1(100)
                                .year2(200)
                                .year3(300)
                                .build())
                        .pricing(FinancialSimulateRequest.PricingInfo.builder()
                                .pricePerCustomer(10000)
                                .build())
                        .costs(FinancialSimulateRequest.CostInfo.builder()
                                .fixedCosts(Map.of())
                                .variableCosts(Map.of())
                                .build())
                        .build())
                .build();

        given(financialService.simulate(eq(projectId), any()))
                .willThrow(new ResourceNotFoundException("프로젝트를 찾을 수 없습니다"));

        // when & then
        mockMvc.perform(post("/api/v1/projects/{projectId}/financial/simulate", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}

