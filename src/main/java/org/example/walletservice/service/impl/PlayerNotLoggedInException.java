package org.example.walletservice.service.impl;

public class PlayerNotLoggedInException extends RuntimeException {

	public PlayerNotLoggedInException(String message) {
		super(message);
	}
}
