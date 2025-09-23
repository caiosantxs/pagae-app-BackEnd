package com.example.pagae_app.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for authentication")
public record AuthenticationDTO(
        @Schema(description = "Login from user", example = "caio")
        String login,

        @Schema(description = "User password", example = "123456")
        String password
) {
}
