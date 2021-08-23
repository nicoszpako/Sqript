package fr.nico.sqript.meta;

import fr.nico.sqript.structures.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

    /**
     * @return The main feature describing this event.
     */
    Feature feature();

    /**
     * @return An array of features describing the accessors available in this event.
     */
    Feature[] accessors();

}
