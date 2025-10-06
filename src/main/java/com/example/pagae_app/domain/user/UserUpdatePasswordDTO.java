package com.example.pagae_app.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO to uptade a user password")
public record UserUpdatePasswordDTO(
        @Schema(description = "ID from user", example = "1")
        Long userId,
        @Schema(description = "new password", example = "12345678")
        String newPassword
) {
}
