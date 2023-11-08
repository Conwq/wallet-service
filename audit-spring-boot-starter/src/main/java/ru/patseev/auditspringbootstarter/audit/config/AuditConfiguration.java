package ru.patseev.auditspringbootstarter.audit.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import ru.patseev.auditspringbootstarter.audit.aspect.AuditAspect;
import ru.patseev.auditspringbootstarter.audit.aspect.TimeCountingAspect;

/**
 * Configuration class for Audit-related aspects.
 */
@AutoConfiguration
public class AuditConfiguration {

	/**
	 * Creates and returns an instance of the {@link AuditAspect} bean.
	 *
	 * @return The {@link AuditAspect} bean.
	 */
	@Bean
	public AuditAspect auditAspect() {
		return new AuditAspect();
	}

	/**
	 * Creates and returns an instance of the {@link TimeCountingAspect} bean.
	 *
	 * @return The {@link TimeCountingAspect} bean.
	 */
	@Bean
	public TimeCountingAspect timeCountingAspect() {
		return new TimeCountingAspect();
	}
}
