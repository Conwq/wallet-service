package ru.patseev.auditspringbootstarter.logger.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.patseev.auditspringbootstarter.logger.entities.Operation;
import ru.patseev.auditspringbootstarter.logger.entities.Status;
import ru.patseev.auditspringbootstarter.logger.model.Log;

/**
 * Mapper for converting between Log entity and LogResponseDto.
 */
@Mapper(componentModel = "spring")
public interface LogMapper {

	/**
	 * Converts log details and player ID to a Log entity.
	 *
	 * @return The Log entity.
	 */
	@Mapping(target = "operation", source = "operation")
	@Mapping(target = "status", source = "status")
	Log toEntity(Operation operation, Status status);
}
