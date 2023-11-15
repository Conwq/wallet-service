package org.example.walletservice.in;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Controller class to handle transaction-related operations.
 */
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionServlet {
	private final TransactionService transactionService;

	/**
	 * Handles the HTTP GET request to retrieve the transaction history of the authenticated player.
	 *
	 * @param userDetails Authorized player data.
	 * @return ResponseEntity containing the list of TransactionResponseDto and HTTP status.
	 */
	@GetMapping
	public ResponseEntity<?> getTransactionHistory(@AuthenticationPrincipal UserDetails userDetails) {

		List<TransactionResponseDto> playerTransactionHistory = transactionService
				.getPlayerTransactionalHistory(userDetails);
		if (playerTransactionHistory.isEmpty()) {
			return generateResponse("Transaction history is empty");
		}
		return new ResponseEntity<>(playerTransactionHistory, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP POST request to perform a credit transaction.
	 *
	 * @param transactionRequest The TransactionRequestDto containing credit transaction information.
	 * @param userDetails         Authorized player data.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/credit")
	public ResponseEntity<InfoResponse> credit(@RequestBody TransactionRequestDto transactionRequest,
											   @AuthenticationPrincipal UserDetails userDetails) {
		transactionService.credit(userDetails, transactionRequest);
		return generateResponse("Credit successfully.");
	}

	/**
	 * Handles the HTTP POST request to perform a debit transaction.
	 *
	 * @param transactionRequest The TransactionRequestDto containing debit transaction information.
	 * @param userDetails         Authorized player data.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/debit")
	public ResponseEntity<InfoResponse> debit(@RequestBody TransactionRequestDto transactionRequest,
											  @AuthenticationPrincipal UserDetails userDetails) {
		transactionService.debit(userDetails, transactionRequest);
		return generateResponse("Debit successfully.");
	}

	/**
	 * Generates a ResponseEntity with InfoResponse and the provided HTTP status and message.
	 *
	 * @param message The response message.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	private ResponseEntity<InfoResponse> generateResponse(String message) {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpStatus.OK.value(), message);
		return new ResponseEntity<>(infoResponse, HttpStatus.OK);
	}
}
