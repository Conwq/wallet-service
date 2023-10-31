package org.example.walletservice.service.exception;

/**
 * The base class for service-related exceptions in the application.
 * This class is intended to be used as a generic exception for various service-related errors.
 */
public class ServiceException extends RuntimeException {
	public ServiceException(String message) {
		super(message);
	}
}
