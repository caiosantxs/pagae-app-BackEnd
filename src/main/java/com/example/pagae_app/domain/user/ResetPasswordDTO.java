package com.example.pagae_app.domain.user;


import jakarta.validation.constraints.NotNull;

public record ResetPasswordDTO(
        @NotNull
        String token,
        @NotNull
        String newPassword
) {
}
