package org.example.walletservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * A data class representing a transaction.
 * This class is annotated with Lombok annotations for generating boilerplate code.
 */
@Data
@Builder
@AllArgsConstructor
public class Transaction {
	private int transactionID;
	private String record;
	private String token;
	private String operation;
	private BigDecimal amount;
	private int playerID;
}
