package com.example.pagae_app.domain.user;

public record RegisterDTO(
        String login,
        String password,
        String name,
        String email,
        UserRole role
) {
}
