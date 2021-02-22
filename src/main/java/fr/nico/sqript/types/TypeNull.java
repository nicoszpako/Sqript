package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "null",
        parsableAs = {})
public class TypeNull extends ScriptType<Object> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        return "undefined";
    }

    public TypeNull() {
        super(null);
    }


}
