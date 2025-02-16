package com.onrank.server.api.service.auth;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final StudentService studentService;

    // Google 에서 사용자 정보를 가져오는 서비스
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. OAuth2 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String username = (String) attributes.get("sub");

        if (username == null) {
            throw new IllegalArgumentException("OAuth2 사용자 정보에 'sub' 값이 없습니다.");
        }

        // 2. 사용자 정보 DB 조회 (기존 회원 여부 확인)
        boolean isNewUser = studentService.findByUsername(username).isEmpty(); // 기존 회원 여부 체크

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub",  // Google에서 고유 식별자(sub)를 기본 키로 사용
                isNewUser // 신규 회원 여부 추가
        );
    }
}