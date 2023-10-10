package org.example.walletservice.in;

import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.util.ScannerProvider;

import java.util.Scanner;

/**
 * Main menu.
 * Handles user interaction for registration, login, and logout.
 */
public final class MainMenu {
	private static MainMenu instance;
	private final PlayerRegistrationHandler playerRegistrationHandler = PlayerRegistrationHandler.getInstance();
	private final PlayerSessionManager playerSessionManager = PlayerSessionManager.getInstance();
	private final OperationChooserVerification operationChooserVerification =
			OperationChooserVerification.getInstance();
	private final Scanner scanner = ScannerProvider.getScanner();

	private MainMenu() {
	}

	public static MainMenu getInstance() {
		if (instance == null) {
			instance = new MainMenu();
		}
		return instance;
	}

	/**
	 * Launches the main menu and allows users to enter registration/login/exit.
	 */
	public void start() {
		System.out.println("\n\t***Hello!***\n");

		while (true) {
			System.out.println("1. Registration\n" +
					"2. Log in\n" +
					"3. Exit\n");
			int userInputValue = operationChooserVerification.userDataVerification(3);

			if (userInputValue == -1) {
				continue;
			}
			if (userInputValue == 3) {
				System.out.println("\nGood bye!");
				scanner.close();
				break;
			}
			executeCommandAccordingUserChoice(userInputValue);
		}
	}

	private void executeCommandAccordingUserChoice(int userInputValue) {
		switch (userInputValue){
			case 1 -> playerRegistrationHandler.registrationPlayer();
			case 2 -> playerSessionManager.logIn();
		}
	}
}