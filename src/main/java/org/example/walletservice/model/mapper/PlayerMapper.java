package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface PlayerMapper {
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
