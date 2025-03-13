package com.onrank.server.domain.student;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return description.toUpperCase();
    }
}