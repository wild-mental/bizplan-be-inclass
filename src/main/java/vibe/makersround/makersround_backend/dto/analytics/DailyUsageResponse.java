package vibe.makersround.makersround_backend.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 일별 이용량 응답 DTO
 * 기간별 일일 서비스 이용 통계
 */
@Data
@Builder
public class DailyUsageResponse {
    
    /** 시작 날짜 */
    private LocalDate startDate;
    
    /** 종료 날짜 */
    private LocalDate endDate;
    
    /** 일별 데이터 목록 */
    private List<DailyData> data;

    /**
     * 일별 데이터
     */
    @Data
    @Builder
    public static class DailyData {
        /** 날짜 */
        private LocalDate date;
        
        /** 랜딩 페이지 조회 수 */
        private long landingPageViews;
        
        /** 사업계획서 작성 데모 이용 수 */
        private long writingDemoUsage;
        
        /** AI 평가 데모 이용 수 */
        private long evaluationDemoUsage;
        
        /** 총 API 요청 수 */
        private long totalRequests;
    }
}
