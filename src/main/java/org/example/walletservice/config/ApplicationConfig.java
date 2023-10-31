package org.example.walletservice.config;

import org.example.walletservice.jwt.JwtInterceptor;
import org.example.walletservice.jwt.JwtService;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

/**
 * Application configuration class.
 */
@Configuration
@EnableWebMvc
@ComponentScan("org.example.walletservice")
@PropertySources({
		@PropertySource(value = "classpath:application.yml", factory = YamlProperty.class)
})
@EnableAspectJAutoProxy
@EnableSwagger2
public class ApplicationConfig implements WebMvcConfigurer {
	@Value("${database.url}")
	private String url;
	@Value("${database.username}")
	private String username;
	@Value("${database.password}")
	private String password;

	/**
	 * Configures the data source.
	 *
	 * @return The configured data source.
	 */
	@Bean
	public DataSource dataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	/**
	 * Configures the JDBC template.
	 *
	 * @return The configured JDBC template.
	 */
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	/**
	 * Configures the JWT service.
	 *
	 * @return The configured JWT service.
	 */
	@Bean
	public JwtService jwtService() {
		return new JwtService();
	}

	/**
	 * Configures the JWT interceptor.
	 *
	 * @return The configured JWT interceptor.
	 */
	@Bean
	public JwtInterceptor jwtInterceptor() {
		return new JwtInterceptor(jwtService());
	}

	/**
	 * Adds the JWT interceptor to the registry.
	 *
	 * @param registry The interceptor registry.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtInterceptor()).addPathPatterns("/**");
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("org.example.walletservice.in"))
				.paths(PathSelectors.any())
				.build();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}