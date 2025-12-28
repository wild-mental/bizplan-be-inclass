package vibe.bizplan.bizplan_be_inclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.evaluation.*;
import vibe.bizplan.bizplan_be_inclass.entity.*;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * AI 평가 서비스
 * 사업계획서 6대 영역 평가를 수행합니다.
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

        Project project = projectRepository.findById(UUID.fromString(request.getProjectId()))
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트를 찾을 수 없습니다: " + request.getProjectId()));

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

        // 강점/약점 생성 (데모에서는 일부 블러 처리)
        List<EvaluationResultResponse.FeedbackItem> strengths = List.of(
                EvaluationResultResponse.FeedbackItem.builder()
                        .area("ability")
                        .title("팀 구성 우수")
                        .description("전문 경력을 갖춘 팀 구성이 돋보입니다.")
                        .isBlurred(false)
                        .build(),
                EvaluationResultResponse.FeedbackItem.builder()
                        .area("market")
                        .title("명확한 타깃 시장")
                        .description("명확한 타깃 설정이 좋습니다.")
                        .isBlurred(false)
                        .build(),
                EvaluationResultResponse.FeedbackItem.builder()
                        .area("technology")
                        .title("기술 차별화")
                        .description("기존 서비스와의 차별화 포인트입니다.")
                        .isBlurred(isDemo)
                        .build()
        );

        List<EvaluationResultResponse.FeedbackItem> weaknesses = List.of(
                EvaluationResultResponse.FeedbackItem.builder()
                        .area("economics")
                        .title("수익 모델 구체화 필요")
                        .description("구체적인 가격 정책이 필요합니다.")
                        .isBlurred(false)
                        .build(),
                EvaluationResultResponse.FeedbackItem.builder()
                        .area("realization")
                        .title("개발 일정 검토 필요")
                        .description("개발 기간이 다소 낙관적으로 설정되어 있습니다.")
                        .isBlurred(isDemo)
                        .build()
        );

        List<EvaluationResultResponse.Recommendation> recommendations = List.of(
                EvaluationResultResponse.Recommendation.builder()
                        .priority(1)
                        .area("economics")
                        .title("수익 모델 보완")
                        .description("B2B 연계 모델을 추가하면 수익 안정성이 높아집니다.")
                        .isBlurred(isDemo)
                        .build(),
                EvaluationResultResponse.Recommendation.builder()
                        .priority(2)
                        .area("realization")
                        .title("MVP 범위 조정")
                        .description("핵심 기능 3개로 MVP 범위를 좁히고 단계적 확장을 권장합니다.")
                        .isBlurred(isDemo)
                        .build()
        );

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

    /**
     * 평가 처리 (비동기)
     */
    @Transactional
    protected void processEvaluation(UUID evaluationId) {
        try {
            Thread.sleep(2000); // 시뮬레이션 지연

            Evaluation evaluation = evaluationRepository.findById(evaluationId).orElseThrow();
            Random random = new Random();

            int totalScore = 0;
            for (EvaluationScore.AreaCode area : EvaluationScore.AreaCode.values()) {
                int score = 65 + random.nextInt(25); // 65-90 점
                totalScore += score;

                EvaluationScore evalScore = EvaluationScore.builder()
                        .evaluation(evaluation)
                        .areaCode(area)
                        .score(score)
                        .feedback("자동 생성된 피드백입니다.")
                        .build();
                evaluationScoreRepository.save(evalScore);
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
        }
    }

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

