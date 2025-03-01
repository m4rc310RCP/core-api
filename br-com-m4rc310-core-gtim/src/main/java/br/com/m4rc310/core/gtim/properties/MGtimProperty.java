package br.com.m4rc310.core.gtim.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import br.com.m4rc310.core.gtim.constants.IConsts;
import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class MGraphQLProperty.
 */
@Data
@ConfigurationProperties(IConsts.PROPS_GTIM)
public class MGtimProperty implements IConsts{
	
	/** The enable. */
	private boolean enable;
	
	private final Api api;
	
	/**
	 * <p>Constructor for .</p>
	 */
	public MGtimProperty(){
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
