package com.tn.server.repository.user;

import com.tn.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);
}