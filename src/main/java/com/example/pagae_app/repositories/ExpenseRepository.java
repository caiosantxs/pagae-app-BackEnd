package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
