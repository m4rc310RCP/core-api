package br.com.m4rc310.core.graphql.configurations.security.dtos;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: Auto-generated Javadoc
/**
 * The Enum MEnumToken.
 */
@AllArgsConstructor
public enum MEnumToken {
	
	/** The test. */
 TEST("Test"), 
 /** The basic. */
 BASIC("Basic"), 
 /** The bearer. */
 BEARER("Bearer"), 
 /** The bearer. */
 AUTH("Auth"), 
 /** The none. */
 NONE("none");

	/** The description. */
	@Getter
	private String description;
	
	 /**
 	 * From description.
 	 *
 	 * @param description the description
 	 * @return the m enum token
 	 */
 	public static MEnumToken fromDescription(String description) {
	        return Arrays.stream(MEnumToken.values())
	                .filter(e -> e.description.equalsIgnoreCase(description))
	                .findFirst()
	                .orElseThrow(() -> new IllegalArgumentException("Invalid description: " + description));
	    }
}
