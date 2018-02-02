package com.spd.efs.jfinal.ext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
@Documented 
public @interface Scheduled {
 
    int fixedDelay() default 0;
    String cron() default "";
    boolean enable() default true;
     
}