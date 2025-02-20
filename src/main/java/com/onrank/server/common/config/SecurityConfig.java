package com.onrank.server.common.config;

import com.onrank.server.api.service.auth.CustomOAuth2UserService;
import com.onrank.server.api.service.auth.OAuth2AuthenticationSuccessHandler;
import com.onrank.server.common.security.jwt.JwtAuthenticationFilter;
import com.onrank.server.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filerChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
                .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음

                // jwt 필터 등록
                .addFilterAfter(new JwtAuthenticationFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class)

                // oauth2 설정
                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("http://localhost:3000/auth/callback?isNewUser=true", true)
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(oAuth2UserService) // 사용자 정보를 처리할 서비스 설정
                                )
                                .successHandler(successHandler) // 로그인 성공시 핸들러 실행
                )

                // request 인증, 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth2/**", "/auth/**", "/login/**").permitAll() // 인증 없이 접근 가능
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
