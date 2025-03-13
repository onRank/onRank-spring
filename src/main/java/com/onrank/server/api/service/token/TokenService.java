package com.onrank.server.api.service.token;

import com.onrank.server.domain.refreshtoken.RefreshToken;
import com.onrank.server.domain.refreshtoken.RefreshTokenJpaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class TokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration; // 30분
    private final long refreshTokenExpiration; // 2시간
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public TokenService(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.access.expirationMs}") long accessTokenExpiration,
                        @Value("${jwt.refresh.expirationMs}") long refreshTokenExpiration,
                        RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    public String createJwt(String category, String username, String email) {

        Long expiredMs = 0L;
        if (category.equals("access")) {
            expiredMs = accessTokenExpiration;
        } else if (category.equals("refresh")) {
            expiredMs = refreshTokenExpiration;
        }

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String getUsername(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getEmail(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public String getCategory(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }

    public Boolean isExpired(String token) {

        Claims claims = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (claims.getExpiration().before(new Date())) {
            // 토큰이 만료된 경우 명시적으로 예외 발생
            throw new ExpiredJwtException(null, claims, "Token has expired");
        }

        return false;
    }

    public Boolean validateRefreshToken(String token) {

        return refreshTokenJpaRepository.existsByRefreshToken(token);
    }

    public void deleteRefreshToken(String token) {

        refreshTokenJpaRepository.deleteByRefreshToken(token);
    }

    public void save(String username, String refreshToken) {

        RefreshToken rtk = new RefreshToken();
        rtk.setUsername(username);
        rtk.setRefreshToken(refreshToken);
        refreshTokenJpaRepository.save(rtk);
    }
}