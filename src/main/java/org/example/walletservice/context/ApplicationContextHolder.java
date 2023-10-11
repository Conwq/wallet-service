package org.example.walletservice.context;

import org.example.walletservice.controller.PlayerController;
import org.example.walletservice.in.MainMenu;
import org.example.walletservice.in.PlayerRegistrationHandler;
import org.example.walletservice.in.PlayerSessionManager;
import org.example.walletservice.in.util.OperationChooserVerification;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.impl.PlayerRepositoryImpl;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.impl.PlayerServiceImpl;
import org.example.walletservice.service.logger.TransactionLog;
import org.example.walletservice.util.Cleaner;
import org.example.walletservice.util.ScannerProvider;

import java.util.Scanner;

public class ApplicationContextHolder {
	private static ApplicationContextHolder instance;

	private ApplicationContextHolder(){
	}

	final Scanner scanner = ScannerProvider.getScanner();
	final Cleaner cleaner = Cleaner.getInstance();
	final TransactionLog transactionLog = TransactionLog.getInstance();
	final OperationChooserVerification operationChooserVerification = new OperationChooserVerification(scanner, cleaner);

	final PlayerRepository playerRepository = new PlayerRepositoryImpl();
	final PlayerService playerService = new PlayerServiceImpl(playerRepository, cleaner, scanner, transactionLog);
	final PlayerController playerController = new PlayerController(playerService);
	final PlayerRegistrationHandler playerRegistrationHandler = new PlayerRegistrationHandler(playerController,
			scanner, cleaner);
	final PlayerSessionManager playerSessionManager = new PlayerSessionManager(cleaner, operationChooserVerification,
			playerController, scanner, transactionLog);
	final MainMenu mainMenu = new MainMenu(playerRegistrationHandler, playerSessionManager,
			operationChooserVerification, scanner);

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

	public TransactionLog getTransactionLog() {
		return transactionLog;
	}

	public OperationChooserVerification getOperationChooserVerification() {
		return operationChooserVerification;
	}

	public PlayerRepository getPlayerRepository() {
		return playerRepository;
	}

	public PlayerService getPlayerService() {
		return playerService;
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
