/*
 * 
 */
package br.com.m4rc310.core.graphql.configurations.security;

import java.util.function.Function;

import br.com.m4rc310.core.graphql.configurations.security.dtos.MEnumToken;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import br.com.m4rc310.core.graphql.properties.IConsts;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Interface IMGraphQLJwtService.
 */
public interface IMGraphQLJwtService extends IConsts {
	
	/**
	 * Gets the DTO user.
	 *
	 * @param request the request
	 * @return the DTO user
	 */
	MUser getDTOUser(HttpServletRequest request);

	/**
	 * Gets the m user.
	 *
	 * @param type the type
	 * @param token the token
	 * @return the m user
	 * @throws Exception the exception
	 */
	MUser getMUser(MEnumToken type, String token) throws Exception;

	/**
	 * Encrypt.
	 *
	 * @param text the text
	 * @return the string
	 * @throws Exception the exception
	 */
	String encrypt(String text) throws Exception;

	/**
	 * Encrypt.
	 *
	 * @param text the text
	 * @param key the key
	 * @return the string
	 * @throws Exception the exception
	 */
	public String encrypt(String text, String key) throws Exception;

	/**
	 * Decrypt.
	 *
	 * @param text the text
	 * @return the string
	 * @throws Exception the exception
	 */
	String decrypt(String text) throws Exception;

	/**
	 * Extract username.
	 *
	 * @param token the token
	 * @return the string
	 */
	String extractUsername(String token);

	/**
	 * Extract claim.
	 *
	 * @param <T> the generic type
	 * @param token the token
	 * @param resolver the resolver
	 * @return the t
	 */
	<T> T extractClaim(String token, Function<Claims, T> resolver);

	/**
	 * Checks if is token expirate.
	 *
	 * @param token the token
	 * @return true, if is token expirate
	 */
	boolean isTokenExpirate(String token);

	/**
	 * Decrypt.
	 *
	 * @param text the text
	 * @param key the key
	 * @return the string
	 * @throws Exception the exception
	 */
	String decrypt(String text, String key) throws Exception;

	/**
	 * Gets the key byte.
	 *
	 * @return the key byte
	 * @throws Exception the exception
	 */
	byte[] getKeyByte() throws Exception;

	/**
	 * Gets the key byte.
	 *
	 * @param skey the skey
	 * @return the key byte
	 * @throws Exception the exception
	 */
	byte[] getKeyByte(String skey) throws Exception;
}
