package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.expense.ExpenseRequestDTO;
import com.example.pagae_app.domain.expense.ExpenseResponseDTO;
import com.example.pagae_app.domain.expense_shares.ExpenseShareDTO;
import com.example.pagae_app.domain.hangout.HangOutRequestDTO;
import com.example.pagae_app.domain.hangout.HangOutResponseDTO;
import com.example.pagae_app.domain.hangout_member.AddMemberRequestDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.domain.user.UserResponseDTO;
import com.example.pagae_app.services.ExpenseService;
import com.example.pagae_app.services.ExpenseShareService;
import com.example.pagae_app.services.HangOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hangouts")
@Tag(name = "Hangouts", description = "Operations related to managing Hang Outs")
public class HangOutController {

    @Autowired
    private HangOutService hangOutService;

    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private ExpenseShareService expenseShareService;


    @Operation(
            summary = "Create a new hang out",
            description = "Registers a new hang out based on the provided information and returns details of the created hang out."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Hang out created successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HangOutResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided to create a hang out.",
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
    @PostMapping
    public ResponseEntity<HangOutResponseDTO> create(
            @Parameter(
                    description = "Data for the hang out to create.",
                    required = true,
                    schema = @Schema(implementation = HangOutRequestDTO.class)
            )
            @RequestBody @Valid HangOutRequestDTO data, Authentication authentication) {

        User authenticatedUser = (User) authentication.getPrincipal();
        HangOutResponseDTO newHangOut = hangOutService.create(data, authenticatedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newHangOut);
    }

    @Operation(
            summary = "List my hangouts",
            description = "Retrieves a paginated list of hangouts that the currently authenticated user is a member of." // Clear description of what it does
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of hangouts.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not authenticated.",
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
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<HangOutResponseDTO>> findMyHangOuts(
            Authentication authentication,
            @Parameter(hidden = true)
            Pageable pageable
    ) {

        User authenticatedUser = (User) authentication.getPrincipal();
        Page<HangOutResponseDTO> hangOuts = hangOutService.findHangOutsByUserId(authenticatedUser.getId(), pageable);
        return ResponseEntity.ok(hangOuts);
    }

    @Operation(
            summary = "Update a hangout",
            description = "Updates the title and/or description of an existing hangout. Only the creator of the hangout can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Hangout updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided for the update.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not the creator of the hangout or is not authenticated.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hangout not found with the specified ID.",
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
    @PutMapping("/{hangOutId}")
    public ResponseEntity<Void> update(
            @Parameter(
                    description = "ID of the hangout to update",
                    required = true,
                    example = "58"
            )
            @PathVariable Long hangOutId,
            @Parameter(
                    description = "Updated data for the hangout",
                    required = true,
                    schema = @Schema(implementation = HangOutRequestDTO.class)
            )
            @RequestBody @Valid HangOutRequestDTO data,
            Authentication authentication
    ) {

        User authenticatedUser = (User) authentication.getPrincipal();
        hangOutService.update(data, hangOutId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete a hangout",
            description = "Deletes an existing hangout. Only the creator of the hangout can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Hangout deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not the creator of the hangout or is not authenticated.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hangout not found with the specified ID.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            )
    })
    @DeleteMapping("/{hangOutId}")
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "ID of the hangout to delete",
                    required = true,
                    example = "58"
            )
            @PathVariable Long hangOutId,
            Authentication authentication
    ) {

        User authenticatedUser = (User) authentication.getPrincipal();
        hangOutService.delete(hangOutId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add a member to a hangout",
            description = "Adds an existing user as a member to a specified hangout. Only current members of the hangout can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Member added successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided (e.g., user ID missing).",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the requesting user is not a member of the hangout or is not authenticated.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hangout or User to be added not found.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict: The user is already a member of this hangout.",
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
    @PostMapping("/{hangOutId}/new-member")
    public ResponseEntity<Void> addMember(
            @Parameter(
                    description = "ID of the hangout to add a member to",
                    required = true,
                    example = "58"
            )
            @PathVariable Long hangOutId,
            @Parameter(
                    description = "Data containing the ID of the user to add",
                    required = true,
                    schema = @Schema(implementation = AddMemberRequestDTO.class)
            )
            @RequestBody @Valid AddMemberRequestDTO memberData,
            Authentication authentication
    ) {

        User authenticatedUser = (User) authentication.getPrincipal();
        hangOutService.addMemberToHangOut(hangOutId, memberData.userId(), authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Create a new expense in a hangout",
            description = "Registers a new expense within a specified hangout. If participant IDs are provided, the expense is divided among them. Returns the details of the newly created expense." // Corrected & Clarified
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Expense created successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExpenseResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not a member of the hangout.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hangout not found.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authenticaded",
                    content = @Content
            )
    })
    @PostMapping("/{hangOutId}/expenses")
    public ResponseEntity<ExpenseResponseDTO> createExpense(
            @Parameter(
                    description = "ID of the hangout to add the expense to",
                    required = true,
                    example = "58"
            )
            @PathVariable Long hangOutId,
            @Parameter(
                    description = "Data for the expense to be created",
                    required = true,
                    schema = @Schema(implementation = ExpenseRequestDTO.class)
            )
            @RequestBody @Valid ExpenseRequestDTO expense,
            Authentication auth
    ) {

        User authenticatedUser = (User) auth.getPrincipal();
        ExpenseResponseDTO newExpense = expenseService.createExpense(expense, hangOutId, authenticatedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }

    @Operation(
            summary = "List expenses for a hangout",
            description = "Retrieves a paginated list of all expenses for a specific hangout. Only members of the hangout can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of expenses."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the user is not a member of the hangout.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hangout not found.",
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
    @GetMapping("{hangOutId}/expenses")
    @PageableAsQueryParam
    public ResponseEntity<Page<ExpenseResponseDTO>> getExpensesForHangOut(
            @Parameter(
                    description = "ID of the hangout to retrieve expenses from",
                    required = true,
                    example = "58"
            )
            @PathVariable Long hangOutId,
            @Parameter(hidden = true)
            Pageable pageable,
            Authentication authentication
    ) {

        User authenticatedUser = (User) authentication.getPrincipal();
        Page<ExpenseResponseDTO> expenses = expenseService.getExpenses(hangOutId, authenticatedUser.getId(), pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("{hangOutId}/members")
    public ResponseEntity<List<UserResponseDTO>> getHangOutMembers (@PathVariable Long hangOutId) {
        List<UserResponseDTO> members = hangOutService.getHangoutParticipants(hangOutId);
       return ResponseEntity.ok(members);
    }

    @GetMapping("{hangOutId}")
    public ResponseEntity<HangOutResponseDTO> getHangOutById (@PathVariable Long hangOutId) {
        return ResponseEntity.ok(hangOutService.getHangOutById(hangOutId));
    }

    @GetMapping("{hangOutId}/expense-shares")
    public ResponseEntity<List<ExpenseShareDTO>> getSharesByUserIdAndHangOutId (Authentication authentication, @PathVariable Long hangOutId) {
        User authenticatedUser = (User) authentication.getPrincipal();
        List<ExpenseShareDTO> shares = expenseShareService.getExpenseShareByUserAndHangOut(hangOutId, authenticatedUser.getId());
        return ResponseEntity.ok(shares);
    }

    @PatchMapping("/{id}/finalize")
    public ResponseEntity<Void> finalizeHangout(@PathVariable Long id) {
        hangOutService.finalize(id);
        return ResponseEntity.noContent().build();
    }



}
