package org.example.walletservice.in;

import org.example.walletservice.controller.PlayerController;
import org.example.walletservice.util.Cleaner;
import org.example.walletservice.util.ScannerProvider;

import java.util.Scanner;

/**
 * Player registration.
 * Allows users to enter a username and password to register.
 */
public final class PlayerRegistrationHandler {
	private static PlayerRegistrationHandler instance;
	private final Cleaner cleaner = Cleaner.getInstance();
	private final PlayerController playerController = PlayerController.getInstance();
	private final Scanner scanner = ScannerProvider.getScanner();

	private PlayerRegistrationHandler() {
	}

	public static PlayerRegistrationHandler getInstance(){
		if (instance == null){
			instance = new PlayerRegistrationHandler();
		}
		return instance;
	}

	/**
	 *  Enter your login and password to register from the app console.
	 */
	public void registrationPlayer() {
		cleaner.cleanBuffer(scanner);

		System.out.print("Enter username: ");
		String username = scanner.nextLine().trim();

		System.out.print("Enter password: ");
		String password = scanner.nextLine().trim();

		playerController.registrationPlayer(username, password);
	}
}
