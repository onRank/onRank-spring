package com.onrank.server.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByUsername(String username);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
