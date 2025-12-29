package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 2 - 인증 API
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 소셜 로그인 제공자와 ID로 사용자 조회
     */
    Optional<User> findByProviderAndProviderId(User.AuthProvider provider, String providerId);
}

