package vibe.makersround.makersround_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.makersround.makersround_backend.dto.evaluation.*;
import vibe.makersround.makersround_backend.entity.*;
import vibe.makersround.makersround_backend.exception.ResourceNotFoundException;
import vibe.makersround.makersround_backend.repository.*;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 평가 서비스
 * 사업계획서 6대 영역 평가를 수행합니다.
 * Gemini AI를 사용하여 실제 평가를 수행합니다.
 * 
 * @see PRE-SUB-FUNC-002.md Section 6 - AI 평가 API
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EvaluationScoreRepository evaluationScoreRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ChatModel chatModel;
    
    @Value("${spring.ai.google.genai.chat.options.model:gemini-2.5-flash-lite}")
    private String geminiModelName;

    // 영역별 정보
    private static final Map<String, AreaInfo> AREA_INFO = Map.of(
            "market", new AreaInfo("시장성", "M", "purple"),
            "ability", new AreaInfo("수행능력", "A", "blue"),
            "technology", new AreaInfo("핵심기술", "K", "cyan"),
            "economics", new AreaInfo("경제성", "E", "emerald"),
            "realization", new AreaInfo("실현가능성", "R", "orange"),
            "social", new AreaInfo("사회적가치", "S", "pink")
    );

    /**
     * 평가 요청
     * 
     * @param request 평가 요청
     * @return 평가 상태 응답
     */
    @Transactional
    public EvaluationStatusResponse createEvaluation(EvaluationRequest request) {
        log.info("AI 평가 요청: projectId={}", request.getProjectId());

        Project project;
        if ("demo".equals(request.getProjectId())) {
            project = getOrCreateDemoProject();
        } else {
            project = projectRepository.findById(UUID.fromString(request.getProjectId()))
                    .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다: " + request.getProjectId()));
        }

        // 평가 엔티티 생성
        Evaluation evaluation = Evaluation.builder()
                .project(project)
                .evaluationType(Evaluation.EvaluationType.valueOf(
                        request.getEvaluationType() != null ? request.getEvaluationType() : "demo"))
                .status(Evaluation.EvaluationStatus.processing)
                .build();

        try {
            evaluation.setInputData(objectMapper.writeValueAsString(request.getInputData()));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("입력 데이터 변환 실패", e);
        }

        evaluation = evaluationRepository.save(evaluation);

        // 비동기 평가 시작 (실제로는 별도 스레드에서 처리)
        final UUID evaluationId = evaluation.getId();
        CompletableFuture.runAsync(() -> processEvaluation(evaluationId));

        // 초기 상태 응답
        return EvaluationStatusResponse.builder()
                .evaluationId(evaluation.getId().toString())
                .status("processing")
                .estimatedTime(30)
                .queuePosition(1)
                .stages(getInitialStages())
                .build();
    }

    /**
     * 평가 상태 조회
     * 
     * @param evaluationId 평가 ID
     * @return 평가 상태 응답
     */
    public EvaluationStatusResponse getEvaluationStatus(UUID evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("평가를 찾을 수 없습니다: " + evaluationId));

        List<EvaluationScore> scores = evaluationScoreRepository.findByEvaluation(evaluation);

        List<EvaluationStatusResponse.StageInfo> stages = new ArrayList<>();
        int completedCount = 0;

        for (String areaCode : List.of("market", "ability", "technology", "economics", "realization", "social")) {
            Optional<EvaluationScore> score = scores.stream()
                    .filter(s -> s.getAreaCode().name().equals(areaCode))
                    .findFirst();

            String status = score.isPresent() ? "completed" : 
                    (evaluation.getStatus() == Evaluation.EvaluationStatus.processing && completedCount == stages.size() ? "processing" : "pending");
            
            stages.add(EvaluationStatusResponse.StageInfo.builder()
                    .id(areaCode)
                    .name(AREA_INFO.get(areaCode).label)
                    .status(status)
                    .score(score.map(EvaluationScore::getScore).orElse(null))
                    .build());

            if (score.isPresent()) completedCount++;
        }

        int progress = completedCount * 100 / 6;

        return EvaluationStatusResponse.builder()
                .evaluationId(evaluationId.toString())
                .status(evaluation.getStatus().name())
                .progress(progress)
                .currentStage(completedCount < 6 ? stages.get(completedCount).getId() : null)
                .stages(stages)
                .estimatedRemaining(progress < 100 ? (100 - progress) * 30 / 100 : 0)
                .build();
    }

    /**
     * 평가 결과 조회
     * 
     * @param evaluationId 평가 ID
     * @return 평가 결과 응답
     */
    public EvaluationResultResponse getEvaluationResult(UUID evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("평가를 찾을 수 없습니다: " + evaluationId));

        if (evaluation.getStatus() != Evaluation.EvaluationStatus.completed) {
            throw new IllegalStateException("평가가 아직 완료되지 않았습니다");
        }

        List<EvaluationScore> scores = evaluationScoreRepository.findByEvaluation(evaluation);
        boolean isDemo = evaluation.getEvaluationType() == Evaluation.EvaluationType.demo;

        // 영역별 점수 맵 생성
        Map<String, EvaluationResultResponse.ScoreInfo> scoreMap = new LinkedHashMap<>();
        for (EvaluationScore score : scores) {
            AreaInfo areaInfo = AREA_INFO.get(score.getAreaCode().name());
            scoreMap.put(score.getAreaCode().name(), EvaluationResultResponse.ScoreInfo.builder()
                    .score(score.getScore())
                    .label(areaInfo.label)
                    .letter(areaInfo.letter)
                    .color(areaInfo.color)
                    .maxScore(100)
                    .build());
        }

        // 등급 결정
        String grade = evaluation.getTotalScore() >= 90 ? "A" :
                evaluation.getTotalScore() >= 80 ? "B" :
                evaluation.getTotalScore() >= 70 ? "C" :
                evaluation.getTotalScore() >= 60 ? "D" : "F";

        List<EvaluationResultResponse.FeedbackItem> strengths = new ArrayList<>();
        List<EvaluationResultResponse.FeedbackItem> weaknesses = new ArrayList<>();
        List<EvaluationResultResponse.Recommendation> recommendations = new ArrayList<>();
        
        int recPriority = 1;
        for (EvaluationScore score : scores) {
            String areaCode = score.getAreaCode().name();
            AreaInfo areaInfo = AREA_INFO.get(areaCode);
            String areaLabel = areaInfo != null ? areaInfo.label() : areaCode;
            
            if (score.getScore() >= 75) {
                strengths.add(EvaluationResultResponse.FeedbackItem.builder()
                        .area(areaCode)
                        .title(areaLabel + " 영역 우수")
                        .description(score.getFeedback() != null ? score.getFeedback() : "해당 영역이 우수합니다.")
                        .isBlurred(isDemo && strengths.size() >= 2)
                        .build());
            } else {
                weaknesses.add(EvaluationResultResponse.FeedbackItem.builder()
                        .area(areaCode)
                        .title(areaLabel + " 영역 보완 필요")
                        .description(score.getFeedback() != null ? score.getFeedback() : "해당 영역의 보완이 필요합니다.")
                        .isBlurred(isDemo && weaknesses.size() >= 1)
                        .build());
                
                recommendations.add(EvaluationResultResponse.Recommendation.builder()
                        .priority(recPriority++)
                        .area(areaCode)
                        .title(areaLabel + " 개선 권장")
                        .description(score.getFeedback() != null ? score.getFeedback() : "해당 영역의 개선을 권장합니다.")
                        .isBlurred(isDemo)
                        .build());
            }
        }

        return EvaluationResultResponse.builder()
                .evaluationId(evaluationId.toString())
                .projectId(evaluation.getProject().getId().toString())
                .completedAt(evaluation.getCompletedAt())
                .summary(EvaluationResultResponse.Summary.builder()
                        .totalScore(evaluation.getTotalScore())
                        .grade(grade)
                        .passRate(evaluation.getPassRate())
                        .passRateMessage(getPassRateMessage(evaluation.getPassRate()))
                        .build())
                .scores(scoreMap)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .recommendations(recommendations)
                .accessLevel(evaluation.getEvaluationType().name())
                .upgradePrompt(isDemo ? EvaluationResultResponse.UpgradePrompt.builder()
                        .message("상세 피드백과 개선 전략을 확인하려면 유료 플랜을 이용하세요.")
                        .availablePlans(List.of("plus", "pro", "premium"))
                        .build() : null)
                .build();
    }

    @Transactional
    protected void processEvaluation(UUID evaluationId) {
        try {
            Evaluation evaluation = evaluationRepository.findById(evaluationId).orElseThrow();
            String inputDataJson = evaluation.getInputData();

            int totalScore = 0;
            for (EvaluationScore.AreaCode area : EvaluationScore.AreaCode.values()) {
                AreaEvaluationResult areaResult = evaluateAreaWithGemini(area, inputDataJson);
                totalScore += areaResult.score();

                EvaluationScore evalScore = EvaluationScore.builder()
                        .evaluation(evaluation)
                        .areaCode(area)
                        .score(areaResult.score())
                        .feedback(areaResult.feedback())
                        .build();
                evaluationScoreRepository.save(evalScore);
                
                log.debug("영역 평가 완료: area={}, score={}", area.name(), areaResult.score());
            }

            int avgScore = totalScore / 6;
            int passRate = avgScore >= 80 ? 85 : avgScore >= 70 ? 65 : 40;

            evaluation.setTotalScore(avgScore);
            evaluation.setPassRate(passRate);
            evaluation.setStatus(Evaluation.EvaluationStatus.completed);
            evaluation.setCompletedAt(LocalDateTime.now());
            evaluationRepository.save(evaluation);

            log.info("AI 평가 완료: evaluationId={}, score={}", evaluationId, avgScore);
        } catch (Exception e) {
            log.error("AI 평가 실패: evaluationId={}", evaluationId, e);
            handleEvaluationFailure(evaluationId);
        }
    }
    
    private AreaEvaluationResult evaluateAreaWithGemini(EvaluationScore.AreaCode area, String inputDataJson) {
        String systemPrompt = buildEvaluationSystemPrompt();
        String userPrompt = buildAreaEvaluationPrompt(area, inputDataJson);
        
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
        ));
        
        try {
            ChatResponse response = chatModel.call(prompt);
            String content = response.getResult().getOutput().getText();
            return parseEvaluationResponse(content, area);
        } catch (Exception e) {
            log.warn("Gemini 평가 호출 실패 (area={}): {}", area.name(), e.getMessage());
            return new AreaEvaluationResult(70, "평가 중 오류가 발생했습니다. 기본 점수가 부여되었습니다.");
        }
    }
    
    private String buildEvaluationSystemPrompt() {
        return """
            당신은 정부 지원사업 평가 전문가입니다. 사업계획서의 특정 영역을 0-100점 사이로 평가합니다.
            
            평가 기준:
            - 90-100점: 매우 우수 (정부 지원사업 합격 가능성 높음)
            - 80-89점: 우수 (보완 시 합격 가능성 높음)
            - 70-79점: 보통 (추가 보완 필요)
            - 60-69점: 미흡 (상당한 개선 필요)
            - 60점 미만: 매우 미흡
            
            반드시 다음 JSON 형식으로만 응답하세요:
            {"score": 숫자, "feedback": "피드백 내용"}
            """;
    }
    
    private String buildAreaEvaluationPrompt(EvaluationScore.AreaCode area, String inputDataJson) {
        AreaInfo areaInfo = AREA_INFO.get(area.name());
        String areaName = areaInfo != null ? areaInfo.label() : area.name();
        
        String criteriaDescription = switch (area) {
            case market -> "시장 규모, 성장성, 타깃 고객 명확성, 경쟁 환경 분석";
            case ability -> "팀 구성, 관련 경력, 전문성, 실행 역량";
            case technology -> "기술 차별성, 특허/IP, 기술 완성도, 진입 장벽";
            case economics -> "수익 모델, 가격 전략, 투자 대비 수익성, BEP 달성 계획";
            case realization -> "개발 일정, 마일스톤, 리스크 관리, 자원 확보 계획";
            case social -> "사회적 가치, 일자리 창출, 환경/지역사회 기여";
        };
        
        return """
            다음 사업계획서 데이터를 기반으로 [%s] 영역을 평가하세요.
            
            평가 기준: %s
            
            === 사업계획서 데이터 ===
            %s
            === 데이터 끝 ===
            
            위 데이터에서 [%s] 관련 내용을 분석하고, 점수와 구체적인 피드백을 JSON 형식으로 제공하세요.
            """.formatted(areaName, criteriaDescription, inputDataJson, areaName);
    }
    
    private AreaEvaluationResult parseEvaluationResponse(String content, EvaluationScore.AreaCode area) {
        try {
            Pattern scorePattern = Pattern.compile("\"score\"\\s*:\\s*(\\d+)");
            Pattern feedbackPattern = Pattern.compile("\"feedback\"\\s*:\\s*\"([^\"]+)\"");
            
            Matcher scoreMatcher = scorePattern.matcher(content);
            Matcher feedbackMatcher = feedbackPattern.matcher(content);
            
            int score = 70;
            String feedback = "평가가 완료되었습니다.";
            
            if (scoreMatcher.find()) {
                score = Math.min(100, Math.max(0, Integer.parseInt(scoreMatcher.group(1))));
            }
            if (feedbackMatcher.find()) {
                feedback = feedbackMatcher.group(1);
            }
            
            return new AreaEvaluationResult(score, feedback);
        } catch (Exception e) {
            log.warn("평가 응답 파싱 실패 (area={}): {}", area.name(), e.getMessage());
            return new AreaEvaluationResult(70, "평가 응답 분석 중 오류가 발생했습니다.");
        }
    }
    
    private Project getOrCreateDemoProject() {
        List<Project> demoProjects = projectRepository.findByTemplateCode("demo");
        if (!demoProjects.isEmpty()) {
            return demoProjects.get(0);
        }
        
        // Create demo user if needed
        User demoUser = userRepository.findByEmail("demo@makersround.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("demo@makersround.com")
                        .name("Demo User")
                        .passwordHash("demo")
                        .provider(User.AuthProvider.local)
                        .build()));
                        
        return projectRepository.save(Project.builder()
                .user(demoUser)
                .name("Demo Project")
                .templateCode("demo")
                .status(Project.ProjectStatus.in_progress)
                .build());
    }
    
    private void handleEvaluationFailure(UUID evaluationId) {
        try {
            Evaluation evaluation = evaluationRepository.findById(evaluationId).orElse(null);
            if (evaluation != null) {
                evaluation.setStatus(Evaluation.EvaluationStatus.failed);
                evaluationRepository.save(evaluation);
            }
        } catch (Exception ex) {
            log.error("평가 실패 상태 업데이트 실패: evaluationId={}", evaluationId, ex);
        }
    }
    
    private record AreaEvaluationResult(int score, String feedback) {}

    private List<EvaluationStatusResponse.StageInfo> getInitialStages() {
        return List.of(
                new EvaluationStatusResponse.StageInfo("market", "시장성 분석", "pending", null),
                new EvaluationStatusResponse.StageInfo("ability", "수행능력 분석", "pending", null),
                new EvaluationStatusResponse.StageInfo("technology", "핵심기술 분석", "pending", null),
                new EvaluationStatusResponse.StageInfo("economics", "경제성 분석", "pending", null),
                new EvaluationStatusResponse.StageInfo("realization", "실현가능성 분석", "pending", null),
                new EvaluationStatusResponse.StageInfo("social", "사회적가치 분석", "pending", null)
        );
    }

    private String getPassRateMessage(int passRate) {
        if (passRate >= 80) return "현재 점수로는 합격 가능성이 높습니다.";
        if (passRate >= 60) return "현재 점수로는 합격 가능성이 보통입니다. 80점 이상 달성 시 합격 가능성이 높아집니다.";
        return "개선이 필요합니다. 평가 피드백을 참고하세요.";
    }

    private record AreaInfo(String label, String letter, String color) {}
}

