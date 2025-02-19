package br.com.m4rc310.core.graphql.configurations.security;

import br.com.m4rc310.core.graphql.configurations.security.dtos.MUser;

public interface IMAuthUserProvider {
	MUser authUser(String username, Object password) throws Exception;

	MUser getUserFromUsername(String username);

	boolean isValidUser(MUser user);
}
