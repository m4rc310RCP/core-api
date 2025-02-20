package br.com.m4rc310.core.graphql.configurations.security.wrappers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class MGraphQLServletInputStreamWrapper.
 */
public class MGraphQLServletInputStreamWrapper extends ServletInputStream {
	
	/** The buffer. */
	private final ByteArrayInputStream buffer;
	
	 /**
 	 * Instantiates a new m graph QL servlet input stream wrapper.
 	 *
 	 * @param contents the contents
 	 */
 	public MGraphQLServletInputStreamWrapper(byte[] contents) {
         this.buffer = new ByteArrayInputStream(contents);
     }


	/**
	 * Checks if is finished.
	 *
	 * @return true, if is finished
	 */
	@Override
	public boolean isFinished() {
		return buffer.available() == 0;
	}

	/**
	 * Checks if is ready.
	 *
	 * @return true, if is ready
	 */
	@Override
	public boolean isReady() {
		return true;
	}

	/**
	 * Sets the read listener.
	 *
	 * @param listener the new read listener
	 */
	@Override
	public void setReadListener(ReadListener listener) {
		
	}

	/**
	 * Read.
	 *
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public int read() throws IOException {
		return buffer.read();
	}

}
