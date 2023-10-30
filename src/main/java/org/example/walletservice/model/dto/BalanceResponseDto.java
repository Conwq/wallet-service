package org.example.walletservice.model.dto;

import java.math.BigDecimal;

public record BalanceResponseDto(String username, BigDecimal balance) {
}
