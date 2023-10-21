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
				case SIGN_IN -> {
					try {
						AuthPlayerDto authPlayerDto = playerService.logIn(playerRequestDto);
						HttpSession session = req.getSession(true);
						session.setAttribute(AUTH_PLAYER_PARAM, authPlayerDto);
						resp.setStatus(HttpServletResponse.SC_OK);
					} catch (InvalidInputDataException | PlayerAlreadyExistException e) {
						generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
					}
				}
				case REGISTRATION -> {
					try {
						playerService.registrationPlayer(playerRequestDto);
						resp.setStatus(HttpServletResponse.SC_CREATED);
					} catch (InvalidInputDataException | PlayerAlreadyExistException e) {
						generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
					}
				}
			}
		} catch (NullPointerException e) {
			generateResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Content doesn't exist.");
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getSession().getAttribute(AUTH_PLAYER_PARAM);
		if (authPlayerDto == null) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		BigDecimal playerBalance = playerService.getPlayerBalance(authPlayerDto);
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getOutputStream()
				.write(this.objectMapper
						.writeValueAsBytes(String.format("%s, your balance -> %s", authPlayerDto.username(), playerBalance)));
	}

	private void generateResponse(HttpServletResponse resp, int status, String message) throws IOException {
		InfoResponse infoResponse = new InfoResponse(status, message);
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(infoResponse));
	}
}
