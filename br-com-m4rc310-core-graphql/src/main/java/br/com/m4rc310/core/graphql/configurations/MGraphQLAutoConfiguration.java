package br.com.m4rc310.core.graphql.configurations;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import br.com.m4rc310.core.graphql.properties.MGraphQLProperty;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class MGraphQLAutoConfiguration.
 */
@Slf4j
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(MGraphQLProperty.class)
@ConditionalOnProperty(name = MGraphQLProperty.ENABLE_GRAPHQL, havingValue = "true", matchIfMissing = false)
@ComponentScan(basePackages = "br.com.m4rc310.core.graphql")
public class MGraphQLAutoConfiguration {
	
	/**
	 * MGraphQLAutoConfiguration
	 */
	public MGraphQLAutoConfiguration() {
		super();
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	void init() {
		log.info("~> Loading {}...", getClass().getSimpleName());
	}
	
	
	
}
