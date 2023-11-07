package ru.patseev.auditspringbootstarter.logger.config;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.patseev.auditspringbootstarter.logger.bean.LoggerAspect;
import ru.patseev.auditspringbootstarter.logger.bean.LoggerRepository;
import ru.patseev.auditspringbootstarter.logger.bean.LoggerRepositoryImpl;
import ru.patseev.auditspringbootstarter.logger.mapper.LogMapper;

@AutoConfiguration
public class LoggerConfiguration {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public LoggerConfiguration(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Bean
	public LogMapper logMapper() {
		return Mappers.getMapper(LogMapper.class);
	}

	@Bean
	public LoggerRepository loggerRepository() {
		return new LoggerRepositoryImpl(jdbcTemplate);
	}

	@Bean
	public LoggerAspect loggerAspect() {
		return new LoggerAspect(loggerRepository(), logMapper());
	}
}
