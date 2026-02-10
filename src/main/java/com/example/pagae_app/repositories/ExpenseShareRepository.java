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

    ExpenseShare findByExpense_IdAndUser_Id(Long expenseId, Long userId);

    List<ExpenseShare> findByUser_IdAndExpense_HangOut_Id(Long userId, Long expenseHangOutId);

    // 1. A PAGAR: Soma tudo que o usuário atual DEVE (amountOwed) e que ainda não foi pago
    @Query("SELECT COALESCE(SUM(es.amountOwed), 0) FROM expense_shares es " +
            "WHERE es.user.id = :userId AND es.isPaid = false")
    BigDecimal sumTotalOwedByUserId(@Param("userId") Long userId);

    // 2. QUANTIDADE DE PENDÊNCIAS: Conta quantas dívidas o usuário tem em aberto
    @Query("SELECT COUNT(es) FROM expense_shares es " +
            "WHERE es.user.id = :userId AND es.isPaid = false")
    Integer countPendingDebtsByUserId(@Param("userId") Long userId);

    // 3. A RECEBER: Soma a dívida dos OUTROS, em despesas onde o usuário atual foi o PAGADOR
    // Perceba que usamos "es.user.id != :userId" para não somar a parte que o usuário deve a ele mesmo.
    @Query("SELECT COALESCE(SUM(es.amountOwed), 0) FROM expense_shares es " +
            "JOIN es.expense ex " +
            "WHERE ex.payer.id = :userId " +
            "AND es.user.id != :userId " +
            "AND es.isPaid = false")
    BigDecimal sumTotalReceivableByUserId(@Param("userId") Long userId);
}
