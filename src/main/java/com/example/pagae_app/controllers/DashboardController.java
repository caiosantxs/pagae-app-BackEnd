package com.example.pagae_app.controllers;

import com.example.pagae_app.domain.hangout.DashboardStatsDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoints para estatísticas do dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get user dashboard statistics", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DashboardStatsDTO> getStats(Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        DashboardStatsDTO stats = dashboardService.getDashboardStats(authenticatedUser.getId());
        return ResponseEntity.ok(stats);
    }
}
