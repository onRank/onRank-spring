package com.onrank.server.api.service.token;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.domain.refreshtoken.RefreshToken;
import com.onrank.server.domain.refreshtoken.RefreshTokenJpaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
public class TokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration; // 2시간 (2시간 후 만료)
    private final long refreshTokenExpiration;

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public TokenService(@Value("${jwt.secret}")String secret,
                        @Value("${jwt.access.expirationMs}") long accessTokenExpiration,
                        @Value("${jwt.refresh.expirationMs}") long refreshTokenExpiration,
                        RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    public boolean validateToken(String token) {
        try {
            // 토큰 파싱: 유효하지 않거나 만료된 경우 예외 발생
            Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            // 예외 발생 시 false 반환
            return false;
        }
    }

    public Boolean isExpired(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public String getUsernameFromToken(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Object getEmailFromToken(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email");
    }

    // AccessToken 생성
    public String generateAccessToken(CustomOAuth2User customOAuth2User) {

        String username = customOAuth2User.getName();
        String email = customOAuth2User.getEmail();

        return Jwts.builder()
                .subject(username)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(CustomOAuth2User customOAuth2User) {

        String rtk = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(customOAuth2User.getName());
        refreshToken.setRefreshToken(rtk);
        refreshToken.setExpiration(Instant.now().plusMillis(refreshTokenExpiration));
        refreshTokenJpaRepository.save(refreshToken);

        return rtk;
    }
}