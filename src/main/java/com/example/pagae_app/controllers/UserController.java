package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.domain.user.UserResponseDTO;
import com.example.pagae_app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "Operations related to user data management")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Delete a user account",
            description = "Delete a registry of a user"
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden if the current user is not a Administrator, he can't delete a User",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User with the id specified was not fount",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            )
    }
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, Authentication auth) {
        User authenticatedUser = (User) auth.getPrincipal();
        this.userService.delete(userId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }



}
