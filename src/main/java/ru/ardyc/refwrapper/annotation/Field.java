package ru.ardyc.refwrapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    String value();

    boolean isSetter() default false;

}
