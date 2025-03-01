package br.com.m4rc310.core.graphql.services;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
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
	
	@Autowired
	protected TaskScheduler taskScheduler;
	
	
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
	
	protected String generateRandomDigits(int length) {
		SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }
	
}
