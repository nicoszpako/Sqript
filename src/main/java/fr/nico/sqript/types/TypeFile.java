package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeString;

import java.io.File;
import java.util.Locale;

@Type(name = "file",
        parsableAs = {})
public class TypeFile extends ScriptType<File> {

    @Override
    public ScriptElement<?> parse(String typeName) {
        switch(typeName.toLowerCase(Locale.ROOT)){
            case "string":
                return new TypeString(getObject().getName());
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getObject().getName();
    }

    public TypeFile(File file) {
        super(file);
    }


}
