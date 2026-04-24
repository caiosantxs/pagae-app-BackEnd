package com.example.pagae_app.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for authentication")
public record AuthenticationDTO(

        @Schema(description = "Login from user", example = "caio.samtxs")
        @NotBlank
        String login,

        @Schema(description = "User password", example = "123456789")
        @NotBlank
        String password
) {
}
