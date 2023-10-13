package org.example.walletservice.in;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.controller.FrontController;
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

class PlayerRegistrationHandlerTest {
	private final Scanner scanner = Mockito.mock(Scanner.class);
	private final FrontController frontController = Mockito.mock(FrontController.class);
	private final Cleaner cleaner = Mockito.mock(Cleaner.class);
	private final PlayerRegistrationHandler registrationHandler =
			new PlayerRegistrationHandler(frontController, scanner, cleaner);
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private static final String USERNAME = "user123";
	private static final String PASSWORD = "13213";

	@BeforeEach
	void setUp() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				String.format("%s\n%s", USERNAME, PASSWORD).getBytes());
		System.setIn(inputStream);
	}

	@AfterEach
	public void tearDown() {
		System.setOut(origOut);
		System.setIn(origIn);
	}

	@Test
	public void shouldShowTextEnteringLoginAndPassword() {
		Mockito.when(scanner.nextLine()).thenReturn(USERNAME).thenReturn(PASSWORD);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		registrationHandler.registrationPlayer();

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Enter username:", "Enter password:");

		Mockito.verify(frontController, Mockito.times(1))
				.registrationPlayer(USERNAME, PASSWORD);
	}
}