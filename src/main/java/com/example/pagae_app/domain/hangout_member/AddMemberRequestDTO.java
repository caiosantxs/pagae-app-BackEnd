package com.example.pagae_app.domain.hangout_member;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO for adding a new member to a HangOut")
public record AddMemberRequestDTO(
        @Schema(description = "ID of the user to be added as a member", example = "102")
        @NotNull
        Long userId
) {}
