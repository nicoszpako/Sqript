package fr.nico.sqript.meta;

import fr.nico.sqript.types.ScriptType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface Primitive {

    String name();
    Class<? extends ScriptType>[] parsableAs();
    String pattern();

}
