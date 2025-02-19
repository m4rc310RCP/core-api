package br.com.m4rc310.core.graphql.configurations.security.dtos;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MEnumToken {
	TEST("Test"), BASIC("Basic"), BEARER("Bearer"), NONE("none");

	@Getter
	private String description;
	
	 public static MEnumToken fromDescription(String description) {
	        return Arrays.stream(MEnumToken.values())
	                .filter(e -> e.description.equalsIgnoreCase(description))
	                .findFirst()
	                .orElseThrow(() -> new IllegalArgumentException("Invalid description: " + description));
	    }
}
