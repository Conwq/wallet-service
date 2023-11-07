package org.example.walletservice.model.entity;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A data class representing a transaction.
 * This class is annotated with Lombok annotations for generating boilerplate code.
 */
public class Transaction {
	private int transactionID;
	private String record;
	private String token;
	private String operation;
	private BigDecimal amount;
	private int playerID;

	public Transaction() {
	}

	public Transaction(int transactionID, String record, String token, String operation, BigDecimal amount, int playerID) {
		this.transactionID = transactionID;
		this.record = record;
		this.token = token;
		this.operation = operation;
		this.amount = amount;
		this.playerID = playerID;
	}

	public int getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transaction that = (Transaction) o;
		return transactionID == that.transactionID && playerID == that.playerID && Objects.equals(record, that.record) && Objects.equals(token, that.token) && Objects.equals(operation, that.operation) && Objects.equals(amount, that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transactionID, record, token, operation, amount, playerID);
	}

	@Override
	public String toString() {
		return "Transaction{" +
				"transactionID=" + transactionID +
				", record='" + record + '\'' +
				", token='" + token + '\'' +
				", operation='" + operation + '\'' +
				", amount=" + amount +
				", playerID=" + playerID +
				'}';
	}
}
