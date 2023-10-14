package org.example.walletservice.context;

import org.example.walletservice.controller.FrontController;
import org.example.walletservice.in.MainMenu;
import org.example.walletservice.in.PlayerRegistrationHandler;
import org.example.walletservice.in.PlayerSessionManager;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.impl.LoggerRepositoryImpl;
import org.example.walletservice.repository.impl.PlayerRepositoryImpl;
import org.example.walletservice.repository.impl.TransactionRepositoryImpl;
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.example.walletservice.repository.manager.DBResourceManager;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.impl.LoggerServiceImpl;
import org.example.walletservice.service.impl.PlayerAccessServiceImpl;
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
	final DBResourceManager resourceManager = new DBResourceManager();
	final ConnectionProvider connectionProvider = new ConnectionProvider(resourceManager);

	final PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionProvider);
	final TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionProvider);
	final LoggerRepository loggerRepository = new LoggerRepositoryImpl(connectionProvider);

	final LoggerService loggerService =
			new LoggerServiceImpl(loggerRepository, playerRepository);
	final TransactionService transactionService = new TransactionServiceImpl(scanner,
			loggerService, cleaner, transactionRepository);
	final PlayerAccessService playerAccessService =
			new PlayerAccessServiceImpl(playerRepository, loggerService);

	final FrontController frontController =
			new FrontController(playerAccessService, transactionService, loggerService);

	final PlayerRegistrationHandler playerRegistrationHandler = new PlayerRegistrationHandler(frontController,
			scanner, cleaner);
	final PlayerSessionManager playerSessionManager = new PlayerSessionManager(cleaner, operationChooserVerification,
			frontController, scanner, loggerService);
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
