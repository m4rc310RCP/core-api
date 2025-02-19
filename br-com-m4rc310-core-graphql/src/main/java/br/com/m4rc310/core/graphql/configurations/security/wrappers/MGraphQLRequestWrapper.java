package br.com.m4rc310.core.graphql.configurations.security.wrappers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.util.StreamUtils;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class MGraphQLRequestWrapper extends HttpServletRequestWrapper {

	private byte[] body;

	public MGraphQLRequestWrapper(HttpServletRequest request) {
		super(request);
		try {
			InputStream inputStream = request.getInputStream();
			this.body = StreamUtils.copyToByteArray(inputStream);
		} catch (Exception e) {
		}

	}
	
	@Override
    public ServletInputStream getInputStream() {
        return new MGraphQLServletInputStreamWrapper(this.body);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}
