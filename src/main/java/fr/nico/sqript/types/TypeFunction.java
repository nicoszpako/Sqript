package fr.nico.sqript.types;

import fr.nico.sqript.blocks.ScriptBlockFunction;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

@Type(name = "function",
        parsableAs = {})
public class TypeFunction extends ScriptType<ScriptBlockFunction> {

    @Override
    public String toString() {
        return this.getObject().name;
    }

    public TypeFunction(ScriptBlockFunction function) {
        super(function);
    }


}
