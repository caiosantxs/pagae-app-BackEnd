package com.example.pagae_app.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @Schema(description = "User's full name", example = "Ana Silva")
        @NotBlank
        String name,

        @Schema(description = "Unique login for the user", example = "ana.silva")
        @NotBlank
        String login,

        @Schema(description = "User's unique email", example = "ana.silva@example.com")
        @NotBlank @Email
        String email,

        @Schema(description = "User's password", example = "strongPassword123")
        @NotBlank @Size(min = 8)
        String password,

        @Schema(description = "User's role", example = "ADMIN")
        UserRole role
) {
}
