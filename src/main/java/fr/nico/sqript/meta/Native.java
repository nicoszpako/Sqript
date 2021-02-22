package fr.nico.sqript.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Native {

    //name value represents the package of functions. Example : "maths"
    //Definition syntax : funcName(type1,type2,...):returnType
    //Example : "cos(number):number"

    String name();
    String[] definitions();
    String[] description();
    String[] examples();

}
