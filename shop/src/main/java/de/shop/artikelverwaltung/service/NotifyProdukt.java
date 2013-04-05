package de.shop.artikelverwaltung.service;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;
	
@InterceptorBinding
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface NotifyProdukt {
}

