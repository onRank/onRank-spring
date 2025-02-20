package com.onrank.server.domain.refreshtoken;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private String username;

    private String refreshToken;

    private Instant expiration;

    public RefreshToken(String username, String refreshToken, Instant expiration) {
        this.username = username;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
