package com.onrank.server.common.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            if (authentication != null) {
                log.info("JWT 인증 성공 - SecurityContext에 사용자 정보 저장");
                // SecurityContextHolder 에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("JWT 검증 성공했지만 사용자 정보 없음");
            }
        } else {
            log.warn("JWT 가 유효하지 않음");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {

        // 1. Authorization 헤더에서 토큰 확인
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.info("number1 Authorization header: {}", bearerToken.substring(7));
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값만 가져옴
        }

        // 2. Authorization 쿠키에서 토큰 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    log.info("number2 Authorization header: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
