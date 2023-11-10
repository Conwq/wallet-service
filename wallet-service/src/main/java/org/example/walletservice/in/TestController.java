package org.example.walletservice.in;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.example.walletservice.repository.rep.impl.PlayerRep;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
	private final PlayerRep playerRep;


	@GetMapping
	public void testPrint() {
		Optional<PlayerEntity> player = playerRep.findByUsername("admin");
	}
}
