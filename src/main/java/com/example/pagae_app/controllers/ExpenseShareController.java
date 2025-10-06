package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.expense_shares.ExpenseShareDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.services.ExpenseShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/shares")
public class ExpenseShareController {

    @Autowired
    private ExpenseShareService expenseShareService;

    @PatchMapping("/{shareId}/pay")
    public ResponseEntity<Void> markShareAsPaid(
            @PathVariable Long shareId,
            Authentication authentication
    ) {
        User authenticatedUser = (User) authentication.getPrincipal();
        expenseShareService.markShareAsPaid(shareId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseShareDTO>> getAllSharesByUser(Authentication authentication, Pageable pageable) {
        User authenticatedUser = (User) authentication.getPrincipal();

        Page<ExpenseShareDTO> shares = expenseShareService.getAllExpenseSharesByUser(authenticatedUser.getId(), pageable);

        return ResponseEntity.ok(shares);
    }

    @GetMapping("/hangouts/{hangOutId}")
    public ResponseEntity<Page<ExpenseShareDTO>> getAllSharesByHangOut(@PathVariable Long hangOutId, Authentication authentication, Pageable pageable) {
        User authenticatedUser = (User) authentication.getPrincipal();

        Page<ExpenseShareDTO> shares = expenseShareService.getExpensesSharesByHangOut(hangOutId, authenticatedUser.getId(), pageable);
        return ResponseEntity.ok(shares);
    }
}
