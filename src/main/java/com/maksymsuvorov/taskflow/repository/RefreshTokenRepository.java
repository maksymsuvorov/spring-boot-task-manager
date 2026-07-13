package com.maksymsuvorov.taskflow.repository;

import com.maksymsuvorov.taskflow.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // join fetch: the user must be initialized because it is read after the
    // token row is deleted, possibly outside this repository's session.
    @Query("select rt from RefreshToken rt join fetch rt.user where rt.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHashWithUser(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("delete from RefreshToken rt where rt.expiresAt < :now")
    int deleteAllExpiredBefore(@Param("now") LocalDateTime now);

}
