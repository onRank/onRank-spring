package com.onrank.server.api.dto.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
//
//@Getter
//public class CustomOAuth2User extends DefaultOAuth2User {
//
//    private final String username;
//
//    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
//                            Map<String, Object> attributes,
//                            String nameAttributeKey) {
//
//        super(authorities, attributes, nameAttributeKey);
//        this.username = attributes.get(nameAttributeKey).toString();
//    }
//}
