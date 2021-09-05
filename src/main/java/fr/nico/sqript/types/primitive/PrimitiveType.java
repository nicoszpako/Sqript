package fr.nico.sqript.types.primitive;

import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

public abstract class PrimitiveType<T> extends ScriptType<T> {

    //Désigne tous les types pouvant être instanciés en dehors d'une expression
    //ex : print "hello !" -> instancie un string, sans qu'aucune expression n'ait été appelée
    //Seul le premier pattern d'un type primitif est pris en compte, pour éviter les confusions
    //Doit avoir un constructeur acceptant un unique String

    // /!\ Le regex doit comprendre les parenthèses pour recevoir le groupe à parser. Un regex sans parenthèses sera ignoré. /!\

    public PrimitiveType(){};

    public PrimitiveType(T object) {
        super(object);
    }

    @Override
    public String toString() {
        return String.valueOf(getObject());
    }

}
