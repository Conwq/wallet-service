package org.example.walletservice.model.dto;

import java.math.BigDecimal;

public record TransactionRequestDto (BigDecimal inputPlayerAmount, String transactionToken) {
}
