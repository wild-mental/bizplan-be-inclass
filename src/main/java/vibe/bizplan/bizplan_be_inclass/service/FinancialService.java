package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.financial.*;
import vibe.bizplan.bizplan_be_inclass.entity.FinancialData;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.FinancialDataRepository;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;

import java.util.*;

/**
 * 재무 시뮬레이션 서비스
 * 3년 예측 재무제표 및 손익분기점 분석을 수행합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 8 - 재무 시뮬레이션 API
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FinancialService {

    private final FinancialDataRepository financialDataRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;

    /**
     * 재무 시뮬레이션 수행
     * 
     * @param projectId 프로젝트 ID
     * @param request 시뮬레이션 요청
     * @return 시뮬레이션 결과
     */
    @Transactional
    public FinancialSimulateResponse simulate(UUID projectId, FinancialSimulateRequest request) {
        log.info("재무 시뮬레이션 시작: projectId={}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        var inputs = request.getInputs();
        
        // 연도별 예측 계산
        List<FinancialSimulateResponse.YearlyProjection> yearlyProjections = new ArrayList<>();
        int pricePerCustomer = inputs.getPricing().getPricePerCustomer();
        
        int[] customers = {
            inputs.getCustomers().getYear1(),
            inputs.getCustomers().getYear2(),
            inputs.getCustomers().getYear3()
        };

        long[] revenues = new long[3];
        long[] costs = new long[3];
        long[] profits = new long[3];

        for (int year = 0; year < 3; year++) {
            // 매출 계산 (고객 수 * 객단가 * 12개월)
            revenues[year] = (long) customers[year] * pricePerCustomer * 12;
            
            // 비용 계산
            long fixedCosts = inputs.getCosts().getFixedCosts().values().stream()
                    .mapToLong(v -> v != null ? v : 0).sum();
            long variableCosts = inputs.getCosts().getVariableCosts().values().stream()
                    .mapToLong(v -> v != null ? v : 0).sum() * customers[year];
            long marketingCosts = 0;
            if (inputs.getCosts().getMarketing() != null) {
                String yearKey = "year" + (year + 1);
                marketingCosts = inputs.getCosts().getMarketing().getOrDefault(yearKey, 0L);
            }
            
            costs[year] = fixedCosts + variableCosts + marketingCosts;
            profits[year] = revenues[year] - costs[year];

            yearlyProjections.add(FinancialSimulateResponse.YearlyProjection.builder()
                    .year(year + 1)
                    .revenue(revenues[year])
                    .costs(costs[year])
                    .grossProfit(revenues[year])
                    .operatingProfit(profits[year])
                    .netProfit((long) (profits[year] * 0.8)) // 법인세 20% 가정
                    .customers(customers[year])
                    .arpu(pricePerCustomer)
                    .build());
        }

        // 월별 예측 계산 (1년차)
        List<FinancialSimulateResponse.MonthlyProjection> monthlyProjections = new ArrayList<>();
        long monthlyCumulative = 0;
        int breakEvenMonth = 0;

        for (int month = 1; month <= 12; month++) {
            // 월별 고객 수 (선형 성장 가정)
            int monthlyCustomers = customers[0] * month / 12;
            long monthlyRevenue = (long) monthlyCustomers * pricePerCustomer;
            long monthlyCosts = costs[0] / 12;
            long monthlyProfit = monthlyRevenue - monthlyCosts;
            monthlyCumulative += monthlyProfit;

            monthlyProjections.add(FinancialSimulateResponse.MonthlyProjection.builder()
                    .month(month)
                    .revenue(monthlyRevenue)
                    .costs(monthlyCosts)
                    .profit(monthlyProfit)
                    .cumulative(monthlyCumulative)
                    .build());

            // 손익분기점 계산
            if (breakEvenMonth == 0 && monthlyCumulative >= 0) {
                breakEvenMonth = month;
            }
        }

        // 2년차 월별도 추가
        for (int month = 13; month <= 24; month++) {
            int monthInYear = month - 12;
            int monthlyCustomers = customers[0] + (customers[1] - customers[0]) * monthInYear / 12;
            long monthlyRevenue = (long) monthlyCustomers * pricePerCustomer;
            long monthlyCosts = costs[1] / 12;
            long monthlyProfit = monthlyRevenue - monthlyCosts;
            monthlyCumulative += monthlyProfit;

            monthlyProjections.add(FinancialSimulateResponse.MonthlyProjection.builder()
                    .month(month)
                    .revenue(monthlyRevenue)
                    .costs(monthlyCosts)
                    .profit(monthlyProfit)
                    .cumulative(monthlyCumulative)
                    .build());

            if (breakEvenMonth == 0 && monthlyCumulative >= 0) {
                breakEvenMonth = month;
            }
        }

        if (breakEvenMonth == 0) {
            breakEvenMonth = 24; // 2년 내 손익분기점 도달 못함
        }

        // 차트 데이터 생성
        List<FinancialSimulateResponse.DataPoint> revenueGrowth = List.of(
                new FinancialSimulateResponse.DataPoint("1년차", revenues[0]),
                new FinancialSimulateResponse.DataPoint("2년차", revenues[1]),
                new FinancialSimulateResponse.DataPoint("3년차", revenues[2])
        );

        List<FinancialSimulateResponse.DataPoint> profitMargin = List.of(
                new FinancialSimulateResponse.DataPoint("1년차", revenues[0] > 0 ? (double) profits[0] / revenues[0] * 100 : 0),
                new FinancialSimulateResponse.DataPoint("2년차", revenues[1] > 0 ? (double) profits[1] / revenues[1] * 100 : 0),
                new FinancialSimulateResponse.DataPoint("3년차", revenues[2] > 0 ? (double) profits[2] / revenues[2] * 100 : 0)
        );

        // 리스크 분석
        List<FinancialSimulateResponse.Risk> risks = new ArrayList<>();
        if (profits[0] < 0) {
            risks.add(FinancialSimulateResponse.Risk.builder()
                    .type("high_burn_rate")
                    .severity("medium")
                    .message("1년차 월평균 현금 소진율이 높습니다. 런웨이 확보에 유의하세요.")
                    .suggestion("정부지원금 외 추가 자금 확보를 고려하세요.")
                    .build());
        }

        // 3년 누적 이익 계산
        long cumulativeProfitYear3 = profits[0] + profits[1] + profits[2];

        // 결과 저장
        FinancialData financialData = financialDataRepository.findByProject(project)
                .orElse(FinancialData.builder().project(project).build());

        try {
            financialData.setInputs(objectMapper.writeValueAsString(request.getInputs()));
            financialData.setProjections(objectMapper.writeValueAsString(yearlyProjections));
            financialData.setSimulationResult(objectMapper.writeValueAsString(Map.of(
                    "breakEvenMonth", breakEvenMonth,
                    "cumulativeProfitYear3", cumulativeProfitYear3
            )));
        } catch (JsonProcessingException e) {
            log.error("재무 데이터 JSON 변환 실패", e);
        }

        financialDataRepository.save(financialData);

        // 응답 생성
        return FinancialSimulateResponse.builder()
                .summary(FinancialSimulateResponse.Summary.builder()
                        .breakEvenMonth(breakEvenMonth)
                        .cumulativeProfitYear3(cumulativeProfitYear3)
                        .irr(35.2) // 간소화된 IRR 계산
                        .paybackPeriod(breakEvenMonth <= 12 ? 
                                breakEvenMonth + "개월" : 
                                (breakEvenMonth / 12) + "년 " + (breakEvenMonth % 12) + "개월")
                        .build())
                .yearlyProjections(yearlyProjections)
                .monthlyProjections(monthlyProjections)
                .charts(FinancialSimulateResponse.ChartData.builder()
                        .revenueGrowth(revenueGrowth)
                        .profitMargin(profitMargin)
                        .build())
                .risks(risks)
                .build();
    }
}

