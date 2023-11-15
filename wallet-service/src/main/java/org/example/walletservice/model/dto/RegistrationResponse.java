package org.example.walletservice.model.dto;

/**
 * Data transfer object representing a registration response.
 *
 * @param createAt The timestamp of the response.
 * @param message  The message included in the response.
 */
public record RegistrationResponse(String createAt, String message) {
}
