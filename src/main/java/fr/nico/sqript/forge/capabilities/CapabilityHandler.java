package fr.nico.sqript.forge.capabilities;

import fr.nico.sqript.forge.SqriptForge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {

    public static final ResourceLocation MAIN = new ResourceLocation(SqriptForge.MODID, "main");


    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) return;
        event.addCapability(MAIN, new CapabilityProvider());

    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {

        EntityPlayer player = event.getEntityPlayer();

        //Get the old cap
        Capability oldCapability = event.getOriginal().getCapability(CapabilityProvider.MAIN, null);
        NBTTagCompound oldData = CapabilityStorage.toNBT(oldCapability);

        //Put it in the new one
        Capability capability = player.getCapability(CapabilityProvider.MAIN, null);
        CapabilityStorage.fromNBT(oldData, capability);
        capability.setPrevZ(player.posZ);
        capability.setPrevY(player.posY);
        capability.setPrevX(player.posX);
    }

    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
        CapabilityManager.INSTANCE.register(Capability.class, new CapabilityStorage(), Capability::new);
    }
}
