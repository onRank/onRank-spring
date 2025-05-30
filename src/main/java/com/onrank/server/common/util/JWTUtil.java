package com.onrank.server.common.util;

import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.refreshtoken.RefreshToken;
import com.onrank.server.domain.refreshtoken.RefreshTokenJpaRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Slf4j
@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration; // 30분
    private final long refreshTokenExpiration; // 2시간
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public JWTUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access.expirationMs}") long accessTokenExpiration,
                   @Value("${jwt.refresh.expirationMs}") long refreshTokenExpiration,
                   RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    public String createJwt(String category, String username, String email) {

        long expiredMs = 0L;
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

    public boolean isTokenExpired(String token) {
        try {
            Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            // 예외 없이 완전히 파싱되었으면 만료되지 않음
            return false;
        } catch (ExpiredJwtException e) {
            log.info("token expired, token: {}", token);
            // 토큰의 exp 클레임이 현재 시각보다 이전인 경우
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("token is invalid, token: {}", token);
            // 서명 불일치, 형식 오류 등 다른 검증 실패는
            // '만료' 여부와 별개이므로 false로 처리
            return false;
        }
    }

    public void validateAccessToken(String token) {
        try {
            Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ACCESS_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(INVALID_ACCESS_TOKEN);
        }
    }

    public void validateRefreshToken(String token) {
        if (token == null) {
            throw new CustomException(REFRESH_TOKEN_IS_NULL);
        }
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            // DB 에서도 삭제
            deleteRefreshToken(token);
            throw new CustomException(REFRESH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            // DB 에서도 삭제
            deleteRefreshToken(token);
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
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