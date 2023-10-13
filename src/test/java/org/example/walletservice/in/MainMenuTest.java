package org.example.walletservice.in;

import org.assertj.core.api.Assertions;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

class MainMenuTest {
	private MainMenu mainMenu;
	private final PlayerRegistrationHandler registrationHandler = Mockito.mock(PlayerRegistrationHandler.class);
	private final PlayerSessionManager sessionManager = Mockito.mock(PlayerSessionManager.class);
	private final OperationChooserVerification chooserVerification = Mockito.mock(OperationChooserVerification.class);
	private final Scanner scanner = Mockito.mock(Scanner.class);
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	public void setUp() {
		mainMenu = new MainMenu(registrationHandler, sessionManager, chooserVerification, scanner);

		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		ByteArrayInputStream inputStream = new ByteArrayInputStream("1\n".getBytes());
		System.setIn(inputStream);
	}

	@AfterEach
	public void tearDown(){
		System.setOut(origOut);
		System.setIn(origIn);
	}

	@Test
	public void shouldDisplayMainMenu_successful() {
		Mockito.when(chooserVerification.userDataVerification(3)).thenReturn(3);

		mainMenu.start();

		Mockito.verify(registrationHandler, Mockito.never()).registrationPlayer();
		Mockito.verify(sessionManager, Mockito.never()).logIn();
		Assertions.assertThat(outputStream.toString())
				.contains("1. Registration", "2. Log in", "3. Exit", "Good bye!");
	}
}