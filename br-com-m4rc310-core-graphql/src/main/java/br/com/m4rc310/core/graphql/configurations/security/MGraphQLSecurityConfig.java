package br.com.m4rc310.core.graphql.configurations.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.m4rc310.core.graphql.configurations.security.filters.MGraphQLOncePerRequestFilter;
import br.com.m4rc310.core.graphql.properties.IConsts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class MGraphQLSecurityConfig.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class MGraphQLSecurityConfig implements IConsts {
	
	/** The is dev. */
	@Value(VALUE_IS_DEV)
	private boolean isDev;
	
	/** The gui endpoint. */
	@Value(VALUE_GRAPHQL_GUI_ENDPOINT)
	private String GUI_ENDPOINT;
	
	/** The server endpoint. */
	@Value(VALUE_GRAPHQL_SERVER_ENDPOINT)
	private String SERVER_ENDPOINT;
	
	
	/**
	 * 
	 */
	public MGraphQLSecurityConfig() {
	}
	
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	void init() {
		log.info("~> MGraphQLSecurityConfig in DEV: {}", isDev);
	}

	/**
	 * User details service.
	 *
	 * @return the user details service
	 */
	@Bean
	@ConditionalOnProperty(name = ENABLE_GRAPHQL_SECURITY, havingValue = "false", matchIfMissing = true)
	UserDetailsService userDetailsService() {
		return username -> null;
	}

	/**
	 * Security filter chain disbled security.
	 *
	 * @param http the http
	 * @return the security filter chain
	 * @throws Exception the exception
	 */
	@Bean
	@ConditionalOnProperty(name = ENABLE_GRAPHQL_SECURITY, havingValue = "false", matchIfMissing = true)
	SecurityFilterChain securityFilterChainDisbledSecurity(HttpSecurity http) throws Exception {

		log.info("~> Security has been disabled. Please ensure this is intentional and does not expose sensitive data. Set property: [{}=true] to enable.", ENABLE_GRAPHQL_SECURITY);

		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).csrf(csrf -> csrf.disable())
				.formLogin(form -> form.disable()).httpBasic(httpBasic -> httpBasic.disable());

		return http.build();
	}
	
	/**
	 * Security filter chain from graph QL.
	 *
	 * @param http the http
	 * @param filter the filter
	 * @return the security filter chain
	 * @throws Exception the exception
	 */
	@Bean
	@ConditionalOnProperty(name = ENABLE_GRAPHQL_SECURITY, havingValue = "true", matchIfMissing = false)
	SecurityFilterChain securityFilterChainFromGraphQL(HttpSecurity http, MGraphQLOncePerRequestFilter filter) throws Exception {
		
		String endpointGUI = String.format("%s/**", GUI_ENDPOINT);
		String endpointGQL = String.format("%s/**", SERVER_ENDPOINT);
		
		
		http = http.cors(AbstractHttpConfigurer::disable);
		http = http.csrf(AbstractHttpConfigurer::disable);
		http = http.anonymous(AbstractHttpConfigurer::disable);
		http = http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http = http.securityContext(c -> c.requireExplicitSave(false));
		http = http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		http = http.authorizeHttpRequests(auth -> {
			auth.requestMatchers(HttpMethod.GET, endpointGUI, endpointGQL).permitAll();
			auth.requestMatchers(HttpMethod.POST, endpointGQL).authenticated();
			auth.anyRequest().denyAll();
		});
		
		return http.build();
	}
	
	
}
