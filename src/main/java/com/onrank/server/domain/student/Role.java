package com.onrank.server.domain.student;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Getter
public enum Role implements GrantedAuthority {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return description.toUpperCase();
    }
}