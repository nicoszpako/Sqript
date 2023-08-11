package fr.nico.sqript.structures;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.meta.TypeDefinition;

@Type(name="element",parsableAs = {})
public class ScriptElement<T> {

    /*
    Classe un peu inutile pour l'instant mais plus tard par la création d'interface elle deviendra intéressante
     */

    /**
     * Invoked once, when the type is registered.
     * Override this method to access this type definition and
     * define some OperationDefinition or ParseDefinition in it.
     * @param definition
     */
    public void load(TypeDefinition definition){};

    private T object;



    public ScriptElement(){}

    public ScriptElement(T object){
        setObject(object);
    }

    public T getObject() {
        return object;
    }



    public void setObject(T object) {
        this.object = object;
    }

    public Class getType(){
        return object.getClass();
    }

    @Override
    public String toString() {
        return object.toString();
    }

}
