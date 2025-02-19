package br.com.m4rc310.core.graphql.configurations.security.wrappers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

public class MGraphQLServletInputStreamWrapper extends ServletInputStream {
	
	private final ByteArrayInputStream buffer;
	
	 public MGraphQLServletInputStreamWrapper(byte[] contents) {
         this.buffer = new ByteArrayInputStream(contents);
     }


	@Override
	public boolean isFinished() {
		return buffer.available() == 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener listener) {
		
	}

	@Override
	public int read() throws IOException {
		return buffer.read();
	}

}
