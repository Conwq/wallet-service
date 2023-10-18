package org.example.walletservice.in;

import org.example.walletservice.controller.FrontController;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.util.Cleaner;

import java.util.Scanner;

/**
 * Manages the player's session.
 * Includes the login process and available operations for the authorized user.
 */
public final class PlayerSessionManager {
	private final Cleaner cleaner;
	private final OperationChooserVerification operationChooserVerification;
	private final FrontController frontController;
	private final Scanner scanner;
	private final LoggerService loggerService;
	private static final String WELCOME_BACK = "Welcome back, %s!\n\n";
	private static final String GOODBYE = "Good bye, %s!\n";
	private static final String LOG_OUT_TEMPLATE = "%d. Log out\n";
	private static final String ENTER_WITHDRAW_AMOUNT = "Enter the amount to withdraw funds: ";
	private static final String ENTER_CREDIT_AMOUNT = "Please enter the amount credit: ";
	private static final String ENTER_TRANSACTION_NUMBER = "Please enter transaction number: ";
	private static final String ENTER_USERNAME_PLAYER = "Enter the name of the user you want to see the logs: ";
	private static final String SHOW_LOGS = "5. Show logs";
	private static final String ENTER_USERNAME = "Enter username: ";
	private static final String ENTER_PASSWORD = "Enter password: ";
	private static final String LOG_MENU =
					"""
					1. All logs
					2. Players logs
					3. Back"
					""";
	private static final String MENU_AUTH_PLAYER =
					"""
					1. Balance
					2. Credit
					3. Debit
					4. Show transactional history
					""";

	public PlayerSessionManager(Cleaner cleaner,
								OperationChooserVerification operationChooserVerification,
								FrontController frontController, Scanner scanner, LoggerService loggerService) {
		this.cleaner = cleaner;
		this.operationChooserVerification = operationChooserVerification;
		this.frontController = frontController;
		this.scanner = scanner;
		this.loggerService = loggerService;
	}

	/**
	 * Initiates the login process by prompting users to enter a username and password.
	 * Upon successful login, provides available operations for the authorized user.
	 */
	public void logIn() {
		cleaner.cleanBuffer(scanner);

		System.out.print(ENTER_USERNAME);
		String username = scanner.nextLine();

		System.out.print(ENTER_PASSWORD);
		String password = scanner.nextLine();

		Player player = frontController.logIn(username, password);
		if (player == null) {
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
		System.out.printf(WELCOME_BACK, player.getUsername());
		int numberCommandToSelect = player.getRole() == Role.ADMIN ? 6 : 5;

		do {
			displayMenuOptions(player, numberCommandToSelect);

			int userInputValue = operationChooserVerification.userDataVerification(numberCommandToSelect);
			if (userInputValue == -1) {
				continue;
			}
			if (userInputValue == numberCommandToSelect) {
				System.out.printf(GOODBYE, player.getUsername());
				loggerService.recordActionInLog(Operation.EXIT, player, Status.SUCCESSFUL);
				break;
			}
			executeCommandAccordingUserChoice(userInputValue, player);
		}
		while (true);
	}

	/**
	 * Displays the menu options based on the player's role.
	 *
	 * @param player                The player for whom the menu options are displayed.
	 * @param numberCommandToSelect The number representing the command to log out.
	 */
	private void displayMenuOptions(Player player, int numberCommandToSelect) {
		System.out.print(MENU_AUTH_PLAYER);
		if (player.getRole() == Role.ADMIN) {
			System.out.println(SHOW_LOGS);
		}
		System.out.printf(LOG_OUT_TEMPLATE, numberCommandToSelect);
	}

	/**
	 * Executes action based on the user's choice.
	 *
	 * @param userInputValue The value representing the user's choice.
	 * @param player         Player for whom the command is executed.
	 */
	private void executeCommandAccordingUserChoice(int userInputValue, Player player) {
		switch (userInputValue) {
			case 1 -> frontController.displayPlayerBalance(player);
			case 2 -> executingCreditTransaction(player);
			case 3 -> executingDebitTransaction(player);
			case 4 -> frontController.displayPlayerTransactionalHistory(player);
			case 5 -> displayLogOptions(player);
		}
	}

	/**
	 * Displays log-related options for the player, allowing them to view all logs,
	 * view logs for specific players, or go back to the main menu.
	 *
	 * @param player Player for whom the log options are displayed.
	 */
	private void displayLogOptions(Player player) {
		boolean exit = false;
		do {
			System.out.println(LOG_MENU);

			int userInputValue = operationChooserVerification.userDataVerification(3);
			switch (userInputValue) {
				case 1 -> frontController.showAllLogs(player);
				case 2 -> {
					cleaner.cleanBuffer(scanner);
					System.out.print(ENTER_USERNAME_PLAYER);
					frontController.showLogsByUsername(player, scanner.nextLine());
				}
				case 3 -> exit = true;
			}
		}
		while (!exit);
	}

	/**
	 * Executes a credit transaction for the specified player.
	 * The method prompts the user to enter the credit amount and transaction number.
	 *
	 * @param player The player for whom the credit transaction is executed.
	 */
	private void executingCreditTransaction(Player player) {
		UserInputData inputData = processUserInput(ENTER_CREDIT_AMOUNT);
		if (inputData != null) {
			frontController.credit(player, inputData.amount, inputData.transactionToken);
		}
	}

	/**
	 * Executes a debit transaction for the specified player.
	 * The method prompts the user to enter the debit amount and transaction number.
	 *
	 * @param player The player for whom the debit transaction is executed.
	 */
	private void executingDebitTransaction(Player player) {
		UserInputData inputData = processUserInput(ENTER_WITHDRAW_AMOUNT);
		if (inputData != null) {
			frontController.debit(player, inputData.amount, inputData.transactionToken);
		}
	}

	/**
	 * Processing the user's input of the amount and transaction number.
	 *
	 * @param prompt Display the operation to the user.
	 * @return An object containing user input data (transaction amount and token).
	 */
	private UserInputData processUserInput(String prompt) {
		System.out.print(prompt);
		if (!scanner.hasNextDouble()) {
			cleaner.cleanBuffer(scanner);
			return null;
		}
		double inputPlayerAmount = scanner.nextDouble();
		System.out.print(ENTER_TRANSACTION_NUMBER);
		cleaner.cleanBuffer(scanner);
		String transactionalToken = scanner.nextLine();

		return new UserInputData(inputPlayerAmount, transactionalToken);
	}

	/**
	 * Represents the data entered by the user, including the amount and token of the transaction.
	 */
	private record UserInputData(double amount, String transactionToken) {
	}
}