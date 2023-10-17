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

	public MainMenu(PlayerRegistrationHandler playerRegistrationHandler,
					PlayerSessionManager playerSessionManager,
					OperationChooserVerification operationChooserVerification,
					Scanner scanner
	) {
		this.playerRegistrationHandler = playerRegistrationHandler;
		this.playerSessionManager = playerSessionManager;
		this.operationChooserVerification = operationChooserVerification;
		this.scanner = scanner;
	}

	/**
	 * Launches the main menu and allows users to enter registration/login/exit.
	 */
	public void start() {
		System.out.println("\n\t***Hello!***\n");
		boolean exit = false;
		do {
			System.out.println("1. Registration\n2. Log in\n3. Exit\n");
			int userInputValue = operationChooserVerification.userDataVerification(3);

			switch (userInputValue) {
				case 1 -> playerRegistrationHandler.registrationPlayer();
				case 2 -> playerSessionManager.logIn();
				case 3 -> {
					System.out.println("\nGood bye!\n");
					exit = true;
					scanner.close();
				}
			}
		} while (!exit);
	}
}