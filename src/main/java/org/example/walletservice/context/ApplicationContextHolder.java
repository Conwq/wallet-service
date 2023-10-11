package org.example.walletservice.context;

import org.example.walletservice.controller.PlayerController;
import org.example.walletservice.database.CustomDatabase;
import org.example.walletservice.in.MainMenu;
import org.example.walletservice.in.PlayerRegistrationHandler;
import org.example.walletservice.in.PlayerSessionManager;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.impl.PlayerRepositoryImpl;
import org.example.walletservice.repository.impl.TransactionRepositoryImpl;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.impl.PlayerAccessServiceImpl;
import org.example.walletservice.service.impl.TransactionServiceImpl;
import org.example.walletservice.service.logger.PlayerActivityLogger;
import org.example.walletservice.util.Cleaner;
import org.example.walletservice.util.ScannerProvider;

import java.util.Scanner;

/**
 * Singleton class responsible for managing the application context.
 */
public class ApplicationContextHolder {
	private static ApplicationContextHolder instance;

	final Scanner scanner = ScannerProvider.getScanner();
	final Cleaner cleaner = Cleaner.getInstance();
	final PlayerActivityLogger playerActivityLogger = PlayerActivityLogger.getInstance();
	final OperationChooserVerification operationChooserVerification =
			new OperationChooserVerification(scanner, cleaner);

	final CustomDatabase customDatabase = CustomDatabase.getInstance();
	final PlayerRepository playerRepository = new PlayerRepositoryImpl(customDatabase);
	final TransactionRepository transactionRepository = new TransactionRepositoryImpl(customDatabase);
	final TransactionService transactionService = new TransactionServiceImpl(scanner,
			playerActivityLogger, cleaner, transactionRepository);
	final PlayerAccessService playerAccessService = new PlayerAccessServiceImpl(playerRepository, playerActivityLogger);
	final PlayerController playerController = new PlayerController(playerAccessService, transactionService);

	final PlayerRegistrationHandler playerRegistrationHandler = new PlayerRegistrationHandler(playerController,
			scanner, cleaner);
	final PlayerSessionManager playerSessionManager = new PlayerSessionManager(cleaner, operationChooserVerification,
			playerController, scanner, playerActivityLogger);
	final MainMenu mainMenu = new MainMenu(playerRegistrationHandler, playerSessionManager,
			operationChooserVerification, scanner);

	private ApplicationContextHolder(){
	}

	public static ApplicationContextHolder getInstance(){
		if (instance == null){
			instance = new ApplicationContextHolder();
		}
		return instance;
	}

	public Scanner getScanner() {
		return scanner;
	}

	public Cleaner getCleaner() {
		return cleaner;
	}

	public PlayerActivityLogger getTransactionLog() {
		return playerActivityLogger;
	}

	public OperationChooserVerification getOperationChooserVerification() {
		return operationChooserVerification;
	}

	public PlayerRepository getPlayerRepository() {
		return playerRepository;
	}

	public PlayerAccessService getPlayerService() {
		return playerAccessService;
	}

	public PlayerController getPlayerController() {
		return playerController;
	}

	public PlayerRegistrationHandler getPlayerRegistrationHandler() {
		return playerRegistrationHandler;
	}

	public PlayerSessionManager getPlayerSessionManager() {
		return playerSessionManager;
	}

	public MainMenu getMainMenu() {
		return mainMenu;
	}
}
