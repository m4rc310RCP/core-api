package br.com.m4rc310.core.graphql.configurations.security.impls;

import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import br.com.m4rc310.core.graphql.configurations.security.IMAuthUserProvider;
import br.com.m4rc310.core.graphql.configurations.security.IMGraphQLJwtService;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MEnumToken;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MGraphQLJwtService implements IMGraphQLJwtService {
	
	@Autowired
	private IMAuthUserProvider provider;
	
	@Value(AUTH_SECURITY_SIGNING)
	private String JWT_SIGNING_KEY;
	
	@Value(AUTH_SECURITY_SALT)
	private String JWT_SALT;
	
	@Value(AUTH_SECURITY_ITERATION)
	private int JWT_ITERATION;
	
	@Value(AUTH_SECURITY_KEY_LENGTH)
	private int JWT_KEY_LENGTH;
	

	@Override
	public MUser getDTOUser(HttpServletRequest request) {
		try {
			MEnumToken type = getMEnumToken(request);
			String token = getToken(request);
			return getMUser(type, token);
		} catch (Exception e) {
			return null;
		}

	}

	private String getToken(HttpServletRequest request) {
		MEnumToken type = getMEnumToken(request);
		if (type.compareTo(MEnumToken.NONE) == 0) {
			return null;
		}
		String authorizationHeader = request.getHeader(AUTHORIZATION).replace(AUTHORIZATION, "");
		return authorizationHeader.replace(type.getDescription(), "").trim();

	}

	private MEnumToken getMEnumToken(HttpServletRequest request) {

		String authorizationHeader = request.getHeader(AUTHORIZATION);
		
		if (authorizationHeader == null) {
			return MEnumToken.NONE;
		}

		authorizationHeader = authorizationHeader.replace(AUTHORIZATION, "").trim();

		for (MEnumToken et : MEnumToken.values()) {
			if (authorizationHeader.startsWith(et.getDescription())) {
				return et;
			}
		}

		return MEnumToken.NONE;
	}

	@Override
	public MUser getMUser(MEnumToken type, String token) throws Exception {
		switch (type) {
		case TEST:
			int i = token.indexOf(":");
			String username = token.substring(0, i);
			String password = token.substring(i + 1);
//			password= encoder.encode(password);
			return provider.authUser(username, password);
		case BASIC:
			String sbasic = decrypt(token);
			i = sbasic.indexOf(":");
			username = sbasic.substring(0, i);
			password = sbasic.substring(i + 1);
			return provider.authUser(username, password);
		case BEARER:
			if (isTokenExpirate(token)) {
				throw new Exception("Token as expiraded.");
			}
			username = extractUsername(token);
			return provider.getUserFromUsername(username);
		default:
			return null;
		}
	}
	
	@Override
	public String encrypt(String text) throws Exception {
		return encrypt(text, JWT_SIGNING_KEY);
	}

	@Override
	public String encrypt(String text, String key) throws Exception {
		byte[] keyByte = getKeyByte(key);
		SecretKeySpec keyAES = new SecretKeySpec(keyByte, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keyAES);
		return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
	}

	@Override
	public String decrypt(String text) throws Exception {
		return decrypt(text, JWT_SIGNING_KEY);
	}
	
	@Override
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	@Override
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = Jwts.parser().setSigningKey(JWT_SIGNING_KEY).parseClaimsJws(token).getBody();
		return resolver.apply(claims);
	}
	
	@Override
	public boolean isTokenExpirate(String token) {
		try {
			Date exp = extractClaim(token, Claims::getExpiration);
			return exp.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String decrypt(String text, String key) throws Exception {		
		byte[] keyByte = getKeyByte(key);
		byte[] textDecoded = Base64.getDecoder().decode(text);
		
		SecretKeySpec keyAES = new SecretKeySpec(keyByte, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keyAES);
		byte[] textoDescriptografado = cipher.doFinal(textDecoded);
		return new String(textoDescriptografado);
	}
	
	@Override
	public byte[] getKeyByte() throws Exception {
		return getKeyByte(JWT_SIGNING_KEY);
	}

	@Override
	public byte[] getKeyByte(String skey) throws Exception {
		JWT_SIGNING_KEY = skey;
		PBEKeySpec spec = new PBEKeySpec(JWT_SIGNING_KEY.toCharArray(), JWT_SALT.getBytes(), JWT_ITERATION, JWT_KEY_LENGTH);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		return factory.generateSecret(spec).getEncoded();
	}

}
