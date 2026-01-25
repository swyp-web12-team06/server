package com.redot.auth.repository;

import com.redot.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
}
