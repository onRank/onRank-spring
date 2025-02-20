package com.onrank.server.common.security.jwt;

import com.onrank.server.common.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@NonNullApi
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String accessToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {

                log.info("{} : {}", cookie.getName(), cookie.getValue());
                if (cookie.getName().equals("AccessToken")) {
                    accessToken = cookie.getValue();
                }
            }
        }

        if (accessToken == null) {

            log.info("❌ 쿠키에 AccessToken 없음");
            filterChain.doFilter(request, response);
            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰 소멸 시간 검증
        else if (jwtUtil.isExpired(accessToken)) {

            log.info("❌ AccessToken 만료됨");
            filterChain.doFilter(request, response);
            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        else {
            //토큰에서 username 획득
            String username = jwtUtil.getUsername(accessToken);

            log.info("✅ AccessToken 인증 성공 - 사용자: {}", username);

            // DefaultOAuth2User 생성자 인자 생성
            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            String sub = jwtUtil.getSubFromToken(accessToken);
            Map<String, Object> attributes = Collections.singletonMap("sub", sub);
            String nameAttributeKey = "sub";

            // DefaultOAuth2User 객체 생성
            DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
            log.info("✅ defaultOAuth2User : {}", defaultOAuth2User);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(defaultOAuth2User, null, defaultOAuth2User.getAuthorities());
            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        }
    }
}
