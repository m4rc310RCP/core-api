package br.com.m4rc310.core.graphql.configurations.security.impls;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.m4rc310.core.graphql.configurations.security.IMAuthUserProvider;
import br.com.m4rc310.core.graphql.configurations.security.IMGraphQLJwtService;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MEnumToken;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class MGraphQLJwtService.
 */
@Slf4j
@Configuration
public class MGraphQLJwtService implements IMGraphQLJwtService {
	
	/** The provider. */
	@Autowired
	private IMAuthUserProvider provider;
	
	/** The jwt signing key. */
	@Value(AUTH_SECURITY_SIGNING)
	private String JWT_SIGNING_KEY;
	
	/** The jwt salt. */
	@Value(AUTH_SECURITY_SALT)
	private String JWT_SALT;
	
	/** The jwt iteration. */
	@Value(AUTH_SECURITY_ITERATION)
	private int JWT_ITERATION;
	
	/** The jwt key length. */
	@Value(AUTH_SECURITY_KEY_LENGTH)
	private int JWT_KEY_LENGTH;
	
	/** The jwt expiration. */
	@Value(AUTH_SECURITY_EXPIRATION)
	private Long JWT_SECURITY_EXPIRATION;
	
	
	/**
	 * <p>Constructor for .</p>
	 */
	public MGraphQLJwtService (){}
	

	/**
	 * Gets the DTO user.
	 *
	 * @param request the request
	 * @return the DTO user
	 */
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

	/**
	 * Gets the token.
	 *
	 * @param request the request
	 * @return the token
	 */
	private String getToken(HttpServletRequest request) {
		MEnumToken type = getMEnumToken(request);
		if (type.compareTo(MEnumToken.NONE) == 0) {
			return null;
		}
		String authorizationHeader = request.getHeader(AUTHORIZATION).replace(AUTHORIZATION, "");
		return authorizationHeader.replace(type.getDescription(), "").trim();
	}

	/**
	 * Gets the m enum token.
	 *
	 * @param request the request
	 * @return the m enum token
	 */
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

	/**
	 * Gets the m user.
	 *
	 * @param type the type
	 * @param token the token
	 * @return the m user
	 * @throws Exception the exception
	 */
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
		case AUTH:
			return provider.authUser(token, this);
		default:
			return null;
		}
	}
	
	/**
	 * Encrypt.
	 *
	 * @param text the text
	 * @return the string
	 * @throws Exception the exception
	 */
	@Override
	public String encrypt(String text) throws Exception {
		return encrypt(text, JWT_SIGNING_KEY);
	}

	/**
	 * Encrypt.
	 *
	 * @param text the text
	 * @param key the key
	 * @return the string
	 * @throws Exception the exception
	 */
	@Override
	public String encrypt(String text, String key) throws Exception {
		byte[] keyByte = getKeyByte(key);
		SecretKeySpec keyAES = new SecretKeySpec(keyByte, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keyAES);
		return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
	}

	/**
	 * Decrypt.
	 *
	 * @param text the text
	 * @return the string
	 * @throws Exception the exception
	 */
	@Override
	public String decrypt(String text) throws Exception {
		return decrypt(text, JWT_SIGNING_KEY);
	}
	
	
	/**
	 * <p>
	 * generateToken.
	 * </p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @return a {@link java.lang.String} object
	 */
	@Override
	public String generateToken(MUser user) throws Exception{
		Map<String, Object> claim = new HashMap<>();
		return createToken(claim, user);
	}
	
	private String createToken(Map<String, Object> claims, UserDetails details) {
		Date now = Date.from(Instant.now());
		Date exp = new Date(now.getTime() + JWT_SECURITY_EXPIRATION);

		JwtBuilder ret = Jwts.builder();
		ret.setClaims(claims);
		ret.setSubject(details.getUsername());
		ret.claim(KEY_AUTH, details.getAuthorities());
		ret.setIssuedAt(now);
		ret.setExpiration(exp);
		ret.signWith(SignatureAlgorithm.HS256, JWT_SIGNING_KEY);

		return ret.compact();
	}
	
	
	/**
	 * Extract username.
	 *
	 * @param token the token
	 * @return the string
	 */
	@Override
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extract expiration.
	 *
	 * @param token the token
	 * @return Date
	 */
	@Override
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	/**
	 * Extract claim.
	 *
	 * @param <T> the generic type
	 * @param token the token
	 * @param resolver the resolver
	 * @return the t
	 */
	@Override
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = Jwts.parser().setSigningKey(JWT_SIGNING_KEY).parseClaimsJws(token).getBody();
		return resolver.apply(claims);
	}
	
	/**
	 * Checks if is token expirate.
	 *
	 * @param token the token
	 * @return true, if is token expirate
	 */
	@Override
	public boolean isTokenExpirate(String token) {
		try {
			Date exp = extractClaim(token, Claims::getExpiration);
			return exp.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Decrypt.
	 *
	 * @param text the text
	 * @param key the key
	 * @return the string
	 * @throws Exception the exception
	 */
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
	
	/**
	 * Gets the key byte.
	 *
	 * @return the key byte
	 * @throws Exception the exception
	 */
	@Override
	public byte[] getKeyByte() throws Exception {
		return getKeyByte(JWT_SIGNING_KEY);
	}

	/**
	 * Gets the key byte.
	 *
	 * @param skey the skey
	 * @return the key byte
	 * @throws Exception the exception
	 */
	@Override
	public byte[] getKeyByte(String skey) throws Exception {
		PBEKeySpec spec = new PBEKeySpec(skey.toCharArray(), JWT_SALT.getBytes(), JWT_ITERATION, JWT_KEY_LENGTH);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		return factory.generateSecret(spec).getEncoded();
	}

}
