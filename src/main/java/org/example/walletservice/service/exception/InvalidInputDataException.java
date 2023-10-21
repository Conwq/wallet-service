package org.example.walletservice.service.exception;

public class InvalidInputDataException extends RuntimeException{
	public InvalidInputDataException(String message) {
		super(message);
	}
}
