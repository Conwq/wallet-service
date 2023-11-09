package ru.patseev.auditspringbootstarter.audit.annotation;

import org.springframework.context.annotation.Import;
import ru.patseev.auditspringbootstarter.audit.config.AuditConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * With the help of this annotation, you can enable an audit service that will display the audit of the player's actions.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AuditConfiguration.class})
public @interface EnableAuditService {
}
