package com.example.pagae_app.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for returning public user data")
public record UserResponseDTO(

        @Schema(description = "User's unique identifier", example = "101")
        Long id,

        @Schema(description = "User's full name", example = "Ana Silva")
        String name,

        @Schema(description = "Unique login for the user", example = "ana.silva")
        String login

) {
        public UserResponseDTO(User user){
                this(
                        user.getId(), user.getName(), user.getLogin()
                );
        }
}
