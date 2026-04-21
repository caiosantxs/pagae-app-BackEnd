package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.expense.ExpenseRequestDTO;
import com.example.pagae_app.domain.expense.UpdateDescriptionDTO;
import com.example.pagae_app.domain.expense_shares.Devendo2DTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.services.ExpenseService;
import com.example.pagae_app.services.HangOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Expenses operations")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private HangOutService hangOutService;

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

    @GetMapping("/descontos")
    public ResponseEntity<List<Devendo2DTO>> listarDescontosMutuos(Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();

        List<Devendo2DTO> descontos = expenseService.calculandoDescontos(authenticatedUser.getId());

        return ResponseEntity.ok(descontos);
    }

    @PostMapping("/descontos/aplicar/{targetUserId}")
    public ResponseEntity<String> aplicarDescontoMutuo(
            @PathVariable Long targetUserId,
            Authentication authentication) {

        User authenticatedUser = (User) authentication.getPrincipal();
        expenseService.realizandoDescontos(authenticatedUser.getId(), targetUserId);

        return ResponseEntity.ok("Desconto mútuo aplicado com sucesso!");
    }

}
