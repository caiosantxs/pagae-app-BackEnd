package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.user.*;
import com.example.pagae_app.infra.exceptions.InvalidTokenException;
import com.example.pagae_app.infra.security.TokenService;
import com.example.pagae_app.repositories.UserRepository;
import com.example.pagae_app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication & User Management", description = "Endpoints for user authentication and registration.")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user based on their login and password, and returns a JWT token upon successful authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful, JWT token returned.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request: Invalid data provided (e.g., empty login or password).",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: Invalid login or password.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Parameter(description = "User's login credentials", required = true, schema = @Schema(implementation = AuthenticationDTO.class))
            @RequestBody @Valid AuthenticationDTO data
    ) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var user = (User) auth.getPrincipal();
        var token = tokenService.generateToken((User) auth.getPrincipal());
        
        return ResponseEntity.ok(new LoginResponseDTO(token, user.getName(), user.getId()));
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account in the system. The password will be securely encrypted before being stored."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request: Invalid data provided or the login is already in use.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            )
    })
    @PostMapping("/register")
    public ResponseEntity register(
            @Parameter(description = "Data for the new user account", required = true, schema = @Schema(implementation = RegisterDTO.class))
            @RequestBody @Valid RegisterDTO data
    ) {
        if(this.userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().body("Error: Login is already taken!");
        }
        UserResponseDTO newUser = userService.create(data);
        return ResponseEntity.ok().body(newUser);
    }

    @Operation(
            summary = "Request password change",
            description = "Requests a password change and sends an email to the user with the token to reset the password."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password change request accepted. An email will be sent if the user exists.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided (e.g., invalid email format).",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordDTO dto) {
        try{
            userService.resetPasswordRequest(dto.email());
            return ResponseEntity.ok().body("Password reset email sent if the user exists.");
        }catch(EntityNotFoundException e){
            return ResponseEntity.ok().body("Password reset email sent if the user exists.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request.");
        }
    }



    @Operation(
            summary = "Reset user password",
            description = "Resets the user's password using a valid token provided via email and the new desired password."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request: Invalid or expired token, or new password does not meet requirements.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Parameter(description = "Token received via email and the new password", required = true,
                    schema = @Schema(implementation = ResetPasswordDTO.class))
            @RequestBody @Valid ResetPasswordDTO data) {
        try {
            userService.resetPassword(data.token(), data.newPassword());
            return ResponseEntity.ok().body("Password reset successful!");
        } catch (InvalidTokenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
