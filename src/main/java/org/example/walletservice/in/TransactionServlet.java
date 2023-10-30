package org.example.walletservice.in;

import jakarta.servlet.http.HttpServletRequest;
import org.example.walletservice.model.Role;
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

	@GetMapping
	public ResponseEntity<List<TransactionResponseDto>> getTransactionHistory(HttpServletRequest req) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute(AUTH_PLAYER);

		List<TransactionResponseDto> playerTransactionHistory = transactionService
				.getPlayerTransactionalHistory(authPlayerDto);
		return new ResponseEntity<>(playerTransactionHistory, HttpStatus.OK);
	}

	@PostMapping("/credit")
	public ResponseEntity<InfoResponse> credit(@RequestBody TransactionRequestDto transactionRequest) {
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		transactionService.credit(authPlayerDto, transactionRequest);
		return generateResponse(HttpStatus.OK, "Credit successfully.");
	}

	@PostMapping("/debit")
	public ResponseEntity<InfoResponse> debit(@RequestBody TransactionRequestDto transactionRequest) {
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		transactionService.debit(authPlayerDto, transactionRequest);
		return generateResponse(HttpStatus.OK, "Debit successfully.");
	}

	private ResponseEntity<InfoResponse> generateResponse(HttpStatus status, String message) {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status.value(), message);
		return new ResponseEntity<>(infoResponse, status);
	}

//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		try {
//			AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute(AUTH_PLAYER);
//			List<TransactionResponseDto> playerTransactionHistory = transactionService
//					.getPlayerTransactionalHistory(authPlayerDto);
//			generateResponse(resp, HttpServletResponse.SC_OK, playerTransactionHistory);
//
//		} catch (PlayerNotLoggedInException e) {
//			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
//		}
//	}

//	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		try (BufferedReader reader = req.getReader()) {
//			AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute(AUTH_PLAYER);
//
//			TransactionRequestDto transactionRequest =
//					objectMapper.readValue(jsonObject.toString(), TransactionRequestDto.class);
//
//			switch (command) {
//				case CREDIT -> creditExecution(resp, authPlayerDto, transactionRequest);
//				case DEBIT -> debitExecution(resp, authPlayerDto, transactionRequest);
//			}
//
//		} catch (PlayerNotLoggedInException e) {
//			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
//	}

//	public void creditExecution(HttpServletResponse resp, AuthPlayerDto authPlayerDto,
//								TransactionRequestDto transactionRequest) throws IOException {
//		try {
//			transactionService.credit(authPlayerDto, transactionRequest);
//			generateResponse(resp, HttpServletResponse.SC_OK, "Credit successfully.");
//
//	}

//	public void debitExecution(HttpServletResponse resp, AuthPlayerDto authPlayerDto,
//							   TransactionRequestDto transactionRequest) throws IOException {
//		try {
//			transactionService.debit(authPlayerDto, transactionRequest);
//			generateResponse(resp, HttpServletResponse.SC_OK, "Debit successfully.");
//	}
}
