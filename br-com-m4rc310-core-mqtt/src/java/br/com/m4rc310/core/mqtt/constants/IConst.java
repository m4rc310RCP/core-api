package br.com.m4rc310.core.mqtt.constants;

public interface IConst {

	public static final String PROPS_MQTT = "br.com.m4rc310.core.mqtt";
	public static final String PROPS_MQTT_ENABLE = "br.com.m4rc310.core.mqtt.enable";
	public static final String PROPS_MQTT_API_URI = "${MQTT_URI:${br.com.m4rc310.core.mqtt.api.uri}}";
	
	public static final String PROPS_MQTT_SECURITY_USER_VALUE = "${MQTT_USER:${br.com.m4rc310.core.mqtt.user}}";
	public static final String PROPS_MQTT_SECURITY_PSWD_VALUE = "${MQTT_PSWD:${br.com.m4rc310.core.mqtt.pswd}}";
}
