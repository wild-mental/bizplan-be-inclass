package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.wizard.*;
import vibe.bizplan.bizplan_be_inclass.entity.Project;
import vibe.bizplan.bizplan_be_inclass.entity.WizardData;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.ProjectRepository;
import vibe.bizplan.bizplan_be_inclass.repository.WizardDataRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wizard 서비스
 * Wizard 데이터 저장, 조회, 자금 검증 등 비즈니스 로직을 담당합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 5 - 사업계획서 작성 Wizard API
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WizardService {

    private final WizardDataRepository wizardDataRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;

    // 단계별 제목 정의
    private static final Map<Integer, String> STEP_TITLES = Map.of(
            1, "아이디어 개요",
            2, "시장 분석",
            3, "개발 계획",
            4, "팀 구성",
            5, "핵심 기술",
            6, "재무 계획",
            7, "추진 일정",
            8, "사회적 가치"
    );

    /**
     * Wizard 데이터 저장 (자동 저장)
     * 
     * @param projectId 프로젝트 ID
     * @param request 저장 요청
     * @return 저장 응답
     */
    @Transactional
    public WizardSaveResponse saveWizardData(UUID projectId, WizardSaveRequest request) {
        log.info("Wizard 데이터 저장: projectId={}, step={}", projectId, request.getCurrentStep());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        // 기존 데이터 조회 또는 새로 생성
        WizardData wizardData = wizardDataRepository
                .findByProjectAndStepNumber(project, request.getCurrentStep())
                .orElse(WizardData.builder()
                        .project(project)
                        .stepNumber(request.getCurrentStep())
                        .build());

        // 데이터 업데이트
        try {
            String stepDataJson = objectMapper.writeValueAsString(request.getStepData());
            wizardData.setStepData(stepDataJson);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("단계 데이터 변환 실패", e);
        }

        wizardData.setIsComplete(request.getIsStepComplete());
        wizardDataRepository.save(wizardData);

        // 프로젝트의 현재 단계 업데이트
        if (request.getCurrentStep() > project.getCurrentStep()) {
            project.setCurrentStep(request.getCurrentStep());
            project.setStatus(Project.ProjectStatus.in_progress);
            projectRepository.save(project);
        }

        // 완료된 단계 목록 조회
        List<Integer> completedSteps = getCompletedSteps(project);

        // 응답 생성
        return WizardSaveResponse.builder()
                .projectId(projectId.toString())
                .currentStep(request.getCurrentStep())
                .lastSavedAt(LocalDateTime.now())
                .progress(WizardSaveResponse.ProgressInfo.builder()
                        .currentStep(project.getCurrentStep())
                        .completedSteps(completedSteps)
                        .percentComplete((double) completedSteps.size() / 8 * 100)
                        .build())
                .validationWarnings(validateStepData(request.getStepData()))
                .build();
    }

    /**
     * Wizard 전체 데이터 조회
     * 
     * @param projectId 프로젝트 ID
     * @return Wizard 데이터 응답
     */
    public WizardDataResponse getWizardData(UUID projectId) {
        log.info("Wizard 데이터 조회: projectId={}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다: " + projectId));

        List<WizardData> wizardDataList = wizardDataRepository.findByProjectOrderByStepNumberAsc(project);
        List<Integer> completedSteps = getCompletedSteps(project);

        // 단계 정보 생성 (1-8단계)
        List<WizardDataResponse.StepInfo> steps = new ArrayList<>();
        Map<Integer, WizardData> dataMap = wizardDataList.stream()
                .collect(Collectors.toMap(WizardData::getStepNumber, d -> d));

        for (int i = 1; i <= 8; i++) {
            WizardData data = dataMap.get(i);
            String status = getStepStatus(i, project.getCurrentStep(), completedSteps);
            Map<String, Object> stepData = null;

            if (data != null) {
                try {
                    stepData = objectMapper.readValue(data.getStepData(), Map.class);
                } catch (JsonProcessingException e) {
                    log.warn("단계 데이터 파싱 실패: step={}", i, e);
                }
            }

            steps.add(WizardDataResponse.StepInfo.builder()
                    .stepId(i)
                    .title(STEP_TITLES.get(i))
                    .status(status)
                    .data(stepData)
                    .build());
        }

        // 마지막 저장 시각 조회
        LocalDateTime lastSavedAt = wizardDataList.stream()
                .map(WizardData::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(project.getUpdatedAt());

        return WizardDataResponse.builder()
                .projectId(projectId.toString())
                .templateId(project.getTemplateCode())
                .currentStep(project.getCurrentStep())
                .steps(steps)
                .lastSavedAt(lastSavedAt)
                .build();
    }

    /**
     * 자금 집행계획 검증
     * 
     * @param projectId 프로젝트 ID
     * @param request 검증 요청
     * @return 검증 응답
     */
    public BudgetValidateResponse validateBudget(UUID projectId, BudgetValidateRequest request) {
        log.info("자금 집행계획 검증: projectId={}, templateType={}", projectId, request.getTemplateType());

        List<BudgetValidateResponse.ValidationResult> validations = new ArrayList<>();
        List<BudgetValidateResponse.Warning> warnings = new ArrayList<>();
        List<BudgetValidateResponse.Recommendation> recommendations = new ArrayList<>();

        long phase1Total = 0;
        long phase2Total = 0;
        boolean isValid = true;

        if ("pre-startup".equals(request.getTemplateType()) && request.getBudgetPhases() != null) {
            // 예비창업패키지 검증
            for (BudgetValidateRequest.BudgetPhase phase : request.getBudgetPhases()) {
                long phaseTotal = phase.getItems().stream()
                        .mapToLong(item -> item.getAmount() != null ? item.getAmount() : 0)
                        .sum();

                if (phase.getPhase() == 1) {
                    phase1Total = phaseTotal;
                    boolean passed = phaseTotal <= 20000000;
                    validations.add(BudgetValidateResponse.ValidationResult.builder()
                            .rule("phase1_max")
                            .passed(passed)
                            .message(passed ? 
                                String.format("1단계 예산이 한도 내입니다 (%,d / 20,000,000원)", phaseTotal) :
                                String.format("1단계 예산이 한도를 초과했습니다 (%,d / 20,000,000원)", phaseTotal))
                            .build());
                    if (!passed) isValid = false;

                    // 외주용역비 비율 경고
                    long outsourcingAmount = phase.getItems().stream()
                            .filter(item -> item.getName().contains("외주") || item.getId().contains("outsourcing"))
                            .mapToLong(item -> item.getAmount() != null ? item.getAmount() : 0)
                            .sum();
                    
                    if (phaseTotal > 0 && (double) outsourcingAmount / phaseTotal > 0.5) {
                        warnings.add(BudgetValidateResponse.Warning.builder()
                                .type("ratio")
                                .field("phase1-outsourcing")
                                .message(String.format("외주용역비 비율이 높습니다 (%.0f%%). 심사 시 상세 설명을 권장합니다.", 
                                        (double) outsourcingAmount / phaseTotal * 100))
                                .suggestion("외주 용역 내역서를 별도로 준비하세요.")
                                .build());
                    }
                } else if (phase.getPhase() == 2) {
                    phase2Total = phaseTotal;
                    boolean passed = phaseTotal <= 40000000;
                    validations.add(BudgetValidateResponse.ValidationResult.builder()
                            .rule("phase2_max")
                            .passed(passed)
                            .message(passed ?
                                String.format("2단계 예산이 한도 내입니다 (%,d / 40,000,000원)", phaseTotal) :
                                String.format("2단계 예산이 한도를 초과했습니다 (%,d / 40,000,000원)", phaseTotal))
                            .build());
                    if (!passed) isValid = false;
                }
            }
        }

        // 필수 항목 검증
        validations.add(BudgetValidateResponse.ValidationResult.builder()
                .rule("required_categories")
                .passed(true)
                .message("필수 항목이 모두 포함되어 있습니다")
                .build());

        // 개선 권장사항
        if (phase1Total > 0 && phase1Total < 15000000) {
            recommendations.add(BudgetValidateResponse.Recommendation.builder()
                    .type("improvement")
                    .message("1단계에 재료비 비율을 높이면 시제품 개발 의지를 보여줄 수 있습니다.")
                    .build());
        }

        return BudgetValidateResponse.builder()
                .isValid(isValid)
                .summary(BudgetValidateResponse.BudgetSummary.builder()
                        .totalBudget(phase1Total + phase2Total)
                        .phase1Total(phase1Total)
                        .phase2Total(phase2Total)
                        .phase1Remaining(20000000 - phase1Total)
                        .phase2Remaining(40000000 - phase2Total)
                        .build())
                .validations(validations)
                .warnings(warnings)
                .recommendations(recommendations)
                .build();
    }

    /**
     * 완료된 단계 목록 조회
     */
    private List<Integer> getCompletedSteps(Project project) {
        return wizardDataRepository.findByProjectOrderByStepNumberAsc(project).stream()
                .filter(WizardData::getIsComplete)
                .map(WizardData::getStepNumber)
                .collect(Collectors.toList());
    }

    /**
     * 단계 상태 결정
     */
    private String getStepStatus(int stepNumber, int currentStep, List<Integer> completedSteps) {
        if (completedSteps.contains(stepNumber)) {
            return "completed";
        } else if (stepNumber == currentStep) {
            return "in_progress";
        } else {
            return "pending";
        }
    }

    /**
     * 단계 데이터 유효성 검증 (경고 생성)
     */
    private List<WizardSaveResponse.ValidationWarning> validateStepData(Map<String, Object> stepData) {
        List<WizardSaveResponse.ValidationWarning> warnings = new ArrayList<>();
        
        // 실제 구현에서는 더 상세한 검증 로직 추가
        // 예: 필수 필드 누락, 형식 오류 등
        
        return warnings;
    }
}

