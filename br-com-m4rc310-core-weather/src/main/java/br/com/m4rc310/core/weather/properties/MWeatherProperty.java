package br.com.m4rc310.core.weather.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import br.com.m4rc310.core.weather.constants.IConsts;
import static br.com.m4rc310.core.weather.constants.IConsts.PROPS_WEATHER;
import lombok.Data;

/**
 * 
 */
@Data
@ConfigurationProperties(PROPS_WEATHER)
public class MWeatherProperty implements IConsts {
	/**
	 * 
	 */
	private boolean enable;
	
	/**
	 * 
	 */
	private Api api = new Api();
	
	private Ibge ibge = new Ibge();
	
	@Data
	public class Api {
		private String url;
		private String key;
	}
	
	@Data
	public class Ibge{
		private String url;
	}
	
}
