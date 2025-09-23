package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.expense_shares.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
}
