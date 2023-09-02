package fr.nico.sqript.types;

import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.interfaces.ISerialisable;
import net.minecraft.nbt.NBTTagCompound;
import javax.annotation.Nullable;

@Type(name = "nbttagcompound",
        parsableAs = {}
)
public class TypeNBTTagCompound extends ScriptType< NBTTagCompound > implements ISerialisable {

    public TypeNBTTagCompound(NBTTagCompound object) {
        super(object);
    }

    public TypeNBTTagCompound() {
        super(new NBTTagCompound());
    }

    @Override
    public String toString() {
        return getObject().toString();
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        return getObject();
    }

    @Override
    public void read(NBTTagCompound compound) {
        setObject(compound);
    }
}
