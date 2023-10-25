package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.in.command.Command;
import org.example.walletservice.in.command.CommandProvider;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class TransactionServletTest {
	private static final String COMMAND = "command";
	private TransactionServlet transactionServlet;
	private BufferedReader bufferedReader;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private ObjectMapper objectMapper;
	private CommandProvider commandProvider;
	private TransactionService transactionService;
	private ServletOutputStream outputStream;
	private static final String AUTH_PLAYER = "authPlayer";
	private static final String CONTENT_TYPE = "application/json";
	private AuthPlayerDto authPlayer;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		transactionService = Mockito.mock(TransactionService.class);
		bufferedReader = Mockito.mock(BufferedReader.class);
		commandProvider = Mockito.mock(CommandProvider.class);
		transactionServlet = new TransactionServlet(transactionService, objectMapper, commandProvider);
		outputStream = Mockito.mock(ServletOutputStream.class);
		req = Mockito.mock(HttpServletRequest.class);
		resp = Mockito.mock(HttpServletResponse.class);


		authPlayer = new AuthPlayerDto(1, "admin", Role.ADMIN);
	}

	@Test
	public void shouldReturnTransactionHistory() throws IOException, ServletException {
		final List<TransactionResponseDto> transactionList = new ArrayList<>() {{
			add(new TransactionResponseDto(Operation.CREDIT.name(), new BigDecimal(100), "token #1"));
			add(new TransactionResponseDto(Operation.DEBIT.name(), new BigDecimal(20), "token #2"));
		}};

		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(transactionService.getPlayerTransactionalHistory(authPlayer)).thenReturn(transactionList);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		transactionServlet.doGet(req, resp);

		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldReturnEmptyTransactionHistory() throws IOException, ServletException {
		final String message = "Transactions is empty.";
		final List<TransactionResponseDto> transactionList = new ArrayList<>();

		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(transactionService.getPlayerTransactionalHistory(authPlayer)).thenReturn(transactionList);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		transactionServlet.doGet(req, resp);

		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowException_PlayerNotLoggedInException() throws IOException, ServletException {
		final String message = "You need to log in. This resource is not available to you.";

		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(null);
		Mockito.when(transactionService.getPlayerTransactionalHistory(null))
				.thenThrow(new PlayerNotLoggedInException(message));

		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		transactionServlet.doGet(req, resp);

		Mockito.verify(resp).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);

	}

	@Test
	public void shouldCredit() throws IOException, ServletException {
		final String command = "credit";
		final String message = "Credit successfully.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.CREDIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).credit(authPlayer, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionCredit_InvalidInputDataException() throws IOException, ServletException {
		final String command = "credit";
		final String message = "You need to log in. This resource is not available to you.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(-1), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.CREDIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException(message))
				.when(transactionService).credit(authPlayer, transactionRequest);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).credit(authPlayer, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionCredit_TransactionNumberAlreadyExist() throws IOException, ServletException {
		final String command = "credit";
		final String message = "A transaction with this number already exists.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.CREDIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new TransactionNumberAlreadyExist(message))
				.when(transactionService).credit(authPlayer, transactionRequest);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).credit(authPlayer, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_CONFLICT);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionCredit_PlayerDoesNotHaveAccessException() throws IOException, ServletException {
		final String command = "credit";
		final String message = "You need to log in. This resource is not available to you.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(null);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.CREDIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new PlayerNotLoggedInException(message))
				.when(transactionService).credit(null, transactionRequest);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).credit(null, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldDebit() throws IOException, ServletException {
		final String command = "debit";
		final String message = "Debit successfully.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.DEBIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).debit(authPlayer, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionDebit_InvalidInputDataException() throws IOException, ServletException {
		final String command = "debit";
		final String message = "The amount to be entered cannot be less than 0.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(-1), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.DEBIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException(message))
				.when(transactionService).debit(authPlayer, transactionRequest);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).debit(authPlayer, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	@DisplayName("The amount of funds to withdraw is greater than the available funds")
	public void shouldThrowExceptionDebit_InvalidInputData() throws IOException, ServletException {
		final String command = "debit";
		final String message = "The number of funds to be withdrawn exceeds the number of funds on the account.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(1000), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.DEBIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException(message))
				.when(transactionService).debit(authPlayer, transactionRequest);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).debit(authPlayer, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionDebit_TransactionNumberAlreadyExist() throws IOException, ServletException {
		final String command = "debit";
		final String message = "A transaction with this number already exists.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.DEBIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new TransactionNumberAlreadyExist(message))
				.when(transactionService).debit(authPlayer, transactionRequest);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).debit(authPlayer, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_CONFLICT);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionDebit_PlayerDoesNotHaveAccessException() throws IOException, ServletException {
		final String command = "debit";
		final String message = "You need to log in. This resource is not available to you.";

		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		String jsonObject = objectMapper.writeValueAsString(transactionRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(null);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.DEBIT);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new PlayerNotLoggedInException(message))
				.when(transactionService).debit(null, transactionRequest);

		transactionServlet.doPost(req, resp);

		Mockito.verify(transactionService).debit(null, transactionRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}
}