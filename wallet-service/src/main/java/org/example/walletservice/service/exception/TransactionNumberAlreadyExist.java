package org.example.walletservice.service.exception;

/**
 * Exception indicating that a transaction with the specified number already exists.
 */
public class TransactionNumberAlreadyExist extends RuntimeException {

	/**
	 * Constructs a new exception with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public TransactionNumberAlreadyExist(String message) {
		super(message);
	}
}
