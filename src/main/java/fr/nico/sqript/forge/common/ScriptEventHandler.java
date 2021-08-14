package fr.nico.sqript.forge.common;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.ScriptTimer;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.events.*;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeBlock;
import fr.nico.sqript.types.TypeItem;
import fr.nico.sqript.types.TypeNull;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class ScriptEventHandler {

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) throws ScriptException {
        if(event.phase == TickEvent.Phase.START)
            ScriptTimer.tick();
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) throws ScriptException {
        if(event.phase == TickEvent.Phase.START)
            ScriptManager.callEvent(new EvtOnWorldTick());
    }

    @SubscribeEvent
    public void onItemUse(PlayerEvent.ItemPickupEvent event) throws ScriptException {
        ScriptManager.callEvent(new EvtPlayer.EvtOnItemPickup((EntityPlayer) event.player,event.getStack()));
    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent.Start event) throws ScriptException {
        if(event.getEntity() instanceof EntityPlayer){
            if(ScriptManager.callEvent(new EvtPlayer.EvtOnItemUse((EntityPlayer) event.getEntity(),event.getItem()))) {
                event.setCanceled(true);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        try{
            if(event.getType()== RenderGameOverlayEvent.ElementType.HEALTH) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderHealthBar(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
                Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
            }else  if(event.getType()== RenderGameOverlayEvent.ElementType.CHAT) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderChat(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            }else if(event.getType()== RenderGameOverlayEvent.ElementType.FOOD) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderFoodBar(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            }else if(event.getType()== RenderGameOverlayEvent.ElementType.ALL) {
                //long start = System.currentTimeMillis();
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderOverlay(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            /*
            long end = System.currentTimeMillis();
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            Minecraft.getMinecraft().fontRenderer.drawString((end-start)+"ms",resolution.getScaledWidth()-Minecraft.getMinecraft().fontRenderer.getStringWidth((end-start)+"ms"),resolution.getScaledHeight()-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT,0xFFFFFFFF);
            */
            }else if(event.getType()== RenderGameOverlayEvent.ElementType.EXPERIENCE) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderXPBar(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            }else if(event.getType()== RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
                if (ScriptManager.callEvent(new EvtRender.EvtOnRenderCrosshair(Minecraft.getMinecraft().player))) {
                    event.setCanceled(true);
                }
            }
        }catch(Exception e){
            Minecraft.getMinecraft().fontRenderer.drawString(e.toString(),0,0,0xFFFF0000);
        }
    }

    @SubscribeEvent
    public void onPlayerSendMessage(ServerChatEvent event) {
        if (event.getPlayer() instanceof EntityPlayer) {
            ScriptContext context = null;
            try {
                context = ScriptManager.callEventAndGetContext(new EvtPlayer.EvtOnPlayerSendMessage(event.getPlayer(), event.getMessage()));
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            if(context.getAccessor("message") != null)
                event.setComponent(new TextComponentString((String) context.getAccessor("message").element.getObject()));
            if ((boolean) context.returnValue.element.getObject()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if(ScriptManager.callEvent(new EvtRender.EvtOnDrawNameplate((EntityPlayer)event.getEntity()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if(ScriptManager.callEvent(new EvtBlock.EvtOnBlockClick((EntityPlayer)event.getEntity(),new TypeBlock(Block.getBlockFromItem(event.getItemStack().getItem()).getDefaultState()),event.getHand(),1,event.getPos()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlace(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if(ScriptManager.callEvent(new EvtBlock.EvtOnBlockClick((EntityPlayer)event.getEntity(),new TypeBlock(Block.getBlockFromItem(event.getItemStack().getItem()).getDefaultState(),event.getPos(),event.getWorld()),event.getHand(),0, event.getPos()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if(ScriptManager.callEvent(new EvtBlock.EvtOnBlockPlace((EntityPlayer)event.getEntity(),new TypeBlock(event.getPlacedBlock(),event.getPos(),event.getWorld())))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.BreakEvent event) {
        if(ScriptManager.callEvent(new EvtBlock.EvtOnBlockBreak(event.getPlayer(),new TypeBlock(event.getState(),event.getPos(),event.getWorld())))) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if(ScriptManager.callEvent(new EvtPlayer.EvtOnItemRightClick((EntityPlayer)event.getEntity(),event.getItemStack(),event.getHand()))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if(ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerJump((EntityPlayer)event.getEntity()))) {
                event.getEntityLiving().setVelocity(0,0,0);
                event.getEntityLiving().velocityChanged = true;
            }
        }
    }


    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerLogin(event.player))){
            ((EntityPlayerMP)event.player).connection.disconnect(new TextComponentString("Disconnected."));
        }
    }

    @SubscribeEvent
    public void onPlayerHit(LivingAttackEvent event){
        if(event.getEntity() instanceof EntityPlayer){
            if(event.getSource().getImmediateSource() instanceof EntityPlayer){
                if(ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerHit((EntityPlayer)event.getEntity(),(EntityPlayer)event.getSource().getImmediateSource()))){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        EntityPlayer player = event.player;
        if(event.phase == TickEvent.Phase.START){
        }else{
            double px = player.prevPosX;
            double py = player.prevPosY;
            double pz = player.prevPosZ;
            if((px-player.posX)!=0 || (py-player.posY)!=0 || (pz-player.posZ)!=0){ //Player moved
                if(ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerMove(player))){ //We post the event OnPlayerEvent and move back the player if the event was cancelled
                    player.setPositionAndUpdate(px,py,pz);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event){
        if(ScriptManager.callEvent(new EvtPlayer.EvtOnEntityInteract(event.getTarget(), event.getHand()))){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event){
        if(ScriptManager.callEvent(new EvtLiving.EvtOnLivingDamage(event.getEntity(), event.getSource(), event.getAmount()))){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event){
        if(ScriptManager.callEvent(new EvtLiving.EvtOnLivingDeath(event.getEntity(), event.getSource()))){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event){
        if(ScriptManager.callEvent(new EvtLiving.EvtOnLivingFall(event.getEntity(), event.getDistance(), event.getDamageMultiplier()))){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event){
        ArrayList< TypeItem > list = new ArrayList<>();
        event.getDrops().forEach(entityItem -> { list.add(new TypeItem(entityItem.getItem())); });
        if(ScriptManager.callEvent(new EvtLiving.EvtOnLivingDrops(event.getEntity(), event.getSource(), new TypeArray(list)))){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerDrops(PlayerDropsEvent event){
        ArrayList< TypeItem > list = new ArrayList<>();
        event.getDrops().forEach(entityItem -> { list.add(new TypeItem(entityItem.getItem())); });
        if(ScriptManager.callEvent(new EvtPlayer.EvtOnPlayerDrops(event.getEntityPlayer(), event.getSource(), new TypeArray(list)))){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event){
        if(ScriptManager.callEvent(new EvtPlayer.EvtOnItemToss(event.getPlayer(), event.getEntityItem()))){
            event.setCanceled(true);
            event.getPlayer().inventory.addItemStackToInventory(event.getEntityItem().getItem());
        }
    }
}
