package br.com.m4rc310.core.weather.configurations;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import br.com.m4rc310.core.weather.properties.MWeatherProperty;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(MWeatherProperty.class)
@ConditionalOnProperty(name = MWeatherProperty.PROPS_WEATHER_ENABLE, havingValue = "true", matchIfMissing = false)
@ComponentScan(basePackages = MWeatherProperty.PROPS_WEATHER)
public class MWeatherAutoConfiguration {

	/**
	 * MWeatherAutoConfiguration
	 */
	public MWeatherAutoConfiguration() {
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
