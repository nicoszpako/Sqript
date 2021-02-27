package fr.nico.sqript.types.interfaces;

import net.minecraft.nbt.NBTTagCompound;

public interface ISerialisable {

    public abstract NBTTagCompound write(NBTTagCompound compound);
    public abstract void read(NBTTagCompound compound);

}
