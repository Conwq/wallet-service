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
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerNotFoundException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Controller class to handle log-related operations.
 */
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

	/**
	 * Handles HTTP GET requests for log-related operations.
	 *
	 * @param req  The HttpServletRequest object.
	 * @param resp The HttpServletResponse object.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an I/O error occurs.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getSession().getAttribute(AUTH_PLAYER_PARAM);
		if (authPlayerDto == null) {
			System.out.println("Only an authorized administrator can perform this operation");
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, "Need to log in as an administrator.");
		} else if (authPlayerDto.role() == Role.ADMIN) {
			try {
				Command command = commandProvider.getCommand(req.getParameter(COMMAND));
				switch (command) {
					case SHOW_ALL_LOG -> {
						List<LogResponseDto> logList = loggerService.getAllLogs(authPlayerDto);
						generateResponse(resp, HttpServletResponse.SC_OK, logList);
					}
					case SHOW_PLAYER_LOG -> {
						try {
							String inputUsernameForSearch = req.getParameter(USERNAME);
							List<LogResponseDto> logList = loggerService
									.getLogsByUsername(authPlayerDto, inputUsernameForSearch);
							generateResponse(resp, HttpServletResponse.SC_OK, logList);
						} catch (PlayerNotFoundException e) {
							generateResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
						}
					}
				}
			} catch (NullPointerException e) {
				generateResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Content doesn't exist.");
			}
		} else {
			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, "You do not have access to this resource.");
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
	 * Generates a JSON response for the HttpServletResponse with the given status and list of log entries.
	 *
	 * @param resp    The HttpServletResponse object.
	 * @param status  The HTTP status code.
	 * @param content The list of log entries to be included in the response.
	 * @throws IOException If an I/O error occurs.
	 */
	private void generateResponse(HttpServletResponse resp, int status, List<LogResponseDto> content) throws IOException {
		resp.setStatus(status);
		resp.setContentType(CONTENT_TYPE);

		ServletOutputStream outputStream = resp.getOutputStream();

		if (content.isEmpty()) {
			InfoResponse infoResponse = new InfoResponse(new Date().toString(), status, "No logs.");
			outputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		} else {
			outputStream.write(objectMapper.writeValueAsBytes(content));
		}

	}
}