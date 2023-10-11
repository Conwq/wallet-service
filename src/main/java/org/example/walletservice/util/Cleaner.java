package org.example.walletservice.util;

import java.util.Scanner;

/**
 * This class is designed to clear extra characters after using the Scanner class.
 * Class singleton with lazy initialization.
 */
public final class Cleaner {
	private static Cleaner instance;

	private Cleaner(){
	}

	/**
	 * The method returns a single instance of the Cleaner type.
	 * If the instance has not yet been created, a new instance is created,
	 * otherwise the existing instance is returned
	 *
	 * @return a single instance of type Cleaner
	 */
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
