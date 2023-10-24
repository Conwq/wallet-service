package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.in.command.Command;
import org.example.walletservice.in.command.CommandProvider;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Controller class to handle transaction-related operations.
 */
@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {
	private static final String COMMAND = "command";
	private static final String CONTENT_TYPE = "application/json";
	private final TransactionService transactionService;
	private final ObjectMapper objectMapper;
	private final CommandProvider commandProvider;

	public TransactionServlet() {
		ApplicationContextHolder context = ApplicationContextHolder.getInstance();
		this.transactionService = context.getTransactionService();
		this.objectMapper = context.getObjectMapper();
		this.commandProvider = context.getCommandProvider();
	}

	public TransactionServlet(TransactionService transactionService,
							  ObjectMapper objectMapper,
							  CommandProvider commandProvider) {
		this.transactionService = transactionService;
		this.objectMapper = objectMapper;
		this.commandProvider = commandProvider;
	}

	/**
	 * Handles HTTP GET requests for retrieving player transaction history.
	 *
	 * @param req  The HttpServletRequest object.
	 * @param resp The HttpServletResponse object.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an I/O error occurs.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute("authPlayer");
			List<TransactionResponseDto> playerTransactionHistory = transactionService
					.getPlayerTransactionalHistory(authPlayerDto);
			generateResponse(resp, HttpServletResponse.SC_OK, playerTransactionHistory);

		} catch (PlayerNotLoggedInException e) {
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
		}
	}

	/**
	 * Handles HTTP POST requests for performing credit or debit transactions.
	 *
	 * @param req  The HttpServletRequest object.
	 * @param resp The HttpServletResponse object.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an I/O error occurs.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (BufferedReader reader = req.getReader()) {
			AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute("authPlayer");
			Command command = commandProvider.getCommand(req.getParameter(COMMAND));
			StringBuilder jsonObject = new StringBuilder();

			while (reader.ready()) {
				jsonObject.append(reader.readLine());
			}

			TransactionRequestDto transactionRequest =
					objectMapper.readValue(jsonObject.toString(), TransactionRequestDto.class);

			switch (command) {
				case CREDIT -> creditExecution(resp, authPlayerDto, transactionRequest);
				case DEBIT -> debitExecution(resp, authPlayerDto, transactionRequest);
			}

		} catch (PlayerNotLoggedInException e) {
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());

		} catch (NullPointerException e) {
			System.out.println("[FAIL] Accessing a non-existent resource.");
			generateResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Content doesn't exist.");
		}
	}

	/**
	 * Executes the credit transaction operation and generates a response.
	 *
	 * @param resp               The HttpServletResponse object.
	 * @param authPlayerDto      The authenticated player DTO.
	 * @param transactionRequest The transaction request DTO.
	 * @throws IOException If an I/O error occurs.
	 */
	public void creditExecution(HttpServletResponse resp, AuthPlayerDto authPlayerDto,
								TransactionRequestDto transactionRequest) throws IOException {
		try {
			transactionService.credit(authPlayerDto, transactionRequest);
			generateResponse(resp, HttpServletResponse.SC_OK, "Credit successfully.");

		} catch (InvalidInputDataException e) {
			generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

		} catch (TransactionNumberAlreadyExist e) {
			generateResponse(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
		}
	}

	/**
	 * Executes the debit transaction operation and generates a response.
	 *
	 * @param resp               The HttpServletResponse object.
	 * @param authPlayerDto      The authenticated player DTO.
	 * @param transactionRequest The transaction request DTO.
	 * @throws IOException If an I/O error occurs.
	 */
	public void debitExecution(HttpServletResponse resp, AuthPlayerDto authPlayerDto,
							   TransactionRequestDto transactionRequest) throws IOException {
		try {
			transactionService.debit(authPlayerDto, transactionRequest);
			generateResponse(resp, HttpServletResponse.SC_OK, "Debit successfully.");
		} catch (InvalidInputDataException e) {
			generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (TransactionNumberAlreadyExist e) {
			generateResponse(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
		}
	}

	/**
	 * Generates a JSON response for the HttpServletResponse with the given status and message.
	 *
	 * @param resp    The HttpServletResponse object.
	 * @param status  The HTTP status code.
	 * @param message The message to be included in the response.
	 * @throws IOException If an I/O error occurs.
	 */
	private void generateResponse(HttpServletResponse resp, int status, String message) throws IOException {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status, message);
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(infoResponse));
	}

	/**
	 * Generates a JSON response for the HttpServletResponse with the given status and list of content.
	 *
	 * @param resp    The HttpServletResponse object.
	 * @param status  The HTTP status code.
	 * @param content The list of content entries to be included in the response.
	 * @throws IOException If an I/O error occurs.
	 */
	private void generateResponse(HttpServletResponse resp, int status, List<TransactionResponseDto> content)
			throws IOException {
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);

		ServletOutputStream outputStream = resp.getOutputStream();

		if (content.isEmpty()) {
			InfoResponse infoResponse = new InfoResponse(new Date().toString(), status, "Transactions is empty.");
			outputStream.write(objectMapper.writeValueAsBytes(infoResponse));
		} else {
			outputStream.write(objectMapper.writeValueAsBytes(content));
		}
	}
}
