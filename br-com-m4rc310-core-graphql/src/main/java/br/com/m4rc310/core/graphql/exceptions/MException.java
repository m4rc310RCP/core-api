package br.com.m4rc310.core.graphql.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: Auto-generated Javadoc
/**
 * The Class MException.
 */
/**
 * 
 */
@Getter
@AllArgsConstructor(staticName = "to")
public class MException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8768531949753036256L;
	
	/** The code. */
	private int code;
	
	/** The message. */
	private String message;
	
	/**
	 * <p>Constructor for MException.</p>
	 */
	public MException() {
	}
}