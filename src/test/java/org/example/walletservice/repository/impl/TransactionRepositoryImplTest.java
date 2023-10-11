package org.example.walletservice.repository.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.database.CustomDatabase;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class TransactionRepositoryImplTest {
	private TransactionRepository transactionRepository;
	private final CustomDatabase customDatabase = Mockito.mock(CustomDatabase.class);
	private static final double AMOUNT = 100.0;
	private static final String TRANSACTION_TOKEN = "transaction_token";
	private Player player;

	@BeforeEach
	public void setUp(){
		transactionRepository = new TransactionRepositoryImpl(customDatabase);

		String username = "user123";
		String password = "1313";
		player = new Player(username, password, Role.USER);
		player.setBalance(AMOUNT);
	}

	@Test
	public void shouldGetPlayerBalance_successful() {
		String balance = transactionRepository.getPlayerBalance(player);
		AssertionsForClassTypes.assertThat(balance).isEqualTo(String.valueOf(AMOUNT));
	}

	@Test
	public void shouldCredit_successful(){

		transactionRepository.credit(AMOUNT, player, TRANSACTION_TOKEN);

		String balance = transactionRepository.getPlayerBalance(player);
		Map<String, String> transactionHistory = player.getTransactionalHistory();

		AssertionsForClassTypes.assertThat(balance).isEqualTo(String.valueOf(AMOUNT * 2));
		AssertionsForClassTypes.assertThat(transactionHistory.containsKey(TRANSACTION_TOKEN)).isTrue();
	}

	@Test
	public void shouldDebit_successful(){
		transactionRepository.debit(50.0, player, TRANSACTION_TOKEN);

		String balance = transactionRepository.getPlayerBalance(player);
		Map<String, String> transactionHistory = player.getTransactionalHistory();

		AssertionsForClassTypes.assertThat(balance).isEqualTo(String.valueOf(50.0));
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
		Mockito.when(customDatabase.getPlayer(player.getUsername())).thenReturn(Optional.of(player));

		Map<String, String> expected = transactionRepository.getPlayerTransactionalHistory(player.getUsername());

		AssertionsForClassTypes.assertThat(expected).isEqualTo(transactionalHistory);
	}
}