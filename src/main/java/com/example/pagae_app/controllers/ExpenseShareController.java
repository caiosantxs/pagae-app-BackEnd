package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.expense_shares.ExpenseShareDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.services.ExpenseShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/shares")
@Tag(name = "Expense Shares (Debts)", description = "Operations related to managing individual expense shares or debts.")
public class ExpenseShareController {

    @Autowired
    private ExpenseShareService expenseShareService;

    @Operation(
            summary = "Mark a debt as paid",
            description = "Updates the status of a specific expense share (debt) to 'paid'. This action can only be performed by the user who owes the debt or by the creator of the hangout."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully marked as paid."),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not the debtor or the hangout creator.",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Expense share not found with the specified ID.",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content)
    })
    @PatchMapping("/{shareId}/pay")
    public ResponseEntity<Void> markShareAsPaid(
            @Parameter(description = "ID of the expense share to mark as paid", required = true, example = "734")
            @PathVariable Long shareId,
            Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        expenseShareService.markShareAsPaid(shareId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List all my debts",
            description = "Retrieves a paginated list of all expense shares (debts) assigned to the currently authenticated user across all their hangouts."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of debts."),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not authenticated.",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content)
    })
    @GetMapping("/my-shares")
    @PageableAsQueryParam
    public ResponseEntity<Page<ExpenseShareDTO>> getAllSharesByUser(
            Authentication authentication,
            @Parameter(hidden = true)
            Pageable pageable) {
        User authenticatedUser = (User) authentication.getPrincipal();
        Page<ExpenseShareDTO> shares = expenseShareService.getAllExpenseSharesByUser(authenticatedUser.getId(), pageable);
        return ResponseEntity.ok(shares);
    }

    @Operation(
            summary = "List all debts for a specific hangout",
            description = "Retrieves a paginated list of all expense shares (debts) for a given hangout. Only members of the hangout can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of debts."),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not a member of the hangout.",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hangout not found with the specified ID.",
                    content = @Content),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content)
    })
    @GetMapping("/hangouts/{hangOutId}/expense-shares")
    @PageableAsQueryParam
    public ResponseEntity<Page<ExpenseShareDTO>> getAllSharesByHangOut(
            @Parameter(description = "ID of the hangout to retrieve debts from", required = true, example = "58")
            @PathVariable Long hangOutId,
            Authentication authentication,
            @Parameter(hidden = true)
            Pageable pageable) {
        User authenticatedUser = (User) authentication.getPrincipal();
        Page<ExpenseShareDTO> shares = expenseShareService.getExpensesSharesByHangOut(hangOutId, authenticatedUser.getId(), pageable);
        return ResponseEntity.ok(shares);
    }
}
