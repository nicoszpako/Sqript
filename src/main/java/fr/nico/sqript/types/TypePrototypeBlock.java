package fr.nico.sqript.types;

import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.prototypes.ProtoypeBlock;

import javax.annotation.Nullable;

public class TypePrototypeBlock extends ScriptType<ProtoypeBlock> {

    public TypePrototypeBlock(ProtoypeBlock object) {
        super(object);
    }

    @Nullable
    @Override
    public ScriptElement parse(String typeName) {
        return null;
    }

}
