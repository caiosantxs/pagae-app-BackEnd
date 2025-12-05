package com.example.pagae_app.domain.hangout;

import java.math.BigDecimal;

public record DashboardStatsDTO(
        Long totalHangouts,
        BigDecimal totalOwed
) {
}
