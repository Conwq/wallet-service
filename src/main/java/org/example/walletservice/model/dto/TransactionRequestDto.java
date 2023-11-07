package org.example.walletservice.model.dto;

import java.math.BigDecimal;

/**
 * Data transfer object representing a transaction request.
 *
 * @param inputPlayerAmount The amount involved in the transaction.
 * @param transactionToken  The transaction token associated with the transaction.
 */
public record TransactionRequestDto(BigDecimal inputPlayerAmount, String transactionToken) {
}
