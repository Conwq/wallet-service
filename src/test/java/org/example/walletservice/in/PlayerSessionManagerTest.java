package org.example.walletservice.in;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.controller.FrontController;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.util.Cleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

class PlayerSessionManagerTest {
	private PlayerSessionManager playerSessionManager;
	private final Cleaner cleaner = Mockito.mock(Cleaner.class);
	private final OperationChooserVerification operationChooser =
			Mockito.mock(OperationChooserVerification.class);
	private final FrontController frontController = Mockito.mock(FrontController.class);
	private final Scanner scanner = Mockito.mock(Scanner.class);
	private final LoggerService loggerService = Mockito.mock(LoggerService.class);
	private final InputStream origIn = System.in;
	private final PrintStream origOut = System.out;
	private static final String USERNAME = "user123";
	private static final String PASSWORD = "13213";
	private static final Player PLAYER = Player.builder().playerID(1)
			.username(USERNAME)
			.password(PASSWORD).build();
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	void setUp() {
		playerSessionManager =
				new PlayerSessionManager(cleaner, operationChooser, frontController, scanner, loggerService);

		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				String.format("%s\n%s", USERNAME, PASSWORD).getBytes());
		System.setIn(inputStream);
	}

	@AfterEach
	void tearDown() {
		System.setIn(origIn);
		System.setOut(origOut);
	}

	@Test
	public void shouldDisplayMenuForAuthorizedPlayerWithUserRole() {
		Mockito.when(scanner.hasNextLine()).thenReturn(false);
		Mockito.when(scanner.nextLine()).thenReturn(USERNAME).thenReturn(PASSWORD);
		Mockito.when(operationChooser.userDataVerification(5)).thenReturn(5);
		Mockito.when(scanner.nextInt()).thenReturn(5);
		Mockito.when(frontController.logIn(USERNAME, PASSWORD)).thenReturn(PLAYER);

		playerSessionManager.logIn();

		Mockito.verify(cleaner, Mockito.times(1)).cleanBuffer(scanner);

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("Enter username:", "Enter password:", String.format("Welcome back, %s!", USERNAME));
		AssertionsForClassTypes.assertThat(outputStream.toString()).doesNotContain("5. Show logs");
	}
}