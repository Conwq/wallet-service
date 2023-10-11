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
	private PlayerController playerController = PlayerController.getInstance();
	private Scanner scanner = ScannerProvider.getScanner();

	private PlayerRegistrationHandler() {
	}

	public PlayerRegistrationHandler(PlayerController playerController, Scanner scanner){
		this.playerController = playerController;
		this.scanner = scanner;
	}

	/**
	 * The method returns a single instance of the PlayerRegistrationHandler type.
	 * If the instance has not yet been created, a new instance is created,
	 * otherwise the existing instance is returned
	 *
	 * @return a single instance of type PlayerRegistrationHandler
	 */
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
