package fr.nico.sqript.meta;

import fr.nico.sqript.structures.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

    String name();
    String[] description();
    String[] examples();
    String[] patterns();
    String[] accessors();
    Side side() default Side.RELATIVE;
}
