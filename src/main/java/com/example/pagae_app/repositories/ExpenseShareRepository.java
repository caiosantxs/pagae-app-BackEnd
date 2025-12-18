package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.expense_shares.ExpenseShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    Page<ExpenseShare> findExpenseShareByUserId(Long userId, Pageable pageable);

    Page<ExpenseShare> findByExpense_HangOut_Id(Long expenseHangOutId, Pageable pageable);

    @Query("SELECT SUM(es.amountOwed) FROM expense_shares es WHERE es.user.id = :userId AND es.isPaid = false")
    BigDecimal sumTotalOwedByUserId(@Param("userId") Long userId);

    ExpenseShare findByExpense_IdAndUser_Id(Long expenseId, Long userId);

    List<ExpenseShare> findByUser_IdAndExpense_HangOut_Id(Long userId, Long expenseHangOutId);
}
