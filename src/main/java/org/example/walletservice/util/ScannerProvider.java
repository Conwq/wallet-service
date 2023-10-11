package org.example.walletservice.util;

import java.util.Scanner;

/**
 * The class is designed to obtain one copy of Scanner.
 * It is intended to protect against the creation of unnecessary copies of objects of the Scanner type.
 * Class singleton with lazy initialization
 */
public final class ScannerProvider {
	private static Scanner scanner;

	private ScannerProvider(){
	}

	/**
	 * The method returns a single instance of the Scanner type.
	 * If the instance has not yet been created, a new instance is created,
	 * otherwise the existing instance is returned
	 *
	 * @return a single instance of type Scanner
	 */
	public static Scanner getScanner(){
		if (scanner == null){
			scanner = new Scanner(System.in);
		}
		return scanner;
	}
}
