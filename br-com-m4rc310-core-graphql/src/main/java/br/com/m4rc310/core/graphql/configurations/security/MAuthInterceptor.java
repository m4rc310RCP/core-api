package br.com.m4rc310.core.graphql.configurations.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.m4rc310.core.graphql.configurations.security.dtos.MUserPrincipal;
import br.com.m4rc310.core.graphql.exceptions.MException;
import br.com.m4rc310.core.graphql.mappers.annotations.MAuth;
import io.leangen.graphql.ExtensionProvider;
import io.leangen.graphql.GeneratorConfiguration;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
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
	 * MAuthInterceptor
	 */
	public MAuthInterceptor() {
	}

	/**
	 * Custom interceptors.
	 *
	 * @return the extension provider
	 */
	@Bean
	ExtensionProvider<GeneratorConfiguration, ResolverInterceptorFactory> customInterceptors() {
		List<ResolverInterceptor> authInterceptor = Collections.singletonList(this);
		return (_, interceptors) -> interceptors.append(new GlobalResolverInterceptorFactory(authInterceptor));
	}

	/**
	 * Around invoke.
	 *
	 * @param context      the context
	 * @param continuation the continuation
	 * @return the object
	 * @throws Exception the exception
	 */
	@Override
	public Object aroundInvoke(InvocationContext context, Continuation continuation) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Method method = (Method) context.getResolver().getExecutable().getDelegate();
	    if (isGetter(method)) {
	        return continuation.proceed(context);
	    }

		if (Objects.nonNull(authentication)) {
			MUserPrincipal principal = (MUserPrincipal) authentication.getPrincipal();
			boolean isBasicToken = principal == null ? false
					: principal.getAuthorities().stream().anyMatch(aa -> "basic".equalsIgnoreCase(aa.getAuthority()));

			boolean isAuthToken = principal == null ? false
					: principal.getAuthorities().stream().anyMatch(aa -> "auth".equalsIgnoreCase(aa.getAuthority()));

			MAuth auth = context.getResolver().getExecutable().getDelegate().getAnnotation(MAuth.class);

			String unauthorizadeMessage = auth == null ? "Access unauthorizade." : auth.message();
			
			if (Objects.isNull(auth) && isBasicToken) {
				throw getWebException(401, unauthorizadeMessage);
			} else if (Objects.isNull(auth) && isAuthToken) {
				throw getWebException(401, unauthorizadeMessage);
			} else if (Objects.nonNull(auth)) {
				if (isBasicToken
						&& !Arrays.asList(auth.roles()).stream().anyMatch(aa -> "basic".equalsIgnoreCase(aa))) {
					throw getWebException(401, unauthorizadeMessage);
				}

				if (isAuthToken && !Arrays.asList(auth.roles()).stream().anyMatch(aa -> "auth".equalsIgnoreCase(aa))) {
					throw getWebException(401, unauthorizadeMessage);
				}

				boolean isAuth = principal == null ? false : principal.getAuthorities().stream().anyMatch(ga -> {
					for (String role : auth.roles()) {
						if (role.equalsIgnoreCase(ga.getAuthority())) {
							return true;
						}
					}
					return false;
				});

				if (!isAuth) {
					throw getWebException(401, unauthorizadeMessage);
				}
			}

		}

		return continuation.proceed(context);
	}

	/**
	 * isGetter
	 * 
	 */
	private boolean isGetter(Method method) {
	    String methodName = method.getName();
	    
	    if (hasGraphQLAnnotations(method)) {
	        return false;
	    }

	    boolean startsWithGetOrIs = methodName.startsWith("get") || methodName.startsWith("is");
	    boolean hasNoParameters = method.getParameterCount() == 0;
	    boolean hasReturnValue = !void.class.equals(method.getReturnType());
	    boolean isBooleanGetter = methodName.startsWith("is") && method.getReturnType().equals(boolean.class);

	    return startsWithGetOrIs && hasNoParameters && (hasReturnValue || isBooleanGetter);
	}

	/**
	 * hasGraphQLAnnotations
	 */
	private boolean hasGraphQLAnnotations(Method method) {
	    for (Annotation annotation : method.getAnnotations()) {
	        if (annotation.annotationType().equals(GraphQLMutation.class) ||
	            annotation.annotationType().equals(GraphQLQuery.class)) {
	            return true;
	        }
	    }
	    return false;
	}

	/**
	 * Gets the web exception.
	 *
	 * @param code    the code
	 * @param message the message
	 * @param args    the args
	 * @return the web exception
	 */
	private MException getWebException(int code, String message, Object... args) {
		message = MessageFormat.format(message, args);
		return MException.to(code, message);
	}

}
