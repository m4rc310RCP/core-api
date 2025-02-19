package br.com.m4rc310.core.graphql.configurations.security;

import java.util.function.Function;

import br.com.m4rc310.core.graphql.configurations.security.dtos.MEnumToken;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import br.com.m4rc310.core.graphql.properties.IConsts;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public interface IMGraphQLJwtService extends IConsts {
	MUser getDTOUser(HttpServletRequest request);

	MUser getMUser(MEnumToken type, String token) throws Exception;

	String encrypt(String text) throws Exception;

	public String encrypt(String text, String key) throws Exception;

	String decrypt(String text) throws Exception;

	String extractUsername(String token);

	<T> T extractClaim(String token, Function<Claims, T> resolver);

	boolean isTokenExpirate(String token);

	String decrypt(String text, String key) throws Exception;

	byte[] getKeyByte() throws Exception;

	byte[] getKeyByte(String skey) throws Exception;
}
