package org.example.walletservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.patseev.auditspringbootstarter.logger.annotation.EnableLoggerService;

@SpringBootApplication
@EnableLoggerService
public class WalletServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletServiceApplication.class, args);
	}
}