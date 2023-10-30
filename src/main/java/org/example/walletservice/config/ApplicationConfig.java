package org.example.walletservice.config;

import org.example.walletservice.jwt.JwtInterceptor;
import org.example.walletservice.jwt.JwtService;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@ComponentScan("org.example.walletservice")
@PropertySources({
		@PropertySource(value = "classpath:application.yml", factory = YamlProperty.class)
})
@EnableAspectJAutoProxy
public class ApplicationConfig implements WebMvcConfigurer {
	@Value("${database.url}")
	private String url;
	@Value("${database.username}")
	private String username;
	@Value("${database.password}")
	private String password;
	@Value("${liquibase.change-log}")
	private String changelogFile;

	@Autowired
	public ApplicationConfig() {
	}

	@Bean
	public DataSource dataSource() {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public JwtService jwtService() {
		return new JwtService();
	}

//	@Bean
//	public SpringLiquibase liquibase(DataSource dataSource) {
//		SpringLiquibase liquibase = new SpringLiquibase();
//		liquibase.setDataSource(dataSource);
//		liquibase.setChangeLog(changelogFile);
//		//TODO нужно создать схему для Liquibase!
//		return null;
//	}

	@Bean
	public JwtInterceptor jwtInterceptor() {
		return new JwtInterceptor(jwtService());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtInterceptor()).addPathPatterns("/**");
	}
}