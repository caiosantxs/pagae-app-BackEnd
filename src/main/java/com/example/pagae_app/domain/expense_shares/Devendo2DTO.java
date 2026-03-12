package com.example.pagae_app.domain.expense_shares;

import java.math.BigDecimal;

public record Devendo2DTO(
        Long userId,
        String name,
        BigDecimal quantoDevo,
        BigDecimal quantoMeDeve,
        BigDecimal saldoLiquido
) {}
