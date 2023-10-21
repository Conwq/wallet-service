package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.in.command.Command;
import org.example.walletservice.in.command.CommandProvider;
import org.example.walletservice.model.dto.AuthPlayerDto;
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
		HttpSession session = req.getSession();
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) session.getAttribute(AUTH_PLAYER_PARAM);
		List<String> playerTransactionHistory = transactionService.getPlayerTransactionalHistory(authPlayerDto);
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(this.objectMapper.writeValueAsBytes(playerTransactionHistory));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Command command = commandProvider.getCommand(req.getParameter(COMMAND));

		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getSession().getAttribute(AUTH_PLAYER_PARAM);
		try (BufferedReader reader = req.getReader()) {
			StringBuilder jsonObject = new StringBuilder();
			while (reader.ready()) {
				jsonObject.append(reader.readLine());
			}
			TransactionRequestDto transactionRequest = objectMapper.readValue(
					jsonObject.toString(), TransactionRequestDto.class);
			switch (command) {
				case CREDIT -> {
					transactionService.credit(authPlayerDto, transactionRequest);
					resp.setStatus(HttpServletResponse.SC_OK);
				}
				case DEBIT -> {
					transactionService.debit(authPlayerDto, transactionRequest);
					resp.setStatus(HttpServletResponse.SC_OK);
				}
				default -> resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}