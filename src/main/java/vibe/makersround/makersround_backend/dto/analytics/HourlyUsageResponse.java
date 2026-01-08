package vibe.makersround.makersround_backend.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 시간대별 이용량 응답 DTO
 * 24시간 기준 시간대별 서비스 이용 통계
 */
@Data
@Builder
public class HourlyUsageResponse {
    
    /** 조회 날짜 */
    private LocalDate date;
    
    /** 카테고리 (랜딩 페이지, 사업계획서 작성 데모, AI 평가 데모) */
    private String category;
    
    /** 총 이용 횟수 */
    private long totalCount;
    
    /** 고유 요청 수 (Request ID 기준) */
    private long uniqueRequests;
    
    /** 피크 시간대 (0-23) */
    private int peakHour;
    
    /** 시간대별 상세 데이터 (24개) */
    private List<HourlyData> hourlyData;

    /**
     * 시간대별 데이터
     */
    @Data
    @Builder
    public static class HourlyData {
        /** 시간 (0-23) */
        private int hour;
        
        /** 시간 라벨 (00:00 형식) */
        private String hourLabel;
        
        /** 이용 횟수 */
        private long count;
    }
}
