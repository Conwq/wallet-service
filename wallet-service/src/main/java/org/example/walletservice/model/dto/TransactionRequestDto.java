package org.example.walletservice.model.dto;

import org.example.walletservice.model.enums.Operation;

import java.math.BigDecimal;

/**
 * Data transfer object representing a transaction request.
 *
 * @param inputPlayerAmount The amount involved in the transaction.
 * @param transactionToken  The transaction token associated with the transaction.
 */
public record TransactionRequestDto(Operation operation, BigDecimal inputPlayerAmount, String transactionToken) {
}
