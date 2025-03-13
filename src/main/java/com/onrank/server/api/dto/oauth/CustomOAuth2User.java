package com.onrank.server.api.dto.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.Map;

@Getter // (?) record class로 바꿀 수 있음
public class CustomOAuth2User implements OAuth2User {

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String username;
    private final String email;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String username, String email) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.username = username;
        this.email = email;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
