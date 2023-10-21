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
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller class to perform player operations.
 */
@WebServlet("/players")
public final class PlayerController extends HttpServlet {
	private static final String CONTENT_TYPE = "application/json";
	private static final String COMMAND = "command";
	private static final String AUTH_PLAYER_PARAM = "authPlayer";
	private final PlayerService playerService;
	private final ObjectMapper objectMapper;
	private final CommandProvider commandProvider;

	public PlayerController() {
		ApplicationContextHolder context = ApplicationContextHolder.getInstance();
		this.objectMapper = context.getObjectMapper();
		this.playerService = context.getPlayerService();
		this.commandProvider = context.getCommandProvider();
	}

	/**
	 * Handles HTTP POST requests for player operations.
	 *
	 * @param req  The HttpServletRequest.
	 * @param resp The HttpServletResponse.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an I/O error occurs.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (BufferedReader reader = req.getReader()) {
			Command command = commandProvider.getCommand(req.getParameter(COMMAND));
			StringBuilder jsonObject = new StringBuilder();
			while (reader.ready()) {
				jsonObject.append(reader.readLine());
			}
			PlayerRequestDto playerRequestDto = objectMapper.readValue(jsonObject.toString(), PlayerRequestDto.class);
			switch (command) {
				case SIGN_IN -> signInExecution(req, resp, playerRequestDto);
				case REGISTRATION -> registrationExecution(resp, playerRequestDto);
			}
		} catch (NullPointerException e) {
			generateResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Content doesn't exist.");
		}
	}

	/**
	 * Handles HTTP GET requests for retrieving player balance.
	 *
	 * @param req  The HttpServletRequest.
	 * @param resp The HttpServletResponse.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an I/O error occurs.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getSession().getAttribute(AUTH_PLAYER_PARAM);
		if (authPlayerDto == null) {
			System.out.println("[FAIL] Performing an operation by an unregistered user.");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		BigDecimal playerBalance = playerService.getPlayerBalance(authPlayerDto);
		generateResponse(resp, HttpServletResponse.SC_OK,
				String.format("%s, your balance -> %s", authPlayerDto.username(), playerBalance));
	}

	/**
	 * Executes the sign-in operation.
	 *
	 * @param req               The HttpServletRequest.
	 * @param resp              The HttpServletResponse.
	 * @param playerRequestDto  The PlayerRequestDto for sign-in.
	 * @throws IOException If an I/O error occurs.
	 */
	private void signInExecution(HttpServletRequest req, HttpServletResponse resp,
								 PlayerRequestDto playerRequestDto) throws IOException {
		try {
			AuthPlayerDto authPlayerDto = playerService.logIn(playerRequestDto);
			HttpSession session = req.getSession(true);
			session.setAttribute(AUTH_PLAYER_PARAM, authPlayerDto);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (InvalidInputDataException e) {
			generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (PlayerNotFoundException e) {
			generateResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Executes the registration operation.
	 *
	 * @param resp             The HttpServletResponse.
	 * @param playerRequestDto The PlayerRequestDto for registration.
	 * @throws IOException If an I/O error occurs.
	 */
	private void registrationExecution(HttpServletResponse resp, PlayerRequestDto playerRequestDto) throws IOException {
		try {
			playerService.registrationPlayer(playerRequestDto);
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} catch (InvalidInputDataException e) {
			generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (PlayerAlreadyExistException e) {
			generateResponse(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
		}
	}

	/**
	 * Generates a response with the specified status and message.
	 *
	 * @param resp    The HttpServletResponse.
	 * @param status  The HTTP status code.
	 * @param message The message to be included in the response.
	 * @throws IOException If an I/O error occurs.
	 */
	private void generateResponse(HttpServletResponse resp, int status, String message) throws IOException {
		InfoResponse infoResponse = new InfoResponse(status, message);
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(infoResponse));
	}
}