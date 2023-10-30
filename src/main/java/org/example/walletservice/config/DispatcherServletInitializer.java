package org.example.walletservice.config;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@NonNullApi
public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{ApplicationConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}
}
