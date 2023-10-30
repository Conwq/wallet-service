package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.entity.Log;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Log entity and LogResponseDto.
 */
@Mapper(componentModel = "spring")
public interface LogMapper {

	/**
	 * Converts log details and player ID to a Log entity.
	 *
	 * @param record   The log record details.
	 * @param playerID The ID of the player associated with the log.
	 * @return The Log entity.
	 */
	default Log toEntity(String record, int playerID) {
		Log log = new Log();
		log.setLog(record);
		log.setPlayerID(playerID);
		return log;
	}

	/**
	 * Converts a Log entity to a LogResponseDto.
	 *
	 * @param log The Log entity.
	 * @return The LogResponseDto.
	 */
	@Mapping(target = "record", source = "log")
	LogResponseDto toDto(Log log);
}
