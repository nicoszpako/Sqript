package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;

import javax.annotation.Nullable;
import java.util.UUID;

@Type(name = "uuid",
        parsableAs = {}
)
public class TypeUUID extends ScriptType< UUID > {

    public TypeUUID(UUID object) {
        super(object);
    }

    @Override
    public String toString() {
        return getObject().toString();
    }

}
