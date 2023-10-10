package org.example.walletservice.util;

import java.util.Scanner;

/**
 * This method is designed to clear extra characters after using the Scanner class.
 */
public final class Cleaner {
	private static Cleaner instance;

	private Cleaner(){
	}

	public static Cleaner getInstance(){
		if (instance == null){
			instance = new Cleaner();
		}
		return instance;
	}


	/**
	 * The method takes a parameter of type Scanner and clears its line strings.
	 */
	public void cleanBuffer(Scanner scanner){
		if (scanner.hasNextLine()){
			scanner.nextLine();
		}
	}
}
