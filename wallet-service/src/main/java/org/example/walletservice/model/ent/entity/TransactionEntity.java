package org.example.walletservice.model.ent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.walletservice.model.enums.Operation;

import java.math.BigDecimal;

/**
 * A data class representing a transaction.
 * This class is annotated with Lombok annotations for generating boilerplate code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "wallet_service", name = "transaction")
public class TransactionEntity {
	@Id
	@Column(name = "transaction_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq")
	@SequenceGenerator(name = "transaction_id_seq", sequenceName = "transaction_id_seq", allocationSize = 1)
	private int transactionID;
	@Column(name = "token")
	private String token;
	@Column(name = "operation")
	private String operation;
	@Column(name = "amount")
	private BigDecimal amount;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "player_id")
	private PlayerEntity playerEntity;

	@Override
	public String toString() {
		return "";
	}
}
