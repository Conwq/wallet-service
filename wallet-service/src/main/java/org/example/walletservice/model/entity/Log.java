package org.example.walletservice.model.entity;

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
public class Log {
	private int logID;
	private String log;
	private int playerID;
}
