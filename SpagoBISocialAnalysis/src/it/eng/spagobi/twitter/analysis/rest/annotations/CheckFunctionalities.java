package it.eng.spagobi.twitter.analysis.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
// can use in method only.
@Target(ElementType.METHOD)
public @interface CheckFunctionalities {

	String[] funcs() default "";
}
