package org.example.walletservice.in.util;

import org.example.walletservice.util.Cleaner;
import org.example.walletservice.util.ScannerProvider;

import java.util.Scanner;

/**
 * The class is designed to validate user input to select one of the provided operations
 */
public final class OperationChooserVerification {
	private static OperationChooserVerification instance;
	private final Scanner scanner = ScannerProvider.getScanner();
	private final Cleaner cleaner = Cleaner.getInstance();

	private OperationChooserVerification(){
	}

	public static OperationChooserVerification getInstance(){
		if (instance == null){
			instance = new OperationChooserVerification();
		}
		return instance;
	}

	public int userDataVerification(int numberCommandsToSelect) {
		if (!scanner.hasNextInt()) {
			System.out.println("*{{FAIL}} This is not a number, try again!*\n");
			cleaner.cleanBuffer(scanner);
			return -1;
		}
		int userInputValue = scanner.nextInt();
		if (userInputValue > numberCommandsToSelect || userInputValue < 1) {
			System.out.println("*{{FAIL}} There is no such choice, try again!*\n");
			cleaner.cleanBuffer(scanner);
			return -1;
		}
		return userInputValue;
	}
}
