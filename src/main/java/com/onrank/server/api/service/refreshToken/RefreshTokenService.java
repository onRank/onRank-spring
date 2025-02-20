package com.onrank.server.api.service.refreshtoken;

import com.onrank.server.domain.refreshtoken.RefreshToken;
import com.onrank.server.domain.refreshtoken.RefreshTokenJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class RefreshTokenService {

    private final long refreshTokenExpiration;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshTokenService(@Value("${jwt.refresh.expirationMs}") long refreshTokenExpiration, RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    // 사용자 ID로 새 Refresh Token을 생성하여 DB에 저장
    public void createRefreshToken(String username, String rtk) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setRefreshToken(rtk);
        refreshToken.setExpiration(Instant.now().plusMillis(refreshTokenExpiration));
        refreshTokenJpaRepository.save(refreshToken);
    }
}
