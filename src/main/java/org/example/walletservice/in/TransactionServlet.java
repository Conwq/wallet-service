package org.example.walletservice.in;

import jakarta.servlet.http.HttpServletRequest;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Controller class to handle transaction-related operations.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionServlet {

	private static final String AUTH_PLAYER = "authPlayer";
	private final TransactionService transactionService;

	@Autowired
	public TransactionServlet(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * Handles the HTTP GET request to retrieve the transaction history of the authenticated player.
	 *
	 * @param req The HttpServletRequest object.
	 * @return ResponseEntity containing the list of TransactionResponseDto and HTTP status.
	 */
	@GetMapping
	public ResponseEntity<List<TransactionResponseDto>> getTransactionHistory(HttpServletRequest req) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute(AUTH_PLAYER);

		List<TransactionResponseDto> playerTransactionHistory = transactionService
				.getPlayerTransactionalHistory(authPlayerDto);
		return new ResponseEntity<>(playerTransactionHistory, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP POST request to perform a credit transaction.
	 *
	 * @param transactionRequest The TransactionRequestDto containing credit transaction information.
	 * @param request            The HttpServletRequest object.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/credit")
	public ResponseEntity<InfoResponse> credit(@RequestBody TransactionRequestDto transactionRequest,
											   HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute(AUTH_PLAYER);

		transactionService.credit(authPlayerDto, transactionRequest);
		return generateResponse(HttpStatus.OK, "Credit successfully.");
	}

	/**
	 * Handles the HTTP POST request to perform a debit transaction.
	 *
	 * @param transactionRequest The TransactionRequestDto containing debit transaction information.
	 * @param request            The HttpServletRequest object.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/debit")
	public ResponseEntity<InfoResponse> debit(@RequestBody TransactionRequestDto transactionRequest,
											  HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute(AUTH_PLAYER);

		transactionService.debit(authPlayerDto, transactionRequest);
		return generateResponse(HttpStatus.OK, "Debit successfully.");
	}

	/**
	 * Generates a ResponseEntity with InfoResponse and the provided HTTP status and message.
	 *
	 * @param status  The HTTP status.
	 * @param message The response message.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	private ResponseEntity<InfoResponse> generateResponse(HttpStatus status, String message) {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status.value(), message);
		return new ResponseEntity<>(infoResponse, status);
	}
}
