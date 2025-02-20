package br.com.m4rc310.core.graphql.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class MGraphQLProperty.
 */
@Data
@ConfigurationProperties(IConsts.PROPS_GRAPHQL)
public class MGraphQLProperty implements IConsts{
	
	/** The enable. */
	private boolean enable;
	
	/** The security. */
	private Security security = new Security();
	
	/** The ws. */
	private Ws ws = new Ws();
	
	/**
	 * 
	 */
	public MGraphQLProperty(){}
	
	/**
	 * The Class Security.
	 */
	@Data
	public class Security{
		
		/** The enable. */
		private boolean enable;
		
		/**
		 * 
		 */
		public Security() {}
	}

	/**
	 * The Class Ws.
	 */
	@Data
	public class Ws{
		
		/** The enable. */
		private boolean enable;
		
		/**
		 * 
		 */
		public Ws() {}
	}
	
	
}
