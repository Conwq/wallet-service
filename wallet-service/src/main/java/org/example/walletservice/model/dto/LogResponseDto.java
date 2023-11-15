package org.example.walletservice.model.dto;

import org.example.walletservice.model.enums.Operation;
import org.example.walletservice.model.enums.Status;

/**
 * Data transfer object representing a log response.
 */
public record LogResponseDto(Operation operation, Status status, String username) {
}
