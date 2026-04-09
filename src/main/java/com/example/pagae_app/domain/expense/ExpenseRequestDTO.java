package com.example.pagae_app.domain.expense;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "DTO para receber dados na criação de uma nova despesa")
public record ExpenseRequestDTO(

        @Schema(description = "nome de uma despesa", example = "Uber")
        @NotNull
        String name,

        @Schema(description = "descrição da despesa", example = "Uber que pegamos para ir para padaria")
        String description,

        @Schema(description = "gasto total da despesa", example = "30.00")
        @NotNull
        BigDecimal totalAmount,

        @Schema(description = "Lista de código dos envolvidos na despesa")
        List<Long> participantsIds,

        @Schema(description = "Código do pagador da despesa")
        Long payerId
) {}
