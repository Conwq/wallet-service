package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.entity.Log;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LogMapper {
	LogMapper instance = Mappers.getMapper(LogMapper.class);

	default Log toEntity(String record, int playerID){
		Log log = new Log();
		log.setLog(record);
		log.setPlayerID(playerID);
		return log;
	}

	@Mapping(target = "record", source = "log")
	LogResponseDto toDto(Log log);
}
