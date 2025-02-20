package br.com.m4rc310.core.graphql.configurations.security;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.m4rc310.core.graphql.configurations.security.annotations.MAuth;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUserPrincipal;
import br.com.m4rc310.core.graphql.exceptions.MException;
import io.leangen.graphql.ExtensionProvider;
import io.leangen.graphql.GeneratorConfiguration;
import io.leangen.graphql.execution.InvocationContext;
import io.leangen.graphql.execution.ResolverInterceptor;
import io.leangen.graphql.execution.ResolverInterceptorFactory;
import io.leangen.graphql.spqr.spring.util.GlobalResolverInterceptorFactory;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class MAuthInterceptor.
 */
@Slf4j
@Configuration
public class MAuthInterceptor implements ResolverInterceptor {
	
	/**
	 * Custom interceptors.
	 *
	 * @return the extension provider
	 */
	@Bean
	ExtensionProvider<GeneratorConfiguration, ResolverInterceptorFactory> customInterceptors() {
		List<ResolverInterceptor> authInterceptor = Collections.singletonList(this);
		return (config, interceptors) -> interceptors.append(new GlobalResolverInterceptorFactory(authInterceptor));
	}

	/**
	 * Around invoke.
	 *
	 * @param context the context
	 * @param continuation the continuation
	 * @return the object
	 * @throws Exception the exception
	 */
	@Override
	public Object aroundInvoke(InvocationContext context, Continuation continuation) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (Objects.nonNull(authentication)) {
			MUserPrincipal principal = (MUserPrincipal) authentication.getPrincipal();
			boolean isBasicToken = principal == null ? false
					: principal.getAuthorities().stream().anyMatch(aa -> "basic".equalsIgnoreCase(aa.getAuthority()));

			MAuth auth = context.getResolver().getExecutable().getDelegate().getAnnotation(MAuth.class);
			
			if (Objects.isNull(auth) && isBasicToken) {
				throw getWebException(401, "Access unauthorizade. User with basic privileges!");
			} else if (Objects.nonNull(auth)) {
				if (isBasicToken && !Arrays.asList(auth.rolesRequired()).stream().anyMatch(aa -> "basic".equalsIgnoreCase(aa))) {
					throw getWebException(401, "Access unauthorizade. User with basic privileges!");
				}

				boolean isAuth = principal == null ? false : principal.getAuthorities().stream().anyMatch(ga -> {
					for (String role : auth.rolesRequired()) {
						if (role.equalsIgnoreCase(ga.getAuthority())) {
							return true;
						}
					}
					return false;
				});
				
				if (!isAuth) {
					throw getWebException(401, "Access unauthorizade.");
				}
			}

		}

		return continuation.proceed(context);
	}

	/**
	 * Gets the web exception.
	 *
	 * @param code the code
	 * @param message the message
	 * @param args the args
	 * @return the web exception
	 */
	private MException getWebException(int code, String message, Object... args) {
		message = MessageFormat.format(message, args);
		return MException.to(code, message);
	}

}
