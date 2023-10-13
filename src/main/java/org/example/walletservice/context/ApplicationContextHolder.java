package org.example.walletservice.context;

import org.example.walletservice.controller.FrontController;
import org.example.walletservice.database.PlayerActionLoggerDatabase;
import org.example.walletservice.database.PlayerDatabase;
import org.example.walletservice.database.TransactionDatabase;
import org.example.walletservice.in.MainMenu;
import org.example.walletservice.in.PlayerRegistrationHandler;
import org.example.walletservice.in.PlayerSessionManager;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.repository.PlayerActionLoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.impl.PlayerActionLoggerRepositoryImpl;
import org.example.walletservice.repository.impl.PlayerRepositoryImpl;
import org.example.walletservice.repository.impl.TransactionRepositoryImpl;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.PlayerActionLoggerService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.impl.PlayerAccessServiceImpl;
import org.example.walletservice.service.impl.PlayerActionLoggerServiceImpl;
import org.example.walletservice.service.impl.TransactionServiceImpl;
import org.example.walletservice.util.Cleaner;

import java.util.Scanner;

/**
 * Singleton class responsible for managing the application context.
 */
public class ApplicationContextHolder {
	private static ApplicationContextHolder instance;

	final Scanner scanner = new Scanner(System.in);
	final Cleaner cleaner = new Cleaner();

	final OperationChooserVerification operationChooserVerification =
			new OperationChooserVerification(scanner, cleaner);

	final PlayerDatabase playerDatabase = new PlayerDatabase();
	final TransactionDatabase transactionDatabase = new TransactionDatabase();
	final PlayerActionLoggerDatabase playerActionLoggerDatabase = new PlayerActionLoggerDatabase();

	final PlayerRepository playerRepository = new PlayerRepositoryImpl(playerDatabase, transactionDatabase);
	final TransactionRepository transactionRepository = new TransactionRepositoryImpl(transactionDatabase);
	final PlayerActionLoggerRepository playerActionLoggerRepository =
			new PlayerActionLoggerRepositoryImpl(playerActionLoggerDatabase);


	final PlayerActionLoggerServiceImpl playerActionLoggerService2 =
			new PlayerActionLoggerServiceImpl(playerActionLoggerRepository);
	final TransactionService transactionService = new TransactionServiceImpl(scanner,
			playerActionLoggerService2, cleaner, transactionRepository);
	final PlayerAccessService playerAccessService =
			new PlayerAccessServiceImpl(playerRepository, playerActionLoggerService2);
	final PlayerActionLoggerService playerActionLoggerService =
			new PlayerActionLoggerServiceImpl(playerActionLoggerRepository);
	final FrontController frontController =
			new FrontController(playerAccessService, transactionService, playerActionLoggerService);

	final PlayerRegistrationHandler playerRegistrationHandler = new PlayerRegistrationHandler(frontController,
			scanner, cleaner);
	final PlayerSessionManager playerSessionManager = new PlayerSessionManager(cleaner, operationChooserVerification,
			frontController, scanner, playerActionLoggerService);
	final MainMenu mainMenu = new MainMenu(playerRegistrationHandler, playerSessionManager,
			operationChooserVerification, scanner);

	private ApplicationContextHolder() {
	}

	public static ApplicationContextHolder getInstance() {
		if (instance == null) {
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

	public PlayerActionLoggerServiceImpl getTransactionLog() {
		return playerActionLoggerService2;
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

	public FrontController getPlayerController() {
		return frontController;
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
