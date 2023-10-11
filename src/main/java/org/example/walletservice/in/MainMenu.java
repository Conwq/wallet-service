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
	private PlayerRegistrationHandler playerRegistrationHandler = PlayerRegistrationHandler.getInstance();
	private PlayerSessionManager playerSessionManager = PlayerSessionManager.getInstance();
	private OperationChooserVerification operationChooserVerification =
			OperationChooserVerification.getInstance();
	private final Scanner scanner = ScannerProvider.getScanner();

	private MainMenu() {
	}

	public MainMenu(PlayerRegistrationHandler playerRegistrationHandler,
					PlayerSessionManager playerSessionManager,
					OperationChooserVerification operationChooserVerification){
		this.playerRegistrationHandler = playerRegistrationHandler;
		this.playerSessionManager = playerSessionManager;
		this.operationChooserVerification = operationChooserVerification;
	}

	/**
	 * The method returns a single instance of the MainMenu type.
	 * If the instance has not yet been created, a new instance is created,
	 * otherwise the existing instance is returned.
	 *
	 * @return a single instance of type MainMenu.
	 */
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
		}
		while (!exit);
	}
}