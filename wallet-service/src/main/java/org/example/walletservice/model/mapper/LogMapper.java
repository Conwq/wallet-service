package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.ent.entity.LogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Log entity and LogResponseDto.
 */
@Mapper(componentModel = "spring")
public interface LogMapper {

	/**
	 * Converts a Log entity to a LogResponseDto.
	 *
	 * @param logEntity The Log entity.
	 * @return The LogResponseDto.
	 */
	@Mapping(target = "record", source = "log")
	LogResponseDto toDto(LogEntity logEntity);
}
