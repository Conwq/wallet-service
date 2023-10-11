package org.example.walletservice;

import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.in.MainMenu;

/**
 * The main class for the Wallet Service application.
 * Starts the main menu of the application.
 */
public class WalletServiceApplication {
	private static final ApplicationContextHolder context = ApplicationContextHolder.getInstance();

	public static void main(String[] args) {
		MainMenu mainMenu = new MainMenu(context.getPlayerRegistrationHandler(),
				context.getPlayerSessionManager(),
				context.getOperationChooserVerification(),
				context.getScanner());

		mainMenu.start();
	}
}