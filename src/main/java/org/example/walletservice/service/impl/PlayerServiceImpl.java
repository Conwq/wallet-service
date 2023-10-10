package org.example.walletservice.service.impl;

import org.example.walletservice.model.Role;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.util.Cleaner;
import org.example.walletservice.util.ScannerProvider;
import org.example.walletservice.model.Player;
import org.example.walletservice.service.logger.TransactionLog;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.RepositoryProvider;
import org.example.walletservice.service.PlayerService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Implementation of the {@link PlayerService} interface that provides functionality
 * for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
public final class PlayerServiceImpl implements PlayerService {
	private static final PlayerService instance = new PlayerServiceImpl();
	private final Cleaner cleaner = Cleaner.getInstance();
	private PlayerRepository playerRepository = RepositoryProvider.getInstance().getPlayerRepository();
	private Scanner scanner = ScannerProvider.getScanner();
	private TransactionLog transactionLog = TransactionLog.getInstance();

	private PlayerServiceImpl(){
	}

	public PlayerServiceImpl(PlayerRepository playerRepository, Scanner scanner, TransactionLog transactionLog) {
		this.playerRepository = playerRepository;
		this.scanner = scanner;
		this.transactionLog = transactionLog;
	}

	/**
	 * Gets the singleton instance of {@code PlayerServiceImpl}.
	 *
	 * @return The singleton instance.
	 */
	public static PlayerService getInstance(){
		return instance;
	}

	/**
	 * The method takes as input the parameters with which the user wants to register.
	 * The user is being checked for users with the same username.
	 * If the user is not found, then a new user  is created and a record of successful registration
	 * is recorded in the log.
	 * If the user has this username, then a response about the existence of
	 * a user with the selected username is returned and this operation is recorded
	 * in the log with the status FAIL.
	 */
	@Override
	public void registrationPlayer(String username, String password){
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if(optionalPlayer.isPresent()){
			System.out.println("\n*{{FAIL}} This user is already registered. Try again.*\n");

			transactionLog.recordTransaction(Operation.REGISTRATION, "UNKNOWN", Status.FAIL);
			return;
		}
		Player player = new Player(username, password, Role.USER);
		playerRepository.registrationPayer(player);
		System.out.println("\n*User successfully registered!*\n");

		transactionLog.recordTransaction(Operation.REGISTRATION, username, Status.SUCCESSFUl);
	}

