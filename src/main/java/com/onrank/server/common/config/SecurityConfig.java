package com.onrank.server.common.config;

import com.onrank.server.api.service.auth.CustomOAuth2UserService;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
//    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain filerChain(HttpSecurity http) throws Exception {
        http
                .csrf(auth -> auth.disable()) // CSRF 보호 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .cors(Customizer.withDefaults()) // cors 비활성화 -> 프론트와 연결 시 따로 설정 필요
                .httpBasic(auth -> auth.disable()) // 기본 인증 로그인 비활성화
                .formLogin(auth -> auth.disable()) // 기본 login form 비활성화
                .logout(auth -> auth.disable()) // 기본 logout 비활성화
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음

                // request 인증, 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login").permitAll() // 인증 없이 접근 가능한 경로
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )

                // oauth2 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 커스텀 로그인 페이지 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService) // 사용자 정보를 처리할 서비스 설정
                        )
                        .defaultSuccessUrl("/api/auth/login-success", true) // 로그인 성공 후 리다이렉트 URL
                )

                // jwt 관련 설정
//                http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    // 비밀번호 암호화 시 사용할 BCryptPasswordEncoder를 스프링 빈으로 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
