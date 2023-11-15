package ru.patseev.auditspringbootstarter.logger.config;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.patseev.auditspringbootstarter.logger.aspect.LoggerAspect;
import ru.patseev.auditspringbootstarter.logger.mapper.LogMapper;
import ru.patseev.auditspringbootstarter.logger.repository.LoggerRepository;
import ru.patseev.auditspringbootstarter.logger.repository.impl.LoggerRepositoryImpl;
import ru.patseev.auditspringbootstarter.logger.repository.impl.PlayRepository;

/**
 * Configuration class for the Logger module.
 */
@AutoConfiguration
public class LoggerConfiguration {

	private final JdbcTemplate jdbcTemplate;

	/**
	 * Constructor to inject dependencies.
	 *
	 * @param jdbcTemplate The JDBC template for database interactions.
	 */
	@Autowired
	public LoggerConfiguration(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Bean definition for the LogMapper.
	 *
	 * @return An instance of the LogMapper.
	 */
	@Bean
	public LogMapper logMapper() {
		return Mappers.getMapper(LogMapper.class);
	}

	/**
	 * Bean definition for the LoggerRepository.
	 *
	 * @return An instance of the LoggerRepository.
	 */
	@Bean
	public LoggerRepository loggerRepositoryImpl() {
		return new LoggerRepositoryImpl(jdbcTemplate);
	}

	/**
	 * Creates and configures a PlayRepository bean.
	 *
	 * @return The configured PlayRepository bean.
	 */
	@Bean
	public PlayRepository playRepository() {
		return new PlayRepository(jdbcTemplate);
	}

	/**
	 * Bean definition for the LoggerAspect.
	 *
	 * @return An instance of the LoggerAspect.
	 */
	@Bean
	public LoggerAspect loggerAspect() {
		return new LoggerAspect(loggerRepositoryImpl(), logMapper(), playRepository());
	}
}
