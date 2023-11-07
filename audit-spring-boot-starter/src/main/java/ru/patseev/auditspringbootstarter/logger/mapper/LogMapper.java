package ru.patseev.auditspringbootstarter.logger.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.patseev.auditspringbootstarter.logger.model.Log;

/**
 * Mapper for converting between Log entity and LogResponseDto.
 */
@Mapper(componentModel = "spring")
public interface LogMapper {

	/**
	 * Converts log details and player ID to a Log entity.
	 *
	 * @param record The log record details.
	 * @return The Log entity.
	 */
	@Mapping(target = "log", source = "record")
	Log toEntity(String record);
}
