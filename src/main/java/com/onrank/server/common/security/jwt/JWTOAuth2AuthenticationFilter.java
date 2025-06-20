package com.onrank.server.common.security.jwt;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.exception.CustomErrorInfo;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.common.util.JWTUtil;
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
import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@NonNullApi // (?) 이게 왜 있어야 되지?
public class JWTOAuth2AuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final StudentService studentService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Access Token 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Access token not found");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.substring(7);
        String username;
        String email;

        try {
            // Access Token이 유효한지 확인
            jwtUtil.isTokenValid(accessToken);
            username = jwtUtil.getUsername(accessToken);
            email = jwtUtil.getEmail(accessToken);

            if(!"access".equals(jwtUtil.getCategory(accessToken))) {
                throw new CustomException(CustomErrorInfo.INVALID_ACCESS_TOKEN);
            }
        } catch (CustomException e) {
            writeErrorResponse(response, e.getCustomErrorInfo());
            return;
        }

        // 신규 유저는 SecurityContext 등록하지 않음 (필터 건너뛰기)
        if (studentService.checkIfNewUser(username)) {
            log.info("New user logged in");
            filterChain.doFilter(request, response);
            return;
        }

        // "ROLE_USER" 권한 부여
        Collection<? extends GrantedAuthority> authorities = studentService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"))
                .getRoles();

        // OAuth2AuthenticationToken 생성
        OAuth2AuthenticationToken authToken = getoAuth2AuthenticationToken(authorities, username, email);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    // OAuth2AuthenticationToken 생성
    private OAuth2AuthenticationToken getoAuth2AuthenticationToken(Collection<? extends GrantedAuthority> authorities, String username, String email) {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                authorities,
                Map.of("username", username, "email", email),
                username,
                email
        );

        // google, kakao 등 OAuth2 제공자 이름 추출
        String provider = extractProviderFromUsername(username);

        // OAuth2AuthenticationToken 생성
        return new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), provider);
    }

    private void writeErrorResponse(HttpServletResponse response, CustomErrorInfo errorInfo) throws IOException {
        response.setStatus(errorInfo.getHttpStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"code\": \"%s\", \"message\": \"%s\"}",
                errorInfo.name(), errorInfo.getMessage())
        );
    }

    /**
     * Extracts the provider from the username.
     * The username is expected to be in the format "provider username".
     * (ex. "google 101754090114191059089")
     */
    private String extractProviderFromUsername(String username) {
        String[] parts = username.split(" ");
        return parts.length > 0 ? parts[0] : "";
    }
}