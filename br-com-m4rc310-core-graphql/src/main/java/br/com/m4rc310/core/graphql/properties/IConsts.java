package br.com.m4rc310.core.graphql.properties;

// TODO: Auto-generated Javadoc
/**
 * The Interface IConsts.
 */
public interface IConsts {
	
	/** The Constant PROPS_GRAPHQL. */
	public static final String PROPS_GRAPHQL = "br.com.m4rc310.core.graphql";
	
	/** The Constant ENABLE_GRAPHQL. */
	public static final String ENABLE_GRAPHQL = "br.com.m4rc310.core.graphql.enable";
	
	/** The Constant ENABLE_GRAPHQL_SECURITY. */
	public static final String ENABLE_GRAPHQL_SECURITY = "br.com.m4rc310.core.graphql.security.enable";
	
	/** The Constant ENABLE_GRAPHQL_SECURITY_VALUE. */
	public static final String ENABLE_GRAPHQL_SECURITY_VALUE = "${br.com.m4rc310.core.graphql.security.enable:true}";

	/** The Constant ENABLE_GRAPHQL_WS. */
	public static final String ENABLE_GRAPHQL_WS = "graphql.spqr.ws.enabled";
	
	/** The Constant ENABLE_GRAPHQL_WS_VALUE. */
	public static final String ENABLE_GRAPHQL_WS_VALUE = "${graphql.spqr.ws.enabled:false}";

	/** The Constant AUTH_SECURITY_SALT. */
	public static final String AUTH_SECURITY_SALT = "${AUTH_SECURITY_SALT}";
	
	/** The Constant AUTH_SECURITY_SIGNING. */
	public static final String AUTH_SECURITY_SIGNING = "${AUTH_SECURITY_SIGNING}";
	
	/** The Constant AUTH_SECURITY_ITERATION. */
	public static final String AUTH_SECURITY_ITERATION = "${AUTH_SECURITY_ITERATION:10000}";
	
	/** The Constant AUTH_SECURITY_KEY_LENGTH. */
	public static final String AUTH_SECURITY_KEY_LENGTH = "${AUTH_SECURITY_KEY_LENGTH:128}";
	
	/** The Constant AUTH_SECURITY_EXPIRATION. */
	public static final String AUTH_SECURITY_EXPIRATION = "${AUTH_SECURITY_EXPIRATION:864000000}";

	/** The Constant IS_DEV. */
	public static final String IS_DEV = "IS_DEV";
	
	/** The Constant VALUE_IS_DEV. */
	public static final String VALUE_IS_DEV = "${IS_DEV:false}";
	
	/** The Constant VALUE_GRAPHQL_GUI_ENDPOINT. */
	public static final String VALUE_GRAPHQL_GUI_ENDPOINT = "${graphql.spqr.gui.endpoint:/gui}";
	
	/** The Constant VALUE_GRAPHQL_SERVER_ENDPOINT. */
	public static final String VALUE_GRAPHQL_SERVER_ENDPOINT = "${graphql.spqr.http.endpoint:/graphql}";

	/** The Constant AUTHORIZATION. */
	public static final String AUTHORIZATION = "Authorization";
	
	/** The Constant KEY_AUTH. */
	public static final String KEY_AUTH = "authorities";
}
