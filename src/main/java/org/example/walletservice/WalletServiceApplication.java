package org.example.walletservice;

import org.example.walletservice.in.MainMenu;

/**
 * The main class for the Wallet Service application.
 * Starts the main menu of the application.
 */
public class WalletServiceApplication {

	public static void main(String[] args) {
		MainMenu.getInstance().start();
	}
}