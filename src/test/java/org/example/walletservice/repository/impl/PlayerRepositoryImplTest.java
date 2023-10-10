package org.example.walletservice.repository.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.RepositoryProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class PlayerRepositoryImplTest {
	private  PlayerRepository playerRepository;
	private static final double AMOUNT = 100.0;
	private static final String TRANSACTION_TOKEN = "transaction_token";
	private Player player;

	@BeforeEach
	public void setUp(){
		playerRepository = RepositoryProvider.getInstance().getPlayerRepository();

		String username = "user123";
		String password = "1313";
		player = new Player(username, password, Role.USER);
	}

	@Test
	public void shouldFindPlayer_returnPlayer(){
		playerRepository.registrationPayer(player);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());

		AssertionsForClassTypes.assertThat(optionalPlayer).contains(player);
	}

	@Test
	public void shouldFindPlayer_returnEmptyPlayer(){
		Optional<Player> optionalPlayer = playerRepository.findPlayer("testing");

		AssertionsForClassTypes.assertThat(optionalPlayer).isEmpty();
	}

	@Test
	public void shouldRegistrationPlayer_successful(){
		playerRepository.registrationPayer(player);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());

		AssertionsForClassTypes.assertThat(optionalPlayer).contains(player);
	}

	@Test
	public void shouldGetPlayerBalance_successful() {
		player.setBalance(AMOUNT);
		playerRepository.registrationPayer(player);
		String balance = playerRepository.getPlayerBalance(player);

		AssertionsForClassTypes.assertThat(balance).isEqualTo(String.valueOf(AMOUNT));
	}

	@Test
	public void shouldCredit_successful(){
		playerRepository.registrationPayer(player);
		playerRepository.credit(AMOUNT, player, TRANSACTION_TOKEN);

		String balance = playerRepository.getPlayerBalance(player);
		Map<String, String> transactionHistory = player.getTransactionalHistory();

		AssertionsForClassTypes.assertThat(balance).isEqualTo(String.valueOf(AMOUNT));
		AssertionsForClassTypes.assertThat(transactionHistory.containsKey(TRANSACTION_TOKEN)).isTrue();
	}

	@Test
	public void shouldDebit_successful(){
		playerRepository.registrationPayer(player);
		playerRepository.debit(0.0, player, TRANSACTION_TOKEN);
		String balance = playerRepository.getPlayerBalance(player);
		Map<String, String> transactionHistory = player.getTransactionalHistory();

		AssertionsForClassTypes.assertThat(balance).isEqualTo(String.valueOf(0.0));
		AssertionsForClassTypes.assertThat(transactionHistory.containsKey(TRANSACTION_TOKEN)).isTrue();
	}

	@Test
	public void shouldGetTransactionalHistory_successful(){
		Map<String, String> transactionalHistory = new HashMap<>(){{
			put("#1", "Transactional #1");
			put("#2", "Transactional #2");
			put("#3", "Transactional #3");
		}};
		player.setTransactionalHistory(transactionalHistory);
		playerRepository.registrationPayer(player);

		Map<String, String> expected = playerRepository.getPlayerTransactionalHistory(player.getUsername());

		AssertionsForClassTypes.assertThat(expected).isEqualTo(transactionalHistory);
	}
}