package br.com.m4rc310.core.graphql.exceptions;

import lombok.Getter;

// TODO: Auto-generated Javadoc
/**
 * The Class MException.
 */
/**
 * 
 */
@Getter
//@AllArgsConstructor(staticName = "to")
public class MException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8768531949753036256L;
	
	/** The code. */
	private int code;
	
	/** The message. */
	private String message;

	/**
	 * @param code
	 * @param message
	 */
	public MException(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	/**
	 * @param code
	 * @param message
	 * @return
	 */
	public static final MException to(int code, String message) {
		return new MException(code, message);
	}
	
	
	
}