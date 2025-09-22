package domain.model.expense_shares;

import domain.model.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO representing a single user's share of an expense")
public record ExpenseShareDTO(
        @Schema(description = "Unique identifier for the share", example = "734")
        Long id,

        @Schema(description = "ID of the expense this share belongs to", example = "210")
        Long expenseId,

        @Schema(description = "Details of the user who owes this share")
        UserResponseDTO user,

        @Schema(description = "Amount owed by the user for this share", example = "62.63")
        BigDecimal amountOwed,

        @Schema(description = "Indicates if this share has been settled", example = "false")
        boolean isPaid
) {}
