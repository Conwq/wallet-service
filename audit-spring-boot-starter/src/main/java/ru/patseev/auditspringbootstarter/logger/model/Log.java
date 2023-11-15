package ru.patseev.auditspringbootstarter.logger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.patseev.auditspringbootstarter.logger.entities.Operation;
import ru.patseev.auditspringbootstarter.logger.entities.Status;

/**
 * A data class representing a log entry.
 * This class is annotated with Lombok annotations for generating boilerplate code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Log {
	private int logID;
	private Operation operation;
	private Status status;
}