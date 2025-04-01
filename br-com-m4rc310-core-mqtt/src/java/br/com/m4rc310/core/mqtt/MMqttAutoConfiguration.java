package br.com.m4rc310.core.mqtt;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import br.com.m4rc310.core.mqtt.properties.MqttProperty;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(MqttProperty.class)
@ComponentScan(basePackages = MqttProperty.PROPS_MQTT)
@ConditionalOnProperty(name = MqttProperty.PROPS_MQTT_ENABLE, havingValue = "true", matchIfMissing = false)
public class MMqttAutoConfiguration {
	
	@PostConstruct
	void init() {
		log.info("~> Loading {}...", getClass().getSimpleName());
	}
}
