package com.onrank.server.api.service.auth;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        /**
         * (?) 제공 받은 OAuth2User의 authorities 값은 어디에 쓰이나?
         */

        /**
         * 커스텀 OAuth2User의 authorities:
         * 기본적으로 "ROLE_USER"를 부여한 후,
         * 제공 받은 OAuth2User를 활용하여
         * 특정 조건(예: 이메일)에 따라 추가적인 역할 할당 혹은 추후에 역할 할당
         * (더 알아봐야 함)
         */

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("attributes: {}", attributes);

        // Google에서 제공하는 고유 식별자 (sub)
        String sub = (String) attributes.get("sub");


        // Client Registration Id ("google")
        String registartionId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2User의 username을 "google 1234..."과 같이 구성
        String username = registartionId + " " + sub;
        log.info("username: {}", username);

        String email = (String) attributes.get("email");
        log.info("email: {}", email);

        // 처음 로그인할 때는 "ROLE_USER" 부여
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                username,
                email
        );

    }
}