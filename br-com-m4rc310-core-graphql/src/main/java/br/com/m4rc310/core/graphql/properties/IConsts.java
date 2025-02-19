package br.com.m4rc310.core.graphql.properties;

public interface IConsts {
	public static final String PROPS_GRAPHQL = "br.com.m4rc310.core.graphql";
	public static final String ENABLE_GRAPHQL = "br.com.m4rc310.core.graphql.enable";
	public static final String ENABLE_GRAPHQL_SECURITY = "br.com.m4rc310.core.graphql.security.enable";
	public static final String ENABLE_GRAPHQL_SECURITY_VALUE = "${br.com.m4rc310.core.graphql.security.enable:true}";

	public static final String ENABLE_GRAPHQL_WS = "graphql.spqr.ws.enabled";
	public static final String ENABLE_GRAPHQL_WS_VALUE = "${graphql.spqr.ws.enabled:false}";

	public static final String AUTH_SECURITY_SALT = "${AUTH_SECURITY_SALT}";
	public static final String AUTH_SECURITY_SIGNING = "${AUTH_SECURITY_SIGNING}";
	public static final String AUTH_SECURITY_ITERATION = "${AUTH_SECURITY_ITERATION:10000}";
	public static final String AUTH_SECURITY_KEY_LENGTH = "${AUTH_SECURITY_KEY_LENGTH:128}";
	public static final String AUTH_SECURITY_EXPIRATION = "${AUTH_SECURITY_EXPIRATION:864000000}";

	public static final String IS_DEV = "IS_DEV";
	public static final String VALUE_IS_DEV = "${IS_DEV:false}";
	
	public static final String VALUE_GRAPHQL_GUI_ENDPOINT = "${graphql.spqr.gui.endpoint:/gui}";
	public static final String VALUE_GRAPHQL_SERVER_ENDPOINT = "${graphql.spqr.http.endpoint:/graphql}";

	public static final String AUTHORIZATION = "Authorization";
	
	public static final String KEY_AUTH = "authorities";
}
