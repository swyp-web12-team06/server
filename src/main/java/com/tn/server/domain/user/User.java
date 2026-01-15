package com.tn.server.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String provider;
    private String providerId;
    private String profileImageUrl;


    @Builder
    public User(String nickname, String email, String profileImageUrl, Role role, String provider, String providerId) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
    public void updateNickname(String nickname) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void upgradeToUser() {
        this.role = Role.USER;
    }
    public void upgradeToSeller(){this.role=Role.SELLER; }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
