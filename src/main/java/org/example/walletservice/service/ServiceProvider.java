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

	/**
	 * The method returns a single instance of the ServiceProvider type.
	 * If the instance has not yet been created, a new instance is created,
	 * otherwise the existing instance is returned
	 *
	 * @return a single instance of type ServiceProvider
	 */
	public static ServiceProvider getInstant(){
		if (serviceProvider == null){
			serviceProvider = new ServiceProvider();
		}
		return serviceProvider;
	}

	/**
	 * A method that returns a finished instance of the PlayerService class.
	 * @return A ready-to-use instance of the PlayerService class.
	 */
	public PlayerService getPlayerService() {
		return playerService;
	}
}
