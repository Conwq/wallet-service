package org.example.walletservice.model.dto;

import java.math.BigDecimal;

/**
 * A Data Transfer Object (DTO) representing the response for a transaction.
 *
 * @param operation The type of operation (e.g., "CREDIT" or "DEBIT").
 * @param amount    The amount involved in the transaction.
 * @param token     The unique token associated with the transaction.
 */
public record TransactionResponseDto(String operation, BigDecimal amount, String token) {
}
