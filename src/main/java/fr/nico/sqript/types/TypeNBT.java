package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Locale;

@Type(name = "nbt",
        parsableAs = {})
public class TypeNBT extends ScriptType<NBTTagCompound> {


    @Override
    public String toString() {
        return this.getObject().toString();
    }

    public TypeNBT(NBTTagCompound nbtTagCompound) {
        super(nbtTagCompound);
    }

    @Nullable
    @Override
    public ScriptElement parse(String typeName) {
        return null;
    }
}
