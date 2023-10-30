package org.example.walletservice.in.handler;

import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class TransactionExceptionHandler {

	@ExceptionHandler(TransactionNumberAlreadyExist.class)
	public ResponseEntity<InfoResponse> transactionNumberAlreadyExistHandler(TransactionNumberAlreadyExist e) {
		return generateResponse(HttpStatus.CONFLICT, e.getMessage());
	}

	private ResponseEntity<InfoResponse> generateResponse(HttpStatus status, String message) {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status.value(), message);
		return new ResponseEntity<>(infoResponse, status);
	}
}
