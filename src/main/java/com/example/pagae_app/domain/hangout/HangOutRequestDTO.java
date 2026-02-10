package com.example.pagae_app.domain.hangout;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "DTO for creating a new HangOut")
public record HangOutRequestDTO(
        @Schema(description = "Title of the HangOut", example = "Viagem para a Praia")
        @NotNull
        String title,

        List<Long> memberIds
) {}
