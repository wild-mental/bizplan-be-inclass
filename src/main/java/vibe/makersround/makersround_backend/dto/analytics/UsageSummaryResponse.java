package vibe.makersround.makersround_backend.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 서비스 이용 요약 응답 DTO
 * 기간별 전체 서비스 이용 요약 통계
 */
@Data
@Builder
public class UsageSummaryResponse {
    
    /** 시작 날짜 */
    private LocalDate startDate;
    
    /** 종료 날짜 */
    private LocalDate endDate;
    
    /** 총 API 요청 수 */
    private long totalRequests;
    
    /** 랜딩 페이지 조회 수 */
    private long landingPageViews;
    
    /** 사업계획서 작성 데모 이용 수 */
    private long writingDemoUsage;
    
    /** AI 평가 데모 이용 수 */
    private long evaluationDemoUsage;
    
    /** 고유 요청 ID 수 */
    private long uniqueRequestIds;
}
