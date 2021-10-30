package fr.nico.sqript.forge.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;

public class CapabilityProvider implements ICapabilitySerializable<NBTBase> {


    @CapabilityInject(fr.nico.sqript.forge.capabilities.Capability.class)
    public static Capability<fr.nico.sqript.forge.capabilities.Capability> MAIN = null;

    private final fr.nico.sqript.forge.capabilities.Capability instance = MAIN.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == MAIN;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        return capability == MAIN ? MAIN.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return MAIN.getStorage().writeNBT(MAIN, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        MAIN.getStorage().readNBT(MAIN, this.instance, null, nbt);
    }

}
