package br.com.m4rc310.core.graphql.mappers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface MCase.
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MAuth {
	String[] roles() default "";
	String message() default "Access unauthorizade.";
}
