package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.expense_shares.ExpenseShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    Page<ExpenseShare> findExpenseShareByUserId(Long userId, Pageable pageable);

    Page<ExpenseShare> findByExpense_HangOut_Id(Long expenseHangOutId, Pageable pageable);
}
