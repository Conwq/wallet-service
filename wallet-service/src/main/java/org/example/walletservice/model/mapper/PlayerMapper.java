package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Player entity and AuthPlayerDto.
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper {

	/**
	 * Converts PlayerRequestDto to a Player entity.
	 *
	 * @param playerRequestDto The PlayerRequestDto.
	 * @return The Player entity.
	 */
	@Mapping(target = "username", source = "username")
	@Mapping(target = "password", source = "password")
	Player toEntityFromRequest(PlayerRequestDto playerRequestDto);

	/**
	 * Converts Player entity to AuthPlayerDto.
	 *
	 * @param player The Player entity.
	 * @return The AuthPlayerDto.
	 */
	@Mapping(target = "playerID", source = "playerID")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "role", source = "role")
	AuthPlayerDto toAuthPlayerDto(Player player);

	/**
	 * Converts AuthPlayerDto to a Player entity.
	 *
	 * @param authPlayerDto The AuthPlayerDto.
	 * @return The Player entity.
	 */
	@Mapping(target = "playerID", source = "playerID")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "role", source = "role")
	Player toEntity(AuthPlayerDto authPlayerDto);
}
