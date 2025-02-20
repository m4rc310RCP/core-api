package br.com.m4rc310.core.graphql.configurations.security.wrappers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.util.StreamUtils;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

// TODO: Auto-generated Javadoc
/**
 * The Class MGraphQLRequestWrapper.
 */
public class MGraphQLRequestWrapper extends HttpServletRequestWrapper {

	/** The body. */
	private byte[] body;

	/**
	 * Instantiates a new m graph QL request wrapper.
	 *
	 * @param request the request
	 */
	public MGraphQLRequestWrapper(HttpServletRequest request) {
		super(request);
		try {
			InputStream inputStream = request.getInputStream();
			this.body = StreamUtils.copyToByteArray(inputStream);
		} catch (Exception e) {
		}

	}
	
	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
	 */
	@Override
    public ServletInputStream getInputStream() {
        return new MGraphQLServletInputStreamWrapper(this.body);
    }

    /**
     * Gets the reader.
     *
     * @return the reader
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}
