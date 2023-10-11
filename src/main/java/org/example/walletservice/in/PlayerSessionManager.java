package org.example.walletservice.in;

import org.example.walletservice.controller.PlayerController;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.logger.PlayerActivityLogger;
import org.example.walletservice.util.Cleaner;

import java.util.Scanner;

/**
 * Manages the player's session.
 * Includes the login process and available operations for the authorized user.
 */
public final class PlayerSessionManager {
	private final Cleaner cleaner;
	private final OperationChooserVerification operationChooserVerification;
	private final PlayerController playerController;
	private final Scanner scanner;
	private final PlayerActivityLogger playerActivityLogger;

	public PlayerSessionManager(Cleaner cleaner,
								OperationChooserVerification operationChooserVerification,
								PlayerController playerController,
								Scanner scanner,
								PlayerActivityLogger playerActivityLogger) {
		this.cleaner = cleaner;
		this.operationChooserVerification = operationChooserVerification;
		this.playerController = playerController;
		this.scanner = scanner;
		this.playerActivityLogger = playerActivityLogger;
	}

	/**
	 * Initiates the login process by prompting users to enter a username and password.
	 * Upon successful login, provides available operations for the authorized user.
	 */
	public void logIn() {
		cleaner.cleanBuffer(scanner);

		System.out.print("Enter username: ");
		String username = scanner.nextLine();

		System.out.print("Enter password: ");
		String password = scanner.nextLine();

		Player player = playerController.logIn(username, password);
		if (player == null){
			return;
		}
		displayOperationsMenuForAuthorizedPlayer(player);
	}

	/**
	 * The method displays menu items to choose from, depending on which role the authorized user has.
	 *
	 * @param player An authorized player for whom menu items will be displayed depending on the role.
	 */
	private void displayOperationsMenuForAuthorizedPlayer(Player player) {
		System.out.printf("\nWelcome back, %s!\n\n", player.getUsername());
		int numberCommandToSelect = player.getRole() == Role.ADMIN ? 6 : 5;

		do {
			displayMenuOptions(player, numberCommandToSelect);

			int userInputValue = operationChooserVerification.userDataVerification(numberCommandToSelect);
			if (userInputValue == -1) {
				continue;
			}
			if (userInputValue == numberCommandToSelect) {
				System.out.printf("\nGood bye, %s!\n\n", player.getUsername());
				playerActivityLogger.recordAction(Operation.EXIT, player.getUsername(), Status.SUCCESSFUL);
				break;
			}
			executeCommandAccordingUserChoice(userInputValue, player);
		}
		while (true);
	}

	/**
	 * Displays the menu options based on the player's role.
	 *
	 * @param player The player for whom the menu options are displayed.
	 * @param numberCommandToSelect The number representing the command to log out.
	 */
	private void displayMenuOptions(Player player, int numberCommandToSelect) {
		System.out.println("\n1. Balance\n2. Credit\n3. Debit\n4. Show transactional history");
		if (player.getRole() == Role.ADMIN) {
			System.out.println("5. Show logs");
		}
		System.out.printf("%d. Log out\n", numberCommandToSelect);
	}

	/**
	 * Executes action based on the user's choice.
	 *
	 * @param userInputValue The value representing the user's choice.
	 * @param player The player for whom the command is executed.
	 */
	private void executeCommandAccordingUserChoice(int userInputValue, Player player) {
		switch (userInputValue) {
			case 1 -> playerController.displayPlayerBalance(player);
			case 2 -> playerController.credit(player);
			case 3 -> playerController.debit(player);
			case 4 -> playerController.displayPlayerTransactionalHistory(player);
			case 5 -> displayLogOptions(player);
		}
	}

	/**
	 * Displays log-related options for the player, allowing them to view all logs,
	 * view logs for specific players, or go back to the main menu.
	 *
	 * @param player The player for whom the log options are displayed.
	 */
	private void displayLogOptions(Player player){
		boolean exit = false;

		do {
			System.out.println("\n1. All logs\n2. Players logs\n3. Back");

			int userInputValue = operationChooserVerification.userDataVerification(3);
			switch (userInputValue){
				case 1 -> playerController.showAllLogs(player);
				case 2 -> {
					cleaner.cleanBuffer(scanner);
					System.out.print("\nEnter the name of the user you want to see the logs: ");
					playerController.showLogsByUsername(player, scanner.nextLine());
				}
				case 3 -> exit = true;
			}
		}
		while (!exit);
	}
}