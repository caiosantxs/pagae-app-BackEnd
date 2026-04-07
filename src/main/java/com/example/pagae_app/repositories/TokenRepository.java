package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.user.PasswordResetToken;
import com.example.pagae_app.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
