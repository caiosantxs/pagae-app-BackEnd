package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.payment.PaymentRequestDTO;
import com.example.pagae_app.domain.payment.PaymentResponseDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import com.example.pagae_app.domain.expense.ExpenseRequestDTO;
import com.example.pagae_app.domain.expense.ExpenseResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/{hangOutId}")
    public ResponseEntity<ExpenseResponseDTO> addExpense(@RequestBody ExpenseRequestDTO expense, @PathVariable Long hangOutId, Authentication auth) {
        User authenticatedUser = (User) auth.getPrincipal();
        ExpenseResponseDTO newExpense = expenseService.createExpense(expense, hangOutId, authenticatedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }

    @PostMapping("/{expenseId}/payments")
    public ResponseEntity<PaymentResponseDTO> addPayment(@RequestBody PaymentRequestDTO payment, @PathVariable Long expenseId, Authentication auth) {
        User authenticatedUser = (User) auth.getPrincipal();
        PaymentResponseDTO newPayment = expenseService.addPayment(payment, expenseId, authenticatedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newPayment);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId, Authentication auth) {
        User authenticatedUser = (User) auth.getPrincipal();

        expenseService.deleteExpense(expenseId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<Void> updateExpense(@RequestBody ExpenseRequestDTO expense, @PathVariable Long expenseId, Authentication auth) {
        User authenticatedUser = (User) auth.getPrincipal();
        expenseService.updateExpense(expenseId, expense, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hangouts/{hangOutId}/expenses")
    public ResponseEntity<Page<ExpenseResponseDTO>> getExpensesForHangOut(
            @PathVariable Long hangOutId,
            Pageable pageable,
            Authentication authentication
    ) {
        User authenticatedUser = (User) authentication.getPrincipal();
        Page<ExpenseResponseDTO> expenses = expenseService.getExpenses(hangOutId, authenticatedUser.getId(), pageable);
        return ResponseEntity.ok(expenses);
    }

}
