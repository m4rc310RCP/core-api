package br.com.m4rc310.core.mqtt.services;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.m4rc310.core.mqtt.constants.IConst;
import br.com.m4rc310.core.mqtt.properties.MqttProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(name = MqttProperty.PROPS_MQTT_ENABLE, havingValue = "true", matchIfMissing = false)
public class MMqttService implements IConst{

	@Value(PROPS_MQTT_API_URI)
	private String MQTT_API_URI;
	
	@Value(PROPS_MQTT_SECURITY_USER_VALUE)
	private String MQTT_USER;
	
	@Value(PROPS_MQTT_SECURITY_PSWD_VALUE)
	private String MQTT_PSWD;
	
	@Bean
	MqttClient connect() throws Exception {
		MqttClient client = new MqttClient(MQTT_API_URI, MQTT_USER);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(MQTT_USER);
        options.setPassword(MQTT_PSWD.toCharArray());
        client.connect(options);
        return client;
	}
	
	
	
	
	
}
