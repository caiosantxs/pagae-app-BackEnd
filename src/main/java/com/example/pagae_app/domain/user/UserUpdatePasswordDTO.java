package com.example.pagae_app.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO to uptade a user password")
public record UserUpdatePasswordDTO(

        @Schema(description = "old password", example = "123456789")
        @NotBlank
        String currentPassword,

        @Schema(description = "new password", example = "12345678")
        @NotBlank
        String newPassword,

        @Schema(description = "Confirmation of the new password", required = true, example = "newStrongPassword!456")
        @NotBlank
        String confirmPassword
) {
}