	/**
	 * Log in a player with the input username and password.
	 *
	 * @param username The username of the player.
	 * @param password The password of the player.
	 * @return The logged-in player if successful, or {@code null} if the login fails.
	 */
	@Override
	public Player logIn(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isEmpty()){
			System.out.println("\n*{{FAIL}} Current player not found. Please try again.*\n");

			transactionLog.recordTransaction(Operation.LOG_IN, "UNKNOWN", Status.FAIL);
			return null;
		}

		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(password)){
			System.out.println("\n*{{FAIL}} Incorrect password!*\n");

			transactionLog.recordTransaction(Operation.LOG_IN, username, Status.FAIL);
			return null;
		}
		transactionLog.recordTransaction(Operation.LOG_IN, username, Status.SUCCESSFUl);
		return player;
	}

	/**
	 * Displays the balance of the specified player.
	 *
	 * @param player The player for whom to retrieve the balance.
	 */
	@Override
	public void getPlayerBalance(Player player) {
		String balance = playerRepository.getPlayerBalance(player);
		System.out.printf("\n*Balance -- %s*\n\n", balance);

		transactionLog.recordTransaction(Operation.VIEW_BALANCE, player.getUsername(), Status.SUCCESSFUl);
	}

	/**
	 * Replenishes the player's account.
	 *
	 * @param player A player who performs a credit operation.
	 */
	@Override
	public void credit(Player player) {
		System.out.print("Please enter the amount credit: ");

		double amountTransaction;
		String transactionalToken;
		if (scanner.hasNextDouble() && ((amountTransaction = scanner.nextDouble()) >= 0.0)
				&& (transactionalToken = checkingEnteredUserToken()) != null) {

			playerRepository.credit(amountTransaction, player, transactionalToken);
			System.out.println("\n*Credit successfully.*\n");

			transactionLog.recordTransaction(Operation.CREDIT, player.getUsername(), Status.SUCCESSFUl);
		}
		else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
			transactionLog.recordTransaction(Operation.CREDIT, player.getUsername(), Status.FAIL);
		}
	}

	/**
	 *Withdraw the desired amount of funds from your account.
	 *
	 * @param player A player performing a write-off operation.
	 */
	@Override
	public void debit(Player player) {
		System.out.print("Enter the amount to withdraw funds: ");

		double inputPlayerAmount;
		String transactionalToken;
		if (scanner.hasNextDouble() && ((inputPlayerAmount = scanner.nextDouble()) >= 0.0)
				&& (transactionalToken = checkingEnteredUserToken()) != null){

			if (player.getBalance() - inputPlayerAmount < 0.0){
				System.out.println("\n*{{FAIL}} There are not enough funds in the account!*\n");

				transactionLog.recordTransaction(Operation.DEBIT, player.getUsername(), Status.FAIL);
				return;
			}
			playerRepository.debit(inputPlayerAmount, player, transactionalToken);
			System.out.println("\n*Debit successfully.*\n");

			transactionLog.recordTransaction(Operation.DEBIT, player.getUsername(), Status.SUCCESSFUl);
		}
		else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
			transactionLog.recordTransaction(Operation.DEBIT, player.getUsername(), Status.FAIL);
		}
	}

	/**
	 * Displays the transactional history of a player.
	 *
	 * @param player The player for whom display the transactional history.
	 */
	@Override
	public void getPlayerTransactionalHistory(Player player) {
		Map<String, String> playerTransactionalHistory =
				playerRepository.getPlayerTransactionalHistory(player.getUsername());

		if (playerTransactionalHistory.isEmpty()){
			System.out.println("\n**********************");
			System.out.println("Transactions is empty.");
			System.out.println("**********************\n");
			return;
		}

		for (Map.Entry<String, String> transaction : playerTransactionalHistory.entrySet()) {
			System.out.println(transaction.getValue());
		}
		transactionLog.recordTransaction(Operation.TRANSACTIONAL_HISTORY, player.getUsername(), Status.SUCCESSFUl);
	}

	/**
	 * Displays all logs for players.
	 */
	@Override
	public void showAllLogs(Player player) {
		List<String> allLogs = transactionLog.getAllTransactionRecords();

		if (allLogs.isEmpty()){
			System.out.println("\n*No logs*\n");
			transactionLog.recordTransaction(Operation.SHOW_ALL_LOGS, player.getUsername(), Status.FAIL);
			return;
		}

		for (String record : allLogs){
			System.out.println(record);
		}
		System.out.println();
		transactionLog.recordTransaction(Operation.SHOW_ALL_LOGS, player.getUsername(), Status.SUCCESSFUl);
	}

	/**
	 * Displays player logs.
	 * @param player Whoever uses the search function
	 * @param inputUsernameForSearch Username of who we're looking at logs
	 */
	@Override
	public void showLogsByUsername(Player player, String inputUsernameForSearch) {
		List<String> playerLogs = transactionLog.getLogsForPlayer(inputUsernameForSearch);
		if (playerLogs == null){
			System.out.printf("\n*Player %s not found*\n", inputUsernameForSearch);
			return;
		}
		if (playerLogs.isEmpty()){
			System.out.printf("\n*No logs for player %s*\n", inputUsernameForSearch);
			return;
		}

		for (String record : playerLogs){
			System.out.println(record);
		}
		System.out.println();
		transactionLog.recordTransaction(Operation.SHOW_LOGS_PLAYER, player.getUsername(), Status.SUCCESSFUl);
	}

	/**
	 * Checks the user's entered transaction number for uniqueness.
	 *
	 * @return transaction number or {@code null} if it already exists.
	 */
	private String checkingEnteredUserToken(){
		cleaner.cleanBuffer(scanner);
		System.out.print("Please enter transaction number: ");
		String transactionalToken = scanner.nextLine();

		if(playerRepository.checkTokenExistence(transactionalToken)) {
			return null;
		}
		return transactionalToken;
	}
}