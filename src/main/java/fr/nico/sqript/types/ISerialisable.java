package fr.nico.sqript.types;

import net.minecraft.nbt.NBTTagCompound;

public interface ISerialisable {

    public abstract NBTTagCompound write(NBTTagCompound compound);
    public abstract void read(NBTTagCompound compound);

}
