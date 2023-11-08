package ru.patseev.auditspringbootstarter.logger.annotation;

import org.springframework.context.annotation.Import;
import ru.patseev.auditspringbootstarter.logger.config.LoggerConfiguration;

import java.lang.annotation.*;

/**
 * Annotation to enable the Logger Service.
 * This annotation should be used on configuration classes to enable the Logger Service.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({LoggerConfiguration.class})
public @interface EnableLoggerService {
}
