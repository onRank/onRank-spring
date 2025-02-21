package com.onrank.server.common.security.jwt;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.auth.CustomOAuth2UserService;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.api.service.token.TokenService;
import com.onrank.server.domain.student.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@NonNullApi // (?) 이게 왜 있어야 되지?
public class JwtOAuth2AuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final StudentService studentService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // "Authorization" 헤더에서 accessToken 추출
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = header.substring(7);

        // 토큰에서 username 추출
        // accessToken은 유효하지만 DB에 저장되어 있지 않은 사용자의 경우:
        // -> 이 필터를 건너뜀
        // -> 회원 등록 요청일 경우 회원 등록 (DB 저장) 후에 다시 요청이 옴
        // -> 다른 경로로 온 요청일 경우 UnAuthorized(401) 상태코드가 반환됨
        String username = tokenService.getUsernameFromToken(accessToken);
        if (studentService.checkIfNewUser(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (tokenService.validateToken(accessToken)) {

                String[] parts = accessToken.split(" ");
                String authorizedClientRegistrationId = parts[0];
                String sub = parts[1];

                Set<Role> roles = studentService.findByUsername(username).get().getRoles();
                Collection<? extends GrantedAuthority> authorities = roles;

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("name", username);
                String email = (String) tokenService.getEmailFromToken("email");
                attributes.put("email", email);

                CustomOAuth2User customOAuth2User = new CustomOAuth2User(authorities, attributes, username, email);

                // OAuth2AuthenticationToken 생성
                OAuth2AuthenticationToken authentication =
                        new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), authorizedClientRegistrationId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access token expired\"}");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
