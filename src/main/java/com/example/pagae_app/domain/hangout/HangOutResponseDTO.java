package com.example.pagae_app.domain.hangout;

import com.example.pagae_app.domain.expense.ExpenseResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "DTO for returning HangOut data")
public record HangOutResponseDTO(
        @Schema(description = "HangOut's unique identifier", example = "58")
        Long id,

        @Schema(description = "Title of the HangOut", example = "Viagem para a Praia")
        String title,

        @Schema(description = "Brief description of the HangOut", example = "Organização da viagem de fim de ano da turma.")
        String description,

        @Schema(description = "ID of the user who created the HangOut", example = "101")
        Long creatorId,

        @Schema(description = "Expenses from hangout")
        List<ExpenseResponseDTO> expenses
) {
    public HangOutResponseDTO(HangOut hangOut) {
        this(
                hangOut.getId(),
                hangOut.getTitle(),
                hangOut.getDescription(),
                hangOut.getCreator().getId(),
                hangOut.getExpenses().stream()
                        .map(ExpenseResponseDTO::new).collect(Collectors.toList())
        );
    }
}
