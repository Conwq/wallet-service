package org.example.walletservice.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Properties;

/**
 * Custom property source factory for YAML files.
 */
public class YamlProperty extends DefaultPropertySourceFactory {

	/**
	 * Creates a PropertySource for the specified YAML resource.
	 *
	 * @param name     The name of the property source, or {@code null} if none.
	 * @param resource The resource to be loaded.
	 * @return The PropertySource created from the YAML resource.
	 */
	@Override
	public PropertySource<?> createPropertySource(@Nullable String name,
												  EncodedResource resource) {
		Properties propertiesFromYaml = loadYamlIntoProperties(resource);
		String sourceName = name != null ? name : resource.getResource().getFilename();
		return new PropertiesPropertySource(Objects.requireNonNull(sourceName), propertiesFromYaml);
	}

	/**
	 * Loads YAML content into Properties.
	 *
	 * @param resource The resource containing YAML content.
	 * @return Properties loaded from the YAML content.
	 */
	private Properties loadYamlIntoProperties(EncodedResource resource) {
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(resource.getResource());
		factory.afterPropertiesSet();
		return Objects.requireNonNull(factory.getObject());
	}
}