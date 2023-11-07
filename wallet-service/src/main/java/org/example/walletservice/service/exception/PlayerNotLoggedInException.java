package org.example.walletservice.service.exception;

/**
 * Exception indicating that a player is not logged in when attempting an operation that requires authentication.
 */
public class PlayerNotLoggedInException extends RuntimeException {

	/**
	 * Constructs a new PlayerNotLoggedInException with the specified detail message.
	 *
	 * @param message The detail message.
	 */
	public PlayerNotLoggedInException(String message) {
		super(message);
	}
}

