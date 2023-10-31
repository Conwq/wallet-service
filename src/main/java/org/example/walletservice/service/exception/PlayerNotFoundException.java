package org.example.walletservice.service.exception;

/**
 * Exception thrown when a player is not found in the service layer.
 */
public class PlayerNotFoundException extends RuntimeException{

	/**
	 * Constructs a PlayerNotFoundException with the specified detail message.
	 *
	 * @param message message.
	 */
	public PlayerNotFoundException(String message) {
		super(message);
	}
}
