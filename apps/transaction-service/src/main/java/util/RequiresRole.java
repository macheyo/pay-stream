package util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Project: pay-stream
 * Module: util
 * File: RequiresRole
 * <p>
 * Created by: justice.m on 23/3/2025
 * <p>
 * © 2025 justice.m. All rights reserved
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequiresRole {
    String[] value();
    boolean allOf() default false;
}
