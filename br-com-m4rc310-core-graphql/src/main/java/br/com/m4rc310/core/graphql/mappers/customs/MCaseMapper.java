package br.com.m4rc310.core.graphql.mappers.customs;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;

import br.com.m4rc310.core.graphql.mappers.MGraphQLScalarType;
import br.com.m4rc310.core.graphql.mappers.annotations.MCase;
import br.com.m4rc310.core.graphql.mappers.annotations.MMapper;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

/**
 * The Class MCaseMapper.
 *
 * @author marcelo
 * @version $Id: $Id
 */
@MMapper
public class MCaseMapper extends MGraphQLScalarType<MCase> {

	/**
	 * {@inheritDoc}
	 *
	 * Inits the.
	 */
	@Override
	public GraphQLScalarType init(AnnotatedElement element, AnnotatedType type, MCase annotation) {

		String message = getString("Change case of the text (Case -> {0})", annotation.value().toString());

		Coercing<String, String> coercing = getCoercing(String.class, from -> changeCase(from, annotation),
				to -> changeCase(to, annotation));

		return get(String.format("Case%s", annotation.value()), message, coercing);
	}

	/**
	 * Change case.
	 *
	 * @param value      the value
	 * @param annotation the annotation
	 * @return the string
	 */
	private String changeCase(String value, MCase annotation) {

		switch (annotation.value()) {
		case UPPER:
			return value.toUpperCase();
		case LOWER:
			return value.toLowerCase();
		default:
			return value;
		}
	}

}
