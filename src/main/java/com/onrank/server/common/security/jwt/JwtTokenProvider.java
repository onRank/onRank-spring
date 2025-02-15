package com.onrank.server.common.security.jwt;

import com.onrank.server.api.service.auth.CustomUserDetailService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 60 * 2; // 2시간
    private final CustomUserDetailService customUserDetailService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, CustomUserDetailService customUserDetailService) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.customUserDetailService = customUserDetailService;
    }

    // JWT 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // 이메일을 subject로 사용
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY)) // 만료 시간
                .signWith(secretKey) // 서명
                .compact();
    }

    // JWT 에서 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // subject(email) 반환
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 유효하지 않은 토큰
        }
    }

    // JWT에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(email); // 이메일 기반으로 사용자 정보 가져오기

        if (userDetails == null) {
            log.warn("UserDetails를 찾을 수 없음: {}", email);
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // 인증 객체 반환
    }
}
