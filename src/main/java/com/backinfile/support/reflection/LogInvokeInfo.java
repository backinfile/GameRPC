package com.backinfile.support.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogInvokeInfo {
	String value() default ""; // pattern

	boolean args() default false;

	public static final String DEFAULT_PATTERN = "\"{0}.{1} invoked\"";
	public static final String DEFAULT_PATTERN_ARGS = "\"{0}.{1} invoked \"+{2}";
}
