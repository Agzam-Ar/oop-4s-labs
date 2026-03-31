package com.example.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Named {

	String value();
	
}
