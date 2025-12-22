package com.example.pagae_app.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentActivityDTO(
        Long id,
        String payerName,       // "Caio"
        String description,     // "Uber" (Vem da Expense)
        BigDecimal amount,      // 30.00
        LocalDateTime date,     // (Vem da Expense)
        Long payerId
) {
}
