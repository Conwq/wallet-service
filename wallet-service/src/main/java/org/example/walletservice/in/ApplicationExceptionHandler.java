package org.example.walletservice.in;

import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

/**
 * The handled exceptions include:
 * - {@link org.example.walletservice.service.exception.PlayerNotFoundException}
 * - {@link org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException}
 * - {@link org.example.walletservice.service.exception.PlayerAlreadyExistException}
 * - {@link org.example.walletservice.service.exception.InvalidInputDataException}
 * - {@link org.example.walletservice.service.exception.PlayerNotLoggedInException}
 * - {@link org.example.walletservice.service.exception.TransactionNumberAlreadyExist}
 * Each exception is mapped to an HTTP status code, and the corresponding {@link org.example.walletservice.model.dto.InfoResponse}
 * is generated with the current timestamp, status code, and exception message.
 * This class is annotated with {@link org.springframework.web.bind.annotation.ControllerAdvice}
 * to make it a global exception handler for the entire application.
 */
@ControllerAdvice
public class ApplicationExceptionHandler {

	@ExceptionHandler(PlayerNotFoundException.class)
	public ResponseEntity<InfoResponse> playerNotFoundExceptionHandler(PlayerNotFoundException e) {
		return generateResponse(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(PlayerDoesNotHaveAccessException.class)
	public ResponseEntity<InfoResponse> playerDoesNotHaveAccessExceptionHandler(PlayerDoesNotHaveAccessException e) {
		return generateResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(PlayerAlreadyExistException.class)
	public ResponseEntity<InfoResponse> playerAlreadyExistExceptionHandler(PlayerAlreadyExistException e) {
		return generateResponse(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler(InvalidInputDataException.class)
	public ResponseEntity<InfoResponse> invalidInputDataExceptionHandler(InvalidInputDataException e) {
		return generateResponse(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(PlayerNotLoggedInException.class)
	public ResponseEntity<InfoResponse> playerNotLoggedInExceptionHandler(PlayerNotLoggedInException e) {
		return generateResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(TransactionNumberAlreadyExist.class)
	public ResponseEntity<InfoResponse> transactionNumberAlreadyExistHandler(TransactionNumberAlreadyExist e) {
		return generateResponse(HttpStatus.CONFLICT, e.getMessage());
	}

	/**
	 * Generates a ResponseEntity with InfoResponse containing the provided HTTP status and message.
	 *
	 * @param status  The HTTP status code.
	 * @param message The response message.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	private ResponseEntity<InfoResponse> generateResponse(HttpStatus status, String message) {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status.value(), message);
		return new ResponseEntity<>(infoResponse, status);
	}
}
