package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "null",
        parsableAs = {})
public class TypeNull extends ScriptType<Object> {

    @Override
    public String toString() {
        return "undefined";
    }

    public TypeNull() {
        super(null);
    }


}
