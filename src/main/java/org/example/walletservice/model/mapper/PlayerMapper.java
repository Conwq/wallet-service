package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlayerMapper {
	PlayerMapper instance = Mappers.getMapper(PlayerMapper.class);

	@Mapping(target = "username", source = "username")
	@Mapping(target = "password", source = "password")
	Player toEntityFromRequest(PlayerRequestDto playerRequestDto);

	@Mapping(target = "playerID", source = "playerID")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "role", source = "role")
	AuthPlayerDto toAuthPlayerDto(Player player);

	@Mapping(target = "playerID", source = "playerID")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "role", source = "role")
	Player toEntity(AuthPlayerDto authPlayerDto);
}
