package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.PasswordResetToken;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 비밀번호 재설정 토큰 리포지토리
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    /**
     * 토큰 값으로 조회
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * 사용자 ID로 최신 토큰 조회
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user.id = :userId AND t.used = false ORDER BY t.createdAt DESC")
    Optional<PasswordResetToken> findLatestByUserId(@Param("userId") UUID userId);

    /**
     * 특정 사용자의 모든 미사용 토큰 폐기
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user = :user AND t.used = false")
    int invalidateAllByUser(@Param("user") User user);

    /**
     * 토큰 존재 및 유효성 확인
     */
    @Query("SELECT COUNT(t) > 0 FROM PasswordResetToken t WHERE t.token = :token AND t.used = false AND t.expiresAt > CURRENT_TIMESTAMP")
    boolean existsValidToken(@Param("token") String token);
}

