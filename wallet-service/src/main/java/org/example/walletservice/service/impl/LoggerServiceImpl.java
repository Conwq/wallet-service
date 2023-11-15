package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.entity.PlayerEntity;
import org.example.walletservice.model.mapper.LogMapper;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoggerServiceImpl implements LoggerService {
	private final LoggerRepository loggerRepository;
	private final PlayerRepository playerRepository;
	private final LogMapper logMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getAllLogs()
			throws PlayerNotLoggedInException, PlayerDoesNotHaveAccessException {
		return loggerRepository.findAll().stream().map(logMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getLogsByUsername(String inputUsernameForSearch)
			throws PlayerNotFoundException, PlayerNotLoggedInException, PlayerDoesNotHaveAccessException {

		PlayerEntity foundPlayer = playerRepository.findByUsername(inputUsernameForSearch)
				.orElseThrow(() -> new PlayerNotFoundException(String.format("Player %s not found", inputUsernameForSearch)));
		return foundPlayer.getLogEntity().stream().map(logMapper::toDto).collect(Collectors.toList());
	}
}