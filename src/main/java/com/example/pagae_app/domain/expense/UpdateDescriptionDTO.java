package com.example.pagae_app.domain.expense;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record UpdateDescriptionDTO(
        @Schema(description = "Nova descrição da despesa", example = "Compramos 2 pizzas de calabresa")
        @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
        String description
) {
}
