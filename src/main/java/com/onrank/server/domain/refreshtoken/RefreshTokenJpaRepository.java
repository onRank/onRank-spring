package com.onrank.server.domain.refreshtoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, String> {

    Boolean existsByRefreshToken(String refreshToken);
    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
