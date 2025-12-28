package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.Subscription;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 구독 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.1 - 회원가입
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    /**
     * 사용자의 활성 구독 조회
     */
    Optional<Subscription> findByUserAndStatus(User user, Subscription.SubscriptionStatus status);

    /**
     * 사용자의 모든 구독 조회 (최신순)
     */
    List<Subscription> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자의 활성 구독 존재 여부 확인
     */
    boolean existsByUserAndStatus(User user, Subscription.SubscriptionStatus status);
}

