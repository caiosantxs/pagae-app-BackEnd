package com.example.pagae_app.domain.expense;

import com.example.pagae_app.domain.payment.PaymentResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "DTO for returning detailed expense data")
public record ExpenseResponseDTO(
        @Schema(description = "Expense's unique identifier", example = "210")
        Long id,

        @Schema(description = "Description of the expense", example = "Jantar no restaurante")
        String description,

        @Schema(description = "Total amount of the expense", example = "250.50")
        BigDecimal totalAmount,

        @Schema(description = "List of payments that make up the total amount")
        List<PaymentResponseDTO> payments
) {}