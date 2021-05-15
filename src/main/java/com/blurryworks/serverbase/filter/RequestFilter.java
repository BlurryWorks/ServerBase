package com.blurryworks.serverbase.filter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(RequestFilters.class)
public @interface RequestFilter
{
	Class<? extends com.blurryworks.serverbase.filter.implementation.RequestFilterBase> value();
}
