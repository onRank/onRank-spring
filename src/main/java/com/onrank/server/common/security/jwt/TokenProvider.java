package com.onrank.server.common.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {

    private final SecretKey secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 * 2; // 2시간 (2시간 후 만료)

    public TokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .claim("sub", username) // subject를 claims에 추가
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    // AccessToken에서 이메일 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("sub", String.class); // "sub" 키 값 가져오기
    }
}
