package org.example.walletservice.service.exception;

/**
 * Exception indicating that a player does not have access to a specific resource or operation.
 */
public class PlayerDoesNotHaveAccessException extends RuntimeException {

	/**
	 * Constructs a new PlayerDoesNotHaveAccessException with the specified detail message.
	 *
	 * @param message The message.
	 */
	public PlayerDoesNotHaveAccessException(String message) {
		super(message);
	}
}
