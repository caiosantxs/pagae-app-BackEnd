package com.example.pagae_app.domain.expense;

import com.example.pagae_app.domain.payment.PaymentRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Schema(description = "DTO to receive data on create a new expense")
public record ExpenseRequestDTO(
        @Schema(description = "description of a expense", example = "Pizza")
        @NotNull
        String description,

        @Schema(description = "amount total", example = "30.00")
        @NotNull
        BigDecimal totalAmount,

        Optional<List<PaymentRequestDTO>> payments
) {}
