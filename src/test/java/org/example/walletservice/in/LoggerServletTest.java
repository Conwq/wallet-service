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
	private final String CONTENT_TYPE = "application/json";

	@BeforeEach
	public void setUp() {
		commandProvider = Mockito.mock(CommandProvider.class);
		request = Mockito.mock(HttpServletRequest.class);
		response = Mockito.mock(HttpServletResponse.class);
		outputStream = Mockito.mock(ServletOutputStream.class);
		objectMapper = new ObjectMapper();
		loggerService = Mockito.mock(LoggerService.class);
		loggerServlet = new LoggerServlet(loggerService, objectMapper, commandProvider);
	}

	@Test
	void shouldReturnAllLogs() throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		List<LogResponseDto> logList = new ArrayList<>() {{
			add(new LogResponseDto("Log message 1"));
			add(new LogResponseDto("Log message 2"));
		}};

		Mockito.when(request.getAttribute("authPlayer")).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter("command")).thenReturn("show_all_log");
		Mockito.when(commandProvider.getCommand("show_all_log")).thenReturn(Command.SHOW_ALL_LOG);
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
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(2, "user", Role.USER);
		InfoResponse infoResponse = new InfoResponse(new Date().toString(),
				HttpServletResponse.SC_NOT_ACCEPTABLE, "You do not have access to this resource.");

		Mockito.when(request.getAttribute("authPlayer")).thenReturn(authPlayerDto);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	void shouldThrowException_PlayerDoesNotHaveAccessException() throws ServletException, IOException {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(),
				HttpServletResponse.SC_NOT_ACCEPTABLE,
				"Only an authorized administrator can perform this operation.");
		Mockito.when(request.getAttribute("authPlayer")).thenReturn(null);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	void shouldReturnPlayersLogs() throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		String inputUsername = "user";

		List<LogResponseDto> logList = new ArrayList<>() {{
			add(new LogResponseDto("Log message 1"));
			add(new LogResponseDto("Log message 2"));
		}};

		Mockito.when(request.getAttribute("authPlayer")).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter("command")).thenReturn("show_player_log");
		Mockito.when(commandProvider.getCommand("show_player_log")).thenReturn(Command.SHOW_PLAYER_LOG);
		Mockito.when(loggerService.getLogsByUsername(authPlayerDto, inputUsername)).thenReturn(logList);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);
		Mockito.when(request.getParameter("username")).thenReturn(inputUsername);

		loggerServlet.doGet(request, response);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(logList));
		Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldReturnAnErrorThaSearchPlayerWasNotFound() throws IOException, ServletException {
		String inputUsername = "player_not_exist";
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		Mockito.when(request.getAttribute("authPlayer")).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter("command")).thenReturn("show_player_log");
		Mockito.when(commandProvider.getCommand("show_player_log")).thenReturn(Command.SHOW_PLAYER_LOG);
		Mockito.when(request.getParameter("username")).thenReturn(inputUsername);
		Mockito.when(loggerService.getLogsByUsername(authPlayerDto, inputUsername))
				.thenThrow(new PlayerNotFoundException(String.format("Player %s not found", inputUsername)));
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(),
				HttpServletResponse.SC_BAD_REQUEST, String.format("Player %s not found", inputUsername));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));
		Mockito.verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	void shouldReturnErrorThatResourceDoesNotExist() throws ServletException, IOException {
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		Mockito.when(request.getAttribute("authPlayer")).thenReturn(authPlayerDto);
		Mockito.when(request.getParameter("command")).thenReturn("not_exist_command");
		Mockito.when(commandProvider.getCommand("show_all_log")).thenReturn(Command.NO_COMMAND);
		Mockito.when(response.getOutputStream()).thenReturn(outputStream);

		loggerServlet.doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
		Mockito.verify(response).setContentType(CONTENT_TYPE);
	}
}