package vibe.bizplan.bizplan_be_inclass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.bizplan.bizplan_be_inclass.entity.RefreshToken;
import vibe.bizplan.bizplan_be_inclass.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 리프레시 토큰 리포지토리
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.4 - 토큰 갱신
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * 토큰 값으로 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 특정 사용자의 모든 토큰 폐기
     */
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user = :user AND r.revoked = false")
    int revokeAllByUser(@Param("user") User user);

    /**
     * 토큰 존재 및 유효성 확인
     */
    @Query("SELECT COUNT(r) > 0 FROM RefreshToken r WHERE r.token = :token AND r.revoked = false AND r.expiresAt > CURRENT_TIMESTAMP")
    boolean existsValidToken(@Param("token") String token);
}

