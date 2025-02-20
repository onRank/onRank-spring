package com.onrank.server.api.service.auth;

import com.onrank.server.api.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final StudentService studentService;

    // 리소스 서버에서 받아온 사용자 정보를 처리하는 서비스
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User: {}", oAuth2User);

        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        log.info("authorities: {}", authorities);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("attributes: {}", attributes);

        // Google에서 제공하는 고유 식별자(sub)를 기본 키로 사용
        String username = (String) attributes.get("sub");
        log.info("username: {}", username);

        if (username == null) {
            throw new IllegalArgumentException("OAuth2 사용자 정보에 'sub' 값이 없습니다.");
        }

//        return new CustomOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//                attributes,
//                "sub" // Google에서 제공하는 고유 식별자(sub)를 기본 키로 사용
//        );

        // Google에서 제공하는 고유 식별자(sub)를 기본 키로 사용
        return new DefaultOAuth2User(authorities, attributes, "sub");
    }
}