package com.spd.grid.annotation;


import java.lang.annotation.ElementType;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
  
/** 
 * @author wyp
 */  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
/**
 * @author wyp
 * @since jdk 1.6
 * @Date 2015-12-11
 */
public @interface Column {  
      
    /** 
     */  
    String value();  
      
    /** 
     * @return 
     */  
    Class<?> type() default String.class;  
      
    /** 
     * @return 
     */  
    int length() default 0;  
  
}  
