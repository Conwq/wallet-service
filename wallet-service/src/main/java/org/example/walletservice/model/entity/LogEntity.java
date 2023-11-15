package org.example.walletservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.walletservice.model.enums.Operation;
import ru.patseev.auditspringbootstarter.logger.entities.Status;

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
	@Column(name = "operation")
	@Enumerated(EnumType.STRING)
	private Operation operation;
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "player_id", referencedColumnName = "player_id")
	private PlayerEntity playerEntity;
}
