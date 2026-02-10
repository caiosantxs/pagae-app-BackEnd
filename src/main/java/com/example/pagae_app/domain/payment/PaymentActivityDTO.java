package com.example.pagae_app.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentActivityDTO(
        Long id,
        String payerName,
        String description,
        BigDecimal amount,
        LocalDateTime date,
        Long payerId
) {
}
