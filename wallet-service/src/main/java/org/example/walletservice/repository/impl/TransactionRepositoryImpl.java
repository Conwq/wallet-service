package org.example.walletservice.repository.impl;

import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public TransactionRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Transaction> findPlayerTransactionalHistory(Player player) {
		final String queryToReceiveTransactions = """
				SELECT * FROM wallet_service.transaction WHERE player_id = ?
				""";

		return jdbcTemplate.query(queryToReceiveTransactions, (rs, rowNum) ->
						new Transaction(
								rs.getInt("transaction_id"),
								rs.getString("record"),
								rs.getString("token"),
								rs.getString("operation"),
								rs.getBigDecimal("amount"),
								rs.getInt("player_id")),
				player.getPlayerID());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkTokenExistence(String transactionalToken) {
		final String tokenRequest = """
				SELECT token FROM wallet_service.transaction WHERE token = ?
				""";
		try {
			jdbcTemplate.queryForObject(
					tokenRequest,
					String.class,
					transactionalToken);
			return true;

		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void creditOrDebit(Transaction transaction, BigDecimal newPlayerAmount) {
		final String requestToUpdatePlayerBalance = """
				UPDATE wallet_service.players SET balance = ? WHERE player_id = ?
				""";

		jdbcTemplate.update(requestToUpdatePlayerBalance, ps -> {
			ps.setBigDecimal(1, newPlayerAmount);
			ps.setInt(2, transaction.getPlayerID());
		});

		recordTransactionInPlayerHistory(transaction);
	}

	/**
	 * Records a transaction in the player's transaction history in the database.
	 *
	 * @param transaction The Transaction object containing details of the transaction.
	 */
	private void recordTransactionInPlayerHistory(Transaction transaction) {
		final String requestToAddTransaction = """
				INSERT INTO wallet_service.transaction (record, token, operation, amount, player_id)
				VALUES (?, ?, ?, ?, ?)
				""";

		jdbcTemplate.update(requestToAddTransaction, ps -> {
			ps.setString(1, transaction.getRecord());
			ps.setString(2, transaction.getToken());
			ps.setString(3, transaction.getOperation());
			ps.setBigDecimal(4, transaction.getAmount());
			ps.setInt(5, transaction.getPlayerID());
		});
	}
}