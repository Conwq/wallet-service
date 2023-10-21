package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
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
import org.example.walletservice.service.TransactionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/transaction")
public class TransactionController extends HttpServlet {
	private static final String AUTH_PLAYER_PARAM = "authPlayer";
	private static final String COMMAND = "command";
	private static final String CONTENT_TYPE = "application/json";
	private final TransactionService transactionService;
	private final ObjectMapper objectMapper;
	private final CommandProvider commandProvider;

	public TransactionController() {
		ApplicationContextHolder context = ApplicationContextHolder.getInstance();
		this.transactionService = context.getTransactionService();
		this.objectMapper = context.getObjectMapper();
		this.commandProvider = context.getCommandProvider();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getSession().getAttribute(AUTH_PLAYER_PARAM);
		if (authPlayerDto == null) {
			String noAccessToContent = "You need to log in. This resource is not available to you.";
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, noAccessToContent);
		}
		List<String> playerTransactionHistory = transactionService.getPlayerTransactionalHistory(authPlayerDto);
		generateResponse(resp, HttpServletResponse.SC_OK, playerTransactionHistory);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getSession().getAttribute(AUTH_PLAYER_PARAM);
		if (authPlayerDto == null) {
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, "Log in to perform this operation");
			return;
		}
		try (BufferedReader reader = req.getReader()) {
			Command command = commandProvider.getCommand(req.getParameter(COMMAND));
			StringBuilder jsonObject = new StringBuilder();
			while (reader.ready()) {
				jsonObject.append(reader.readLine());
			}
			TransactionRequestDto transactionRequest = objectMapper.readValue(
					jsonObject.toString(), TransactionRequestDto.class);
			switch (command) {
				case CREDIT -> {
					transactionService.credit(authPlayerDto, transactionRequest);
					generateResponse(resp, HttpServletResponse.SC_OK, "Credit successfully.");
					resp.setStatus(HttpServletResponse.SC_OK);
				}
				case DEBIT -> {
					transactionService.debit(authPlayerDto, transactionRequest);
					generateResponse(resp, HttpServletResponse.SC_OK, "Debit successfully.");
				}
			}
		} catch (NullPointerException e) {
			generateResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Content doesn't exist.");
		}
	}

	private void generateResponse(HttpServletResponse resp, int status, String message) throws IOException {
		InfoResponse infoResponse = new InfoResponse(status, message);
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(infoResponse));
	}

	private void generateResponse(HttpServletResponse resp, int status, List<String> content) throws IOException {
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(content));
	}
}