package com.onrank.server.common.security.jwt;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    String username = tokenProvider.getUsernameFromToken(token); // username 추출

                    if (username != null) {
                        log.info("✅ AccessToken 인증 성공 - 사용자: {}", username);

                        // CustomOAuth2User 객체 생성
                        OAuth2User customOAuth2User = new CustomOAuth2User(
                                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                                Collections.singletonMap("sub", username),
                                "sub",
                                false // 기존 사용자로 가정
                        );

                        // Authentication 객체 생성 및 SecurityContext 설정
                        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                    } else {
                        log.warn("❌ AccessToken이 유효하지 않음");
                    }
                    break; // Authorization 쿠키를 찾으면 루프 종료
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
