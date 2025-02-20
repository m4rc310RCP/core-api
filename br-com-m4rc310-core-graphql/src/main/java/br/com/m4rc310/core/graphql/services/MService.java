package br.com.m4rc310.core.graphql.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.m4rc310.core.graphql.configurations.security.IMFluxService;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUserPrincipal;
import br.com.m4rc310.core.graphql.messages.MMessageBuilder;
import br.com.m4rc310.core.graphql.properties.IConsts;

// TODO: Auto-generated Javadoc
/**
 * The Class MService.
 */
@Configuration
public class MService implements IConsts {

	/** The is dev. */
	@Value(VALUE_IS_DEV)
	protected boolean isDev;
	
	/** The flux. */
	@Autowired
	protected IMFluxService flux;
	
	/** The m. */
	@Autowired
	protected MMessageBuilder m;
	
	
	/**
	 * <p>Constructor for MService.</p>
	 */
	public MService() {
	}

	/**
	 * Gets the user username.
	 *
	 * @return the user username
	 */
	protected String getUserUsername() {
		try {
			MUserPrincipal principal = (MUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			return principal.getUsername();
		} catch (Exception e) {
			return null;
		}
	}
}
