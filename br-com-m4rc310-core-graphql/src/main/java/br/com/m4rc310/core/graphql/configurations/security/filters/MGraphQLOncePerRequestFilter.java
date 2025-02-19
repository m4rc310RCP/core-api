package br.com.m4rc310.core.graphql.configurations.security.filters;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.m4rc310.core.graphql.configurations.security.IMGraphQLJwtService;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;
import br.com.m4rc310.core.graphql.configurations.security.dtos.MUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MGraphQLOncePerRequestFilter extends OncePerRequestFilter {
	
	@Autowired
	private IMGraphQLJwtService jwt;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// Clear context security
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
            
            MUser user = jwt.getDTOUser(request);
            if (Objects.nonNull(user)) {
            	MUserPrincipal principal = MUserPrincipal.create(user);
            	UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null,
        				principal.getAuthorities());
            	
            	SecurityContext context = SecurityContextHolder.createEmptyContext();
        		context.setAuthentication(authentication);
        		SecurityContextHolder.setContext(context);
            }
            
            filterChain.doFilter(request, response);
		} catch (Exception e) {
			filterChain.doFilter(request, response);
		}
	}

}
