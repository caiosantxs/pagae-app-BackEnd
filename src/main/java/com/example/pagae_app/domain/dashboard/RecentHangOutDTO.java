package com.example.pagae_app.domain.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record RecentHangOutDTO(
        Long id,
        String initial,
        String title,
        String date,
        String status,
        BigDecimal total,
        List<ParticipantBadgeDTO> participants
) {
}
