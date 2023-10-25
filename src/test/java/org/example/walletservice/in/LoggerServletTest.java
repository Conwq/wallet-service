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
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class LoggerServletTest {
	private LoggerServlet loggerServlet;
	private LoggerService loggerService;
	private ObjectMapper objectMapper;
	private CommandProvider commandProvider;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream outputStream;
	private AuthPlayerDto authPlayerDto;
	private static final String USERNAME = "username";
	private final String CONTENT_TYPE = "application/json";
	private static final String AUTH_PLAYER = "authPlayer";
	private static final String COMMAND = "command";

	@BeforeEach
	public void setUp() {
		commandProvider = Mockito.mock(CommandProvider.class);
		request = Mockito.mock(HttpServletRequest.class);
		response = Mockito.mock(HttpServletResponse.class);
		outputStream = Mockito.mock(ServletOutputStream.class);
		objectMapper = new ObjectMapper();
		loggerService = Mockito.mock(LoggerService.class);
		loggerServlet = new LoggerServlet(loggerService, objectMapper, commandProvider);

		authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);
	}

	@Test
	void shouldReturnAllLogs() throws ServletException, IOException {
		final String command = "show_all_log";
		List<LogResponseDto> logList = new ArrayList<>() {{
			add(new LogResponseDto("Log message 1"));
			add(new LogResponseDto("Log message 2"));
		}};

		Mockito.when(request.getAttribute(AUTH_PLAYER)).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.SHOW_ALL_LOG);
		Mockito.when(loggerService.getAllLogs(authPlayerDto)).thenReturn(logList);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(logList));
		Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	void shouldThrowException_PlayerNotLoggedInException() throws ServletException, IOException {
		final String message = "You do not have access to this resource.";
		final AuthPlayerDto authPlayerDto = new AuthPlayerDto(2, "user", Role.USER);

		Mockito.when(request.getAttribute(AUTH_PLAYER)).thenReturn(authPlayerDto);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
	}

	@Test
	void shouldThrowException_PlayerDoesNotHaveAccessException() throws ServletException, IOException {
		final String message = "Only an authorized administrator can perform this operation.";

		Mockito.when(request.getAttribute(AUTH_PLAYER)).thenReturn(null);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
	}

	@Test
	void shouldReturnPlayersLogs() throws ServletException, IOException {
		final String command = "show_player_log";
		final String inputUsername = "user";
		final List<LogResponseDto> logList = new ArrayList<>() {{
			add(new LogResponseDto("Log message 1"));
			add(new LogResponseDto("Log message 2"));
		}};

		Mockito.when(request.getAttribute(AUTH_PLAYER)).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.SHOW_PLAYER_LOG);
		Mockito.when(loggerService.getLogsByUsername(authPlayerDto, inputUsername)).thenReturn(logList);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);
		Mockito.when(request.getParameter(USERNAME)).thenReturn(inputUsername);

		loggerServlet.doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldReturnAnErrorThaSearchPlayerWasNotFound() throws IOException, ServletException {
		final String inputUsername = "player_not_exist";
		final String command = "show_player_log";
		final String message = String.format("Player %s not found", inputUsername);

		Mockito.when(request.getAttribute(AUTH_PLAYER)).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.SHOW_PLAYER_LOG);
		Mockito.when(request.getParameter(USERNAME)).thenReturn(inputUsername);
		Mockito.when(loggerService.getLogsByUsername(authPlayerDto, inputUsername))
				.thenThrow(new PlayerNotFoundException(message));
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
	}

	@Test
	void shouldReturnErrorThatResourceDoesNotExist() throws ServletException, IOException {
		final String command = "not_exist_command";

		Mockito.when(request.getAttribute(AUTH_PLAYER)).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.NO_COMMAND);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
	}
}