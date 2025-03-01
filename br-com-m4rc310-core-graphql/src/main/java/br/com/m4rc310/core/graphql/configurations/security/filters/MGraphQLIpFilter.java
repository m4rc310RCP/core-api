package br.com.m4rc310.core.graphql.configurations.security.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.m4rc310.core.graphql.configurations.security.IMFluxService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MGraphQLIpFilter extends OncePerRequestFilter {

	@Autowired
	private IMFluxService flux;

//	@Bean
//	SecurityFilterChain loadSecurityFilterChain(HttpSecurity http) {
//		log.info("loadSecurityFilterChain");
//		return http.addFilter(null);
//	}

//	@PostConstruct
	public void loadSecurityFilterChain(HttpSecurity http) {
		log.info("----");
	}

	/**
	 * Do filter internal.
	 *
	 * @param request     the request
	 * @param response    the response
	 * @param filterChain the filter chain
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty()) {
			ipAddress = request.getRemoteAddr();
		}

		if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
			URL url = URI.create("http://checkip.amazonaws.com/").toURL();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
				ipAddress = br.readLine();
			}
		}
        flux.setIPClient(ipAddress);
		filterChain.doFilter(request, response);
	}

}
