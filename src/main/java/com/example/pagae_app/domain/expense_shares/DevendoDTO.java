package com.example.pagae_app.domain.expense_shares;

import java.math.BigDecimal;

public record DevendoDTO(
        Long userId,
        String name,
        BigDecimal total
) {}
