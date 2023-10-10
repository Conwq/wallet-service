package org.example.walletservice.in;

import org.example.walletservice.controller.PlayerController;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.logger.TransactionLog;
import org.example.walletservice.util.Cleaner;
import org.example.walletservice.util.ScannerProvider;

import java.util.Scanner;

/**
 * Manages the player's session.  Includes the login process and available operations for the authorized user.
 */
public final class PlayerSessionManager {
	private static PlayerSessionManager instance;
	private final Cleaner cleaner = Cleaner.getInstance();
	private final OperationChooserVerification operationChooserVerification = OperationChooserVerification.getInstance();
	private final PlayerController playerController = PlayerController.getInstance();
	private final Scanner scanner = ScannerProvider.getScanner();
	private final TransactionLog transactionLog = TransactionLog.getInstance();

	private PlayerSessionManager(){
	}

	public static PlayerSessionManager getInstance() {
		if(instance == null){
			instance = new PlayerSessionManager();
		}
		return instance;
	}

	/**
	 * Initiates the login process by prompting users to enter a username and password.
	 * Upon successful login, provides available operations for the authorized user.
	 */
	public void logIn() {
		cleaner.cleanBuffer(scanner);

		System.out.print("Enter username: ");
		String username = scanner.nextLine().trim();

		System.out.print("Enter password: ");
		String password = scanner.nextLine().trim();

		Player player = playerController.logIn(username, password);
		if (player == null){
			return;
		}
		displayOperationsMenuForAuthorizedPlayer(player);
	}

	private void displayOperationsMenuForAuthorizedPlayer(Player player) {
		System.out.printf("\nWelcome back, %s!\n\n", player.getUsername());
		int numberCommandToSelect = player.getRole() == Role.ADMIN ? 6 : 5;

		while (true){
			displayMenuOptions(player, numberCommandToSelect);

			int userInputValue = operationChooserVerification.userDataVerification(numberCommandToSelect);
			if (userInputValue == -1) {
				continue;
			}
			if (userInputValue == numberCommandToSelect) {
				System.out.printf("\nGood bye, %s!\n\n", player.getUsername());
				transactionLog.recordTransaction(Operation.EXIT, player.getUsername(), Status.SUCCESSFUl);
				break;
			}
			executeCommandAccordingUserChoice(userInputValue, player);
		}
	}

	private void displayMenuOptions(Player player, int numberCommandToSelect) {
		System.out.println("\n1. Balance\n" +
				"2. Credit\n" +
				"3. Debit\n" +
				"4. Show transactional history");
		if (player.getRole() == Role.ADMIN) {
			System.out.println("5. Show logs");
		}
		System.out.printf("%d. Log out\n", numberCommandToSelect);
	}

	private void executeCommandAccordingUserChoice(int userInputValue, Player player) {
		switch (userInputValue) {
			case 1 -> playerController.showPlayerBalance(player);
			case 2 -> playerController.credit(player);
			case 3 -> playerController.debit(player);
			case 4 -> playerController.showPlayerTransactionalHistory(player);
			case 5 -> displayLogOptions(player);
		}
	}

	private void displayLogOptions(Player player){
		while (true) {
			System.out.println("\n1. All logs\n" +
					"2. Players logs\n" +
					"3. Back");

			int userInputValue = operationChooserVerification.userDataVerification(3);

			if (userInputValue == -1){
				continue;
			}
			if (userInputValue == 3){
				break;
			}

			switch (userInputValue){
				case 1 -> playerController.showAllLogs(player);
				case 2 -> {
					cleaner.cleanBuffer(scanner);
					System.out.print("\nEnter the name of the user you want to see the logs: ");
					String inputUsernameForSearch = scanner.nextLine();
					playerController.showLogsByUsername(player, inputUsernameForSearch);
				}
			}
		}
	}
}