package fr.nico.sqript.meta;

import fr.nico.sqript.structures.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Loop {

    String name();
    String pattern();
    Side side() default Side.BOTH;


    //Highest priorities are checked first
    int priority() default 0;

}
