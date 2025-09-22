package domain.model.hangout;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO for creating a new HangOut")
public record HangOutRequestDTO(
        @Schema(description = "Title of the HangOut", example = "Viagem para a Praia")
        @NotNull
        String title,

        @Schema(description = "Brief description of the HangOut", example = "Organização da viagem de fim de ano da turma.")
        String description
) {}
