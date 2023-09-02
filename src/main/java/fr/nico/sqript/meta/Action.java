package fr.nico.sqript.meta;

import fr.nico.sqript.structures.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    /**
     * @return The global name of this class.
     */
    String name();

    /**
     * @return An array of features that class contains.
     */
    Feature[] features();

    Side side() default Side.BOTH;

    int priority() default 0;
}
