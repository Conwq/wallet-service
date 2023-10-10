package org.example.walletservice.repository;

import org.example.walletservice.repository.impl.PlayerRepositoryImpl;

/**
 * Provider is used to get all repositories layer objects.
 */
public final class RepositoryProvider {
	private static RepositoryProvider repositoryProvider;
	private final PlayerRepository playerRepository = PlayerRepositoryImpl.getInstance();

	private RepositoryProvider(){
	}

	public static RepositoryProvider getInstance(){
		if (repositoryProvider == null){
			repositoryProvider = new RepositoryProvider();
		}
		return repositoryProvider;
	}

	public PlayerRepository getPlayerRepository() {
		return playerRepository;
	}
}
