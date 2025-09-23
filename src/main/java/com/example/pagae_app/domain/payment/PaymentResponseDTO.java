package com.example.pagae_app.domain.payment;

import com.example.pagae_app.domain.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO for returning data about a single payment")
public record PaymentResponseDTO(
        @Schema(description = "Payment's unique identifier", example = "451")
        Long id,

        @Schema(description = "Amount paid", example = "150.50")
        BigDecimal amount,

        @Schema(description = "Details of the user who paid")
        UserResponseDTO user
) {}
