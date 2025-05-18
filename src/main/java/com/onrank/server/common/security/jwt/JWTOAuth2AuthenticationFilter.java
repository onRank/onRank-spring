package com.onrank.server.common.security.jwt;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@NonNullApi // (?) 이게 왜 있어야 되지?
public class JWTOAuth2AuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil JWTUtil;
    private final StudentService studentService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // "Authorization" 헤더에서 access token 추출
        String authHeader = request.getHeader("Authorization");

        // access token이 없으면 필터 건너뛰기
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Access token not found");
            filterChain.doFilter(request, response);
            return;
        }

        // access token이 "Bearer AsDfQwEr..."과 같이 되어 있음
        String accessToken = authHeader.substring(7);

        // username은 "google aSdFqWeR..."과 같이 되어 있음
        String username = JWTUtil.getUsername(accessToken);

        // 등록된 회원이 아니면 필터 건너뛰기
        if (studentService.checkIfNewUser(username)) {
            log.info("New user logged in");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부 및 유효성 확인
        try {
            JWTUtil.isTokenExpired(accessToken);

        } catch (ExpiredJwtException ex) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"access token expired\"}");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = JWTUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }

        String[] parts = username.split(" ");
        String authorizedClientRegistrationId = parts[0];

        // "ROLE_USER" 권한 부여
        Collection<? extends GrantedAuthority> authorities = studentService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"))
                .getRoles();
//        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        log.info("authorities: {}", authorities);

        // (?) JWT 필터에서는 attributes에 어떤 값을 넣어야 하나?
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("username", username);

        String email = JWTUtil.getEmail(accessToken);
        attributes.put("email", email);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(authorities, attributes, username, email);
        log.info("customOAuth2User: {}", customOAuth2User);

        // OAuth2AuthenticationToken 생성
        OAuth2AuthenticationToken authToken =
                new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), authorizedClientRegistrationId);
        log.info("authToken: {}", authToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("SecurityContextHolder: {}", SecurityContextHolder.getContext());

        filterChain.doFilter(request, response);
    }
}