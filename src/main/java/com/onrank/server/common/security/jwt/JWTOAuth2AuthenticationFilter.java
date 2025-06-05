package com.onrank.server.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onrank.server.api.dto.auth.CustomOAuth2User;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@NonNullApi // (?) 이게 왜 있어야 되지?
public class JWTOAuth2AuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final StudentService studentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            jwtUtil.validateAccessToken(accessToken);
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

        String[] parts = username.split(" ");
        String authorizedClientRegistrationId = parts[0];

        log.info("authorizedClientRegistrationId: {}", authorizedClientRegistrationId);
        log.info("username: {}", username);
        // "ROLE_USER" 권한 부여
        Collection<? extends GrantedAuthority> authorities = studentService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"))
                .getRoles();
//        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        log.info("authorities: {}", authorities);

        // (?) JWT 필터에서는 attributes에 어떤 값을 넣어야 하나?
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("username", username);

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

    private void writeErrorResponse(HttpServletResponse response, CustomErrorInfo errorInfo) throws IOException {
        response.setStatus(errorInfo.getHttpStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"code\": \"%s\", \"message\": \"%s\"}",
                errorInfo.name(), errorInfo.getMessage())
        );
    }
}