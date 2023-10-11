package org.example.walletservice.repository.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class PlayerRepositoryImplTest {
	private  PlayerRepository playerRepository;
	private Player player;

	@BeforeEach
	public void setUp(){
		playerRepository = ApplicationContextHolder.getInstance().getPlayerRepository();

		String username = "user123";
		String password = "1313";
		player = new Player(username, password, Role.USER);
	}

	@Test
	public void shouldFindPlayer_returnPlayer(){
		playerRepository.registrationPayer(player);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());

		AssertionsForClassTypes.assertThat(optionalPlayer).contains(player);
	}

	@Test
	public void shouldFindPlayer_returnEmptyPlayer(){
		Optional<Player> optionalPlayer = playerRepository.findPlayer("testing");

		AssertionsForClassTypes.assertThat(optionalPlayer).isEmpty();
	}

	@Test
	public void shouldRegistrationPlayer_successful(){
		playerRepository.registrationPayer(player);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());

		AssertionsForClassTypes.assertThat(optionalPlayer).contains(player);
	}
}