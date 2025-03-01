package br.com.m4rc310.core.weather.constants;

public interface IConsts {
	public static final String PROPS_WEATHER = "br.com.m4rc310.core.weather";
	public static final String PROPS_WEATHER_ENABLE = "br.com.m4rc310.core.weather.enable";
	public static final String PROPS_WEATHER_API_URL_VALUE = "${br.com.m4rc310.core.weather.api.url:http://api.openweathermap.org}";
	public static final String PROPS_WEATHER_API_KEY_VALUE = "${WEATHER_APIKEY:${br.com.m4rc310.core.weather.api.key}}";
	public static final String PROPS_IBGE_URL_VALUE = "${br.com.m4rc310.core.weather.ibge.url:https://servicodados.ibge.gov.br}";
	public static final String PROPS_IP_API_URL_VALUE = "${IP_API_URL:${br.com.m4rc310.core.weather.api.ip.url:http://ip-api.com/json}}";
	
}
