package org.example.walletservice.in;

import org.example.walletservice.controller.PlayerController;
import org.example.walletservice.util.Cleaner;

import java.util.Scanner;

/**
 * Player registration.
 * Allows users to enter a username and password to register.
 */
public final class PlayerRegistrationHandler {
	private final PlayerController playerController;
	private final Scanner scanner;
	private final Cleaner cleaner;

	public PlayerRegistrationHandler(PlayerController playerController, Scanner scanner, Cleaner cleaner){
		this.playerController = playerController;
		this.scanner = scanner;
		this.cleaner = cleaner;
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
