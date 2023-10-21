package org.example.walletservice.service.exception;

/**
 * Exception thrown when invalid input data is encountered in the service layer.
 */
public class InvalidInputDataException extends RuntimeException{

	/**
	 * Constructs an InvalidInputDataException with the specified detail message.
	 *
	 * @param message the detail message.
	 */
	public InvalidInputDataException(String message) {
		super(message);
	}
}
