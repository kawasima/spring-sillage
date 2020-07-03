package net.unit8.sillage;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * Maps a method to a decision processor.
 *
 * @author kawasima
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decision {
    /**
     * A decision point.
     *
     * @return a decision point.
     */
    DecisionPoint value();

    /** A target method(s).
     *
     * @return a target method(s)
     */
    HttpMethod[] method() default {};
}
