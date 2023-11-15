package org.example.walletservice.model.mapper;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.PlayerEntity;
import org.example.walletservice.model.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

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
	default PlayerEntity toEntityFromRequest(PlayerRequestDto playerRequestDto, RoleEntity roleEntity) {
		return PlayerEntity.builder()
				.username(playerRequestDto.username())
				.password(new BCryptPasswordEncoder().encode(playerRequestDto.password()))
				.balance(BigDecimal.ZERO)
				.roleEntity(roleEntity)
				.build();
	}

	/**
	 * Converts Player entity to AuthPlayer.
	 *
	 * @param playerEntity The Player entity.
	 * @return The AuthPlayer.
	 */
	@Mapping(target = "playerID", source = "playerID")
	@Mapping(target = "username", source = "username")
	@Mapping(target = "role", source = "roleEntity.role")
	AuthPlayer toAuthPlayer(PlayerEntity playerEntity);
}
