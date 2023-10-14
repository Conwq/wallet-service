package org.example.walletservice.repository.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

class TransactionRepositoryImplTest {
	private TransactionRepository transactionRepository;
	private final ConnectionProvider connectionProvider = Mockito.mock(ConnectionProvider.class);
	private static final double AMOUNT = 100.0;
	private static final String TRANSACTION_TOKEN = "transaction_token";
	private Player player;

	@BeforeEach
	public void setUp(){
		transactionRepository = new TransactionRepositoryImpl(connectionProvider);

		player = Player.builder().playerID(1).username("user123").password("1313").role(Role.USER).build();
	}

	@Test
	@Disabled
	public void shouldGetPlayerBalance_successful() {
//		Mockito.when(transactionDatabase.findPlayerBalanceByUsername(player.getUsername())).thenReturn(AMOUNT);

		double expected = transactionRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
		AssertionsForClassTypes.assertThat(expected).isEqualTo(AMOUNT);
	}

	@Test
	@Disabled
	public void shouldCredit_successful(){
		List<String> transactionHistory = new ArrayList<>();

//		Mockito.when(transactionDatabase.findPlayerBalanceByUsername(player.getUsername())).thenReturn(0.0)
//				.thenReturn(AMOUNT);
//		Mockito.when(transactionDatabase.findPlayersTransactionHistoryByUsername(player.getUsername()))
//				.thenReturn(transactionHistory);

//		transactionRepository.credit(AMOUNT, player.getUsername(), TRANSACTION_TOKEN);

//		Mockito.verify(transactionDatabase, Mockito.times(1))
//				.savePlayersNewAmountFunds(player.getUsername(), AMOUNT);
//		Mockito.verify(transactionDatabase, Mockito.times(1))
//				.saveTransactionToken(TRANSACTION_TOKEN);
//		Mockito.verify(transactionDatabase, Mockito.times(1))
//				.savePlayersTransactionHistory(player.getUsername(), transactionHistory);
	}

	@Test
	@Disabled
	public void shouldDebit_successful(){
		List<String> transactionHistory = new ArrayList<>();

//		Mockito.when(transactionDatabase.findPlayersTransactionHistoryByUsername(player.getUsername()))
//				.thenReturn(transactionHistory);
//
//		Mockito.when(transactionDatabase.findPlayerBalanceByUsername(player.getUsername())).thenReturn(AMOUNT);
//
//		transactionRepository.debit(50.0, player.getUsername(), TRANSACTION_TOKEN);

//		AssertionsForClassTypes.assertThat(AMOUNT - 50.0).isEqualTo(50.0);
//		Mockito.verify(transactionDatabase, Mockito.times(1))
//				.savePlayersNewAmountFunds(player.getUsername(), 50.0);
//		Mockito.verify(transactionDatabase, Mockito.times(1))
//				.saveTransactionToken(TRANSACTION_TOKEN);
//		Mockito.verify(transactionDatabase, Mockito.times(1))
//				.savePlayersTransactionHistory(player.getUsername(), transactionHistory);
	}

	@Test
	@Disabled
	public void shouldGetTransactionalHistory_successful(){
		List<String> transactionalHistory = new ArrayList<>(){{
			add("Transactional #1");
			add("Transactional #2");
			add("Transactional #3");
		}};

//		Mockito.when(transactionDatabase.findPlayersTransactionHistoryByUsername(player.getUsername()))
//				.thenReturn(transactionalHistory);

		List<String> expected = transactionRepository.findPlayerTransactionalHistoryByPlayerID(player.getPlayerID());

		AssertionsForClassTypes.assertThat(expected).isEqualTo(transactionalHistory);
	}
}