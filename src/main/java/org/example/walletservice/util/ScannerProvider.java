package org.example.walletservice.util;

import java.util.Scanner;

/**
 * The class is designed to obtain one copy of Scanner.
 * It is intended to protect against the creation of unnecessary copies of objects of the Scanner type.
 */
public final class ScannerProvider {
	private static Scanner scanner;
	private ScannerProvider(){
	}

	public static Scanner getScanner(){
		if (scanner == null){
			scanner = new Scanner(System.in);
		}
		return scanner;
	}
}
