package org.example.walletservice.service;

import org.example.walletservice.service.impl.PlayerServiceImpl;

/**
 * Provider is used to get all service layer objects.
 */
public final class ServiceProvider {
	private static ServiceProvider serviceProvider;
	private final PlayerService playerService = PlayerServiceImpl.getInstance();

	private ServiceProvider(){
	}

	public static ServiceProvider getInstant(){
		if (serviceProvider == null){
			serviceProvider = new ServiceProvider();
		}
		return serviceProvider;
	}

	public PlayerService getPlayerService() {
		return playerService;
	}
}
