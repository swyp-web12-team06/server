package com.tn.server.auth;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    protected RefreshToken() {}

    public RefreshToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
        this.expiryDate = Instant.now().plusSeconds(60 * 60 * 24 * 14);
    }

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

}
