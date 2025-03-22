package br.com.m4rc310.core.graphql.configurations.security;

import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import br.com.m4rc310.core.graphql.configurations.security.impls.MGraphQLJwtService;

// TODO: Auto-generated Javadoc
/**
 * The Interface IMAuthUserProvider.
 */
public interface IMAuthUserProvider {
	
	/**
	 * Auth user.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the m user
	 * @throws Exception the exception
	 */
	MUser authUser(String username, Object password) throws Exception;

	/**
	 * Gets the user from username.
	 *
	 * @param username the username
	 * @return the user from username
	 */
	MUser getUserFromUsername(String username);

	/**
	 * Checks if is valid user.
	 *
	 * @param user the user
	 * @return true, if is valid user
	 */
	boolean isValidUser(MUser user);
	
	/**
	 * Auth user.
	 *
	 * @param token of auth
	 * @return the m user
	 * @throws Exception the exception
	 */
	MUser authUser(String token, MGraphQLJwtService jwt) throws Exception;
}
