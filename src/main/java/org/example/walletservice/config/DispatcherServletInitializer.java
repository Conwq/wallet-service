package org.example.walletservice.config;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Dispatcher servlet initializer for the application.
 */
@NonNullApi
public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	/**
	 * Specifies the root configuration classes.
	 *
	 * @return An array of root configuration classes or {@code null} if no root configuration is provided.
	 */
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	/**
	 * Specifies the servlet configuration classes.
	 *
	 * @return An array of servlet configuration classes.
	 */
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{ApplicationConfig.class};
	}

	/**
	 * Specifies the servlet mappings.
	 *
	 * @return An array of servlet mappings.
	 */
	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}
}