package br.com.m4rc310.core.gtim;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import br.com.m4rc310.core.gtim.constants.IConsts;
import br.com.m4rc310.core.gtim.properties.MGtimProperty;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(MGtimProperty.class)
@ComponentScan(basePackages = MGtimProperty.PROPS_GTIM)
@ConditionalOnProperty(name = MGtimProperty.PROPS_GTIM_ENABLE, havingValue = "true", matchIfMissing = false)
public class MGtimAutoConfiguration implements IConsts {
	
	
	@PostConstruct
	void init() {
		log.info("~> Loading {}...", getClass().getSimpleName());
	}
}
