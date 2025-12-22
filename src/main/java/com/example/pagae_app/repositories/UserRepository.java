package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByLogin(String login);
    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u FROM users u 
        WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(u.login) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<User> searchByNameOrLogin( String query, Pageable pageable);
}
