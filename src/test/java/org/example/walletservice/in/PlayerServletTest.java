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
	}

	@Test
	void shouldShowPlayerBalance() throws IOException, ServletException {
		AuthPlayerDto authPlayer = new AuthPlayerDto(1, "admin", Role.ADMIN);
		BigDecimal balance = new BigDecimal(100);

		Mockito.when(req.getAttribute("authPlayer")).thenReturn(authPlayer);
		Mockito.when(playerService.getPlayerBalance(authPlayer)).thenReturn(balance);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		playerServlet.doGet(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_OK,
				String.format("%s, your balance -> %s", authPlayer.username(), balance));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	void shouldThrowExceptionThatPlayerNotLoggedIn() throws IOException, ServletException {
		Mockito.when(req.getAttribute("authPlayer")).thenReturn(null);
		Mockito.when(playerService.getPlayerBalance(null))
				.thenThrow(new PlayerNotLoggedInException("Performing an operation by an unregistered user."));
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		playerServlet.doGet(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_BAD_REQUEST,
				"Performing an operation by an unregistered user.");
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldRegisterPlayer() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto("new_user", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("registration");
		Mockito.when(commandProvider.getCommand("registration")).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_OK,
				"You have successfully registered.");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldThrowInvalidInputException_usernameNull() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto(null, "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("registration");
		Mockito.when(commandProvider.getCommand("registration")).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException("Username or password can`t be empty."))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_BAD_REQUEST,
				"Username or password can`t be empty.");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldThrowInvalidInputException_passwordNull() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto("username", null);
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("registration");
		Mockito.when(commandProvider.getCommand("registration")).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException("Username or password can`t be empty."))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_BAD_REQUEST,
				"Username or password can`t be empty.");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldThrowInvalidInputException_usernameLengthLessThanOne() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto("", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("registration");
		Mockito.when(commandProvider.getCommand("registration")).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException("The length of the username or password cannot be less than 1"))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_BAD_REQUEST,
				"The length of the username or password cannot be less than 1");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldThrowInvalidInputException_passwordLengthLessThanOne() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("registration");
		Mockito.when(commandProvider.getCommand("registration")).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new InvalidInputDataException("The length of the username or password cannot be less than 1"))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_BAD_REQUEST,
				"The length of the username or password cannot be less than 1");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldThrowExceptionPlayerAlreadyExists() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("registration");
		Mockito.when(commandProvider.getCommand("registration")).thenReturn(Command.REGISTRATION);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.doThrow(new PlayerAlreadyExistException("This user is already registered. Try again."))
				.when(playerService).registrationPlayer(playerRequest);

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_CONFLICT,
				"This user is already registered. Try again.");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(playerService).registrationPlayer(playerRequest);
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_CONFLICT);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldPerformSigInOperation() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");
		AuthPlayerDto authPlayer = new AuthPlayerDto(2, playerRequest.username(), Role.USER);
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("sign_in");
		Mockito.when(commandProvider.getCommand("sign_in")).thenReturn(Command.SIGN_IN);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.when(playerService.logIn(playerRequest)).thenReturn(authPlayer);

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_OK,
				"You've successfully logged in");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(jwtService).generateWebToken(Mockito.anyMap(), Mockito.any(AuthPlayerDto.class));
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}

	@Test
	public void shouldThrowExceptionWhenLogIn_PlayerNotFoundException() throws IOException, ServletException {
		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");
		String jsonObject = objectMapper.writeValueAsString(playerRequest);

		Mockito.when(req.getReader()).thenReturn(bufferedReader);
		Mockito.when(req.getParameter("command")).thenReturn("sign_in");
		Mockito.when(commandProvider.getCommand("sign_in")).thenReturn(Command.SIGN_IN);
		Mockito.when(bufferedReader.ready()).thenReturn(true).thenReturn(false);
		Mockito.when(bufferedReader.readLine()).thenReturn(jsonObject);
		Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
		Mockito.when(playerService.logIn(playerRequest))
				.thenThrow(new PlayerNotFoundException("Current player not found. Please try again."));

		playerServlet.doPost(req, resp);

		InfoResponse infoResponse = new InfoResponse(new Date().toString(), HttpServletResponse.SC_NOT_FOUND,
				"Current player not found. Please try again.");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(objectMapper.writeValueAsBytes(infoResponse));

		Mockito.verify(jwtService, Mockito.never()).generateWebToken(Mockito.anyMap(), Mockito.any(AuthPlayerDto.class));
		Mockito.verify(resp).setStatus(HttpServletResponse.SC_NOT_FOUND);
		Mockito.verify(resp).setContentType("application/json");
		Mockito.verify(outputStream).write(byteArrayOutputStream.toByteArray());
	}
}