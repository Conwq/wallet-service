package org.example.walletservice.model.dto;

/**
 * Data transfer object representing an information response.
 *
 * @param status  The status code of the response.
 * @param message The message included in the response.
 */
public record InfoResponse(String createAt, int status, String message) {
}
