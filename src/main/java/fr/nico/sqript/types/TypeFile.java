package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;

import java.io.File;

@Type(name = "file",
        parsableAs = {})
public class TypeFile extends ScriptType<File> {

    @Override
    public ScriptElement<?> parse(String typeName) {
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
