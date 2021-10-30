package fr.nico.sqript.forge.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;


public class CapabilityStorage implements IStorage<Capability> {

    public static NBTTagCompound toNBT(Capability instance) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("prevX", instance.getPrevX());
        tag.setDouble("prevY", instance.getPrevY());
        tag.setDouble("prevZ", instance.getPrevZ());
        return tag;
    }


    public static void fromNBT(NBTBase nbt, Capability instance) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.setPrevX(tag.getDouble("prevX"));
        instance.setPrevY(tag.getDouble("prevY"));
        instance.setPrevZ(tag.getDouble("prevZ"));
    }


    @Nullable
    public NBTBase writeNBT(net.minecraftforge.common.capabilities.Capability<Capability> capability,Capability instance, EnumFacing side) {
        return toNBT(instance);
    }

    public void readNBT(net.minecraftforge.common.capabilities.Capability<Capability> capability, Capability instance, EnumFacing side, NBTBase nbt) {
        fromNBT(nbt, instance);
    }


}
