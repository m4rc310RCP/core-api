package br.com.m4rc310.core.graphql.mappers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface MDate.
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MDate {

	/**
	 * Value.
	 *
	 * @return the string
	 */
	String value() default "dd/MM/yyyy HH:mm:ss";

	/**
	 * Patther of date.
	 *
	 * @return true, if successful
	 */
	DatePatther patther() default DatePatther.CUSTOM;
	
	
//	boolean toUnix() default false;
//	boolean toUTC() default false;
	
	/**
	 * The Enum Case.
	 */
	public enum DatePatther {
		/** The upper. */
		UNIX,
		/** The lower. */
		UTC,
		/** The none. */
		CUSTOM
	}
}
