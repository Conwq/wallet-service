package ru.patseev.auditspringbootstarter.audit.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import ru.patseev.auditspringbootstarter.audit.bean.AuditAspect;
import ru.patseev.auditspringbootstarter.audit.bean.TimeCountingAspect;

@AutoConfiguration
public class AuditConfiguration {

	@Bean
	public AuditAspect auditAspect() {
		return new AuditAspect();
	}

	@Bean
	public TimeCountingAspect timeCountingAspect() {
		return new TimeCountingAspect();
	}
}
