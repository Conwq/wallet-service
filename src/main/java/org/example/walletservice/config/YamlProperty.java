package org.example.walletservice.config;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.util.Objects;
import java.util.Properties;

@NonNullApi
public class YamlProperty extends DefaultPropertySourceFactory {
	@Override
	public PropertySource<?> createPropertySource(@Nullable String name,
												  EncodedResource resource) {
		Properties propertiesFromYaml = loadYamlIntoProperties(resource);
		String sourceName = name != null ? name : resource.getResource().getFilename();
		return new PropertiesPropertySource(Objects.requireNonNull(sourceName), propertiesFromYaml);
	}

	private Properties loadYamlIntoProperties(EncodedResource resource) {
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(resource.getResource());
		factory.afterPropertiesSet();
		return Objects.requireNonNull(factory.getObject());
	}
}

