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
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.dto.PlayerDto;
import org.example.walletservice.service.LoggerService;

import java.io.IOException;
import java.util.List;

@WebServlet("/log")
public class LogController extends HttpServlet {
	private final LoggerService loggerService;
	private final ObjectMapper objectMapper;
	private final CommandProvider commandProvider;

	public LogController() {
		ApplicationContextHolder context = ApplicationContextHolder.getInstance();
		this.loggerService = context.getLoggerService();
		this.objectMapper = context.getObjectMapper();
		this.commandProvider = context.getCommandProvider();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PlayerDto playerDto = (PlayerDto) req.getSession().getAttribute("authPlayer");

		if (playerDto.role() == Role.ADMIN) {
			Command command = commandProvider.getCommand(req.getParameter("command"));

			switch (command) {
				case SHOW_ALL_LOG -> {
					List<LogResponseDto> logList = loggerService.getAllLogs(playerDto);
					resp.setContentType("application/json");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getOutputStream().write(objectMapper.writeValueAsBytes(logList));
				}
				case SHOW_PLAYER_LOG -> {
					String inputUsernameForSearch = req.getParameter("username");
					List<LogResponseDto> logList = loggerService.showLogsByUsername(
							playerDto, inputUsernameForSearch);
					resp.setContentType("application/json");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getOutputStream().write(objectMapper.writeValueAsBytes(logList));
				}
				default -> resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}
}