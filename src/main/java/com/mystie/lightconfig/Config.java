package com.mystie.lightconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation interface for a configuration variable
 *
 * @author		Samuel Longchamps
 * @version		1.0
 * @since		1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    /**
     * @return label value for the configuration variable, empty if none
     * specified (use key name instead)
     */
    String value() default "";

}
