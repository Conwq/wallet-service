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
import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class to perform player operations.
 */
@WebServlet("/players")
public final class PlayerServlet extends HttpServlet {
	private static final String CONTENT_TYPE = "application/json";
	private static final String COMMAND = "command";
	private final PlayerService playerService;
	private final ObjectMapper objectMapper;
	private final CommandProvider commandProvider;
	private final JwtService jwtService;

	public PlayerServlet() {
		ApplicationContextHolder context = ApplicationContextHolder.getInstance();
		this.objectMapper = context.getObjectMapper();
		this.playerService = context.getPlayerService();
		this.commandProvider = context.getCommandProvider();
		jwtService = new JwtService();
	}

	public PlayerServlet (ObjectMapper objectMapper,
						  PlayerService playerService,
						  CommandProvider commandProvider,
						  JwtService jwtService) {
		this.objectMapper = objectMapper;
		this.playerService = playerService;
		this.commandProvider = commandProvider;
		this.jwtService = jwtService;
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
	protected void doGet(HttpServletRequest req,
						 HttpServletResponse resp) throws ServletException, IOException {
		try {
			AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute("authPlayer");

			BigDecimal playerBalance = playerService.getPlayerBalance(authPlayerDto);
			generateResponse(resp, HttpServletResponse.SC_OK,
					String.format("%s, your balance -> %s", authPlayerDto.username(), playerBalance));
		} catch (PlayerNotLoggedInException e) {
			generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
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
	protected void doPost(HttpServletRequest req,
						  HttpServletResponse resp) throws ServletException, IOException {
		try (BufferedReader reader = req.getReader()) {
			Command command = commandProvider.getCommand(req.getParameter(COMMAND));
			StringBuilder jsonObject = new StringBuilder();

			while (reader.ready()) {
				jsonObject.append(reader.readLine());
			}
			PlayerRequestDto playerRequestDto = objectMapper.readValue(jsonObject.toString(), PlayerRequestDto.class);
			processRequest(resp, playerRequestDto, command);
		} catch (NullPointerException e) {
			generateResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Content doesn't exist.");
		}
	}

	/**
	 * Process the player request based on the command.
	 *
	 * @param resp             The HttpServletResponse.
	 * @param command          The command to be executed.
	 * @param playerRequestDto The PlayerRequestDto for the operation.
	 * @throws IOException If an I/O error occurs.
	 */
	private void processRequest(HttpServletResponse resp,
								PlayerRequestDto playerRequestDto,
								Command command) throws IOException, ServletException {
		switch (command) {
			case SIGN_IN -> signInExecution(resp, playerRequestDto);
			case REGISTRATION -> registrationExecution(resp, playerRequestDto);
			default -> generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid command");
		}
	}

	/**
	 * Executes the sign-in operation.
	 *
	 * @param resp             The HttpServletResponse.
	 * @param playerRequestDto The PlayerRequestDto for sign-in.
	 * @throws IOException If an I/O error occurs.
	 */
	private void signInExecution(HttpServletResponse resp,
								 PlayerRequestDto playerRequestDto) throws IOException, ServletException {
		try {
			AuthPlayerDto authPlayerDto = playerService.logIn(playerRequestDto);

			Map<String, Object> extraClaims = new HashMap<>();
			extraClaims.put("role", authPlayerDto.role().name());
			extraClaims.put("playerID", authPlayerDto.playerID());

			String jwtToken = jwtService.generateWebToken(extraClaims, authPlayerDto);

			resp.addHeader("Authorization", "Bearer " + jwtToken);
			generateResponse(resp, HttpServletResponse.SC_OK, "You've successfully logged in");

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
	private void registrationExecution(HttpServletResponse resp,
									   PlayerRequestDto playerRequestDto) throws IOException {
		try {
			playerService.registrationPlayer(playerRequestDto);
			generateResponse(resp, HttpServletResponse.SC_OK, "You have successfully registered.");
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
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status, message);
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(infoResponse));
	}
}