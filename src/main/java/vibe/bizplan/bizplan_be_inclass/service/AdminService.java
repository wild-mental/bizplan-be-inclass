package vibe.bizplan.bizplan_be_inclass.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibe.bizplan.bizplan_be_inclass.dto.admin.UserListResponse;
import vibe.bizplan.bizplan_be_inclass.dto.admin.UserStatisticsResponse;
import vibe.bizplan.bizplan_be_inclass.entity.Subscription;
import vibe.bizplan.bizplan_be_inclass.entity.User;
import vibe.bizplan.bizplan_be_inclass.repository.SubscriptionRepository;
import vibe.bizplan.bizplan_be_inclass.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 어드민 서비스
 * 사용자 목록 조회 및 통계 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * 사용자 목록 조회 (필터링, 정렬, 페이징)
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 필드 (createdAt, email, name 등)
     * @param sortDirection 정렬 방향 (ASC, DESC)
     * @param planFilter 요금제 필터
     * @param providerFilter 소셜 로그인 제공자 필터
     * @param emailVerifiedFilter 이메일 인증 필터
     * @param searchKeyword 검색 키워드 (이메일, 이름)
     */
    public UserListResponse getUserList(
            Integer page,
            Integer size,
            String sortBy,
            String sortDirection,
            String planFilter,
            String providerFilter,
            Boolean emailVerifiedFilter,
            String searchKeyword
    ) {
        log.info("사용자 목록 조회 요청: page={}, size={}, sortBy={}, sortDirection={}", 
                page, size, sortBy, sortDirection);

        // 기본값 설정
        int pageNum = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? size : 20;
        
        // 모든 사용자 조회 (필터링은 메모리에서 처리 - SQLite 제약)
        List<User> allUsers = userRepository.findAll();
        
        // 필터링 적용
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> {
                    // 요금제 필터
                    if (planFilter != null && !planFilter.isEmpty()) {
                        Optional<Subscription> subscription = subscriptionRepository
                                .findByUserAndStatus(user, Subscription.SubscriptionStatus.active);
                        if (subscription.isEmpty() || !subscription.get().getPlan().equals(planFilter)) {
                            return false;
                        }
                    }
                    
                    // 제공자 필터
                    if (providerFilter != null && !providerFilter.isEmpty() 
                            && !user.getProvider().name().equals(providerFilter)) {
                        return false;
                    }
                    
                    // 이메일 인증 필터
                    if (emailVerifiedFilter != null 
                            && !user.getEmailVerified().equals(emailVerifiedFilter)) {
                        return false;
                    }
                    
                    // 검색 키워드 필터
                    if (searchKeyword != null && !searchKeyword.isEmpty()) {
                        String keyword = searchKeyword.toLowerCase();
                        if (!user.getEmail().toLowerCase().contains(keyword) &&
                            !user.getName().toLowerCase().contains(keyword)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        // 정렬 적용 (이미 정렬된 리스트이지만 다시 정렬)
        Comparator<User> comparator = getComparator(sortBy, sortDirection);
        filteredUsers.sort(comparator);
        
        // 페이징 적용
        int start = pageNum * pageSize;
        int end = Math.min(start + pageSize, filteredUsers.size());
        List<User> pagedUsers = start < filteredUsers.size() 
                ? filteredUsers.subList(start, end) 
                : Collections.emptyList();
        
        // 응답 생성
        List<UserListResponse.UserInfo> userInfos = pagedUsers.stream()
                .map(this::convertToUserInfo)
                .collect(Collectors.toList());
        
        long totalElements = filteredUsers.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        UserListResponse.PaginationInfo pagination = UserListResponse.PaginationInfo.builder()
                .page(pageNum)
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(pageNum < totalPages - 1)
                .hasPrevious(pageNum > 0)
                .build();
        
        return UserListResponse.builder()
                .users(userInfos)
                .pagination(pagination)
                .build();
    }

    /**
     * 사용자 통계 조회
     */
    public UserStatisticsResponse getUserStatistics() {
        log.info("사용자 통계 조회 요청");
        
        List<User> allUsers = userRepository.findAll();
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        
        // 전체 통계
        long totalUsers = allUsers.size();
        long verifiedUsers = allUsers.stream().filter(User::getEmailVerified).count();
        long unverifiedUsers = totalUsers - verifiedUsers;
        long marketingConsentUsers = allUsers.stream()
                .filter(u -> Boolean.TRUE.equals(u.getMarketingConsent()))
                .count();
        
        // 요금제별 통계
        Map<String, Long> planCounts = new HashMap<>();
        Map<String, Subscription> userActiveSubscriptions = new HashMap<>();
        
        for (Subscription sub : allSubscriptions) {
            if (sub.getStatus() == Subscription.SubscriptionStatus.active) {
                planCounts.put(sub.getPlan(), planCounts.getOrDefault(sub.getPlan(), 0L) + 1);
                userActiveSubscriptions.put(sub.getUser().getId().toString(), sub);
            }
        }
        
        long paidPlanUsers = planCounts.values().stream().mapToLong(Long::longValue).sum();
        long freePlanUsers = totalUsers - paidPlanUsers;
        
        UserStatisticsResponse.OverallStatistics overall = UserStatisticsResponse.OverallStatistics.builder()
                .totalUsers(totalUsers)
                .verifiedUsers(verifiedUsers)
                .unverifiedUsers(unverifiedUsers)
                .marketingConsentUsers(marketingConsentUsers)
                .paidPlanUsers(paidPlanUsers)
                .freePlanUsers(freePlanUsers)
                .build();
        
        // 가입 시간별 통계
        List<UserStatisticsResponse.TimeSeriesData> signupByDate = calculateSignupByDate(allUsers);
        List<UserStatisticsResponse.TimeSeriesData> signupByWeek = calculateSignupByWeek(allUsers);
        List<UserStatisticsResponse.TimeSeriesData> signupByMonth = calculateSignupByMonth(allUsers);
        
        // 요금제별 통계
        List<UserStatisticsResponse.PlanStatistics> byPlan = planCounts.entrySet().stream()
                .map(entry -> UserStatisticsResponse.PlanStatistics.builder()
                        .plan(entry.getKey())
                        .count(entry.getValue())
                        .percentage(totalUsers > 0 ? (entry.getValue() * 100.0 / totalUsers) : 0.0)
                        .build())
                .sorted(Comparator.comparing(UserStatisticsResponse.PlanStatistics::getCount).reversed())
                .collect(Collectors.toList());
        
        // 소셜 로그인별 통계
        Map<String, Long> providerCounts = allUsers.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getProvider().name(),
                        Collectors.counting()
                ));
        
        List<UserStatisticsResponse.ProviderStatistics> byProvider = providerCounts.entrySet().stream()
                .map(entry -> UserStatisticsResponse.ProviderStatistics.builder()
                        .provider(entry.getKey())
                        .count(entry.getValue())
                        .percentage(totalUsers > 0 ? (entry.getValue() * 100.0 / totalUsers) : 0.0)
                        .build())
                .sorted(Comparator.comparing(UserStatisticsResponse.ProviderStatistics::getCount).reversed())
                .collect(Collectors.toList());
        
        // 사업 분야별 통계
        Map<String, Long> categoryCounts = allUsers.stream()
                .filter(u -> u.getBusinessCategory() != null && !u.getBusinessCategory().isEmpty())
                .collect(Collectors.groupingBy(
                        User::getBusinessCategory,
                        Collectors.counting()
                ));
        
        long usersWithCategory = categoryCounts.values().stream().mapToLong(Long::longValue).sum();
        
        List<UserStatisticsResponse.CategoryStatistics> byCategory = categoryCounts.entrySet().stream()
                .map(entry -> UserStatisticsResponse.CategoryStatistics.builder()
                        .category(entry.getKey())
                        .count(entry.getValue())
                        .percentage(usersWithCategory > 0 ? (entry.getValue() * 100.0 / usersWithCategory) : 0.0)
                        .build())
                .sorted(Comparator.comparing(UserStatisticsResponse.CategoryStatistics::getCount).reversed())
                .collect(Collectors.toList());
        
        return UserStatisticsResponse.builder()
                .overall(overall)
                .signupByDate(signupByDate)
                .signupByWeek(signupByWeek)
                .signupByMonth(signupByMonth)
                .byPlan(byPlan)
                .byProvider(byProvider)
                .byCategory(byCategory)
                .build();
    }

    /**
     * User 엔티티를 UserInfo DTO로 변환
     */
    private UserListResponse.UserInfo convertToUserInfo(User user) {
        Optional<Subscription> activeSubscription = subscriptionRepository
                .findByUserAndStatus(user, Subscription.SubscriptionStatus.active);
        
        UserListResponse.SubscriptionInfo subscriptionInfo = null;
        if (activeSubscription.isPresent()) {
            Subscription sub = activeSubscription.get();
            subscriptionInfo = UserListResponse.SubscriptionInfo.builder()
                    .plan(sub.getPlan())
                    .planKey(sub.getPlanKey().name())
                    .originalPrice(sub.getOriginalPrice())
                    .discountedPrice(sub.getDiscountedPrice())
                    .discountRate(sub.getDiscountRate())
                    .promotionPhase(sub.getPromotionPhase())
                    .promotionCode(sub.getPromotionCode())
                    .startDate(sub.getStartDate())
                    .endDate(sub.getEndDate())
                    .status(sub.getStatus().name())
                    .build();
        }
        
        return UserListResponse.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .businessCategory(user.getBusinessCategory())
                .provider(user.getProvider().name())
                .emailVerified(user.getEmailVerified())
                .marketingConsent(user.getMarketingConsent())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .subscription(subscriptionInfo)
                .build();
    }

    /**
     * 정렬 Comparator 생성
     */
    private Comparator<User> getComparator(String sortBy, String sortDirection) {
        Comparator<User> comparator;
        
        if (sortBy == null || sortBy.equals("createdAt")) {
            comparator = Comparator.comparing(User::getCreatedAt);
        } else if (sortBy.equals("email")) {
            comparator = Comparator.comparing(User::getEmail);
        } else if (sortBy.equals("name")) {
            comparator = Comparator.comparing(User::getName);
        } else {
            comparator = Comparator.comparing(User::getCreatedAt);
        }
        
        if (sortDirection != null && sortDirection.equalsIgnoreCase("ASC")) {
            return comparator;
        } else {
            return comparator.reversed();
        }
    }

    /**
     * 일별 가입 통계 계산
     */
    private List<UserStatisticsResponse.TimeSeriesData> calculateSignupByDate(List<User> users) {
        Map<String, Long> dateCounts = users.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        Collectors.counting()
                ));
        
        return dateCounts.entrySet().stream()
                .map(entry -> UserStatisticsResponse.TimeSeriesData.builder()
                        .date(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(UserStatisticsResponse.TimeSeriesData::getDate))
                .collect(Collectors.toList());
    }

    /**
     * 주별 가입 통계 계산
     */
    private List<UserStatisticsResponse.TimeSeriesData> calculateSignupByWeek(List<User> users) {
        Map<String, Long> weekCounts = users.stream()
                .collect(Collectors.groupingBy(
                        u -> {
                            LocalDate date = u.getCreatedAt().toLocalDate();
                            int year = date.getYear();
                            int week = date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
                            return String.format("%d-W%02d", year, week);
                        },
                        Collectors.counting()
                ));
        
        return weekCounts.entrySet().stream()
                .map(entry -> UserStatisticsResponse.TimeSeriesData.builder()
                        .date(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(UserStatisticsResponse.TimeSeriesData::getDate))
                .collect(Collectors.toList());
    }

    /**
     * 월별 가입 통계 계산
     */
    private List<UserStatisticsResponse.TimeSeriesData> calculateSignupByMonth(List<User> users) {
        Map<String, Long> monthCounts = users.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));
        
        return monthCounts.entrySet().stream()
                .map(entry -> UserStatisticsResponse.TimeSeriesData.builder()
                        .date(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(UserStatisticsResponse.TimeSeriesData::getDate))
                .collect(Collectors.toList());
    }
}
