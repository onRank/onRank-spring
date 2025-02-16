package com.onrank.server.api.dto.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final String username;
    private final boolean isNewUser; // 신규 회원 여부 필드 추가

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            boolean isNewUser) {

        super(authorities, attributes, nameAttributeKey);
        this.username = attributes.get("sub").toString();
        this.isNewUser = isNewUser;
    }
}
