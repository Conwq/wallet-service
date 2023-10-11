package org.example.walletservice.in;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.controller.PlayerController;
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
	private final PlayerController playerController = Mockito.mock(PlayerController.class);
	private PlayerRegistrationHandler registrationHandler = new PlayerRegistrationHandler(playerController, scanner);
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	void setUp() {
		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		ByteArrayInputStream inputStream = new ByteArrayInputStream("user123\n13213".getBytes());
		System.setIn(inputStream);
	}

	@AfterEach
	public void tearDown() {
		System.setOut(origOut);
		System.setIn(origIn);
	}

	@Test
	public void shouldShowTextEnteringLoginAndPassword() {
		Mockito.when(scanner.nextLine()).thenReturn("user123").thenReturn("13213");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		registrationHandler.registrationPlayer();

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Enter username:", "Enter password:");

		Mockito.verify(playerController, Mockito.times(1))
				.registrationPlayer("user123", "13213");
	}
}