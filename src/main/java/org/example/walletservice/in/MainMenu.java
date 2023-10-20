package org.example.walletservice.in;

import org.example.walletservice.in.util.OperationChooserVerification;

import java.util.Scanner;

/**
 * Main menu.
 * Handles user interaction for registration, login, and logout.
 */
public final class MainMenu {
	private final PlayerRegistrationHandler playerRegistrationHandler;
	private final PlayerSessionManager playerSessionManager;
	private final OperationChooserVerification operationChooserVerification;
	private final Scanner scanner;
	private static final String GREETING_MESSAGE = "***Hello!***\n";
	private static final String MENU_OPTIONS =
			"""
			1. Registration
			2. Log in
			3. Exit
			""";
	private static final String GOODBYE_MESSAGE = "Good bye!\n";

	public MainMenu(PlayerRegistrationHandler playerRegistrationHandler, PlayerSessionManager playerSessionManager,
					OperationChooserVerification operationChooserVerification, Scanner scanner) {
		this.playerRegistrationHandler = playerRegistrationHandler;
		this.playerSessionManager = playerSessionManager;
		this.operationChooserVerification = operationChooserVerification;
		this.scanner = scanner;
	}

	/**
	 * Launches the main menu and allows users to enter registration/login/exit.
	 */
	public void start() {
		System.out.println(GREETING_MESSAGE);
		boolean exit = false;
		do {
			System.out.println(MENU_OPTIONS);
			int userInputValue = operationChooserVerification.userDataVerification(3);
			switch (userInputValue) {
				case 1 -> playerRegistrationHandler.registrationPlayer();
				case 2 -> playerSessionManager.logIn();
				case 3 -> exit = exit();
			}
		} while (!exit);
	}

	private boolean exit() {
		System.out.println(GOODBYE_MESSAGE);
		scanner.close();
		return true;
	}
}