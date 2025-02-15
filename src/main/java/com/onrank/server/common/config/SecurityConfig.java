package com.onrank.server.common.config;

import com.onrank.server.api.service.auth.CustomOAuth2UserService;
import com.onrank.server.api.service.auth.OAuth2AuthenticationSuccessHandler;
import com.onrank.server.common.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filerChain(HttpSecurity http) throws Exception {
        http
                .csrf(auth -> auth.disable()) // CSRF 보호 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .httpBasic(auth -> auth.disable()) // 기본 인증 로그인 비활성화
                .formLogin(auth -> auth.disable()) // 기본 login form 비활성화
                .logout(auth -> auth.disable()) // 기본 logout 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음

                // request 인증, 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register-student").permitAll() // 인증 없이 접근 가능
                        .requestMatchers("/studies").authenticated() // JWT 필요
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )

                // oauth2 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService) // 사용자 정보를 처리할 서비스 설정
                        )
                        .successHandler(successHandler) // 로그인 성공시 핸들러 실행
                )

                //JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 비밀번호 암호화 시 사용할 BCryptPasswordEncoder를 스프링 빈으로 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정 (프론트엔드와 연동 시 필요)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*")); // ✅ 모든 포트의 localhost 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization")); // ✅ JWT를 응답 헤더에서 노출
        configuration.setAllowCredentials(true); // ✅ 쿠키 인증 허용 (JWT 쿠키 사용 가능)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
