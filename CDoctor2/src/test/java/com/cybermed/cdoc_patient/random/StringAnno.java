package com.cybermed.cdoc_patient.random;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StringAnno {
    public String value() default "";
}
