package org.example.walletservice.in.util;

import org.example.walletservice.util.Cleaner;

import java.util.Scanner;

/**
 * The class is designed to validate user input to select one of the provided operations
 */
public final class OperationChooserVerification {
	private final Scanner scanner;
	private final Cleaner cleaner;

	public OperationChooserVerification(Scanner scanner, Cleaner cleaner) {
		this.scanner = scanner;
		this.cleaner = cleaner;
	}

	/**
	 * Verifies and retrieves user input for menu commands.
	 *
	 * @param numberCommandsToSelect The maximum number of commands a user can select.
	 * @return The user input value if valid, or -1 if input is invalid.
	 */
	public int userDataVerification(int numberCommandsToSelect) {
		if (!scanner.hasNextInt()) {
			System.out.println("\n*{{FAIL}} This is not a number, try again!*\n");
			cleaner.cleanBuffer(scanner);
			return -1;
		}
		int userInputValue = scanner.nextInt();
		if (userInputValue > numberCommandsToSelect || userInputValue < 1) {
			System.out.println("\n*{{FAIL}} There is no such choice, try again!*\n");
			cleaner.cleanBuffer(scanner);
			return -1;
		}
		return userInputValue;
	}
}
