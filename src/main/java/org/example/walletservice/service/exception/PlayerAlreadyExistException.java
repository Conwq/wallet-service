package org.example.walletservice.service.exception;

public class PlayerAlreadyExistException extends RuntimeException {

	public PlayerAlreadyExistException(String message) {
		super(message);
	}
}
