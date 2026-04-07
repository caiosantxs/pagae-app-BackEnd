package com.example.pagae_app.domain.expense;

public record ExpenseUpdateDescriptionDTO(
        Long hangoutId,
        Long expenseId,
        String description
) {
}
