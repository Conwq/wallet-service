package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Player entity and AuthPlayer.
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
	 * Converts Player entity to AuthPlayer.
	 *
	 * @param player The Player entity.
	 * @return The AuthPlayer.
	 */
	@Mapping(target = "playerID", source = "playerID")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "role", source = "role")
	AuthPlayer toAuthPlayer(Player player);

	/**
	 * Converts authPlayer to a Player entity.
	 *
	 * @param authPlayer The authPlayer.
	 * @return The Player entity.
	 */
	@Mapping(target = "playerID", source = "playerID")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "role", source = "role")
	Player toEntity(AuthPlayer authPlayer);
}
