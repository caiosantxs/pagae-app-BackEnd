package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.expense.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM expenses e WHERE e.hangOut.id = :hangOutId")
    Page<Expense> findByHangOutId(@Param("hangOutId") Long hangOutId, Pageable pageable);
}
