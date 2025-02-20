package br.com.m4rc310.core.graphql.configurations.security.dtos;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class MUserPrincipal.
 */
@Data
public class MUserPrincipal implements Serializable{

/** The Constant serialVersionUID. */
private static final long serialVersionUID = 4607336427719715274L;
	
	/** The username. */
	private String username;
    
    /** The password. */
    private String password;
    
    /** The authorities. */
    private Collection<? extends GrantedAuthority> authorities;
	
	/**
	 * <p>Constructor for UserPrincipal.</p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 */
	public MUserPrincipal(MUser user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		if (Objects.nonNull(user.getRoles())) {
			this.authorities = user.getAuthorities().stream().map(role -> {
				return new SimpleGrantedAuthority(role.getAuthority());
			}).collect(Collectors.toList());
		}
	}
	
	/**
	 * <p>create.</p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @return a {@link br.com.m4rc310.gql.security.UserPrincipal} object
	 */
	public static MUserPrincipal create(MUser user){
        return new MUserPrincipal(user);
    }
	
}
