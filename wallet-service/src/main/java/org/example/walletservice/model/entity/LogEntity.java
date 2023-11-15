package org.example.walletservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data class representing a log entry.
 * This class is annotated with Lombok annotations for generating boilerplate code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "wallet_service", name = "log")
public class LogEntity {
	@Id
	@Column(name = "log_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_service.log_id_seq")
	@SequenceGenerator(name = "wallet_service.log_id_seq", sequenceName = "wallet_service.log_id_seq", allocationSize = 1)
	private int logID;
	@Column(name = "log")
	private String log;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "player_id", referencedColumnName = "player_id")
	private PlayerEntity playerEntity;

	@Override
	public String toString() {
		return log;
	}
}
