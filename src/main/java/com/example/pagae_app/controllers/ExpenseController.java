package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.expense.ExpenseRequestDTO;
import com.example.pagae_app.domain.payment.PaymentRequestDTO;
import com.example.pagae_app.domain.payment.PaymentResponseDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.services.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Expenses operations")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;



    @Operation(
            summary = "Delete an expense",
            description = "Deletes an existing expense. This action can only be performed by the creator of the hangout."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Expense deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not the hangout creator.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Expense not found.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authenticated",
                    content = @Content
            )
    })
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @Parameter(description = "ID of the expense to delete", required = true, example = "210")
            @PathVariable Long expenseId,
            Authentication auth
    ) {
        User authenticatedUser = (User) auth.getPrincipal();
        expenseService.deleteExpense(expenseId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update an expense",
            description = "Updates the description or total amount of an existing expense. This action can only be performed by the creator of the hangout."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Expense updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided for the update.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not the hangout creator.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Expense not found.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authenticated",
                    content = @Content
            )
    })
    @PutMapping("/{expenseId}")
    public ResponseEntity<Void> updateExpense(
            @Parameter(description = "ID of the expense to update", required = true, example = "210")
            @PathVariable Long expenseId,
            @RequestBody @Valid ExpenseRequestDTO expense,
            Authentication auth
    ) {
        User authenticatedUser = (User) auth.getPrincipal();
        expenseService.updateExpense(expenseId, expense, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }


}
