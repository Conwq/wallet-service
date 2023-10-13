package org.example.walletservice.util;

import java.util.Scanner;

/**
 * This class is designed to clear extra characters after using the Scanner class.
 */
public final class Cleaner {

	/**
	 * The method takes a parameter of type Scanner and clears its line strings.
	 */
	public void cleanBuffer(Scanner scanner) {
		if (scanner.hasNextLine()) {
			scanner.nextLine();
		}
	}
}
