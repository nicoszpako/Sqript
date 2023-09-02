package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.network.ScriptMessage;
import fr.nico.sqript.structures.ScriptElement;

import java.io.File;

@Type(name = "message",
        parsableAs = {})
public class TypeMessagePrototype extends ScriptType<ScriptMessage> {

    @Override
    public String toString() {
        return this.getObject().getMessage_id();
    }

    public TypeMessagePrototype(ScriptMessage message) {
        super(message);
    }


}
