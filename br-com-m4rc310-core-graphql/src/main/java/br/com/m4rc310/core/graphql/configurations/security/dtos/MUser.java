package br.com.m4rc310.core.graphql.configurations.security.dtos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class MUser.
 */
@Data
public class MUser  implements UserDetails{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4778346728598710961L;
	
	/** The code. */
	private Long code;
	
	/** The username. */
	private String username;
	
	/** The password. */
	private String password;
	
	/** The roles. */
	private String[] roles;
	
	/** The account non expired. */
	private boolean accountNonExpired;
	
	/** The account non locked. */
	private boolean accountNonLocked;
	
	/** The credentials non expired. */
	private boolean credentialsNonExpired;
	
	/** The enabled. */
	private boolean enabled;
	
	/**
	 * <p>Constructor for MUser.</p>
	 */
	public MUser() {}
	
	
	/**
	 * Gets the authorities.
	 *
	 * @return the authorities
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		if (Objects.nonNull(roles)) {
			for(String role : roles) {
				authorities.add(new SimpleGrantedAuthority(role));
			}
		}
		return authorities;
	}
	
	

}
