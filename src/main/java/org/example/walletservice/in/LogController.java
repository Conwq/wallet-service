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
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerNotFoundException;

import java.io.IOException;
import java.util.List;

@WebServlet("/log")
public class LogController extends HttpServlet {
	private static final String AUTH_PLAYER_PARAM = "authPlayer";
	private static final String COMMAND = "command";
	private static final String USERNAME = "username";
	private static final String CONTENT_TYPE = "application/json";
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
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getSession().getAttribute(AUTH_PLAYER_PARAM);
		if (authPlayerDto == null) {
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE,
					"You need to log in as an administrator.");
		} else if (authPlayerDto.role() == Role.ADMIN) {
			Command command = commandProvider.getCommand(req.getParameter(COMMAND));
			switch (command) {
				case SHOW_ALL_LOG -> {
					List<LogResponseDto> logList = loggerService.getAllLogs(authPlayerDto);
					generateResponse(resp, HttpServletResponse.SC_OK, logList);
				}
				case SHOW_PLAYER_LOG -> {
					try {
						String inputUsernameForSearch = req.getParameter(USERNAME);
						List<LogResponseDto> logList = loggerService.getLogsByUsername(authPlayerDto, inputUsernameForSearch);
						generateResponse(resp, HttpServletResponse.SC_OK, logList);
					}
					catch (PlayerNotFoundException e) {
						generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
					}
				}
				default -> resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE,
					"You do not have access to this resource.");
		}
	}

	private void generateResponse(HttpServletResponse resp, int status, String message) throws IOException {
		InfoResponse infoResponse = new InfoResponse(status, message);
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(infoResponse));
	}

	private void generateResponse(HttpServletResponse resp, int status, List<LogResponseDto> logList) throws IOException {
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);
		resp.getOutputStream().write(objectMapper.writeValueAsBytes(logList));
	}
}