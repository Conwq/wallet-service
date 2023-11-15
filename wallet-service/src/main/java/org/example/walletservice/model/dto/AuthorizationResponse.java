package org.example.walletservice.model.dto;

/**
 * Represents the response of an authorization request.
 *
 * This record contains information such as the creation timestamp, the authentication token,
 * and an optional message providing additional details about the authorization process.
 *
 * @param createAt The timestamp indicating when the authorization response was created.
 * @param token The authentication token associated with the authorization.
 * @param message An optional message providing additional details about the authorization process.
 */
public record AuthorizationResponse (String createAt, String token, String message) {
}
