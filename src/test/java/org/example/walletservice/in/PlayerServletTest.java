package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.in.command.Command;
import org.example.walletservice.in.command.CommandProvider;
import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

class PlayerServletTest {
	private ObjectMapper objectMapper;
	private PlayerService playerService;
	private CommandProvider commandProvider;
	private JwtService jwtService;
	private PlayerServlet playerServlet;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private ServletOutputStream outputStream;
	private BufferedReader bufferedReader;
	private AuthPlayerDto authPlayer;
	private static final String AUTH_PLAYER = "authPlayer";
	private static final String CONTENT_TYPE = "application/json";
	private static final String COMMAND = "command";

	@BeforeEach
	public void setUp() {
		objectMapper = new ObjectMapper();
		playerService = Mockito.mock(PlayerService.class);
		commandProvider = Mockito.mock(CommandProvider.class);
		jwtService = Mockito.mock(JwtService.class);
		req = Mockito.mock(HttpServletRequest.class);
		resp = Mockito.mock(HttpServletResponse.class);
		outputStream = Mockito.mock(ServletOutputStream.class);
		bufferedReader = Mockito.mock(BufferedReader.class);
		playerServlet = new PlayerServlet(objectMapper, playerService, commandProvider, jwtService);
		authPlayer = new AuthPlayerDto(1, "admin", Role.ADMIN);
	}

	@Test
	void shouldShowPlayerBalance() throws IOException, ServletException {
		final BigDecimal balance = new BigDecimal(100);
		final String message = String.format("%s, your balance -> %s", authPlayer.username(), balance);

		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(authPlayer);
		Mockito.when(playerService.getPlayerBalance(authPlayer)).thenReturn(balance);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		playerServlet.doGet(req, resp);

		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	void shouldThrowExceptionThatPlayerNotLoggedIn() throws IOException, ServletException {
		final String message = "Performing an operation by an unregistered user.";

		Mockito.when(req.getAttribute(AUTH_PLAYER)).thenReturn(null);
		Mockito.when(playerService.getPlayerBalance(null))
				.thenThrow(new PlayerNotLoggedInException(message));
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		playerServlet.doGet(req, resp);

		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldRegisterPlayer() throws IOException, ServletException {
		final String command = "registration";
		final String message = "You have successfully registered.";

		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		playerServlet.doPost(req, resp);

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowInvalidInputException_usernameNull() throws IOException, ServletException {
		final String command = "registration";
		final String message = "Username or password can`t be empty.";

		PlayerRequestDto playerRequest = new PlayerRequestDto(null, "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException(message))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowInvalidInputException_passwordNull() throws IOException, ServletException {
		final String command = "registration";
		final String message = "Username or password can`t be empty.";

		PlayerRequestDto playerRequest = new PlayerRequestDto("username", null);
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException(message))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowInvalidInputException_usernameLengthLessThanOne() throws IOException, ServletException {
		final String command = "registration";
		final String message = "The length of the username or password cannot be less than 1";

		PlayerRequestDto playerRequest = new PlayerRequestDto("", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException(message))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowInvalidInputException_passwordLengthLessThanOne() throws IOException, ServletException {
		final String command = "registration";
		final String message = "The length of the username or password cannot be less than 1";

		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException(message))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionPlayerAlreadyExists() throws IOException, ServletException {
		final String command = "registration";
		final String message = "This user is already registered. Try again.";

		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new PlayerAlreadyExistException(message)).when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_CONFLICT);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldPerformSigInOperation() throws IOException, ServletException {
		final String command = "sign_in";

		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");
		AuthPlayerDto authPlayer = new AuthPlayerDto(2, playerRequest.username(), Role.USER);
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.SIGN_IN);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.when(playerService.logIn(playerRequest)).thenReturn(authPlayer);

		playerServlet.doPost(req, resp);

		Mockito.verify(jwtService).generateWebToken(Mockito.anyMap(), Mockito.any(AuthPlayerDto.class));
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}

	@Test
	public void shouldThrowExceptionWhenLogIn_PlayerNotFoundException() throws IOException, ServletException {
		final String command = "sign_in";
		final String message = "Current player not found. Please try again.";

		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter(COMMAND)).thenReturn(command);
		Mockito.when(commandProvider.getCommand(command)).thenReturn(Command.SIGN_IN);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.when(playerService.logIn(playerRequest))
				.thenThrow(new PlayerNotFoundException(message));

		playerServlet.doPost(req, resp);

		Mockito.verify(jwtService, Mockito.never()).generateWebToken(Mockito.anyMap(), Mockito.any(AuthPlayerDto.class));
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_NOT_FOUND);
		Mockito.verify(resp).setContentType(CONTENT_TYPE);
	}
}