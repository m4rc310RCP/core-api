package br.com.m4rc310.core.graphql.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(IConsts.PROPS_GRAPHQL)
public class MGraphQLProperty implements IConsts{
	
	private boolean enable;
	private Security security = new Security();
	private Ws ws = new Ws();
	
	@Data
	public class Security{
		private boolean enable;
	}

	@Data
	public class Ws{
		private boolean enable;
	}
	
	
}
