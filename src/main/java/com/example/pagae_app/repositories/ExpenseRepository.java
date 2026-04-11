package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.expense.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM expenses e WHERE e.hangOut.id = :hangOutId")
    Page<Expense> findByHangOutId(@Param("hangOutId") Long hangOutId, Pageable pageable);

    @Query(value = "SELECT EXISTS (" +
            "  SELECT 1 FROM expense_shares es " +
            "  JOIN expenses e ON e.id = es.expense_id " +
            "  WHERE es.user_id = :userId " +
            "  AND e.hangout_id = :hangOutId " +
            "  AND es.amount_owed > 0" +
            ")",
            nativeQuery = true)
    boolean hasPendingExpenses(@Param("userId") Long userId, @Param("hangOutId") Long hangOutId);

    void deleteByHangOutId(@Param("hangOutId") Long hangOutId);
}
