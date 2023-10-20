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
import org.example.walletservice.model.dto.PlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.PlayerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller class to perform player operations.
 */
@WebServlet("/players")
public final class PlayerController extends HttpServlet {
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		Command command = commandProvider.getCommand(req.getParameter("command"));

		try (BufferedReader reader = req.getReader()) {
			StringBuilder jsonObject = new StringBuilder();
			while (reader.ready()) {
				jsonObject.append(reader.readLine());
			}
			PlayerRequestDto playerRequestDto = objectMapper.readValue(jsonObject.toString(), PlayerRequestDto.class);
			switch (command) {
				case SIGN_IN -> {
					HttpSession session = req.getSession(true);
					PlayerDto playerDto = playerService.logIn(playerRequestDto);
					session.setAttribute("authPlayer", playerDto);
					resp.setStatus(HttpServletResponse.SC_OK);
				}
				case REGISTRATION -> {
					playerService.registrationPlayer(playerRequestDto);
					resp.setStatus(HttpServletResponse.SC_CREATED);
				}
				default -> resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PlayerDto playerDto = (PlayerDto) req.getSession().getAttribute("authPlayer");
		if (playerDto == null) {

			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		BigDecimal playerBalance = playerService.getPlayerBalance(playerDto);
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getOutputStream().write(
				this.objectMapper.writeValueAsBytes(String.format("Your balance -> %s", playerBalance)));
	}
}
