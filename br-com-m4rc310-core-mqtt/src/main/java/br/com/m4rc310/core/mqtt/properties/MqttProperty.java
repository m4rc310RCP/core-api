package br.com.m4rc310.core.mqtt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import br.com.m4rc310.core.mqtt.constants.IConst;
import lombok.Data;

//TODO: Auto-generated Javadoc
/**
* The Class MGraphQLProperty.
*/
@Data
@ConfigurationProperties(IConst.PROPS_MQTT)
public class MqttProperty implements IConst{
	/** The enable. */
	private boolean enable;
	
	private final Api api;
	
	/**
	 * <p>Constructor for .</p>
	 */
	public MqttProperty(){
		api = new Api();
	}
	
	/**
	 * 
	 */
	@Data
	class Api{
		private String uri;
		/**
		 * 
		 */
		public Api() {}
	}


}
