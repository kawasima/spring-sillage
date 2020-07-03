package net.unit8.sillage.resource;

import org.springframework.http.HttpMethod;

import java.lang.annotation.*;

/**
 * Controls whether a resource class allows the given a HTTP method.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AllowedMethods {
    HttpMethod[] value() default {HttpMethod.GET, HttpMethod.HEAD};
}
