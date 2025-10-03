package com.example.pagae_app.domain.expense;

import com.example.pagae_app.domain.payment.Payment;
import com.example.pagae_app.domain.payment.PaymentResponseDTO;
import com.example.pagae_app.domain.user.UserResponseDTO;
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
        List<PaymentResponseDTO> payments,

        boolean isPaid
) {
        public ExpenseResponseDTO(Expense expense){
                this(
                        expense.getId(), expense.getDescription(), expense.getTotalAmount(), expense.getPayments().stream()
                                .map(payment -> new PaymentResponseDTO(
                                        payment.getId(),
                                        payment.getAmount(),
                                        new UserResponseDTO(payment.getUser())
                                ))
                                .toList(),

                        calculateIsPaid(expense)

                );
        }

        private static boolean calculateIsPaid(Expense expense) {
                if (expense.getPayments() == null || expense.getPayments().isEmpty()) {
                        return false;
                }

                BigDecimal totalPaid = expense.getPayments().stream()
                        .map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                return totalPaid.compareTo(expense.getTotalAmount()) == 0;
        }
}