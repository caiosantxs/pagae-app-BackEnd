package domain.model.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "DTO representing a payment within an expense creation request")
public record PaymentRequestDTO(
        @Schema(description = "ID of the user making the payment", example = "101")
        @NotNull
        Long userId,

        @Schema(description = "Amount paid by this user", example = "150.50")
        @NotNull @Positive
        BigDecimal amount
) {}
