package com.example.pagae_app.domain.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsDTO(
        Long totalHangouts,
        BigDecimal totalOwed,
        BigDecimal totalReceivable,
        Integer pendingDebtsCount,
        List<RecentHangOutDTO> recentHangouts
) {
}
