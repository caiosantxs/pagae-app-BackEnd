package com.example.pagae_app.domain.user;

import jakarta.validation.constraints.NotNull;

public record ForgotPasswordDTO(
        @NotNull
        String email
) {
}
