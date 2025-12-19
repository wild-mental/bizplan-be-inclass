package vibe.bizplan.bizplan_be_inclass.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 사업계획서 생성 결과 및 사용량을 저장/조회하는 리포지토리.
 *
 * MVP 단계에서는 별도 DB 저장 없이 로그 기반으로만 사용량을 추적합니다.
 * 추후 Rule 303(JPA/DB 규칙)에 따라 영속화 레이어로 확장할 수 있습니다.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class BusinessPlanGenerationRepository {

    /**
     * Gemini 토큰 사용량을 기록합니다.
     *
     * @param businessPlanId   생성된 사업계획서 ID
     * @param startTimeIso     시작 시간 (ISO 8601 형식)
     * @param endTimeIso       종료 시간 (ISO 8601 형식)
     * @param durationMs       소요 시간 (밀리초)
     * @param promptTokens     입력 토큰 수
     * @param completionTokens 출력 토큰 수
     * @param totalTokens      총 토큰 수
     * @param tokensPerSecond  토큰 처리량 (tokens/sec)
     */
    public void saveUsage(String businessPlanId, String startTimeIso, String endTimeIso, 
                         long durationMs, int promptTokens, int completionTokens, 
                         int totalTokens, double tokensPerSecond) {
        // 현재는 로그 기반 추적만 수행합니다.
        // 향후 DB 저장이 필요해지면 이 메서드에 JPA 엔티티 저장 로직을 추가합니다.
        log.info("[Gemini Usage Log] businessPlanId={}, StartTime: {}, EndTime: {}, Duration: {}ms, Input: {}, Output: {}, Total: {}, Throughput: {} tokens/sec",
                businessPlanId, startTimeIso, endTimeIso, durationMs,
                promptTokens, completionTokens, totalTokens, String.format("%.2f", tokensPerSecond));
    }
}

