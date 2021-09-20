package fr.nico.sqript.meta;

import fr.nico.sqript.structures.Side;

import javax.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Main annotation representing a parseable expression or action.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {

    /**
     * @return The name of this feature.
     */
    String name();

    /**
     * @return The description of this feature.
     */
    String description() default "";

    /**
     * @return A list of examples of this feature.
     */
    String[] examples() default "";

    /**
     * @return The pattern of this feature.
     */
    String pattern() default "";

    /**
     * @return The regex of this feature.
     */
    String regex() default "";

    /**
     * @return If an expression, the return type of this expression.
     */
    String type() default "element";

    /**
     * @return If an expression, whether is can be set to another value using the set() method.
     */
    boolean settable() default true;

    /**
     * @return The running side of this feature.
     */
    Side side() default Side.BOTH;

    /**
     * Highest priorities are checked first.
     * @return The check priority of this action.
     */
    int priority() default 0;
}
