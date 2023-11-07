package ru.patseev.auditspringbootstarter.audit.exception;

/**
 * Exception thrown when attempting to create a player that already exists in the service layer.
 */
public class PlayerAlreadyExistException extends RuntimeException {

	/**
	 * Constructs a PlayerAlreadyExistException with the specified detail message.
	 *
	 * @param message message.
	 */
	public PlayerAlreadyExistException(String message) {
		super(message);
	}
}
